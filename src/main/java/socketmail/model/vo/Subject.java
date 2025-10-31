package socketmail.model.vo;

public record Subject(String value) {
    public Subject {
        if (value == null) {
            throw new IllegalArgumentException("Subject cannot be null.");
        }
    }
}
