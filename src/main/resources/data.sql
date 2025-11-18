// data.sql (in src/main/resources/)
-- Insert Categories
INSERT INTO categories (id, name, description) VALUES 
(1, 'Electronics', 'Electronic devices and gadgets'),
(2, 'Clothing', 'Fashion and apparel'),
(3, 'Books', 'Books and literature'),
(4, 'Home & Kitchen', 'Home appliances and kitchenware'),
(5, 'Sports', 'Sports equipment and accessories');

-- Insert Products
INSERT INTO products (id, name, description, price, stock, image_url, category_id) VALUES 
(1, 'Laptop Pro 15', 'High-performance laptop with 16GB RAM and 512GB SSD', 1299.99, 50, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400', 1),
(2, 'Wireless Headphones', 'Noise-cancelling Bluetooth headphones', 249.99, 100, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400', 1),
(3, 'Smartphone X', 'Latest smartphone with 5G capability', 899.99, 75, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400', 1),
(4, 'Men''s Casual Shirt', 'Comfortable cotton casual shirt', 39.99, 200, 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=400', 2),
(5, 'Women''s Summer Dress', 'Elegant floral print summer dress', 59.99, 150, 'https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=400', 2),
(6, 'Running Shoes', 'Professional running shoes for athletes', 129.99, 80, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400', 2),
(7, 'The Great Novel', 'Bestselling fiction book', 19.99, 300, 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400', 3),
(8, 'Programming Guide', 'Complete guide to modern programming', 49.99, 120, 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400', 3),
(9, 'Coffee Maker', 'Automatic coffee maker with timer', 89.99, 60, 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=400', 4),
(10, 'Kitchen Knife Set', 'Professional chef knife set', 149.99, 45, 'https://images.unsplash.com/photo-1593618998160-e34014e67546?w=400', 4),
(11, 'Yoga Mat', 'Premium non-slip yoga mat', 34.99, 150, 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=400', 5),
(12, 'Tennis Racket', 'Professional tennis racket', 199.99, 40, 'https://images.unsplash.com/photo-1617882524461-2b0338e8b7b1?w=400', 5);

-- Insert Admin User (password: admin123)
INSERT INTO users (id, username, email, password, full_name, address, phone, role, created_at) VALUES 
(1, 'admin', 'admin@ecommerce.com', '$2a$10$XoHFQBKxBvhVd9r9kDqzHOmGZ9J5K5wQQh5Gx1PW.kN5Y5f5J5L5e', 'Admin User', '123 Admin St, City', '1234567890', 'ADMIN', CURRENT_TIMESTAMP);

-- Insert Customer User (password: customer123)
INSERT INTO users (id, username, email, password, full_name, address, phone, role, created_at) VALUES 
(2, 'customer', 'customer@email.com', '$2a$10$XoHFQBKxBvhVd9r9kDqzHOmGZ9J5K5wQQh5Gx1PW.kN5Y5f5J5L5e', 'John Doe', '456 Customer Ave, Town', '9876543210', 'CUSTOMER', CURRENT_TIMESTAMP);