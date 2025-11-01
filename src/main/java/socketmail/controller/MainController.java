package socketmail.controller;

import socketmail.model.Attachment;
import socketmail.model.MessageBody;
import socketmail.model.EmailAddress;
import socketmail.model.EmailForm;
import socketmail.model.Subject;
import socketmail.service.SmtpService;
import socketmail.util.ConfigManager;
import socketmail.view.MainView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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
        this.view.getLoadHtmlButton().addActionListener(new LoadHtmlListener());
    }

    class SendEmailListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String to = view.getToField().getText();
            String subject = view.getSubjectField().getText();
            String body = view.getBodyArea().getText();
            String from = ConfigManager.getProperty("mail.smtp.user");

            try {
                MessageBody messageBody = new MessageBody(body, "text/plain");
                List<Attachment> attachmentList = attachments.stream().map(Attachment::new).collect(Collectors.toList());
                EmailForm email = new EmailForm(new EmailAddress(from), new EmailAddress(to),
                        new Subject(subject), messageBody, htmlBody, attachmentList);
                view.getSendButton().setEnabled(false); // 버튼 비활성화

                // SwingWorker를 사용하여 백그라운드에서 이메일 전송
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        smtpService.send(email);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get(); // doInBackground에서 발생한 예외를 가져옴
                            JOptionPane.showMessageDialog(view, "Email sent successfully!",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(view,
                                    "Failed to send email: " + ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        } finally {
                            view.getSendButton().setEnabled(true); // 버튼 다시 활성화
                            attachments.clear();
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
            int option = fileChooser.showOpenDialog(view);
            if (option == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                for (File file : files) {
                    attachments.add(file);
                }
                view.getAttachmentsLabel().setText("Attachments: " + attachments.stream().map(File::getName).collect(Collectors.joining(", ")));
            }
        }
    }

    class LoadHtmlListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(view);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    htmlBody = new String(Files.readAllBytes(file.toPath()));
                    view.getBodyArea().setText("HTML body loaded from " + file.getName());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(view, "Error loading HTML file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
