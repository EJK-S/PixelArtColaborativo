package com.equipo.pixelart.client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatPanel extends JPanel {
    @Serial
	private static final long serialVersionUID = 1L;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    private final SimpleDateFormat timeFormat;

    public ChatPanel() {
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Chat",
                0,
                0,
                new Font("Arial", Font.BOLD, 12)
        ));
     
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        chatArea.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(280, 200));

        add(scrollPane, BorderLayout.CENTER);

        
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));

        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 12));
        messageField.addActionListener(e -> sendButton.doClick());

        sendButton = new JButton("Enviar");
        sendButton.setFont(new Font("Arial", Font.BOLD, 11));
        sendButton.setPreferredSize(new Dimension(70, 25));

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);
    }

    public void addMessage(String username, String message) {
        String timestamp = timeFormat.format(new Date());
        String formattedMessage = String.format("[%s] %s: %s\n", timestamp, username, message);

        chatArea.append(formattedMessage);

        // Auto-scroll hacia abajo
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void addSystemMessage(String message) {
        String timestamp = timeFormat.format(new Date());
        String formattedMessage = String.format("[%s] *** %s ***\n", timestamp, message);

        chatArea.append(formattedMessage);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void clearChat() {
        chatArea.setText("");
    }

    public String getMessageAndClear() {
        String message = messageField.getText().trim();
        messageField.setText("");
        return message;
    }

    public void focusMessageField() {
        messageField.requestFocusInWindow();
    }

    public void addSendListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }

    public void setEnabled(boolean enabled) {
        messageField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public JTextField getMessageField() {
        return messageField;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public JTextArea getChatArea() {
        return chatArea;
    }
}