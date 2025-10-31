package socketmail.service.smtp;

import java.io.IOException;
import socketmail.model.Host;
import socketmail.model.Port;

public interface SmtpTransport {
    void connect(Host host, Port port) throws IOException;
    void startTls(Host host, Port port) throws IOException;
    void writeLine(String line) throws IOException;
    String readLine() throws IOException;
    void close() throws IOException;
}
