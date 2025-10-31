package socketmail;

import java.io.IOException;

public interface SmtpParser {
    SmtpResponse read(SmtpTransport transport) throws IOException;
}
