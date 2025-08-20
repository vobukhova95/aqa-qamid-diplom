package data.api;

public enum ApiEndpoints {
    PAY ("/api/v1/pay"),
    CREDIT ("/api/v1/credit"),
    WRONG ("/api/v1/wrong");

    private final String endpoint;

    ApiEndpoints(String endpoint){
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
