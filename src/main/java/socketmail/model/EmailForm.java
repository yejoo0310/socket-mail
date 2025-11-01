package socketmail.model;

import java.util.List;

public record EmailForm(
        EmailAddress from,
        EmailAddress to,
        Subject subject,
        MessageBody messageBody,
        String htmlBody,
        List<Attachment> attachments
) {
    public EmailForm(EmailAddress from, EmailAddress to, Subject subject, MessageBody messageBody) {
        this(from, to, subject, messageBody, null, List.of());
    }
}
