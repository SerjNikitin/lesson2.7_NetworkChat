package client;

import client.gui.ChatFrame;
import client.gui.api.Receiver;


public class Chat {
    private ChatFrame chatFrame;
    private ChatCommunication chatCommunication;

    public Chat(String host, int port) {
        chatCommunication = new ChatCommunication(host, port);
        chatFrame = new ChatFrame(data -> chatCommunication.transmit(data));

        new Thread(() -> {
            Receiver receiver = chatFrame.getReceiver();
            while (true) {
                String massage = chatCommunication.receive();
                if (!massage.isBlank()) {
                    receiver.receive(massage);
                }
            }
        }).start();

    }
}
