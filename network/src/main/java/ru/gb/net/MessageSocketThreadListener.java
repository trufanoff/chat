package ru.gb.net;

public interface MessageSocketThreadListener {

    void onMessageReceived(MessageSocketThread thread, String msg);
    void onException(MessageSocketThread thread, Throwable throwable);
    void onSocketClosed(MessageSocketThread thread);
    void onSockedReady(MessageSocketThread thread);
}
