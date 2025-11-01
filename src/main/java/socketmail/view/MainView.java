package socketmail.view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private JTextField toField;
    private JTextField subjectField;
    private JTextArea bodyEditor;
    private JEditorPane htmlPreview;
    private JButton sendButton;
    private JButton attachButton;
    private JButton loadHtmlButton;
    private JButton embedImageButton;
    private JLabel attachmentsLabel;

    public MainView() {
        setTitle("Socket Mail");
        setSize(800, 600);
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

        // Body (Editor and Preview)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        bodyEditor = new JTextArea();
        htmlPreview = new JEditorPane();
        htmlPreview.setEditable(false);
        htmlPreview.setContentType("text/html");
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(bodyEditor), new JScrollPane(htmlPreview));
        splitPane.setResizeWeight(0.5);
        panel.add(splitPane, gbc);


        // Button Panel
        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        attachButton = new JButton("Attach File");
        loadHtmlButton = new JButton("Load HTML");
        embedImageButton = new JButton("Embed Image");
        attachmentsLabel = new JLabel("Attachments: ");
        buttonPanel.add(attachButton);
        buttonPanel.add(loadHtmlButton);
        buttonPanel.add(embedImageButton);
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

    public JTextArea getBodyEditor() {
        return bodyEditor;
    }

    public JEditorPane getHtmlPreview() {
        return htmlPreview;
    }

    public JButton getEmbedImageButton() {
        return embedImageButton;
    }

    public JButton getSendButton() {
        return sendButton;
    }
}
