package socketmail;

import java.util.List;

public record SmtpResponse(int code, List<String> lines) {
}
