package socketmail.service.smtp;

import socketmail.exception.SmtpException;
import socketmail.model.EmailForm;
import socketmail.model.SmtpResponse;
import java.io.IOException;

public class SmtpSession implements AutoCloseable {

    private final SmtpConfig config;
    private final SmtpTransport transport;
    private final SmtpParser parser;

    private static final String FAILED_MESSAGE_FORMAT = "%s failed";

    public SmtpSession(SmtpConfig config, SmtpTransport transport, SmtpParser parser) {
        this.config = config;
        this.transport = transport;
        this.parser = parser;
    }

    public void send(EmailForm email) throws IOException {
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
        String step = "CONNECTION";
        checkResponse(parser.read(transport), SmtpStatusCode.SERVICE_READY,
                FAILED_MESSAGE_FORMAT.formatted(step));
    }

    private void handshake() throws IOException {
        writeCommand(SmtpCommand.EHLO.toString() + " " + config.host().value());
        checkResponse(parser.read(transport), SmtpStatusCode.OK,
                FAILED_MESSAGE_FORMAT.formatted(SmtpCommand.EHLO.toString()));
    }

    private void startTls() throws IOException {
        writeCommand(SmtpCommand.STARTTLS.toString());
        checkResponse(parser.read(transport), SmtpStatusCode.SERVICE_READY,
                FAILED_MESSAGE_FORMAT.formatted(SmtpCommand.STARTTLS.toString()));
        transport.startTls(config.host(), config.port());
    }

    private void authenticate() throws IOException {
        writeCommand(SmtpCommand.AUTH_LOGIN.toString());
        checkResponse(parser.read(transport), SmtpStatusCode.AUTH_CONTINUE,
                FAILED_MESSAGE_FORMAT.formatted(SmtpCommand.AUTH_LOGIN.toString()));

        writeCommand(config.username().encodedValue());
        checkResponse(parser.read(transport), SmtpStatusCode.AUTH_CONTINUE,
                FAILED_MESSAGE_FORMAT.formatted("Username"));

        writeCommand(config.password().encodedValue());
        checkResponse(parser.read(transport), SmtpStatusCode.AUTH_SUCCESS,
                FAILED_MESSAGE_FORMAT.formatted("Password"));
    }

    private void sendMessage(EmailForm email) throws IOException {
        String fromAddress = email.from().value();
        String toAddress = email.to().value();

        writeCommand(SmtpCommand.MAIL_FROM.toString() + ": <" + fromAddress + ">");
        checkResponse(parser.read(transport), SmtpStatusCode.OK,
                FAILED_MESSAGE_FORMAT.formatted(SmtpCommand.MAIL_FROM.toString()));

        writeCommand(SmtpCommand.RCPT_TO.toString() + ": <" + toAddress + ">");
        checkResponse(parser.read(transport), SmtpStatusCode.OK,
                FAILED_MESSAGE_FORMAT.formatted(SmtpCommand.RCPT_TO.toString()));

        writeCommand(SmtpCommand.DATA.toString());
        checkResponse(parser.read(transport), SmtpStatusCode.START_MAIL_INPUT,
                FAILED_MESSAGE_FORMAT.formatted(SmtpCommand.DATA.toString()));

        writeCommand("From: " + fromAddress);
        writeCommand("To: " + toAddress);
        writeCommand("Subject: " + email.subject().value());
        writeCommand("");
        writeCommand(email.body().value());
        writeCommand("."); // End of data

        String step = "Email content sending";
        checkResponse(parser.read(transport), SmtpStatusCode.OK,
                FAILED_MESSAGE_FORMAT.formatted(step));
    }

    private void quit() throws IOException {
        writeCommand(SmtpCommand.QUIT.toString());
        parser.read(transport);
    }

    private void writeCommand(String line) throws IOException {
        transport.writeLine(line);
    }

    private void checkResponse(SmtpResponse response, SmtpStatusCode expectedCode,
            String errorMessage) throws SmtpException {
        if (response.code() != expectedCode.getCode()) {
            throw new SmtpException(errorMessage, response);
        }
    }

    @Override
    public void close() throws IOException {
        transport.close();
    }
}
