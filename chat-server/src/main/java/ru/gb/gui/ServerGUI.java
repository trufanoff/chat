package ru.gb.gui;

import ru.gb.core.ChatServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler{

    private static final int POS_X = 700;
    private static final int POS_Y = 350;
    private static final int WIDTH = 200;
    private static final int HEIGHT = 100;
    private ChatServer chatServer;
    private final JButton buttonStart = new JButton("Start");
    private final JButton buttonStop = new JButton("Stop");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }

    public ServerGUI() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setTitle("Chat Server Admin Console");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(POS_X, POS_Y, WIDTH, HEIGHT);
        setResizable(false);
        setLayout(new GridLayout(1, 2));
        chatServer = new ChatServer();
        buttonStart.addActionListener(this);
        buttonStop.addActionListener(this);
        add(buttonStart);
        add(buttonStop);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == buttonStart) {
            chatServer.start(8181);
//            setVisible(false);
        } else if (src == buttonStop) {
            chatServer.stop();
        } else {
            throw new RuntimeException("Unsupported action: " + src);
        }
    }

    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        StackTraceElement[] ste = e.getStackTrace();
        String msg = String.format("Exception in \"%s\": %s %s%n\t %s",t.getName(),
                e.getClass().getCanonicalName(), e.getMessage(), ste[0]);
        JOptionPane.showMessageDialog(this, msg, "Exception!!!", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
