package com.codecool.shop.controller;

import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.dao.contracts.OrderDao;
import com.codecool.shop.dao.implementation.jdbc.CartDaoJdbc;
import com.codecool.shop.dao.implementation.jdbc.OrderDaoJdbc;
import com.codecool.shop.dao.implementation.mem.CartDaoMem;
import com.codecool.shop.model.Checkout;
import com.codecool.shop.model.LineItem;
import com.codecool.shop.model.User;
import com.codecool.shop.util.Mailer;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@WebServlet(urlPatterns = {"/checkout"}, loadOnStartup = 5)
public class CheckOutController extends ApiCartController {

        CartDao cartDataStore = CartDaoMem.getInstance();
        CartDao cartStore = CartDaoJdbc.getInstance();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OrderDao orderStore = OrderDaoJdbc.getInstance();
        HttpSession session = req.getSession();
        String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Gson gson = new Gson();
        Checkout checkout = gson.fromJson(requestBody, Checkout.class);
        User user = (User) session.getAttribute("user");


        List<LineItem> products = cartDataStore.getAll();
        double totalPrice = cartDataStore.getTotalAmountInCart();
        orderStore.add(checkout, user, products, totalPrice);
        Mailer.sendEmail(checkout);
        emptyDbCart(cartDataStore.getAll(), user, cartStore);
        ((CartDaoMem) cartDataStore).emptyCart();

        flush(resp, new Gson().toJson("ORDER"));
    }




}
