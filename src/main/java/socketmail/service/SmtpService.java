package socketmail.service;

import socketmail.model.Email;
import socketmail.model.SmtpResponse;
import socketmail.model.vo.EmailAddress;
import socketmail.model.vo.Host;
import socketmail.model.vo.Password;
import socketmail.model.vo.Port;
import socketmail.service.smtp.DefaultSmtpParser;
import socketmail.service.smtp.SmtpParser;
import socketmail.service.smtp.SmtpTransport;
import socketmail.service.smtp.TcpSmtpTransport;
import socketmail.util.ConfigManager;

import java.io.IOException;
import java.util.Base64;

public class SmtpService {

    private final Host host = new Host(ConfigManager.getProperty("mail.smtp.host"));
    private final Port port = new Port(Integer.parseInt(ConfigManager.getProperty("mail.smtp.port")));
    private final EmailAddress username = new EmailAddress(ConfigManager.getProperty("mail.smtp.user"));
    private final Password password = new Password(ConfigManager.getProperty("mail.smtp.pass"));

    public void send(Email email) throws IOException {
        SmtpTransport transport = new TcpSmtpTransport();
        SmtpParser parser = new DefaultSmtpParser();

        try {
            transport.connect(host.value(), port.value());
            SmtpResponse response = parser.read(transport);
            if (response.code() != 220) throw new IOException("Connection failed: " + response);

            transport.writeLine("EHLO " + host.value());
            response = parser.read(transport);
            if (response.code() != 250) throw new IOException("EHLO failed: " + response);

            // Note: A more robust implementation would check for STARTTLS support here.
            // For simplicity, we are assuming the server supports it.
            transport.writeLine("STARTTLS");
            response = parser.read(transport);
            if (response.code() != 220) throw new IOException("STARTTLS failed: " + response);
            transport.startTls(host.value(), port.value());

            transport.writeLine("EHLO " + host.value());
            response = parser.read(transport);
            if (response.code() != 250) throw new IOException("EHLO after STARTTLS failed: " + response);

            transport.writeLine("AUTH LOGIN");
            response = parser.read(transport);
            if (response.code() != 334) throw new IOException("AUTH LOGIN failed: " + response);

            transport.writeLine(Base64.getEncoder().encodeToString(username.value().getBytes()));
            response = parser.read(transport);
            if (response.code() != 334) throw new IOException("Username failed: " + response);

            transport.writeLine(Base64.getEncoder().encodeToString(password.value().getBytes()));
            response = parser.read(transport);
            if (response.code() != 235) throw new IOException("Password failed: " + response);

            transport.writeLine("MAIL FROM: <" + email.from().value() + ">");
            response = parser.read(transport);
            if (response.code() != 250) throw new IOException("MAIL FROM failed: " + response);

            transport.writeLine("RCPT TO: <" + email.to().value() + ">");
            response = parser.read(transport);
            if (response.code() != 250) throw new IOException("RCPT TO failed: " + response);

            transport.writeLine("DATA");
            response = parser.read(transport);
            if (response.code() != 354) throw new IOException("DATA failed: " + response);

            transport.writeLine("From: " + email.from().value());
            transport.writeLine("To: " + email.to().value());
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
