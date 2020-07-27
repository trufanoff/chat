package ru.gb.core;

import ru.gb.chat.common.MessageLibrary;
import ru.gb.net.MessageSocketThread;
import ru.gb.net.MessageSocketThreadListener;

import java.net.Socket;

public class ClientSessionThread extends MessageSocketThread {

    private boolean isAuthorized = false;
    private String nickname;
    private boolean reconnected = false;

    public ClientSessionThread(MessageSocketThreadListener listener, String name, Socket socket) {
        super(listener, name, socket);
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public String getNickname() {
        return nickname;
    }

    public void authAccept(String nickname){
        this.nickname = nickname;
        this.isAuthorized = true;
        sendMessage(MessageLibrary.getAuthAcceptMessage(nickname));
    }

    public void authDenied(){
        sendMessage(MessageLibrary.getAuthDeniedMessage());
        close();
    }

    public void authError(String msg){
        sendMessage(MessageLibrary.getMsgFormatErrorMessage(msg));
        close();
    }

    public boolean isReconnected() {
        return reconnected;
    }

    public void setReconnected(boolean reconnected) {
        this.reconnected = reconnected;
    }
}
