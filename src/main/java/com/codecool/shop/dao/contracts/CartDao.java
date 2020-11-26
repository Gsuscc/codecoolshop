package com.codecool.shop.dao.contracts;


import com.codecool.shop.model.LineItem;

import java.util.List;

public interface CartDao extends CartDaoFunctional {

    void add(LineItem lineItem);

    void add(LineItem lineItem, int id);

    LineItem find(int id);

    void remove(int id);

    List<LineItem> getAll();

    int getTotalQuantityInCart();

    double getTotalAmountInCart();
}
