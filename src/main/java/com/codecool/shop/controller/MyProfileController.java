package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.contracts.BillingDao;
import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.dao.contracts.ShippingDao;
import com.codecool.shop.dao.implementation.jdbc.BillingDaoJdbc;
import com.codecool.shop.dao.implementation.jdbc.ShippingDaoJdbc;
import com.codecool.shop.dao.implementation.mem.CartDaoMem;
import com.codecool.shop.model.BillingData;
import com.codecool.shop.model.ShippingData;
import com.codecool.shop.model.User;
import com.google.gson.Gson;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/myprofile", "/myprofile/update"}, loadOnStartup = 8)
public class MyProfileController extends ApiCartController {
    BillingDao billingDao = BillingDaoJdbc.getInstance();
    ShippingDao shippingDao = ShippingDaoJdbc.getInstance();
    CartDao cartDao = CartDaoMem.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/"));
        } else {
            if (userHasBillingAddress(user.getId())) {
                context.setVariable("address", billingDao.find(user.getId()));
            }
            if (userHasShippingAddress(user.getId())) {
                context.setVariable("shipping", shippingDao.find(user.getId()));
            }
            context.setVariable("cartQuantity", cartDao.getTotalQuantityInCart());
            engine.process("myprofile.html", context, resp.getWriter());
        }

    }

    private boolean userHasShippingAddress(int id) {
        return ((ShippingDaoJdbc) shippingDao).hasShippingAddress(id);
    }

    private boolean userHasBillingAddress(int userId) {
        return ((BillingDaoJdbc) billingDao).hasUserSavedBillingAddress(userId);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        HttpSession httpSession = req.getSession();
        int userId = ((User) httpSession.getAttribute("user")).getId();
        String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        if (req.getParameter("type").equals("billing")) {
            BillingData billingData = gson.fromJson(requestBody, BillingData.class);
            int billingAddressId = billingDao.add(billingData, userId);
            flush(resp, gson.toJson(billingAddressId));
        } else if (req.getParameter("type").equals("shipping")) {
            ShippingData shippingData = gson.fromJson(requestBody, ShippingData.class);
            int shippingAddressId = shippingDao.add(shippingData, userId);
            flush(resp, gson.toJson(shippingAddressId));
        }
    }
}
