package socketmail.service.smtp;

import socketmail.model.vo.Host;
import socketmail.model.vo.Port;

import java.io.IOException;

public interface SmtpTransport {
    void connect(Host host, Port port) throws IOException;
    void startTls(Host host, Port port) throws IOException;
    void writeLine(String line) throws IOException;
    String readLine() throws IOException;
    void close() throws IOException;
}
