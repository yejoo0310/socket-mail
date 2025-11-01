package socketmail.model;

import java.util.List;

public record EmailForm(
        EmailAddress from,
        EmailAddress to,
        Subject subject,
        MessageBody messageBody,
        String htmlBody,
        List<Attachment> attachments,
        List<InlineImage> inlineImages
) {
    public EmailForm(EmailAddress from, EmailAddress to, Subject subject, MessageBody messageBody, String htmlBody, List<Attachment> attachments) {
        this(from, to, subject, messageBody, htmlBody, attachments, List.of());
    }
}
