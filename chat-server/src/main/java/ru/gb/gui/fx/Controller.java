package ru.gb.gui.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import ru.gb.core.ChatServer;
import ru.gb.core.ChatServerListener;

public class Controller implements ChatServerListener {

    @FXML
    public TextArea logArea;

    private ChatServer chatServer = new ChatServer(this);

    public void start(ActionEvent actionEvent) {
        chatServer.start(8181);
    }

    public void stop(ActionEvent actionEvent) {
        chatServer.stop();
    }

    public void disconnectAll(ActionEvent actionEvent) {
        chatServer.disconnectAll();
    }

    @Override
    public void onChatServerMessage(String msg) {
        logArea.appendText(msg + System.lineSeparator());
    }
}
