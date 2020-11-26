package com.codecool.shop.dao.contracts;

import com.codecool.shop.model.User;

import java.util.List;

public interface UserDao {
    User find(String email);

    int add(User user, String hash);

    void remove(User user);

    List<User> getAll();
}
