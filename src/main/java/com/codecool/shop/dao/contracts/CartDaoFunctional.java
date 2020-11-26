package com.codecool.shop.dao.contracts;

import com.codecool.shop.model.LineItem;

import java.util.List;

public interface CartDaoFunctional {
    void addAll(List<LineItem> list, int userId);
}
