package ru.gb.core;

import ru.gb.chat.common.MessageLibrary;
import ru.gb.net.MessageSocketThreadListener;
import ru.gb.net.ServerSocketThread;
import ru.gb.net.ServerSocketThreadListener;

import java.net.Socket;

public class ChatServer implements ServerSocketThreadListener, MessageSocketThreadListener {

    private ServerSocketThread serverSocketThread;
    private ClientSessionThread clientSession;
    private ChatServerListener listener;
    private AuthController authController;

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
    }

    @Override
    public void onClientConnected() {
        logMessage("Client connected");
    }

    public void onSocketAccepted(Socket socket) {
        clientSession = new ClientSessionThread(this, "ClientSessionThread", socket);
    }

    @Override
    public void onException(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onSocketClosed() {
        logMessage("Socket closed");
    }

    @Override
    public void onSockedReady() {
        logMessage("Socket ready");
    }

    @Override
    public void onClientTimeout(Throwable throwable) {

    }

    @Override
    public void onMessageReceived(String msg) {
        if (clientSession.isAuthorized()) {
            processAuthorizedUserMessage(msg);
        } else {
            processUnauthorizedUserMessage(msg);
        }
    }

    private void processAuthorizedUserMessage(String msg) {
        logMessage(msg);
        clientSession.sendMessage("Echo: " + msg);
    }

    private void processUnauthorizedUserMessage(String msg) {
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
        if (nickname == null){
            clientSession.authDenied();
            return;
        }
        clientSession.authAccept(nickname);
    }

    public void disconnectAll() {
    }

    private void logMessage(String msg) {
        listener.onChatServerMessage(msg);
    }
}
