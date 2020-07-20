package ru.gb.core;

import ru.gb.data.User;

import java.util.ArrayList;
import java.util.HashMap;

public class AuthController {

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
        users.add(new User("admin", "admin", "sysroot"));
        users.add(new User("login", "123", "def_user"));
        return users;
    }
}
