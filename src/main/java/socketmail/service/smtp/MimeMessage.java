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
        boolean hasInlineImages = email.inlineImages() != null && !email.inlineImages().isEmpty();

        String mixedBoundary = "mixed-" + UUID.randomUUID();
        String relatedBoundary = "related-" + UUID.randomUUID();
        String alternativeBoundary = "alternative-" + UUID.randomUUID();

        StringBuilder message = new StringBuilder();

        // General Headers
        message.append("From: ").append(email.from().value()).append(CRLF);
        message.append("To: ").append(email.to().value()).append(CRLF);
        message.append("Subject: ").append(encodeSubject(email.subject().value())).append(CRLF);
        message.append("MIME-Version: 1.0").append(CRLF);

        if (hasAttachments) {
            message.append("Content-Type: multipart/mixed; boundary=").append(mixedBoundary).append(CRLF).append(CRLF);
            message.append("--").append(mixedBoundary).append(CRLF);
        }

        if (hasInlineImages) {
            message.append("Content-Type: multipart/related; boundary=").append(relatedBoundary).append(CRLF).append(CRLF);
        }

        // Body Part
        appendBody(message, email, hasInlineImages ? relatedBoundary : mixedBoundary, alternativeBoundary);

        // Inline Images Part
        if (hasInlineImages) {
            appendInlineImages(message, email.inlineImages(), relatedBoundary);
            message.append("--").append(relatedBoundary).append("--").append(CRLF);
        }

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
            message.append("Content-Transfer-Encoding: 8bit").append(CRLF).append(CRLF);
            message.append(email.messageBody().value()).append(CRLF);

            // HTML part
            message.append("--").append(alternativeBoundary).append(CRLF);
            message.append("Content-Type: text/html; charset=UTF-8").append(CRLF);
            message.append("Content-Transfer-Encoding: 8bit").append(CRLF).append(CRLF);
            message.append(email.htmlBody()).append(CRLF);
            message.append("--").append(alternativeBoundary).append("--").append(CRLF);
        } else {
            // Plain text only
            message.append("Content-Type: text/plain; charset=UTF-8").append(CRLF);
            message.append("Content-Transfer-Encoding: 8bit").append(CRLF).append(CRLF);
            message.append(email.messageBody().value()).append(CRLF);
        }
    }

    private void appendInlineImages(StringBuilder message, List<InlineImage> inlineImages, String boundary) throws IOException {
        for (InlineImage inlineImage : inlineImages) {
            File file = inlineImage.file();
            String fileName = file.getName();
            String mimeType = Files.probeContentType(file.toPath());
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String encodedFile = Base64.getEncoder().encodeToString(fileContent);

            message.append("--").append(boundary).append(CRLF);
            message.append("Content-Type: ").append(mimeType).append("; name=\"").append(fileName).append("\"").append(CRLF);
            message.append("Content-Transfer-Encoding: base64").append(CRLF);
            message.append("Content-ID: <").append(inlineImage.contentId()).append(">").append(CRLF);
            message.append("Content-Disposition: inline; filename=\"").append(fileName).append("\"").append(CRLF).append(CRLF);
            message.append(encodedFile).append(CRLF);
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

    private String encodeSubject(String subject) {
        if (subject.matches(".*[\\p{IsHangul}].*")) {
            try {
                return "=?UTF-8?B?" + Base64.getEncoder().encodeToString(subject.getBytes("UTF-8")) + "?=";
            } catch (java.io.UnsupportedEncodingException e) {
                return subject;
            }
        }
        return subject;
    }

    @Override
    public String toString() {
        return content;
    }
}
