package socketmail.model;

import socketmail.model.vo.EmailAddress;

public record Email(EmailAddress from, EmailAddress to, String subject, String body) {
}
