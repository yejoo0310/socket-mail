package socketmail.model.vo;

public record Password(String value) {
    public Password {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
    }
}
