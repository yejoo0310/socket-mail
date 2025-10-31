package socketmail.model;

public record Body(String value) {
    public Body {
        if (value == null) {
            throw new IllegalArgumentException("Body cannot be null.");
        }
    }
}
