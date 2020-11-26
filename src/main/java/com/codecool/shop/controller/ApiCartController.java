package com.codecool.shop.controller;

import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.dao.implementation.jdbc.CartDaoJdbc;
import com.codecool.shop.dao.implementation.mem.CartDaoMem;
import com.codecool.shop.model.LineItem;
import com.codecool.shop.model.User;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/api/cart"})
public class ApiCartController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String response = getJsonString();
        flush(resp, response);
    }

    public void flush(HttpServletResponse resp, String response) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.println(response);
        out.flush();
    }


    public String getJsonString() {
        CartDao cartStore = CartDaoMem.getInstance();
        LineItem[] list = new LineItem[cartStore.getAll().size()];
        for (int i = 0; i < list.length; i++) {
            list[i] = cartStore.getAll().get(i);
        }
        return new Gson().toJson(list);
    }

    protected void emptyDbCart(List<LineItem> all, User user, CartDao cartStore) {
        all.stream().forEach(lineItem -> cartStore.remove(lineItem.getId()));
        if(user != null) {
            ((CartDaoJdbc) cartStore).removeUsersCart(user.getId());
        }
    }
}
