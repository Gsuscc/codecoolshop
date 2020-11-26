package com.codecool.shop.controller;

import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.dao.implementation.mem.CartDaoMem;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet(urlPatterns = {"/logout"}, loadOnStartup = 4)
public class LogoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CartDao cartDao = CartDaoMem.getInstance();
        HttpSession session = req.getSession();
        session.setAttribute("user", null);
        ((CartDaoMem) cartDao).emptyCart();
        resp.sendRedirect("/");
    }

}
