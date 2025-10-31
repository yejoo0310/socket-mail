package socketmail.service.smtp;

import socketmail.model.SmtpResponse;

import java.io.IOException;

public interface SmtpParser {
    SmtpResponse read(SmtpTransport transport) throws IOException;
}
