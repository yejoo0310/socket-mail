package socketmail.model.config;

import socketmail.model.vo.EmailAddress;
import socketmail.model.vo.Host;
import socketmail.model.vo.Password;
import socketmail.model.vo.Port;

public record SmtpConfig(Host host, Port port, EmailAddress username, Password password) {
}
