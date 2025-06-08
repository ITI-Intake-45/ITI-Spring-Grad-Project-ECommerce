-- ----------------------------
-- Use database
-- ----------------------------
USE ecomdb;

-- ----------------------------
-- Step 1: Insert Users
-- ----------------------------

INSERT INTO users (
    user_id,
    email,
    password,
    name,
    address,
    credit_balance,
    phone
) VALUES
(0, 'mounir@email.com', '$2a$12$HFKRmoGUoe8/I4px/K3AquKQY//7pJPT1XGnrBG3fYa8cmi0jkIUK', 'Mounir Sabry', '123 Cairo St, Egypt', 500.0, '01011111111'),
(0, 'ahmed@email.com', '$2a$12$HFKRmoGUoe8/I4px/K3AquKQY//7pJPT1XGnrBG3fYa8cmi0jkIUK', 'Ahmed Hassan', '456 Giza Ave, Egypt', 200.0, '01122222222'),
(0, 'hadeer@email.com', '$2a$12$HFKRmoGUoe8/I4px/K3AquKQY//7pJPT1XGnrBG3fYa8cmi0jkIUK', 'Hadeer Adel', '789 Alexandria Blvd, Egypt', 100.0, '01233333333'),
(0, 'lama@email.com', '$2a$12$HFKRmoGUoe8/I4px/K3AquKQY//7pJPT1XGnrBG3fYa8cmi0jkIUK', 'Lama Khaled', '101 Beirut St, Lebanon', 300.0, '01544444444'),
(0, 'kerollos@email.com', '$2a$12$HFKRmoGUoe8/I4px/K3AquKQY//7pJPT1XGnrBG3fYa8cmi0jkIUK', 'Kerollos Samy', '202 Dubai Marina, UAE', 0.0, '01055555555'),
(0, 'leena@email.com', '$2a$12$HFKRmoGUoe8/I4px/K3AquKQY//7pJPT1XGnrBG3fYa8cmi0jkIUK', 'Leena Almekkawy', '303 Riyadh Rd, Saudi Arabia', 150.0, '01266666666');

-- password is 'kero'
-- ----------------------------
-- Step 2: Insert Product Categories
-- ----------------------------

INSERT INTO product_categories (id, name) VALUES
(0, 'Tea Herbs'),
(0, 'Coffee Beans'),
(0, 'Mugs & Cups'),
(0, 'Tea & Coffee Machines'),
(0, 'Accessories');

-- ----------------------------
-- Step 3: Insert Products
-- ----------------------------

INSERT INTO products (
    product_id,
    name,
    description,
    price,
    category_id,
    image,
    stock
) VALUES
(0, 'Green Tea Leaves', 'Premium loose leaf green tea from China.', 75.5, 1, '1.jpg',  50),
(0, 'Arabica Coffee Beans', '100% Arabica whole beans, medium roast.', 120.0, 2, '2.jpg',  80),
(0, 'Porcelain Teacup Set', 'Set of 2 handcrafted porcelain teacups.', 90.0, 3, '3.jpg',  30),
(0, 'Automatic Espresso Machine', 'Semi-professional espresso machine with milk frother.', 1500.0, 4, '4.jpg',  15),
(0, 'Herbal Chamomile Blend', 'Relaxing chamomile herbal infusion in tea bags.', 60.0, 1, '5.jpg',  60),
(0, 'Dark Roast Coffee Beans', 'Bold dark roast beans with rich flavor profile.', 130.0, 2, '6.jpg',  40),
(0, 'Travel Mug - Insulated', 'Double-walled insulated mug with lid.', 85.0, 3, '7.jpg',  25),
(0, 'Manual French Press', 'Classic French press for coffee lovers.', 200.0, 4, '8.jpg',  0),
(0, 'Peppermint Herbal Tea', 'Fresh peppermint leaves for soothing tea.', 55.0, 1, '9.jpg',  70),
(0, 'Tea Infuser Spoon', 'Stainless steel infuser spoon for loose tea.', 30.0, 5, '10.jpg',  100);

-- ----------------------------
-- Step 4: Insert Carts (one per user)
-- ----------------------------

INSERT INTO cart (cart_id) VALUES
(1), (2), (3), (4), (5), (6);

-- ----------------------------
-- Step 5: Insert Orders
-- ----------------------------

INSERT INTO orders (
    order_id,
    user_id,
    total_price,
    status
) VALUES
(0, 1, 225.0, 'ACCEPTED'),
(0, 1, 150.0, 'ACCEPTED'),
(0, 1, 200.0, 'PENDING'),
(0, 2, 120.0, 'ACCEPTED'),
(0, 3, 75.5, 'ACCEPTED'),
(0, 3, 85.0, 'CANCELLED'),
(0, 4, 90.0, 'ACCEPTED'),
(0, 4, 30.0, 'ACCEPTED'),
(0, 5, 1500.0, 'ACCEPTED'),
(0, 6, 55.0, 'ACCEPTED');

-- ----------------------------
-- Step 6: Insert Order Items
-- ----------------------------

INSERT INTO order_items (
    order_item_id,
    order_id,
    product_id,
    quantity,
    item_price
) VALUES
-- Order 1 (User 1)
(1, 1, 1, 2, 75.5),
(2, 1, 2, 1, 120.0),
(3, 1, 10, 1, 30.0),

-- Order 2 (User 1)
(4, 2, 3, 1, 90.0),
(5, 2, 5, 1, 60.0),

-- Order 3 (User 1)
(6, 3, 7, 2, 85.0),

-- Order 4 (User 2)
(7, 4, 2, 1, 120.0),

-- Order 5 (User 3)
(8, 5, 1, 1, 75.5),

-- Order 6 (User 3)
(9, 6, 3, 1, 90.0),

-- Order 7 (User 4)
(10, 7, 3, 1, 90.0),

-- Order 8 (User 4)
(11, 8, 10, 1, 30.0),

-- Order 9 (User 5)
(12, 9, 4, 1, 1500.0),

-- Order 10 (User 6)
(13, 10, 9, 1, 55.0);

-- ----------------------------
-- Step 7: Update Product Stock
-- ----------------------------

UPDATE products SET stock = 47 WHERE product_id = 1;
UPDATE products SET stock = 79 WHERE product_id = 2;
UPDATE products SET stock = 28 WHERE product_id = 3;
UPDATE products SET stock = 14 WHERE product_id = 4;
UPDATE products SET stock = 59 WHERE product_id = 5;
UPDATE products SET stock = 40 WHERE product_id = 6;
UPDATE products SET stock = 23 WHERE product_id = 7;
UPDATE products SET stock = 0 WHERE product_id = 8;
UPDATE products SET stock = 69 WHERE product_id = 9;
UPDATE products SET stock = 98 WHERE product_id = 10;
