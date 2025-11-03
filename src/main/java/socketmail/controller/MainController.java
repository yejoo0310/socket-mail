package socketmail.controller;

import socketmail.model.*;
import socketmail.service.SmtpService;
import socketmail.util.ConfigManager;
import socketmail.view.MainView;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainController {
    private final MainView view;
    private final SmtpService smtpService;
    private final List<File> attachments = new ArrayList<>();
    private String htmlBody = null;

    public MainController(MainView view, ConfigManager config) {
        this.view = view;
        this.smtpService = new SmtpService();

        this.view.getSendButton().addActionListener(new SendEmailListener());
        this.view.getAttachButton().addActionListener(new AttachFileListener());
    }

    class SendEmailListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String toRaw = view.getToField().getText();
                Recipients recipients = new Recipients(toRaw);

                String subject = view.getSubjectField().getText();
                String textBody = view.getBodyEditor().getText();
                htmlBody = null;
                String from = ConfigManager.getProperty("mail.smtp.user");

                MessageBody messageBody = new MessageBody(textBody, "text/plain");
                List<Attachment> attachmentList = attachments.stream().map(Attachment::new).collect(Collectors.toList());

                EmailForm email = new EmailForm(new EmailAddress(from), recipients,
                        new Subject(subject), messageBody, htmlBody, attachmentList);

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
                            htmlBody = null;
                            updateAttachmentsUI();
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
                updateAttachmentsUI();
            }
        }
    }

    private void updateAttachmentsUI() {
        view.getAttachmentPanel().removeAll();
        for (File file : attachments) {
            JPanel filePanel = new JPanel(new BorderLayout());
            JLabel fileNameLabel = new JLabel(file.getName());
            JButton removeButton = new JButton("x");
            removeButton.addActionListener(e -> {
                attachments.remove(file);
                updateAttachmentsUI();
            });
            filePanel.add(fileNameLabel, BorderLayout.CENTER);
            filePanel.add(removeButton, BorderLayout.EAST);
            view.getAttachmentPanel().add(filePanel);
        }
        view.getAttachmentPanel().revalidate();
        view.getAttachmentPanel().repaint();
    }
}
