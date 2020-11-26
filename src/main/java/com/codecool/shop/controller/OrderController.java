package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.contracts.BillingDao;
import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.dao.contracts.ShippingDao;
import com.codecool.shop.dao.implementation.jdbc.BillingDaoJdbc;
import com.codecool.shop.dao.implementation.jdbc.ShippingDaoJdbc;
import com.codecool.shop.dao.implementation.mem.CartDaoMem;
import com.codecool.shop.model.ShippingData;
import com.codecool.shop.model.User;
import com.google.gson.Gson;
import com.sun.jdi.connect.spi.TransportService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/order"}, loadOnStartup = 4)
public class OrderController extends ApiCartController {

    BillingDao billingDao = BillingDaoJdbc.getInstance();
    ShippingDao shippingDao = ShippingDaoJdbc.getInstance();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
        User user = (User) req.getSession().getAttribute("user");
        if (req.getParameter("type") != null && req.getParameter("type").equals("shipping")&& user != null){
            ShippingData[] shippingData = new ShippingData[1];
            shippingData[0] = shippingDao.find(user.getId());
            String response = new Gson().toJson(shippingData);
            flush(resp, response);
        }else if (req.getQueryString().contains("type") && user == null) {
            flush(resp, new Gson().toJson(""));
        }else {
            setContext(context, user);
            engine.process("/order.html", context, resp.getWriter());
        }
    }

    private void setContext(WebContext context, User user) {
        CartDao cartDataStore = CartDaoMem.getInstance();
        if(user != null){
            context.setVariable("billingData",  billingDao.find(user.getId()));
        }
        context.setVariable("cartOverview", cartDataStore.getAll());
        context.setVariable("cartTotalAmount", cartDataStore.getTotalAmountInCart());
        context.setVariable("cartQuantity", cartDataStore.getTotalQuantityInCart());
    }
}
