package server;

import client.ChatHistory;
import dataBase.RequestDB;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {
    private Set<ClientHandler> loggedClient;
    private RequestDB requestDB;

    public RequestDB getRequestDB() {
        return requestDB;
    }

    public ChatServer() {
        loggedClient = new HashSet<>();
        requestDB = new RequestDB();

        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server is running");
            while (true) {
                System.out.println("Waiting connection");
                Socket accept = serverSocket.accept();
                new ClientHandler(accept, this);
            }
        } catch (IOException e) {
            throw new ChatServerException("Что-то пошло не так", e);
        }

    }

    public synchronized void broadcast(String massage) {
        for (ClientHandler clientHandler : loggedClient) {
            clientHandler.sendMassage(massage);
        }
        ChatHistory ch = new ChatHistory();
        ch.writeHistory(massage);
    }

    public synchronized void neroCast(String name, String massage) {
        Iterator<ClientHandler> iterator = loggedClient.iterator();
        while (iterator.hasNext()) {
            ClientHandler clientHandler = iterator.next();
            if (clientHandler.getName().equals(name)) {
                clientHandler.sendMassage(massage);
            }
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        loggedClient.add(clientHandler);
        ChatHistory chatHistory=new ChatHistory();
        ArrayList<String> list = chatHistory.readeHistory();
        clientHandler.sendMassage(Arrays.toString(list.toArray()));
    }

    public void unsubscribe(ClientHandler clientHandler) {
        loggedClient.remove(clientHandler);
    }

    public boolean isLoggedIn(String name) {
        return loggedClient.stream().anyMatch(client -> client.getName().equals(name));
    }
}