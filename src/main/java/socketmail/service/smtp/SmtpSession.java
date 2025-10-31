package socketmail.service.smtp;

import socketmail.exception.SmtpException;
import socketmail.model.Email;
import socketmail.model.SmtpResponse;
import socketmail.model.config.SmtpConfig;
import java.io.IOException;
import java.util.Base64;

public class SmtpSession implements AutoCloseable {

    private final SmtpConfig config;
    private final SmtpTransport transport;
    private final SmtpParser parser;

    private final String CRLF = "\r\n";
    private final String FAILED_MESSAGE_FORMMAT = "%s failed";

    public SmtpSession(SmtpConfig config, SmtpTransport transport, SmtpParser parser) {
        this.config = config;
        this.transport = transport;
        this.parser = parser;
    }

    public void send(Email email) throws IOException {
        connect();
        handshake();
        startTls();
        handshake();
        authenticate();
        sendMessage(email);
        quit();
    }

    private void connect() throws IOException {
        transport.connect(config.host(), config.port());
        String command = "CONNECTION";
        checkResponse(parser.read(transport), 220, FAILED_MESSAGE_FORMMAT.formatted(command));
    }

    private void handshake() throws IOException {
        String command = "EHLO";
        writeCommand(command + " " + config.host().value());
        checkResponse(parser.read(transport), 250, FAILED_MESSAGE_FORMMAT.formatted(command));
    }

    private void startTls() throws IOException {
        writeCommand("STARTTLS");
        checkResponse(parser.read(transport), 220, "STARTTLS failed");
        transport.startTls(config.host(), config.port());
    }

    private void authenticate() throws IOException {
        writeCommand("AUTH LOGIN");
        checkResponse(parser.read(transport), 334, "AUTH LOGIN failed");

        String userBase64 =
                Base64.getEncoder().encodeToString(config.username().value().getBytes());
        writeCommand(userBase64);
        checkResponse(parser.read(transport), 334, "Username failed");

        String passBase64 =
                Base64.getEncoder().encodeToString(config.password().value().getBytes());
        writeCommand(passBase64);
        checkResponse(parser.read(transport), 235, "Password failed");
    }

    private void sendMessage(Email email) throws IOException {
        writeCommand("MAIL FROM: <" + email.from().value() + ">");
        checkResponse(parser.read(transport), 250, "MAIL FROM failed");

        writeCommand("RCPT TO: <" + email.to().value() + ">");
        checkResponse(parser.read(transport), 250, "RCPT TO failed");

        writeCommand("DATA");
        checkResponse(parser.read(transport), 354, "DATA failed");

        writeCommand("From: " + email.from().value());
        writeCommand("To: " + email.to().value());
        writeCommand("Subject: " + email.subject());
        writeCommand("");
        writeCommand(email.body());
        writeCommand(".");
        checkResponse(parser.read(transport), 250, "Email content sending failed");
    }

    private void quit() throws IOException {
        writeCommand("QUIT");
        parser.read(transport); // 응답은 확인하지 않아도 무방
    }

    // 유틸리티 메소드
    private void writeCommand(String line) throws IOException {
        // TODO: 여기에 로깅을 추가하면 디버깅에 유용합니다.
        transport.writeLine(line);
    }

    // DRY 원칙: 응답 코드 검증 로직을 헬퍼 메소드로 분리
    private void checkResponse(SmtpResponse response, int expectedCode, String errorMessage)
            throws SmtpException {
        if (response.code() != expectedCode) {
            throw new SmtpException(errorMessage, response);
        }
    }

    @Override
    public void close() throws IOException {
        transport.close();
    }
}
