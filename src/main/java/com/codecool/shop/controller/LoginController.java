package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.dao.contracts.UserDao;
import com.codecool.shop.dao.implementation.jdbc.CartDaoJdbc;
import com.codecool.shop.dao.implementation.jdbc.UserDaoJdbc;
import com.codecool.shop.dao.implementation.mem.CartDaoMem;
import com.codecool.shop.model.LineItem;
import com.codecool.shop.model.User;
import com.codecool.shop.util.Encrypt;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet(urlPatterns = {"/login"}, loadOnStartup = 4)
public class LoginController extends HttpServlet {

    UserDao userDao = UserDaoJdbc.getInstance();
    CartDao cartDao = CartDaoJdbc.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
        engine.process("/login.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String uname = req.getParameter("username");
        String pass = req.getParameter("password");

        if (isValidUser(uname) && isValidPass(uname, pass)) {
            User user = userDao.find(uname);
            boolean userSavedCart = ((CartDaoJdbc) cartDao).hasUserSavedCart(user.getId());
            session.setAttribute("user", user);
            session.setAttribute("hasCart", userSavedCart);
            System.out.println(session.getAttribute("hasCart"));
            if (hasSavedCart(user.getId())) {
                reloadCart(user.getId());
                resp.sendRedirect("/");
            } else {
                resp.sendRedirect("/");
            }
        } else {
            resp.sendRedirect("/login?error");
        }
    }

    private void reloadCart(int id) {
        CartDao cartDataStore = CartDaoMem.getInstance();
        for (LineItem item : ((CartDaoJdbc) cartDao).getUserCart(id)) {
            cartDataStore.add(item);
        }
    }

    private boolean hasSavedCart(int id) {
        return ((CartDaoJdbc) cartDao).hasUserSavedCart(id);
    }

    private boolean isValidUser(String username) {
        return userDao.find(username) != null;
    }

    private boolean isValidPass(String username, String pass) {
        Encrypt encrypt = Encrypt.getInstance();
        try {
            String encryptedPass = encrypt.encrypt(pass);
            return ((UserDaoJdbc) userDao).getPass(username).equals(encryptedPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
