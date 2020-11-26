package com.codecool.shop.util;

import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.dao.implementation.mem.CartDaoMem;
import com.codecool.shop.model.Checkout;
import com.codecool.shop.model.LineItem;
import com.codecool.shop.model.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.*;


public class Mailer {

    private static final String senderEmail = "crappyexpress";//change with your sender email
    private static final String senderPassword = "Crappy1234";//change with your sender password
    private static CartDao cartStore = CartDaoMem.getInstance();


    public static void sendEmail(Checkout order) {

        MimeMessage msg = new MimeMessage(createSession());
        try {
            msg.setFrom(new InternetAddress(senderEmail));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(order.getBillingData().getEmail()));
            msg.setSubject("Order Confirmation - Codecool WebShop");
            Multipart emailContent = new MimeMultipart();
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(writeTextContent(order));

            emailContent.addBodyPart(textBodyPart);
            attachPicsToEmail(emailContent);
            msg.setContent(emailContent);

            Transport.send(msg);
            System.out.println("Message sent.");
        } catch (MessagingException e) {
            System.out.println("Trouble at sending the e-mail message. " + e);
        }
    }

    public static void sendEmail(User user) {
        MimeMessage msg = new MimeMessage(createSession());
        try {
            msg.setFrom(new InternetAddress(senderEmail));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
            msg.setSubject("Welcome to Codecool WebShop");
            Multipart emailContent = new MimeMultipart();
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(writeTextContent(user));
            emailContent.addBodyPart(textBodyPart);
            msg.setContent(emailContent);

            Transport.send(msg);
            System.out.println("Message sent.");
        } catch (MessagingException e) {
            System.out.println("Trouble at sending the e-mail message. " + e);
        }
    }


    private static String writeTextContent(Checkout order) {
        StringBuilder context = new StringBuilder();
        context.append("Dear ").append(order.getBillingData().getFirstName())
                .append(" ").append(order.getBillingData().getLastName()).append(",\n");
        context.append("\nWe would like to inform you that you ordered the following items from Crappy Express: \n");
        for (LineItem item : cartStore.getAll()) {
            context.append("- ").append(item.getName()).append(" ")
                    .append(": quantity: ").append(item.getQuantity())
                    .append("\n");
        }
        context.append("Total price: ").append(cartStore.getTotalAmountInCart()).append("$\n");
        context.append("\nThank you for choosing us! \n");
        context.append("\nAddress details: \n");
        context.append(order.getBillingData().getAddress()).append(".\n").append(order.getBillingData().getZip()).append(".\n")
                .append(order.getBillingData().getState()).append(".\n").append(order.getBillingData().getCountry()).append(".\n");
        context.append("\nWish you the bests");
        return context.toString();
    }

    private static String writeTextContent(User user) {
        return "Dear " + user.getName() + ",\n" +
                "\nHere is a confirmation about your successful registration \n" +
                "\nOn the following link you can login, and start browsing our products!\n" +
                "\nwww.localhost:8888/login \n" +
                "\nWish you the bests";
    }


    private static Session createSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "false");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        return session;
    }

    private static void attachPicsToEmail(Multipart emailContent) {
        List<LineItem> productsInOrder = cartStore.getAll();
        Set<LineItem> productSet = new HashSet<>(productsInOrder);
        ArrayList<Integer> productIDs = new ArrayList<>();
        for (LineItem item : productSet) {
            productIDs.add(item.getProductId());
        }
        createAttachments(productIDs, emailContent);
    }

    private static void createAttachments(List<Integer> productIDs, Multipart emailContent) {
        try {
            for (Integer productID : productIDs) {
                MimeBodyPart jpgAttachment = new MimeBodyPart();
                jpgAttachment.attachFile("./src/main/webapp/static/img/product_" + productID + ".jpg");
                emailContent.addBodyPart(jpgAttachment);
            }
        } catch (MessagingException | IOException ex) {
            System.err.println("There was a problem with attach files. Type: " + ex.getMessage());
        }
    }
}