package ru.gb.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private final JTextArea chatArea = new JTextArea();
    private final JPanel panelTop = new JPanel(new GridLayout(2, 3));
    private final JTextField ipAddressField = new JTextField("127.0.0.1");
    private final JTextField portField = new JTextField("8181");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("Always on top", true);
    private final JTextField loginField = new JTextField("login");
    private final JPasswordField passwordField = new JPasswordField("123");
    private final JButton buttonLogin = new JButton("Login");

    private final JPanel panelBottom = new JPanel(new BorderLayout());
    private final JButton buttonDisconnect = new JButton("Disconnect");
    private final JTextField messageField = new JTextField();
    private final JButton buttonSend = new JButton("Send");
    private final JList<String> listUsers = new JList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ClientGUI();
            }
        });
    }

    public ClientGUI() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setSize(WIDTH, HEIGHT);
        setTitle("Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);

        listUsers.setListData(new String[]{
                "user1", "user2", "user3", "user4", "user5", "user6", "user7",
                "user8", "user9", "user-with-too-long-name-in-this-chat"
        });

        JScrollPane scrollPaneUsers = new JScrollPane(listUsers);
        JScrollPane scrollPaneChatArea = new JScrollPane(chatArea);
        scrollPaneUsers.setPreferredSize(new Dimension(100, 0));

        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setEditable(false);

        panelTop.add(ipAddressField);
        panelTop.add(portField);
        panelTop.add(cbAlwaysOnTop);
        panelTop.add(loginField);
        panelTop.add(passwordField);
        panelTop.add(buttonLogin);
        panelBottom.add(buttonDisconnect, BorderLayout.WEST);
        panelBottom.add(messageField, BorderLayout.CENTER);
        panelBottom.add(buttonSend, BorderLayout.EAST);

        add(scrollPaneChatArea, BorderLayout.CENTER);
        add(scrollPaneUsers, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);
        add(panelBottom, BorderLayout.SOUTH);

        messageField.addActionListener(this);
        buttonSend.addActionListener(this);
        cbAlwaysOnTop.addActionListener(this);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == cbAlwaysOnTop) {
            setAlwaysOnTop(cbAlwaysOnTop.isSelected());
        } else if (src == buttonSend || src==messageField) {
            sendMessage(loginField.getText(), messageField.getText());
        } else {
            throw new RuntimeException("Unsupported action: " + src);
        }
    }

    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        StackTraceElement[] ste = e.getStackTrace();
        String msg = String.format("Exception in \"%s\": %s %s%n\t %s", t.getName(),
                e.getClass().getCanonicalName(), e.getMessage(), ste[0]);
        showError(msg);
    }

    private void sendMessage(String user, String msg) {
        if (msg.isEmpty()){
            return;
        }
        //23.06.2020 12:20:25 <Login>: сообщение
        String msgToChat = String.format("%s <%s>: %s", sdf.format(Calendar.getInstance().getTime()), user, msg);
        chatArea.append(msgToChat+"\n");
        messageField.setText("");
        messageField.grabFocus();
        putIntoFileHistory(user, msgToChat);
    }

    public void putIntoFileHistory(String user, String msg) {
        try(PrintWriter pw = new PrintWriter(new FileOutputStream(user+"-history.txt",true))){
            pw.println(msg);
        } catch (FileNotFoundException e) {
            showError(msg);
        }
    }

    public void showError(String errorMsg){
        JOptionPane.showMessageDialog(this, errorMsg, "Exception!!!", JOptionPane.ERROR_MESSAGE);
    }
}
