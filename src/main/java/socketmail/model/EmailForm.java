package socketmail.model;

import socketmail.model.vo.Body;
import socketmail.model.vo.EmailAddress;
import socketmail.model.vo.Subject;

public record EmailForm(EmailAddress from, EmailAddress to, Subject subject, Body body) {
}
