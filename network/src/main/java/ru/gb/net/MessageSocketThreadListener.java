package ru.gb.net;

public interface MessageSocketThreadListener {

    void onMessageReceived(String msg);
    void onException(Throwable throwable);

    void onSocketClosed();
    void onSockedReady();
}
