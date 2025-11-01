package socketmail.service.smtp;

import socketmail.model.Attachment;
import socketmail.model.EmailForm;
import socketmail.model.InlineImage;

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
        boolean hasAttachments = email.attachments() != null && !email.attachments().isEmpty();

        String mixedBoundary = "mixed-" + UUID.randomUUID();
        String alternativeBoundary = "alternative-" + UUID.randomUUID();

        StringBuilder message = new StringBuilder();

        // General Headers
        message.append("From: ").append(encodeHeader(email.from().value())).append(CRLF);
        message.append("To: ").append(encodeHeader(email.to().value())).append(CRLF);
        message.append("Subject: ").append(encodeHeader(email.subject().value())).append(CRLF);
        message.append("MIME-Version: 1.0").append(CRLF);

        if (hasAttachments) {
            message.append("Content-Type: multipart/mixed; boundary=").append(mixedBoundary).append(CRLF).append(CRLF);
            message.append("--").append(mixedBoundary).append(CRLF);
        }

        // Body Part
        appendBody(message, email, mixedBoundary, alternativeBoundary);

        // Attachments Part
        if (hasAttachments) {
            appendAttachments(message, email.attachments(), mixedBoundary);
            message.append("--").append(mixedBoundary).append("--").append(CRLF);
        }

        return message.toString();
    }

    private void appendBody(StringBuilder message, EmailForm email, String parentBoundary, String alternativeBoundary) {
        if (email.htmlBody() != null) {
            message.append("Content-Type: multipart/alternative; boundary=").append(alternativeBoundary).append(CRLF).append(CRLF);

            // Plain text part
            message.append("--").append(alternativeBoundary).append(CRLF);
            message.append("Content-Type: text/plain; charset=UTF-8").append(CRLF);
            message.append("Content-Transfer-Encoding: base64").append(CRLF).append(CRLF);
            message.append(Base64.getEncoder().encodeToString(email.messageBody().value().getBytes())).append(CRLF);

            // HTML part
            message.append("--").append(alternativeBoundary).append(CRLF);
            message.append("Content-Type: text/html; charset=UTF-8").append(CRLF);
            message.append("Content-Transfer-Encoding: base64").append(CRLF).append(CRLF);
            message.append(Base64.getEncoder().encodeToString(email.htmlBody().getBytes())).append(CRLF);
            message.append("--").append(alternativeBoundary).append("--").append(CRLF);
        } else {
            // Plain text only
            message.append("Content-Type: text/plain; charset=UTF-8").append(CRLF);
            message.append("Content-Transfer-Encoding: base64").append(CRLF).append(CRLF);
            message.append(Base64.getEncoder().encodeToString(email.messageBody().value().getBytes())).append(CRLF);
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
            message.append("Content-Transfer-Encoding: base64").append(CRLF).append(CRLF);
            message.append(encodedFile).append(CRLF);
        }
    }

    private String encodeHeader(String header) {
        if (header.matches(".*[\\p{IsHangul}].*")) {
            try {
                return "=?UTF-8?B?" + Base64.getEncoder().encodeToString(header.getBytes("UTF-8")) + "?=";
            } catch (java.io.UnsupportedEncodingException e) {
                return header;
            }
        }
        return header;
    }

    @Override
    public String toString() {
        return content;
    }
}
