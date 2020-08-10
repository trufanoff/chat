package ru.gb.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class ServerSocketThread extends Thread {
    private final int port;
    private final int timeout;
    private ServerSocketThreadListener listener;
    public ExecutorService executorService;

    /* входящие данные: класс (ChatServer) который реализует ServerSocketThreadListener,
     *                  имя,
     *                  порт (на котором ожидать подключения)
     *                  timeout (время прекращения ожидания подключения клиента)
     */
    public ServerSocketThread(ServerSocketThreadListener listener, String name, int port, int timeout) {
        super(name);
        this.port = port;
        this.listener = listener;
        this.timeout = timeout;
        this.executorService = Executors.newFixedThreadPool(2);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(getName() + " running on port: " + port);
            serverSocket.setSoTimeout(timeout);
            while (!isInterrupted()) {
                try {
                    System.out.println("Waiting for connect");
                    Socket socket = serverSocket.accept();

                    /*  передача сокета (классу который реализует интерфейс ServerSocketThreadListener СhatServer) для создания клиент сессии */
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSocketAccepted(socket);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    listener.onClientTimeout(e);
                    continue;
                }
                //клиент подключен
                listener.onClientConnected();
            }
        } catch (IOException e) {
            listener.onException(e);
        }
    }
}
