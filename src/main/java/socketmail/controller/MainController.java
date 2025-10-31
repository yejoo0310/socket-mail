package socketmail.controller;

import socketmail.model.Email;
import socketmail.model.vo.EmailAddress;
import socketmail.service.SmtpService;
import socketmail.util.ConfigManager;
import socketmail.view.MainView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainController {
    private final MainView view;
    private final ConfigManager config;
    private final SmtpService smtpService;

    public MainController(MainView view, ConfigManager config) {
        this.view = view;
        this.config = config;
        this.smtpService = new SmtpService(); // SmtpService 인스턴스 생성

        this.view.getSendButton().addActionListener(new SendEmailListener());
    }

    class SendEmailListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String to = view.getToField().getText();
            String subject = view.getSubjectField().getText();
            String body = view.getBodyArea().getText();
            String from = ConfigManager.getProperty("mail.smtp.user");

            if (to.isEmpty() || subject.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Recipient and Subject cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Email email = new Email(new EmailAddress(from), new EmailAddress(to), subject, body);
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
                            JOptionPane.showMessageDialog(view, "Email sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(view, "Failed to send email: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        } finally {
                            view.getSendButton().setEnabled(true); // 버튼 다시 활성화
                        }
                    }
                };
                worker.execute();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(view, "Invalid email address: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
