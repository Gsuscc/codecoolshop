package com.codecool.shop.dao.implementation.mem;

import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.model.LineItem;

import java.util.ArrayList;
import java.util.List;

public class CartDaoMem implements CartDao {

    private static CartDaoMem instance = null;
    private List<LineItem> data = new ArrayList<>();

    public static CartDaoMem getInstance() {
        if (instance == null) {
            instance = new CartDaoMem();
        }
        return instance;
    }


    @Override
    public void add(LineItem lineItem) {
        for (LineItem item : data) {
            if (item.getProductId() == (lineItem.getProductId())) {
                data.get(data.indexOf(item)).incrementQuantity();
                return;
            }
        }
        lineItem.setId(data.size() + 1);
        data.add(lineItem);
    }

    @Override
    public void add(LineItem lineItem, int id) {

    }

    @Override
    public LineItem find(int id) {
        return data.stream().filter(t -> t.getProductId() == id).findFirst().orElse(null);
    }

    @Override
    public void remove(int id) {
        data.removeIf(item -> item.getProductId() == id);
    }

    @Override
    public List<LineItem> getAll() {
        return data;
    }

    @Override
    public int getTotalQuantityInCart() {
        int totalQuantityInCart = 0;
        for (LineItem item : data) {
            totalQuantityInCart += item.getQuantity();
        }
        return totalQuantityInCart;
    }

    @Override
    public double getTotalAmountInCart() {
        int totalAmountInCart = 0;
        for (LineItem item : data) {
            totalAmountInCart += item.getSubTotal();
        }
        return totalAmountInCart;
    }

    public void emptyCart() {
        this.data = new ArrayList<>();
    }

    @Override
    public void addAll(List<LineItem> list, int userId) {
    }
}
