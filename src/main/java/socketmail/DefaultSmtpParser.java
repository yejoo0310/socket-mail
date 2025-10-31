package socketmail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultSmtpParser implements SmtpParser {
    @Override
    public SmtpResponse read(SmtpTransport transport) throws IOException {
        List<String> lines = new ArrayList<>();
        String line = transport.readLine();
        if (line == null || line.length() < 3) {
            throw new IOException("Invalid SMTP response");
        }

        int code = Integer.parseInt(line.substring(0, 3));
        lines.add(line);

        while (line.length() >= 4 && line.charAt(3) == '-') {
            line = transport.readLine();
            if (line == null || line.length() < 3) {
                break;
            }
            lines.add(line);
        }

        return new SmtpResponse(code, lines);
    }
}
