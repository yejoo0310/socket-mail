package socketmail.model;

public record Host(String value) {
    public Host {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Host cannot be null or empty.");
        }
    }
}
