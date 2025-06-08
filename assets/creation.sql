-- ----------------------------
-- Drop database if exists
-- ----------------------------
DROP DATABASE IF EXISTS ecomdb;

-- ----------------------------
-- Create database
-- ----------------------------
CREATE DATABASE ecomdb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- ----------------------------
-- Use database
-- ----------------------------
USE ecomdb;

-- ----------------------------
-- Table: product_categories
-- ----------------------------
CREATE TABLE product_categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- ----------------------------
-- Table: users
-- ----------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    credit_balance DOUBLE NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    reset_token VARCHAR(255),
    reset_token_expiry DATETIME,
    INDEX (email),
    INDEX (phone)
) ENGINE=InnoDB;

-- ----------------------------
-- Table: cart
-- ----------------------------
CREATE TABLE cart (
    cart_id INT PRIMARY KEY,
    CONSTRAINT fk_cart_user FOREIGN KEY (cart_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------
-- Table: products
-- ----------------------------
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    category_id INT NOT NULL,
    image VARCHAR(255) NOT NULL,
    stock INT NOT NULL,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES product_categories(id)
) ENGINE=InnoDB;

-- ----------------------------
-- Table: orders
-- ----------------------------
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_price DOUBLE NOT NULL,
    status VARCHAR(255) NOT NULL,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- ----------------------------
-- Table: cart_items
-- ----------------------------
CREATE TABLE cart_items (
    cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    cart_id INT NOT NULL,
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES products(product_id),
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES cart(cart_id)
) ENGINE=InnoDB;

-- ----------------------------
-- Table: order_items
-- ----------------------------
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    item_price DOUBLE NOT NULL,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB;
