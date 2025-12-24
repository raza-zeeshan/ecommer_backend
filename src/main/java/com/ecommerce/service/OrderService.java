package com.ecommerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	public Order createOrder(Long userId, List<CartItem> cartItems, String shippingAddress) {
		System.out.println("=== Starting order creation ===");
		System.out.println("User ID: " + userId);
		System.out.println("Cart Items: " + cartItems.size());
		System.out.println("Shipping Address: " + shippingAddress);

		try {
			// Get user
			User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

			System.out.println("User found: " + user.getUsername());

			// Create order
			Order order = new Order();
			order.setUser(user);
			order.setOrderDate(LocalDateTime.now());
			order.setStatus(OrderStatus.PENDING);
			order.setShippingAddress(shippingAddress);

			// Calculate total amount
			double totalAmount = 0;

			// Create order items
			List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
				Product product = productRepository.findById(cartItem.getProductId())
						.orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));

				// Check stock
				if (product.getStock() < cartItem.getQuantity()) {
					throw new RuntimeException("Insufficient stock for product: " + product.getName());
				}

				// Create order item
				OrderItem orderItem = new OrderItem();
				orderItem.setOrder(order);
				orderItem.setProduct(product);
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setPrice(product.getPrice());

				// Reduce product stock
				product.setStock(product.getStock() - cartItem.getQuantity());
				productRepository.save(product);

				System.out.println("Order item created: " + product.getName() + " x " + cartItem.getQuantity());

				return orderItem;
			}).collect(Collectors.toList());

			order.setOrderItems(orderItems);

			// Calculate total amount
			totalAmount = orderItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

			order.setTotalAmount(totalAmount);

			System.out.println("Total Amount: " + totalAmount);

			// Save order
			Order savedOrder = orderRepository.save(order);

			System.out.println("Order saved with ID: " + savedOrder.getId());
			System.out.println("=== Order creation completed ===");

			return savedOrder;

		} catch (Exception e) {
			System.err.println("ERROR in createOrder: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to create order: " + e.getMessage());
		}
	}

	public List<Order> getUserOrders(Long userId) {
		System.out.println("Fetching orders for user: " + userId);
		try {
			List<Order> orders = orderRepository.findByUserId(userId);
			System.out.println("Found " + orders.size() + " orders");
			return orders;
		} catch (Exception e) {
			System.err.println("ERROR in getUserOrders: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch orders: " + e.getMessage());
		}
	}

	public List<Order> getAllOrders() {
		System.out.println("Fetching all orders");
		try {
			List<Order> orders = orderRepository.findAll();
			System.out.println("Found " + orders.size() + " orders");
			return orders;
		} catch (Exception e) {
			System.err.println("ERROR in getAllOrders: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch all orders: " + e.getMessage());
		}
	}

	public Order getOrderById(Long id) {
		System.out.println("Fetching order: " + id);
		return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
	}

	public Order updateOrderStatus(Long orderId, OrderStatus status) {
		System.out.println("Updating order " + orderId + " status to: " + status);
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
		order.setStatus(status);
		return orderRepository.save(order);
	}
}