package socketmail.model;

public record EmailForm(EmailAddress from, EmailAddress to, Subject subject, Body body) {
}
