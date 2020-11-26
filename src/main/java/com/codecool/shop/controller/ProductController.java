package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.dao.contracts.ProductCategoryDao;
import com.codecool.shop.dao.contracts.ProductDao;
import com.codecool.shop.dao.contracts.SupplierDao;
import com.codecool.shop.dao.implementation.jdbc.CartDaoJdbc;
import com.codecool.shop.dao.implementation.jdbc.ProductCategoryDaoJdbc;
import com.codecool.shop.dao.implementation.jdbc.ProductDaoJdbc;
import com.codecool.shop.dao.implementation.jdbc.SupplierDaoJdbc;
import com.codecool.shop.dao.implementation.mem.CartDaoMem;
import com.codecool.shop.model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet(urlPatterns = {"/"}, loadOnStartup = 1)
public class ProductController extends HttpServlet {

    ProductDao productDataStore = ProductDaoJdbc.getInstance();
    ProductCategoryDao productCategoryDataStore = ProductCategoryDaoJdbc.getInstance();
    SupplierDao supplierDataStore = SupplierDaoJdbc.getInstance();
    CartDao cartDataStore = CartDaoMem.getInstance();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
        HttpSession session = req.getSession();
        System.out.println(session.getAttribute("user"));

        setContext(req, context, req.getQueryString(), session);
        engine.process("product/index.html", context, resp.getWriter());
    }


    private void setContext(HttpServletRequest req, WebContext context, String queryString, HttpSession session) {
        if (queryString != null) {
            if (queryString.contains("category")) {
                int categoryId = Integer.parseInt(req.getParameter("category"));
                context.setVariable("products", productDataStore.getBy(productCategoryDataStore.find(categoryId)));
                context.setVariable("actualCategory", productCategoryDataStore.find(categoryId).getName());
            } else {
                int supplierId = req.getParameter("supplier") != null ? Integer.parseInt(req.getParameter("supplier")) : 0;
                context.setVariable("products", productDataStore.getBy(supplierDataStore.find(supplierId)));
                context.setVariable("actualSupplier", supplierDataStore.find(supplierId).getName());

            }
        } else {
            context.setVariable("products", productDataStore.getAll());
            context.setVariable("actualCategory", null);

        }
        CartDao cartDao = CartDaoJdbc.getInstance();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            boolean userSavedCart = ((CartDaoJdbc) cartDao).hasUserSavedCart(user.getId());
            session.setAttribute("hasCart", userSavedCart);
        }
        context.setVariable("categories", productCategoryDataStore.getAll());
        context.setVariable("suppliers", supplierDataStore.getAll());
        context.setVariable("cartQuantity", cartDataStore.getTotalQuantityInCart());

    }

}
