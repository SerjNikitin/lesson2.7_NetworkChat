package client.gui;

import client.gui.api.Receiver;
import client.gui.api.Sender;

import javax.swing.*;
import java.awt.*;

public class ChatFrame extends JFrame {

    private final JTextArea chatArea;

    public ChatFrame(Sender sender) {
        setTitle("Chat v1.0");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(0, 0, 400, 500);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.setLayout(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        top.add(chatArea, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BorderLayout());

        JTextField inputField = new JTextField();
        bottom.add(inputField, BorderLayout.CENTER);

        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener(new SubmitButtonListener(inputField, sender));
        bottom.add(submitBtn, BorderLayout.EAST);

        add(top, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setVisible(true);
    }

    public Receiver getReceiver() {
        return (data) -> {
            if (!data.isBlank()) {
                chatArea.append(data);
                chatArea.append("\n");
            }
        };
    }
}