DROP TABLE IF EXISTS public.supplier CASCADE;
create table supplier
(
    id          serial  not null
        constraint supplier_pk
            primary key,
    name        varchar not null,
    description varchar not null
);


alter table supplier
    owner to postgres;

create unique index supplier_id_uindex
    on supplier (id);

DROP TABLE IF EXISTS public.product_category CASCADE;
create table product_category
(
    id          serial  not null
        constraint product_category_pk
            primary key,
    name        varchar,
    type        varchar not null,
    description varchar not null
);

alter table product_category
    owner to postgres;

create unique index product_category_id_uindex
    on product_category (id);

DROP TABLE IF EXISTS public.currency CASCADE;
create table currency
(
    id            serial  not null
        constraint currency_pk
            primary key,
    currency_type varchar not null
);

alter table currency
    owner to postgres;

create unique index currency_id_uindex
    on currency (id);

DROP TABLE IF EXISTS public.product CASCADE;
create table product
(
    id                  serial           not null
        constraint product_pk
            primary key,
    name                varchar          not null,
    description         varchar          not null,
    default_price       double precision not null,
    currency_id         integer          not null
        constraint product_currency_id_fk
            references currency
            on update cascade on delete cascade,
    product_category_id integer          not null
        constraint product_product_category_id_fk
            references product_category
            on update cascade on delete cascade,
    supplier_id         integer          not null
        constraint product_supplier_id_fk
            references supplier
            on update cascade on delete cascade
);

alter table product
    owner to postgres;

create unique index product_id_uindex
    on product (id);

DROP TABLE IF EXISTS public."user" CASCADE;
create table "user"
(
    id       serial  not null
        constraint user_pk
            primary key,
    name     varchar not null,
    password varchar not null
);

alter table "user"
    owner to postgres;

create unique index user_id_uindex
    on "user" (id);

DROP TABLE IF EXISTS public.cart CASCADE;
create table cart
(
    id      serial  not null
        constraint cart_pk
            primary key,
    user_id integer not null
);

alter table cart
    owner to postgres;

create unique index cart_id_uindex
    on cart (id);

DROP TABLE IF EXISTS public.cart_item CASCADE;
create table cart_item
(
    cart_id    integer not null
        constraint cart_item_cart_id_fk
            references cart
            on update cascade on delete cascade,
    id         serial  not null
        constraint cart_item_pk
            primary key,
    product_id integer not null
        constraint cart_item_product_id_fk
            references product
            on update cascade on delete cascade,
    quantity   integer
);

alter table cart_item
    owner to postgres;

create unique index cart_item_id_uindex
    on cart_item (id);

DROP TABLE IF EXISTS public.country CASCADE;
create table country
(
    id   serial  not null
        constraint country_pk
            primary key,
    name varchar not null
);

alter table country
    owner to postgres;

create unique index country_id_uindex
    on country (id);

DROP TABLE IF EXISTS public.state CASCADE;
create table state
(
    id   serial not null
        constraint state_pk
            primary key,
    name varchar
);

alter table state
    owner to postgres;

create unique index state_id_uindex
    on state (id);

DROP TABLE IF EXISTS public.shipping_address CASCADE;
create table shipping_address
(
    id         serial  not null
        constraint shipping_address_pk
            primary key,
    user_id    integer not null
        constraint shipping_address_user_id_fk
            references "user"
            on update cascade on delete cascade,
    first_name varchar not null,
    last_name  varchar not null,
    email      varchar,
    address    varchar not null,
    country_id integer not null
        constraint shipping_address_country_id_fk
            references country
            on update cascade on delete cascade,
    state_id   integer not null
        constraint shipping_address_state_id_fk
            references state
            on update cascade on delete cascade,
    zip_code   varchar not null
);

alter table shipping_address
    owner to postgres;

create unique index shipping_address_id_uindex
    on shipping_address (id);

DROP TABLE IF EXISTS public."order" CASCADE;
create table "order"
(
    id                  serial  not null
        constraint order_pk
            primary key,
    shipping_address_id integer not null
        constraint order_shipping_address_id_fk
            references shipping_address
            on update cascade on delete cascade
--     cart_id integer not null
--         constraint order_cart_cart_id_fk
--             references cart
--             on update cascade on delete cascade
);

alter table "order"
    owner to postgres;

create unique index order_id_uindex
    on "order" (id);



-- INSERT INTO supplier (name, description)VALUES ('Amazon', 'Digital content and services');
-- INSERT INTO supplier (name, description)VALUES ('Lenovo', 'Computers');
-- INSERT INTO supplier (name, description)VALUES ('CHUWI', 'Premium chinese engineering');
-- INSERT INTO supplier (name, description)VALUES ('XPG', 'Another noname stuff');
-- INSERT INTO supplier (name, description)VALUES ('Archos', 'Another noname stuff');
-- INSERT INTO supplier (name, description)VALUES ('CrappyExpress', 'The best manufacturer');
-- INSERT INTO supplier (name, description)VALUES ('IQOO', 'Just look at our name..');
-- INSERT INTO supplier (name, description)VALUES ('Xiaomi', 'Ambitious company from China');
-- INSERT INTO supplier (name, description)VALUES ('Apple', 'One of the worlds biggest company, over-priced products very good marketing strategies.');
--
-- INSERT INTO product_category (name, type, description)VALUES ('Tablet', 'Hardware', 'A tablet computer, commonly shortened to tablet, is a thin, flat mobile computer with a touchscreen display.');
-- INSERT INTO product_category (name, type, description)VALUES ('Computer', 'Hardware', 'A computer is a machine that can be instructed to carry out sequences of arithmetic or logical operations automatically via computer programming.');
-- INSERT INTO product_category (name, type, description)VALUES ('Cell Phones', 'Hardware', 'A cell phone is a portable telephone that can make and receive calls over a radio frequency link while the user is moving within a telephone service area.');
-- INSERT INTO product_category (name, type, description)VALUES ('LCD, LED TVs', 'Entertainment', 'TV is a telecommunication medium used for transmitting moving images');
--
-- INSERT INTO currency (currency_type) VALUES ('USD');
-- INSERT INTO currency (currency_type) VALUES ('HUF');
--
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Amazon Fire', 49.9, 1, 'Fantastic price. Large content ecosystem. Good parental controls. Helpful technical support.', 1, 1);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Lenovo IdeaPad Miix 700', 175, 1, 'Keyboard cover is included. Fanless Core m5 processor. Full-size USB ports. Adjustable kickstand.', 1, 2);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Amazon Fire HD 8', 89, 1, 'Amazon''s latest Fire HD 8 1 is a great value for media consumption.', 1, 1);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('CHUWI HeroBook', 399.99, 1, 'Backlit Keyboard, Bluetooth, Built-in Microphone, Built-in Webcam.', 2, 3);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('XPG XENIA 1660Ti 15.6', 1299.99, 1, 'XPG Xenia Intel i7-9750H GTX 1660Ti 6GB, 1TB NVMe SSD, 32GB RAM, Gaming Laptop.', 2, 4);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Archos 140 Cesium 14.1-Inch LED Notebook', 219, 1, 'Best choice for casual computing, no need to worry someone sealing this', 2, 5);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('7'' Inch Kids 1', 40, 1, 'Google Android PC Quad Core Pad With Wifi Camera And Games.', 1, 1);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('IQOO Neo 3 Smartphone 6GB', 599, 1, 'Excellent configuration, amazing performance', 3, 7);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Xiaomi Redmi Note 8', 175, 1, 'A large screen, a snappy chipset, a good camera, and a 4,000 mAh battery.', 3, 8);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Global Blackview BV9900 8GB+256GB waterproof', 549, 1, 'Tri-proof the most indestructible phone ever', 3, 3);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Cyrus Outdoor-smartph.', 320, 1, 'Not for only indoor usage!', 3, 3);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Amazon Fire HD 10', 130, 1, 'Big display, low performance, lost of missing features, if you love retro its your best choice.', 1, 1);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Apple MacBook Pro 13 (June 2020)', 1200, 1, 'No USB, shitty keyboard, incredibly low storage capacity.', 2, 9);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Apple - iPhone 11 Pro Max 512GB', 1699, 1, 'Featuring a Stunning Pro Display, A13 Bionic, Cutting-Edge Pro Camera System and Longest Battery Life Ever in iPhone with iPhone 11 Pro Max', 3, 9);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Xiaomi Mi Pad 4 LTE 8', 360, 1, 'One-handed operation, 13MP HD camera, wireless network, snapdragon CPU', 1, 8);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Xiaomi Mi Air 13.3'' Notebook Full-HD', 799, 1, 'i5-7200U 8GB 256GB SSD GeForce MX150 Win10', 2, 8);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('LOGIC 32'' HD SMART TV', 250, 1, 'You can see how smart it is by looking at its name. Best choice for your money', 4, 1);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('NABO Television TV 32 LX3000 80cm', 390, 1, 'Mind-blowing quality for low price, it alse has speakers!', 4, 1);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('Condor CA22', 175, 1, 'Vintage product, only for collectors, the top of the famous soviet technology.', 4, 6);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('CrappyExpress Vulcan 2.0', 749.50, 1, 'This super2 was built by geeks, it cranks out over 4 PetaFLOPS', 2, 6);
-- INSERT INTO product (name, default_price, currency_id, description, product_category_id, supplier_id)VALUES ('CrappyExpress', 1099.32, 1, 'For button and battery life lovers, with unbreakable screen', 3, 6);
--
-- INSERT INTO country (name)VALUES ('Hungary');
-- INSERT INTO country (name)VALUES ('China');
-- INSERT INTO country (name)VALUES ('United Kingdom');
--
-- INSERT INTO state (name)VALUES ('Bacs-Kiskun');
-- INSERT INTO state (name)VALUES ('Baranya');
-- INSERT INTO state (name)VALUES ('Bekes');
-- INSERT INTO state (name)VALUES ('Borsod-Abauj-Zemplen');
-- INSERT INTO state (name)VALUES ('Budapest');
-- INSERT INTO state (name)VALUES ('Csongrad');
-- INSERT INTO state (name)VALUES ('Fejer');
-- INSERT INTO state (name)VALUES ('Gyor-Moson-Sopron');
-- INSERT INTO state (name)VALUES ('Hajdu-Bihar');
-- INSERT INTO state (name)VALUES ('Heves');
-- INSERT INTO state (name)VALUES ('Jasz-Nagykun-Szolnok');
-- INSERT INTO state (name)VALUES ('Komarom-Esztergom');
-- INSERT INTO state (name)VALUES ('Nograd');
-- INSERT INTO state (name)VALUES ('Pest');
-- INSERT INTO state (name)VALUES ('Somogy');
-- INSERT INTO state (name)VALUES ('Szabolcs-Szatmar-Bereg');
-- INSERT INTO state (name)VALUES ('Tolna');
-- INSERT INTO state (name)VALUES ('Vas');
-- INSERT INTO state (name)VALUES ('Veszprem');
-- INSERT INTO state (name)VALUES ('Zala');
--
-- INSERT INTO "user" (name, password) VALUES ('ngetzi', '1234');
--
