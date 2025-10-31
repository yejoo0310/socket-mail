package socketmail.view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private JTextField toField;
    private JTextField subjectField;
    private JTextArea bodyArea;
    private JButton sendButton;

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

        // Send Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        sendButton = new JButton("Send");
        panel.add(sendButton, gbc);

        add(panel);
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