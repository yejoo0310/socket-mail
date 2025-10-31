package socketmail.service;

import socketmail.model.Email;
import socketmail.model.SmtpResponse;
import socketmail.service.smtp.DefaultSmtpParser;
import socketmail.service.smtp.SmtpParser;
import socketmail.service.smtp.SmtpTransport;
import socketmail.service.smtp.TcpSmtpTransport;
import socketmail.util.ConfigManager;

import java.io.IOException;
import java.util.Base64;

public class SmtpService {

    private final String host = ConfigManager.getProperty("mail.smtp.host");
    private final int port = Integer.parseInt(ConfigManager.getProperty("mail.smtp.port"));
    private final String username = ConfigManager.getProperty("mail.smtp.user");
    private final String password = ConfigManager.getProperty("mail.smtp.pass");

    public void send(Email email) throws IOException {
        SmtpTransport transport = new TcpSmtpTransport();
        SmtpParser parser = new DefaultSmtpParser();

        try {
            transport.connect(host, port);
            SmtpResponse response = parser.read(transport);
            if (response.code() != 220) throw new IOException("Connection failed: " + response);

            transport.writeLine("EHLO " + host);
            response = parser.read(transport);
            if (response.code() != 250) throw new IOException("EHLO failed: " + response);

            // Note: A more robust implementation would check for STARTTLS support here.
            // For simplicity, we are assuming the server supports it.
            transport.writeLine("STARTTLS");
            response = parser.read(transport);
            if (response.code() != 220) throw new IOException("STARTTLS failed: " + response);
            transport.startTls(host, port);

            transport.writeLine("EHLO " + host);
            response = parser.read(transport);
            if (response.code() != 250) throw new IOException("EHLO after STARTTLS failed: " + response);

            transport.writeLine("AUTH LOGIN");
            response = parser.read(transport);
            if (response.code() != 334) throw new IOException("AUTH LOGIN failed: " + response);

            transport.writeLine(Base64.getEncoder().encodeToString(username.getBytes()));
            response = parser.read(transport);
            if (response.code() != 334) throw new IOException("Username failed: " + response);

            transport.writeLine(Base64.getEncoder().encodeToString(password.getBytes()));
            response = parser.read(transport);
            if (response.code() != 235) throw new IOException("Password failed: " + response);

            transport.writeLine("MAIL FROM: <" + email.from() + ">");
            response = parser.read(transport);
            if (response.code() != 250) throw new IOException("MAIL FROM failed: " + response);

            transport.writeLine("RCPT TO: <" + email.to() + ">");
            response = parser.read(transport);
            if (response.code() != 250) throw new IOException("RCPT TO failed: " + response);

            transport.writeLine("DATA");
            response = parser.read(transport);
            if (response.code() != 354) throw new IOException("DATA failed: " + response);

            transport.writeLine("From: " + email.from());
            transport.writeLine("To: " + email.to());
            transport.writeLine("Subject: " + email.subject());
            transport.writeLine("");
            transport.writeLine(email.body());
            transport.writeLine(".");
            response = parser.read(transport);
            if (response.code() != 250) throw new IOException("Email content sending failed: " + response);

            transport.writeLine("QUIT");
            parser.read(transport);

        } finally {
            transport.close();
        }
    }
}
