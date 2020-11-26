package com.codecool.shop.dao.contracts;

import com.codecool.shop.model.ShippingData;
import com.codecool.shop.model.User;

import java.util.List;

public interface ShippingDao {
    ShippingData find(int userId);

    int add(ShippingData shippingData, int userId);

    int update(ShippingData shippingData, int userId);

    List<ShippingData> getAll();
}
