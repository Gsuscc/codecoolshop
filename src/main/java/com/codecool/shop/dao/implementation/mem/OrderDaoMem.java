package com.codecool.shop.dao.implementation.mem;

import com.codecool.shop.dao.contracts.OrderDao;
import com.codecool.shop.model.Checkout;
import com.codecool.shop.model.LineItem;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.User;

import java.util.ArrayList;
import java.util.List;

public class OrderDaoMem implements OrderDao {
    private static OrderDaoMem instance = null;
    private List<Checkout> data = new ArrayList<>();

    /* A private Constructor prevents any other class from instantiating.
     */
    private OrderDaoMem() {
    }

    public static OrderDaoMem getInstance() {
        if (instance == null) {
            instance = new OrderDaoMem();
        }
        return instance;
    }

    @Override
    public void add(Checkout order, User user, List<LineItem> products, double totalPrice) {
        order.setId(data.size() + 1);
        data.add(order);
    }

    @Override
    public Checkout find(int id) {
        return data.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void remove(int id) {
        data.remove(find(id));
    }

    @Override
    public List<Checkout> getAll() {
        return data;
    }

    @Override
    public List<Order> getAllByUser(int userId) {
        return null;
    }
}
