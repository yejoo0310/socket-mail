package socketmail.view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private JTextField toField;
    private JTextField subjectField;
    private JTextArea bodyArea;
    private JButton sendButton;
    private JButton attachButton;
    private JButton loadHtmlButton;
    private JLabel attachmentsLabel;

    public MainView() {
        setTitle("Socket Mail");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // To
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("To:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        toField = new JTextField();
        panel.add(toField, gbc);

        // Subject
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Subject:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        subjectField = new JTextField();
        panel.add(subjectField, gbc);

        // Body
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        bodyArea = new JTextArea();
        panel.add(new JScrollPane(bodyArea), gbc);

        // Button Panel
        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        attachButton = new JButton("Attach");
        loadHtmlButton = new JButton("Load HTML");
        attachmentsLabel = new JLabel("Attachments: ");
        buttonPanel.add(attachButton);
        buttonPanel.add(loadHtmlButton);
        buttonPanel.add(attachmentsLabel);
        panel.add(buttonPanel, gbc);

        // Send Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        sendButton = new JButton("Send");
        panel.add(sendButton, gbc);

        add(panel);
    }

    public JButton getAttachButton() {
        return attachButton;
    }

    public JButton getLoadHtmlButton() {
        return loadHtmlButton;
    }

    public JLabel getAttachmentsLabel() {
        return attachmentsLabel;
    }

    public JTextField getToField() {
        return toField;
    }

    public JTextField getSubjectField() {
        return subjectField;
    }

    public JTextArea getBodyArea() {
        return bodyArea;
    }

    public JButton getSendButton() {
        return sendButton;
    }
}
