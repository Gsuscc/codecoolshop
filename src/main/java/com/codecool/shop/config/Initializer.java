package com.codecool.shop.config;

import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.dao.contracts.ProductCategoryDao;
import com.codecool.shop.dao.contracts.ProductDao;
import com.codecool.shop.dao.contracts.SupplierDao;
import com.codecool.shop.dao.implementation.mem.CartDaoMem;
import com.codecool.shop.dao.implementation.mem.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.mem.ProductDaoMem;
import com.codecool.shop.dao.implementation.mem.SupplierDaoMem;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Initializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();
        SupplierDao supplierDataStore = SupplierDaoMem.getInstance();
        CartDao cartDaoStore = CartDaoMem.getInstance();

        //setting up a new supplier
        Supplier amazon = new Supplier("Amazon", "Digital content and services");
        supplierDataStore.add(amazon);
        Supplier lenovo = new Supplier("Lenovo", "Computers");
        supplierDataStore.add(lenovo);
        Supplier chuwi = new Supplier("CHUWI", "Premium chinese engineering");
        supplierDataStore.add(chuwi);
        Supplier xpg = new Supplier("XPG", "Another noname stuff");
        supplierDataStore.add(xpg);
        Supplier archos = new Supplier("Archos", "Another noname stuff");
        supplierDataStore.add(archos);
        Supplier unbranded = new Supplier("CrappyExpress", "The best manufacturer");
        supplierDataStore.add(unbranded);
        Supplier iqoo = new Supplier("IQOO", "Just look at our name..");
        supplierDataStore.add(iqoo);
        Supplier xiaomi = new Supplier("Xiaomi", "Ambitious company from China");
        supplierDataStore.add(xiaomi);
        Supplier apple = new Supplier("Apple", "One of the worlds biggest company, over-priced products very good marketing strategies.");
        supplierDataStore.add(apple);

        //setting up a new product category
        ProductCategory tablet = new ProductCategory("Tablet", "Hardware", "A tablet computer, commonly shortened to tablet, is a thin, flat mobile computer with a touchscreen display.");
        productCategoryDataStore.add(tablet);
        ProductCategory computer = new ProductCategory("Computer", "Hardware", "A computer is a machine that can be instructed to carry out sequences of arithmetic or logical operations automatically via computer programming.");
        productCategoryDataStore.add(computer);
        ProductCategory cellphone = new ProductCategory("Cell Phones", "Hardware", "A cell phone is a portable telephone that can make and receive calls over a radio frequency link while the user is moving within a telephone service area.");
        productCategoryDataStore.add(cellphone);
        ProductCategory television = new ProductCategory("LCD, LED TVs", "Entertainment", "TV is a telecommunication medium used for transmitting moving images");
        productCategoryDataStore.add(television);

        //setting up products and printing it
        productDataStore.add(new Product("Amazon Fire", 49.9, "USD", "Fantastic price. Large content ecosystem. Good parental controls. Helpful technical support.", tablet, amazon));
        productDataStore.add(new Product("Lenovo IdeaPad Miix 700", 175, "USD", "Keyboard cover is included. Fanless Core m5 processor. Full-size USB ports. Adjustable kickstand.", tablet, lenovo));
        productDataStore.add(new Product("Amazon Fire HD 8", 89, "USD", "Amazon's latest Fire HD 8 tablet is a great value for media consumption.", tablet, amazon));
        productDataStore.add(new Product("CHUWI HeroBook", 399.99, "USD", "Backlit Keyboard, Bluetooth, Built-in Microphone, Built-in Webcam.", computer, chuwi));
        productDataStore.add(new Product("XPG XENIA 1660Ti 15.6", 1299.99, "USD", "XPG Xenia Intel i7-9750H GTX 1660Ti 6GB, 1TB NVMe SSD, 32GB RAM, Gaming Laptop.", computer, xpg));
        productDataStore.add(new Product("Archos 140 Cesium 14.1-Inch LED Notebook", 219, "USD", "Best choice for casual computing, no need to worry someone stealing this", computer, archos));
        productDataStore.add(new Product("7\" Inch Kids Tablet", 40, "USD", "Google Android PC Quad Core Pad With Wifi Camera And Games.", tablet, amazon));
        productDataStore.add(new Product("IQOO Neo 3 Smartphone 6GB", 599, "USD", "Excellent configuration, amazing performance", cellphone, iqoo));
        productDataStore.add(new Product("Xiaomi Redmi Note 8", 175, "USD", "A large screen, a snappy chipset, a good camera, and a 4,000 mAh battery.", cellphone, xiaomi));
        productDataStore.add(new Product("Global Blackview BV9900 8GB+256GB waterproof", 549, "USD", "Tri-proof the most indestructible phone ever", cellphone, chuwi));
        productDataStore.add(new Product("Cyrus Outdoor-smartph.", 320, "USD", "Not for only indoor usage!", cellphone, chuwi));
        productDataStore.add(new Product("Amazon Fire HD 10", 130, "USD", "Big display, low performance, lost of missing features, if you love retro its your best choice.", tablet, amazon));
        productDataStore.add(new Product("Apple MacBook Pro 13 (June 2020)", 1200, "USD", "No USB, shitty keyboard, incredibly low storage capacity.", computer, apple));
        productDataStore.add(new Product("Apple - iPhone 11 Pro Max 512GB", 1699, "USD", "Featuring a Stunning Pro Display, A13 Bionic, Cutting-Edge Pro Camera System and Longest Battery Life Ever in iPhone with iPhone 11 Pro Max", cellphone, apple));
        productDataStore.add(new Product("Xiaomi Mi Pad 4 LTE 8", 360, "USD", "One-handed operation, 13MP HD camera, wireless network, snapdragon CPU", tablet, xiaomi));
        productDataStore.add(new Product("Xiaomi Mi Air 13.3\" Notebook Full-HD", 799, "USD", "i5-7200U 8GB 256GB SSD GeForce MX150 Win10", computer, xiaomi));
        productDataStore.add(new Product("LOGIC 32\" HD SMART TV", 250, "USD", "You can see how smart it is by looking at its name. Best choice for your money", television, amazon));
        productDataStore.add(new Product("NABO Television TV 32 LX3000 80cm", 390, "USD", "Mind-blowing quality for low price, it alse has speakers!", television, amazon));
        productDataStore.add(new Product("Condor CA22", 175, "USD", "Vintage product, only for collectors, the top of the famous soviet technology.", television, unbranded));
        productDataStore.add(new Product("CrappyExpress Vulcan 2.0", 749.50, "USD", "This supercomputer was built by geeks, it cranks out over 4 PetaFLOPS", computer, unbranded));
        productDataStore.add(new Product("CrappyExpress", 1099.32, "USD", "For button and battery life lovers, with unbreakable screen", cellphone, unbranded));
    }
}
