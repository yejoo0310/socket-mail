package socketmail.view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {

    private final JTextField toField;
    private final JTextField subjectField;
    private final JEditorPane bodyEditor;
    private final JButton sendButton;
    private final JButton attachButton;
    private final JPanel attachmentPanel;

    public MainView() {
        setTitle("SocketMail");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Fields
        toField = new JTextField();
        subjectField = new JTextField();
        attachmentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        bodyEditor = new JEditorPane();
        bodyEditor.setContentType("text/plain");
        JScrollPane bodyScrollPane = new JScrollPane(bodyEditor);

        // Buttons
        sendButton = new JButton("Send");
        attachButton = new JButton("Attach File");

        // Layout
        JPanel topPanel = new JPanel(new GridLayout(3, 2));
        topPanel.add(new JLabel("To:"));
        topPanel.add(toField);
        topPanel.add(new JLabel("Subject:"));
        topPanel.add(subjectField);
        topPanel.add(new JLabel("Attachments:"));
        topPanel.add(new JScrollPane(attachmentPanel));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sendButton);
        buttonPanel.add(attachButton);

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


    public JPanel getAttachmentPanel() {
        return attachmentPanel;
    }
}
