package socketmail.service.smtp;

import socketmail.model.EmailAddress;
import socketmail.model.Host;
import socketmail.model.Password;
import socketmail.model.Port;

public record SmtpConfig(Host host, Port port, EmailAddress username, Password password) {
}
