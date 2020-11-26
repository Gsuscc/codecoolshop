# Codecool shop (sprint 2)

## Story

It's time to enhance the Online Shop, an online eCommerce web-application with Java.
Where users can not only browse products, add them into a Shopping Cart,
checkout items and make payments. But also can log in and see the abandoned shopping cart or order history.

> Did you know that the very first product on eBay was a broken laser pointer?
> If you don't believe, check their history: [eBay history](https://www.ebayinc.com/company/our-history/)

## What are you going to learn?

- how to use the `DAO` design pattern in `Java`,
- using database to make the data persistent
- writing tests to ensure the correct functionality and gain confidence for modification
- enhancing the functionality to make the customer even more happy
- practicing Java Advanced OOP concepts
- refreshing SQL knowledge
- practicing testing

## Tasks

1. Create a new sprint tab on the existing backlog. Last week you had a long list of stories, a few new stories this week.
    - The new items are added to the backlog
    - The team has created a sprint 2 plan based upon the unified backlog

2. As you will work in a new repository but you need the code from the previous sprint, add the `codecool-shop-2` repository as a new remote to the previous sprint's repository, then pull (merge) and push your changes into it.
    - There is a merge commit in the project's repository that contains code from the previous sprint

3. As an Operator of the Shop, I want the product data to be persistent. So that I can restart the application without loosing product data.
    - There is an empty PostgreSQL database called `codecoolshop` (url: localhost:5432, usr/pwd: postgres/postgres)
    - There is a initializer script file `src/main/sql/init_db.sql`. When I run the script file then all of the empty tables are created that will store `Products`, `ProductCategories` and `Suppliers`.
    - When I add `Product` or `ProductCategory` or `Supplier`  via `ProductDaoJdbc` or `ProductCategoryDaoJdbc` or `SupplierDaoJdbc` then I want them appear in a DB table in a PostgreSql database called `codecoolshop`.
    - Given that I have some `Products` with `ProductCategories` and `Suppliers` in the webshop. When I restart the application then the Products with ProductCategories and Suppliers should remain.

4. As a Developer, I want to cover the `ProductDao`, `ProductCategoryDao`  and `SupplierDao` memory implementation with tests. So that I can safely change the implementation later.
    - All methods of the DAOs should be tested where test cases test DAOs through interfaces,  so that we can change the implementation in the whole test class just by changing a single line.
    - When I run the tests from IDE then I see at least 11 passed tests.

5. As a Developer, I want to cover the `ProductDao`, `ProductCategoryDao`  and `SupplierDao` JDBC implementation with tests. So that I can ensure them working correctly.
    - All methods of all JDBC DAO should be tested.
    - Given I have a PostgreSQL database called `codecoolshop` (url: localhost:5432, usr/pwd: postgres/postgres) with empty tables for `Product`, `ProductCategories` and `Suppliers` When I run the tests from IDE then I see at least 11 passed tests.  

6. As a Developer, I want to read the DB connection parameters (url, database name, usr, pwd)  from a config files so that I can change the database under the application.
    - Given that I have the config file `src/main/resources/connection.properties`
with the following structure:
```
url: localhost:5432
database: codecoolshop
user: postgres
password: postgres```

7. As a Developer, I want to use separate database for tests, so that test does not influence the production database.
    - There is an initialized PostgreSQL database called `codecoolshop_test` (url: localhost:5432, usr/pwd: postgres/postgres) with empty tables: `Product`, `ProductCategories` and `Suppliers`
    - There is a separated config file for tests.
    - When I run the DB DAO tests than all test passes and the production database remains untouched.

8. As an Operator of the Shop,  I want to keep Order data safe and persistent, so that I won't loose money because of technical issues.
    - Given the User started a checkout process. Then ensure it saves all Order data into database (in each and every step, except cart).

9. As a User, I want to sign up (make a personal account) so that I can store Orders to my personal account.
    - there is a `Sign up` option on the website
    - it has a form with all the required fields:
- `name`
- `email`
- `password`
    - when the user submits the form with correct/valid information then ensure the system saves it's data as a new `User`
    - the system sends a welcome email after successful registration
    - When the User submits the form with incorrect/invalid information then ensure the program shows the same form with the incorrect data, and some description about the errors.

10. As a User, I want to able to login, so I can authenticate myself and access my personal data. I want to be able to logout so I can close my session.
    - There is a `Login` menu on the website
    - When the user chooses the "Login" menu
then ensure to provide a login form with the following fields:
- email address
- password
    - When the user submits the form with valid information then authenticate and give a new logged-in session to the User
    - When the user submits the form with invalid information then provide an error message
    - Ensure to provide a Logout option for loggend in users. When the user chooses the "Logout" option then reset the session and redirect back to the login form.

11. As a loggedin User, I want to see my Order history, so that I can see my previous Orders and follow their status.
    - There is an `Order history` menu item
    - provide a list with all the Orders of that user, with the following details:
- order date
- order status (checked / paid / confirmed / shipped)
- total price
- product list (with product name, price)

12. As a logged-in User, I want to save the current items of my Shopping cart so that I can order my selected Products later.
    - there is a `Save my cart` button (on the Shopping cart review page)
    - by clicking on this button the system saves the cart items into the database - for that loggedin User
    - Given that there is a User with a previously saved shopping cart. When the user finished the login process then ensure to refill the user's shopping cart with the saved items.

13. As a loggedin User, I want to save my billing and shipping info (to my personal account) so that I don't need to type these data all the time - during checkout.
    - there is a `Billing info` menu item
    - after clicking on the menu a provide a form where the user can fill in
the personal billing and shipping info (what is needed for the checkout)
    - Given there's a Shopping Cart review page. When I click on the "Checkout" button then ensure the system shows the pre-filled billing and shipping info on the checkout form.

## General requirements

- Advanced OOP concepts are used in the project: inheritance, there is at
least on abstract class, there is at least one interface implemented
- The page doesn't show a server error anytime during the review
- All code is pushed to GitHub repository by atomic commits. The implemented feature related commits managed on separated feature branches and merged by a pull request to the `master` branch.

## Hints

- It's not required to integrate real payment services - you can use fake payment implementations.
- Test (and use) the DAO implementations via interfaces so that it will be easy to change the implementation behind the interface. JUnit also provides support for this case, i. e. running the same test set against several implementations of the same interface.

## Starting your project

To start your project click [this link](https://journey.code.cool/v2/project/team/blueprint/codecool-shop-2/java).

## Background materials

- <i class="far fa-exclamation"></i> [Java Dao pattern](https://www.baeldung.com/java-dao-pattern)
- <i class="far fa-exclamation"></i> [Introducing servlets](https://learn.code.cool/full-stack/#/../pages/java/introducing-servlets)
- <i class="far fa-exclamation"></i> [Servlet tutorial](https://www.tutorialspoint.com/servlets/servlets-form-data.htm)
- <i class="far fa-exclamation"></i> [Java properties](https://www.baeldung.com/java-properties)
