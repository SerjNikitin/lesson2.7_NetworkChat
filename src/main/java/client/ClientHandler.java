package client;

import dataBase.RequestDB;
import server.ChatServer;
import server.ChatServerException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class ClientHandler {
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private ChatServer chatServer;
    private Socket socket;

    public ClientHandler(Socket socket, ChatServer chatServer) {
        this.socket = socket;
        this.chatServer = chatServer;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            throw new ChatServerException("Что-то пошло не так при попытке подключения", e);
        }
        new Thread(() -> {
            authentication();
            listen();
        }).start();

    }

    public void listen() {
        receiveMassage();
    }

    public String getName() {
        return name;
    }

    public void authentication() {
        sendMassage("Пожалуйста войдите в аккаунт с помощью команды -auth или создайте новый с помощью команды -nu");
        while (true) {
            try {
                String data = in.readUTF();
                if (data.startsWith("-auth")) {
                    String[] arrayData = data.split(" ");
                    Optional<Entry> mayBeCredentials = chatServer.getRequestDB().findUser(arrayData[1], arrayData[2]);
                    if (mayBeCredentials.isPresent()) {
                        Entry entry = mayBeCredentials.get();

                        if (!chatServer.isLoggedIn(entry.getName())) {
                            name = entry.getName();
                            chatServer.broadcast(String.format("Пользователь %s вошел в общий чат", name));
                            chatServer.subscribe(this);
                            sendMassage("Вы вошли в чат под именем " + name);
                            return;
                        } else {
                            sendMassage(String.format("Пользователь с именем %s уже вошел в чат", entry.getName()));
                        }
                    } else {
                        sendMassage("Пользователя с таким логином и паролем нет");
                    }
                } else if (data.startsWith("-nu")) {
                    String[] arrayData = data.split(" ");
                    chatServer.getRequestDB().save(arrayData[1], arrayData[2], arrayData[3]);
                } else {
                    sendMassage("Не корректно введены данные, введите: -auth логин пароль");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String receiveMassage() {
        while (true) {
            try {
                String massage = in.readUTF();
                chatServer.broadcast(String.format("%s : " + "%s", name, massage));
            } catch (IOException e) {
                throw new ChatServerException("Ошибка при получении сообщения ", e);
            }
        }
    }

    public void sendMassage(String massage) {
        try {
            out.writeUTF(massage);
        } catch (IOException e) {
            throw new ChatServerException("Ошибка при отправке сообщения ", e);
        }
    }
}