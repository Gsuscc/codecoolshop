package com.codecool.shop.controller;

import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.dao.contracts.ProductDao;
import com.codecool.shop.dao.implementation.jdbc.CartDaoJdbc;
import com.codecool.shop.dao.implementation.jdbc.ProductDaoJdbc;
import com.codecool.shop.dao.implementation.mem.CartDaoMem;
import com.codecool.shop.model.LineItem;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.User;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;


@WebServlet(urlPatterns = {"/cart", "api/cart/delete", "api/cart/update", "api/cart/save"}, loadOnStartup = 2)
public class CartController extends ApiCartController {
    ProductDao productStore = ProductDaoJdbc.getInstance();
    CartDao cartDao = CartDaoJdbc.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        CartDao cartStore = CartDaoMem.getInstance();
        if(req.getQueryString().contains("delete")){
            emptyDbCart(cartStore.getAll(), ((User)(req.getSession().getAttribute("user"))), cartDao);
            ((CartDaoMem) cartStore).emptyCart();
            flush(resp,new Gson().toJson(""));
        }
        if (req.getQueryString().contains("addtoCart")) {
            Product product = productStore.find(Integer.parseInt(req.getParameter("addtoCart")));
            cartStore.add(new LineItem(product));
            resp.sendRedirect(req.getHeader("referer"));

        } else if (req.getQueryString().contains("prodId") && req.getQueryString().contains("quantity")) {
            String prodId = req.getParameter("prodId");
            cartStore.find(Integer.parseInt(prodId)).setQuantity(Integer.parseInt(req.getParameter("quantity")));
            flush(resp, getJsonString());

        } else if (req.getQueryString().contains("prodId")) {
            int prodId = Integer.parseInt(req.getParameter("prodId"));
            cartDao.remove(prodId);
            cartStore.remove(prodId);
            if (cartStore.getAll().size() == 0) emptyDbCart(cartStore.getAll(), ((User)(req.getSession().getAttribute("user"))), cartDao);
            flush(resp, getJsonString());

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        LineItem[] lineItems = gson.fromJson(requestBody, LineItem[].class);
        HttpSession httpSession = req.getSession();
        int userId = ((User) httpSession.getAttribute("user")).getId();
        cartDao.addAll(Arrays.asList(lineItems), userId);
        flush(resp, new Gson().toJson(""));
    }
}
