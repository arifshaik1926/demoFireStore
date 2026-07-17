package Service;

public record CustomerRequest(
        String customerId,
        String email,
        Double spend
) {}
