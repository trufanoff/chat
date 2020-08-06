package ru.gb.core;

import ru.gb.data.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class AuthController {

    private Connection connection;
    private Statement statement;

    HashMap<String, User> users = new HashMap<>();

    public void init() {
        for (User user : receiveUsers()) {
            users.put(user.getLogin(), user);
        }
    }

    public String getNickname(String login, String password) {
        User user = users.get(login);
        if (user != null && user.isPasswordCorrect(password)) {
            return user.getNickname();
        }
        return null;
    }

    private ArrayList<User> receiveUsers() {
        ArrayList<User> users = new ArrayList<>();
        try {
            connect();
            ResultSet resultSet = statement.executeQuery("select login, password, nickname from users");
            while(resultSet.next()){
                users.add(new User(resultSet.getString("login"), resultSet.getString("password"), resultSet.getString("nickname")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return users;
    }

    private void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
        statement = connection.createStatement();
    }

    private void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
