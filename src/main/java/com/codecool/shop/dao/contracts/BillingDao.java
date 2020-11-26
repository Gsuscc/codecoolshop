package com.codecool.shop.dao.contracts;

import com.codecool.shop.model.BillingData;
import com.codecool.shop.model.User;
import com.codecool.shop.util.ResultSetHelper;

import java.util.List;

public interface BillingDao {
    BillingData find(int userId);

    int add(BillingData billingData,int userId);

    int update(BillingData billingData, int userId);

    List<BillingData> getAll(int userId, ResultSetHelper helper);
}
