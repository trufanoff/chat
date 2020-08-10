package ru.gb.core;

import ru.gb.chat.common.MessageLibrary;
import ru.gb.net.MessageSocketThread;
import ru.gb.net.MessageSocketThreadListener;
import ru.gb.net.ServerSocketThread;
import ru.gb.net.ServerSocketThreadListener;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;


public class ChatServer implements ServerSocketThreadListener, MessageSocketThreadListener {

    private ServerSocketThread serverSocketThread;
    private ChatServerListener listener;
    private AuthController authController;
    private Vector<ClientSessionThread> clients = new Vector<>();

    public ChatServer(ChatServerListener listener) {
        this.listener = listener;
    }

    public void start(int port) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            return;
        }
        serverSocketThread = new ServerSocketThread(this, "Chat-Server-Socket-Thread", port, 2000);
        serverSocketThread.start();
        authController = new AuthController();
        authController.init();
        System.out.println("Server started");
    }

    public void stop() {
        if (serverSocketThread == null || !serverSocketThread.isAlive()) {
            return;
        }
        serverSocketThread.interrupt();
        System.out.println("Server stopped");
        disconnectAll();
    }

    /*   Server Socked Thread Listener methods  */
    /*  создание новой клиенткой сессии и добавление ее в Vector*/
    public void onSocketAccepted(Socket socket) {
        clients.add(new ClientSessionThread(this, "ClientSessionThread", socket));
    }

    @Override
    public void onClientConnected() {
        logMessage("Client connected");
    }

    @Override
    public void onException(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onClientTimeout(Throwable throwable) {
    }

    /*  Message Socked Thread Listener methods  */
    @Override
    public void onMessageReceived(MessageSocketThread thread, String msg) {
        ClientSessionThread clientSession = (ClientSessionThread) thread;
        System.out.println("chatServer.onMessageReceived(): msg = " + msg);
        System.out.println("isAuthorized: " + clientSession.isAuthorized());
        if (clientSession.isAuthorized()) {
            processAuthorizedUserMessage(msg);
        } else {
            processUnauthorizedUserMessage(clientSession, msg);
        }
    }

    @Override
    public void onSockedReady(MessageSocketThread thread) {
        logMessage("Socket ready");
    }

    @Override
    public void onSocketClosed(MessageSocketThread thread) {
        ClientSessionThread clientSession = (ClientSessionThread) thread;
        logMessage("Socket closed");
        clients.remove(thread);
        if (clientSession.isAuthorized() && !clientSession.isReconnected()) {
            sendToAllAuthorizedClients(MessageLibrary.getBroadcastMessage("server", "User " + clientSession.getNickname() + " disconnected."));
        }
        sendToAllAuthorizedClients(MessageLibrary.getUserList(getUsersList()));
    }

    @Override
    public void onException(MessageSocketThread thread, Throwable throwable) {
        throwable.printStackTrace();
    }

    // рассылка сообщений всем автори
    private void processAuthorizedUserMessage(String msg) {
        logMessage(msg);
        for (ClientSessionThread client : clients) {
            if (!client.isAuthorized()) {
                continue;
            }
            client.sendMessage(msg);
        }
    }

    /*  рассылка сообщений все авторизованным пользователям     */
    private void sendToAllAuthorizedClients(String msg) {
        for (ClientSessionThread client : clients) {
            if (!client.isAuthorized()) {
                continue;
            }
            client.sendMessage(msg);
        }
    }

    private void processUnauthorizedUserMessage(ClientSessionThread clientSession, String msg) {
        System.out.println("processUnauthorizedUserMessage()");
        String[] arr = msg.split(MessageLibrary.DELIMITER);
        if (arr.length < 4 ||
                !arr[0].equals(MessageLibrary.AUTH_METHOD) ||
                !arr[1].equals(MessageLibrary.AUTH_REQUEST)) {
            clientSession.authError("Incorrect request: " + msg);
            return;
        }
        String login = arr[2];
        String password = arr[3];
        String nickname = authController.getNickname(login, password);
        if (nickname == null) {
            clientSession.authDenied();
            return;
        } else {
            ClientSessionThread oldClientSession = findClientSessionByNickname(nickname);
            clientSession.authAccept(nickname);
            if (oldClientSession == null) {
                sendToAllAuthorizedClients(MessageLibrary.getBroadcastMessage("server", "User " + nickname + " connected!"));
            } else {
                oldClientSession.setReconnected(true);
                clients.remove(oldClientSession);
            }
        }
        sendToAllAuthorizedClients(MessageLibrary.getUserList(getUsersList()));
    }

    /*  печать в консоль сообщений которые отправляет сервер после получения данных от клиента и их обработки   */
    private void logMessage(String msg) {
        listener.onChatServerMessage(msg);
    }

    /*  Отключение подключенных на данный момент пользователей   */
    public void disconnectAll() {
        ArrayList<ClientSessionThread> currentClients = new ArrayList<>(clients);
        for (ClientSessionThread client : currentClients) {
            client.close();
            clients.remove(client);
        }
    }

    /*  список подлюченных юзеров к чату    */
    public String getUsersList() {
        StringBuilder sb = new StringBuilder();
        for (ClientSessionThread client : clients) {
            if (!client.isAuthorized()) {
                continue;
            }
            sb.append(client.getNickname()).append(MessageLibrary.DELIMITER);
        }
        return sb.toString();
    }

    /*  поиск старой сессии клиента по никнейму */
    private ClientSessionThread findClientSessionByNickname(String nickname) {
        for (ClientSessionThread client : clients) {
            if (!client.isAuthorized()) {
                continue;
            }
            if (client.getNickname().equals(nickname)) {
                return client;
            }
        }
        return null;
    }

}
