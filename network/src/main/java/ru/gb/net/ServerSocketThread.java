package ru.gb.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketThread extends Thread {
    private final int port;
    private final int timeout;
    private ServerSocketThreadListener listener;

    public ServerSocketThread(ServerSocketThreadListener listener, String name, int port, int timeout) {
        super(name);
        this.port = port;
        this.listener = listener;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(getName() + " running on port: " + port);
            serverSocket.setSoTimeout(timeout);
            while (!isInterrupted()) {
                try{
                    System.out.println("Waiting for connect");
                    Socket socket = serverSocket.accept();
                    listener.onSocketAccepted(socket);
                } catch (IOException e){
                    listener.onClientTimeout(e);
                    continue;
                }
                listener.onClientConnected();
            }
        } catch (IOException e) {
            listener.onException(e);
        }
    }
}
