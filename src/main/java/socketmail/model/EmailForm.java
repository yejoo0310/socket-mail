package socketmail.model;

import java.util.List;

public record EmailForm(EmailAddress from, Recipients to, Subject subject, MessageBody messageBody, String htmlBody,
                        List<Attachment> attachments) {
}
