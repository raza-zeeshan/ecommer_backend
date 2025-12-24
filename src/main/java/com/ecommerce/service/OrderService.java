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
		try {
			// Get user
			User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

			// Create order
			Order order = new Order();
			order.setUser(user);
			order.setOrderDate(LocalDateTime.now());
			order.setStatus(OrderStatus.PENDING);
			order.setShippingAddress(shippingAddress);

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

				return orderItem;
			}).collect(Collectors.toList());

			order.setOrderItems(orderItems);

			// Calculate total amount
			double totalAmount = orderItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

			order.setTotalAmount(totalAmount);

			// Save and return order
			return orderRepository.save(order);

		} catch (Exception e) {
			throw new RuntimeException("Failed to create order: " + e.getMessage());
		}
	}

	public List<Order> getUserOrders(Long userId) {
		return orderRepository.findByUserId(userId);
	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	public Order getOrderById(Long id) {
		return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
	}

	public Order updateOrderStatus(Long orderId, OrderStatus status) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
		order.setStatus(status);
		return orderRepository.save(order);
	}
}