package socketmail.service.smtp;

import socketmail.model.Attachment;
import socketmail.model.EmailForm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class MimeMessage {

    private static final String CRLF = "\r\n";
    private final String content;

    public MimeMessage(EmailForm email) throws IOException {
        this.content = buildContent(email);
    }

    private String buildContent(EmailForm email) throws IOException {
        String boundary = "boundary-" + UUID.randomUUID();
        StringBuilder message = new StringBuilder();

        // Headers
        message.append("From: ").append(email.from().value()).append(CRLF);
        message.append("To: ").append(email.to().value()).append(CRLF);
        message.append("Subject: ").append(email.subject().value()).append(CRLF);
        message.append("MIME-Version: 1.0").append(CRLF);
        message.append("Content-Type: multipart/mixed; boundary=").append(boundary).append(CRLF);
        message.append(CRLF);

        // Body
        message.append("--").append(boundary).append(CRLF);
        appendBody(message, email, boundary);

        // Attachments
        if (email.attachments() != null && !email.attachments().isEmpty()) {
            appendAttachments(message, email.attachments(), boundary);
        }

        message.append("--").append(boundary).append("--").append(CRLF);

        return message.toString();
    }

    private void appendBody(StringBuilder message, EmailForm email, String boundary) {
        if (email.htmlBody() != null) {
            String alternativeBoundary = "alternative-" + UUID.randomUUID();
            message.append("Content-Type: multipart/alternative; boundary=").append(alternativeBoundary).append(CRLF);
            message.append(CRLF);

            // Plain text part
            message.append("--").append(alternativeBoundary).append(CRLF);
            message.append("Content-Type: text/plain; charset=UTF-8").append(CRLF);
            message.append(CRLF);
            message.append(email.messageBody().value()).append(CRLF);

            // HTML part
            message.append("--").append(alternativeBoundary).append(CRLF);
            message.append("Content-Type: text/html; charset=UTF-8").append(CRLF);
            message.append(CRLF);
            message.append(email.htmlBody()).append(CRLF);
            message.append("--").append(alternativeBoundary).append("--").append(CRLF);
        } else {
            message.append("Content-Type: text/plain; charset=UTF-8").append(CRLF);
            message.append(CRLF);
            message.append(email.messageBody().value()).append(CRLF);
        }
    }

    private void appendAttachments(StringBuilder message, List<Attachment> attachments, String boundary) throws IOException {
        for (Attachment attachment : attachments) {
            File file = attachment.file();
            String fileName = file.getName();
            String mimeType = Files.probeContentType(file.toPath());
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String encodedFile = Base64.getEncoder().encodeToString(fileContent);

            message.append("--").append(boundary).append(CRLF);
            message.append("Content-Type: ").append(mimeType).append("; name=\"").append(fileName).append("\"").append(CRLF);
            message.append("Content-Disposition: attachment; filename=\"").append(fileName).append("\"").append(CRLF);
            message.append("Content-Transfer-Encoding: base64").append(CRLF);
            message.append(CRLF);
            message.append(encodedFile).append(CRLF);
        }
    }

    @Override
    public String toString() {
        return content;
    }
}
