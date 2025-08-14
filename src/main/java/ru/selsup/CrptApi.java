package ru.selsup;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CrptApi {
    private final Duration timeWindow;
    private final int requestLimit;
    private volatile LocalDateTime windowStart;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    private static final String apiUrl = "https://ismp.crpt.ru/api/v3/lk/documents/send";
    private final HttpClient client = HttpClient.newHttpClient();

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeWindow = this.TimeUnitConverter(timeUnit);
        this.requestLimit = requestLimit;
    }

    public String createDocument(Document document, String signature, String authToken)
            throws IOException, InterruptedException {
        this.checkRequestLimit();
        String jsonDocument = gson.toJson(document);

        Document.DocumentRequest requestBody = new Document.DocumentRequest(
                "MANUAL",
                Base64.getEncoder().encodeToString(jsonDocument.getBytes(StandardCharsets.UTF_8)),
                "LP_INTRODUCE_GOODS",
                Base64.getEncoder().encodeToString(signature.getBytes(StandardCharsets.UTF_8))
        );

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "*/*")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("API error: " + response.body() + " Error code: " + response.statusCode());
        }
    }

    static class Document {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private LocalDate production_date;
        private String production_type;
        private List<Product> products;
        private LocalDate reg_date;
        private String reg_number;

        public Document() {
        }

        static class Description {
            private String participantInn;

            public Description() {
            }

            public Description(String participantInn) {
                this.participantInn = participantInn;
            }

            public String getParticipantInn() {
                return participantInn;
            }

            public void setParticipantInn(String participantInn) {
                this.participantInn = participantInn;
            }
        }

        static class Product {
            private String certificate_document;
            private LocalDate certificate_document_date;
            private String certificate_document_number;
            private String owner_inn;
            private String producer_inn;
            private LocalDate production_date;
            private String tnved_code;
            private String uit_code;
            private String uitu_code;

            public Product() {
            }

            public String getCertificate_document() {
                return certificate_document;
            }

            public void setCertificate_document(String certificate_document) {
                this.certificate_document = certificate_document;
            }

            public LocalDate getCertificate_document_date() {
                return certificate_document_date;
            }

            public void setCertificate_document_date(LocalDate certificate_document_date) {
                this.certificate_document_date = certificate_document_date;
            }

            public String getCertificate_document_number() {
                return certificate_document_number;
            }

            public void setCertificate_document_number(String certificate_document_number) {
                this.certificate_document_number = certificate_document_number;
            }

            public String getOwner_inn() {
                return owner_inn;
            }

            public void setOwner_inn(String owner_inn) {
                this.owner_inn = owner_inn;
            }

            public String getProducer_inn() {
                return producer_inn;
            }

            public void setProducer_inn(String producer_inn) {
                this.producer_inn = producer_inn;
            }

            public LocalDate getProduction_date() {
                return production_date;
            }

            public void setProduction_date(LocalDate production_date) {
                this.production_date = production_date;
            }

            public String getTnved_code() {
                return tnved_code;
            }

            public void setTnved_code(String tnved_code) {
                this.tnved_code = tnved_code;
            }

            public String getUit_code() {
                return uit_code;
            }

            public void setUit_code(String uit_code) {
                this.uit_code = uit_code;
            }

            public String getUitu_code() {
                return uitu_code;
            }

            public void setUitu_code(String uitu_code) {
                this.uitu_code = uitu_code;
            }
        }

        static class DocumentRequest {
            private final String document_format;
            private final String product_document;
            private final String signature;
            private final String type;

            public DocumentRequest(String format, String product_document, String signature, String type) {
                this.document_format = format;
                this.product_document = product_document;
                this.signature = signature;
                this.type = type;
            }
        }

        public Description getDescription() {
            return description;
        }

        public void setDescription(Description description) {
            this.description = description;
        }

        public String getDoc_id() {
            return doc_id;
        }

        public void setDoc_id(String doc_id) {
            this.doc_id = doc_id;
        }

        public String getDoc_status() {
            return doc_status;
        }

        public void setDoc_status(String doc_status) {
            this.doc_status = doc_status;
        }

        public String getDoc_type() {
            return doc_type;
        }

        public void setDoc_type(String doc_type) {
            this.doc_type = doc_type;
        }

        public boolean isImportRequest() {
            return importRequest;
        }

        public void setImportRequest(boolean importRequest) {
            this.importRequest = importRequest;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public void setOwner_inn(String owner_inn) {
            this.owner_inn = owner_inn;
        }

        public String getParticipant_inn() {
            return participant_inn;
        }

        public void setParticipant_inn(String participant_inn) {
            this.participant_inn = participant_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public void setProducer_inn(String producer_inn) {
            this.producer_inn = producer_inn;
        }

        public LocalDate getProduction_date() {
            return production_date;
        }

        public void setProduction_date(LocalDate production_date) {
            this.production_date = production_date;
        }

        public String getProduction_type() {
            return production_type;
        }

        public void setProduction_type(String production_type) {
            this.production_type = production_type;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        public LocalDate getReg_date() {
            return reg_date;
        }

        public void setReg_date(LocalDate reg_date) {
            this.reg_date = reg_date;
        }

        public String getReg_number() {
            return reg_number;
        }

        public void setReg_number(String reg_number) {
            this.reg_number = reg_number;
        }
    }

    class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }

    private Duration TimeUnitConverter(TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:   return Duration.ofNanos(1);
            case MICROSECONDS:  return Duration.ofNanos(1);
            case MILLISECONDS:  return Duration.ofMillis(1);
            case SECONDS:       return Duration.ofSeconds(1);
            case MINUTES:       return Duration.ofMinutes(1);
            case HOURS:         return Duration.ofHours(1);
            case DAYS:          return Duration.ofDays(1);
            default: throw new IllegalArgumentException("Unknown TimeUnit: " + unit);
        }
    }

    private void checkRequestLimit() {
        LocalDateTime now = LocalDateTime.now();
        synchronized (this) {
            if (windowStart == null || Duration.between(windowStart, now).compareTo(timeWindow) >= 0) {
                windowStart = now;
                requestCount.set(0);
            }
        }
        if (requestCount.incrementAndGet() > requestLimit) {
            throw new ValidationException("Rate limit exceeded");
        }
    }
}