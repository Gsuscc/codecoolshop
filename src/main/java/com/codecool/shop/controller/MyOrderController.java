package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.dao.contracts.OrderDao;
import com.codecool.shop.dao.implementation.jdbc.OrderDaoJdbc;
import com.codecool.shop.dao.implementation.mem.CartDaoMem;
import com.codecool.shop.model.LineItem;
import com.codecool.shop.model.User;
import com.google.gson.Gson;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.DoubleStream;

@WebServlet(urlPatterns = {"/myorder"}, loadOnStartup = 7)
public class MyOrderController extends HttpServlet {

    OrderDao orderDao = OrderDaoJdbc.getInstance();
    CartDao cartDao = CartDaoMem.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        System.out.println(session);
        if (user == null) {
            resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/"));
        } else {
            int userId = ((User) session.getAttribute("user")).getId();
            if (req.getParameter("orderId") != null) {
                int orderId = Integer.parseInt(req.getParameter("orderId"));
                String response = getJsonString(orderId);
                flush(resp, response);
            } else {
                setContext(context, userId);
                engine.process("/myOrder.html", context, resp.getWriter());
            }
        }
    }


    public String getJsonString(int orderId) {
        List<LineItem> orderItems = ((OrderDaoJdbc) orderDao).getOrderDetails(orderId);
        LineItem[] list = new LineItem[orderItems.size()];
        for (int i = 0; i < list.length; i++) {
            list[i] = orderItems.get(i);
        }
        return new Gson().toJson(list);
    }

    public void flush(HttpServletResponse resp, String response) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.println(response);
        out.flush();
    }

    private void setContext(WebContext context, int userId) {
        context.setVariable("orders", orderDao.getAllByUser(userId));
        context.setVariable("cartQuantity", cartDao.getTotalQuantityInCart());
    }

}
