package com.codecool.shop.dao.contracts;


import com.codecool.shop.model.Checkout;
import com.codecool.shop.model.LineItem;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.User;

import java.util.List;

public interface OrderDao {

    void add(Checkout order, User user, List<LineItem> products, double totalPrice);

    Checkout find(int id);

    void remove(int id);

    List<Checkout> getAll();

    List<Order> getAllByUser(int userId);
}
