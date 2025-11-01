package socketmail.view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
        bodyEditor = new JEditorPane();
        bodyEditor.setContentType("text/html");
        JScrollPane bodyScrollPane = new JScrollPane(bodyEditor);

        // Buttons
        sendButton = new JButton("Send");
        attachButton = new JButton("Attach File");
        loadHtmlButton = new JButton("Load HTML");

        // Layout
        JPanel topPanel = new JPanel(new GridLayout(3, 2));
        topPanel.add(new JLabel("To:"));
        topPanel.add(toField);
        topPanel.add(new JLabel("Subject:"));
        topPanel.add(subjectField);
        topPanel.add(new JLabel(""));
        topPanel.add(attachmentsLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sendButton);
        buttonPanel.add(attachButton);
        buttonPanel.add(loadHtmlButton);

        add(topPanel, BorderLayout.NORTH);
        add(bodyScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public JTextField getToField() {
        return toField;
    }

    public JTextField getSubjectField() {
        return subjectField;
    }

    public JEditorPane getBodyEditor() {
        return bodyEditor;
    }

    public JButton getSendButton() {
        return sendButton;
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
}
