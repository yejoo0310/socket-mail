package socketmail.service.smtp;

import java.io.IOException;

public interface SmtpTransport {
    void connect(String host, int port) throws IOException;
    void startTls(String host, int port) throws IOException;
    void writeLine(String line) throws IOException;
    String readLine() throws IOException;
    void close() throws IOException;
}
