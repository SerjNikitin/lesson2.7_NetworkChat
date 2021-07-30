package server;


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
        doTimer();
        while (true) {
            sendMassage("Пожалуйста войдите в аккаунт с помощью команды -auth логин пароль \n" +
                    "или создайте новый с помощью команды -nu логин пароль имя");
            try {
                String data = in.readUTF();
                if (data.startsWith("-auth")) {
                    String[] arrayData = data.split(" ");
                    enteredChat(arrayData[1], arrayData[2]);
                    return;
                } else if (data.startsWith("-nu")) {
                    String[] arrayData = data.split(" ");
                    chatServer.getRequestDB().save(arrayData[1], arrayData[2], arrayData[3]);
                    sendMassage("Вы зарегистрировались");
                    enteredChat(arrayData[1], arrayData[2]);
                    return;
                } else {
                    sendMassage("Не корректно введены данные");
                }
            } catch (IOException | ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public void enteredChat(String login, String password) {
        Optional<Entry> mayBeCredentials = chatServer.getRequestDB().findUser(login, password);
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
                authentication();
            }
        } else {
            sendMassage("Пользователя с таким логином и паролем нет");
            authentication();
        }
    }

    public String receiveMassage() {
        while (true) {
            try {
                String massage = in.readUTF();
                if (massage.startsWith("/w")) {
                    sendMassage("Введите имя пользователя и сообщение");
                    String updMassage = in.readUTF();
                    String[] data = updMassage.split(" ");
                    chatServer.neroCast(data[0], updMassage);
                } else if (massage.startsWith("/del")) {
                    System.out.println("Введите логин и пароль чтобы удалить ваш аккаунт");
                    String updMassage = in.readUTF();
                    String[] data = updMassage.split(" ");
                    chatServer.getRequestDB().delete(data[0], data[1]);
                    sendMassage("Вы удалили свой аккаунт");
                    chatServer.broadcast(String.format("Пользователь %s удалил свой аккаунт", name));
                    chatServer.unsubscribe(this);
                    authentication();
                } else if (massage.startsWith("/upd")) {
                    sendMassage("Введите логин пароль и новое имя");
                    String updMassage = in.readUTF();
                    String[] data = updMassage.split(" ");
                    chatServer.getRequestDB().update(data[0], data[1], data[2]);
                    chatServer.broadcast(String.format("Пользователь %s поменял свой ник на %s", name, data[2]));
                    name = data[2];
                } else if (massage.startsWith("-exit")) {
                    chatServer.broadcast(String.format("Пользователь %s вышел из чата", name));
                    chatServer.unsubscribe(this);
                    authentication();
                } else chatServer.broadcast(String.format("%s : " + "%s", name, massage));
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

    public void doTimer() {
        new Thread(() -> {
            try {
                Thread.sleep(120000);
                if (name == null) {
                    out.writeUTF("The authorization time is over");
                    socket.close();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}