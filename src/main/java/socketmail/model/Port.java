package socketmail.model;

public record Port(int value) {
    public Port {
        if (value < 1 || value > 65535) {
            throw new IllegalArgumentException("Invalid port number: " + value);
        }
    }
}
