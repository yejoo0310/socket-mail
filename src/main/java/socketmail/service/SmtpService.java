package socketmail.service;

import socketmail.model.EmailAddress;
import socketmail.model.EmailForm;
import socketmail.model.Host;
import socketmail.model.Password;
import socketmail.model.Port;
import socketmail.service.smtp.DefaultSmtpParser;
import socketmail.service.smtp.SmtpConfig;
import socketmail.service.smtp.SmtpSession;
import socketmail.service.smtp.TcpSmtpTransport;
import socketmail.util.ConfigManager;

import java.io.IOException;

public class SmtpService {

    private final SmtpConfig config;

    public SmtpService(SmtpConfig config) {
        this.config = config;
    }

    public SmtpService() {
        this.config = new SmtpConfig(new Host(ConfigManager.getProperty("mail.smtp.host")),
                new Port(Integer.parseInt(ConfigManager.getProperty("mail.smtp.port"))),
                new EmailAddress(ConfigManager.getProperty("mail.smtp.user")),
                new Password(ConfigManager.getProperty("mail.smtp.pass")));
    }

    public void send(EmailForm email) throws IOException {
        try (SmtpSession session =
                new SmtpSession(config, new TcpSmtpTransport(), new DefaultSmtpParser())) {
            session.send(email);
        }
    }

}
