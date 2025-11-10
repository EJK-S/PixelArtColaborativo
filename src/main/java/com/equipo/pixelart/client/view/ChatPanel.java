package com.equipo.pixelart.client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Panel de Chat - Permite la comunicación entre usuarios
 */
public class ChatPanel extends JPanel {
    @Serial
	private static final long serialVersionUID = 1L;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    private final SimpleDateFormat timeFormat;

    /**
     * Constructor
     */
    public ChatPanel() {
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        setupUI();
    }

    /**
     * Configura la interfaz del panel
     */
    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Chat",
                0,
                0,
                new Font("Arial", Font.BOLD, 12)
        ));

        // Área de texto del chat (solo lectura)
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

        // Panel inferior con campo de texto y botón
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

    /**
     * Agrega un mensaje al chat
     */
    public void addMessage(String username, String message) {
        String timestamp = timeFormat.format(new Date());
        String formattedMessage = String.format("[%s] %s: %s\n", timestamp, username, message);

        chatArea.append(formattedMessage);

        // Auto-scroll hacia abajo
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    /**
     * Agrega un mensaje del sistema
     */
    public void addSystemMessage(String message) {
        String timestamp = timeFormat.format(new Date());
        String formattedMessage = String.format("[%s] *** %s ***\n", timestamp, message);

        chatArea.append(formattedMessage);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    /**
     * Limpia el chat
     */
    public void clearChat() {
        chatArea.setText("");
    }

    /**
     * Obtiene el texto del campo de mensaje y lo limpia
     */
    public String getMessageAndClear() {
        String message = messageField.getText().trim();
        messageField.setText("");
        return message;
    }

    /**
     * Establece el foco en el campo de mensaje
     */
    public void focusMessageField() {
        messageField.requestFocusInWindow();
    }

    /**
     * Agrega un listener al botón de enviar
     */
    public void addSendListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }

    /**
     * Habilita o deshabilita el chat
     */
    public void setEnabled(boolean enabled) {
        messageField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    // ==================== GETTERS ====================

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