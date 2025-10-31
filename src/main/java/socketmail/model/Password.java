package socketmail.model;

import java.util.Base64;

public record Password(String value) {
    public Password {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
    }

    public String encodedValue() {
        return Base64.getEncoder().encodeToString(value.getBytes());
    }
}
