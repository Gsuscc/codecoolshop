package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.contracts.UserDao;
import com.codecool.shop.dao.implementation.jdbc.UserDaoJdbc;
import com.codecool.shop.model.User;
import com.codecool.shop.util.Encrypt;
import com.codecool.shop.util.Mailer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(urlPatterns = {"/register"}, loadOnStartup = 5)
public class RegistrationController extends HttpServlet {

    UserDao userDao = UserDaoJdbc.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());

        engine.process("/register.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Encrypt encrypt = Encrypt.getInstance();
        String userName = req.getParameter("username");
        String realName = req.getParameter("real-name");
        String pass = req.getParameter("password");

        try {
            String encryptedPass = encrypt.encrypt(pass);
            if (isAlreadyRegistered(userName)) {
                resp.sendRedirect("/register?error");
            } else {
                User newUser = new User(userName, realName);
                newUser.setId(userDao.add(newUser, encryptedPass));
                Mailer.sendEmail(newUser);
                resp.sendRedirect("/login?login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private boolean isAlreadyRegistered(String username) {
        return userDao.find(username) != null;
    }
}
