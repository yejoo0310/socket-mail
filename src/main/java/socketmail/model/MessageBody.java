package socketmail.model;

public record MessageBody(String value, String contentType) {
    public MessageBody {
        if (value == null) {
            throw new IllegalArgumentException("Body cannot be null.");
        }
        if (contentType == null) {
            throw new IllegalArgumentException("ContentType cannot be null.");
        }
    }
}
