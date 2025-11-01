package socketmail.controller;

import socketmail.model.*;
import socketmail.service.SmtpService;
import socketmail.util.ConfigManager;
import socketmail.view.MainView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainController {
    private final MainView view;
    private final SmtpService smtpService;
    private final List<File> attachments = new ArrayList<>();
    private final List<InlineImage> inlineImages = new ArrayList<>();
    private String htmlBody = null;

    public MainController(MainView view, ConfigManager config) {
        this.view = view;
        this.smtpService = new SmtpService();

        // Add listeners
        this.view.getSendButton().addActionListener(new SendEmailListener());
        this.view.getAttachButton().addActionListener(new AttachFileListener());
        this.view.getLoadHtmlButton().addActionListener(new LoadHtmlListener());
        this.view.getEmbedImageButton().addActionListener(new EmbedImageListener());
        this.view.getBodyEditor().getDocument().addDocumentListener(new HtmlPreviewListener());
    }

    class SendEmailListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String to = view.getToField().getText();
            String subject = view.getSubjectField().getText();
            String textBody = view.getBodyEditor().getText(); // Use editor content as plain text fallback
            htmlBody = view.getBodyEditor().getText(); // Also use editor content as HTML body
            String from = ConfigManager.getProperty("mail.smtp.user");

            try {
                MessageBody messageBody = new MessageBody(textBody, "text/plain");
                List<Attachment> attachmentList = attachments.stream().map(Attachment::new).collect(Collectors.toList());

                EmailForm email = new EmailForm(new EmailAddress(from), new EmailAddress(to),
                        new Subject(subject), messageBody, htmlBody, attachmentList, inlineImages);

                view.getSendButton().setEnabled(false);

                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        smtpService.send(email);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            JOptionPane.showMessageDialog(view, "Email sent successfully!",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(view,
                                    "Failed to send email: " + ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        } finally {
                            view.getSendButton().setEnabled(true);
                            attachments.clear();
                            inlineImages.clear();
                            htmlBody = null;
                            view.getAttachmentsLabel().setText("Attachments: ");
                        }
                    }
                };
                worker.execute();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(view, "Invalid input: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class AttachFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                for (File file : fileChooser.getSelectedFiles()) {
                    attachments.add(file);
                }
                String attachmentNames = attachments.stream().map(File::getName).collect(Collectors.joining(", "));
                view.getAttachmentsLabel().setText("Attachments: " + attachmentNames);
            }
        }
    }

    class LoadHtmlListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    view.getBodyEditor().setText(content);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(view, "Error loading HTML file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    class EmbedImageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String cid = UUID.randomUUID().toString();
                inlineImages.add(new InlineImage(file, cid));
                String imgTag = "<img src=\"cid:" + cid + "\" alt=\"" + file.getName() + "\">";
                view.getBodyEditor().insert(imgTag, view.getBodyEditor().getCaretPosition());
            }
        }
    }

    class HtmlPreviewListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updatePreview();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updatePreview();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updatePreview();
        }

        private void updatePreview() {
            view.getHtmlPreview().setText(view.getBodyEditor().getText());
        }
    }
}
