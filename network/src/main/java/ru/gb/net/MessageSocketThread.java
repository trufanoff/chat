package ru.gb.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MessageSocketThread extends Thread {

    private Socket socket;
    private MessageSocketThreadListener listener;

    public MessageSocketThread(MessageSocketThreadListener listener, String name, Socket socket) {
        super(name);
        this.socket = socket;
        this.listener = listener;
        start();
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            while (!isInterrupted()) {
                listener.onMessageReceived(in.readUTF());
            }
        } catch (IOException e) {
            listener.onException(e);
        }
    }

    public void sendMessage(String message) {
        try {
            if(!socket.isConnected() || socket.isClosed()){
                listener.onException(new RuntimeException("Socket is closed or not initialized"));
            }
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
        } catch (IOException e) {
            listener.onException(e);
        }
    }
}
