package socketmail.model;

import java.util.List;

public record SmtpResponse(int code, List<String> lines) {
}
