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
public class OrderService {
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Transactional
	public Order createOrder(Long userId, List<CartItem> cartItems, String shippingAddress) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		// Create order without builder
		Order order = new Order();
		order.setUser(user);
		order.setShippingAddress(shippingAddress);
		order.setStatus(OrderStatus.PENDING);
		order.setOrderDate(LocalDateTime.now());

		List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
			Product product = productRepository.findById(cartItem.getProductId())
					.orElseThrow(() -> new RuntimeException("Product not found"));

			if (product.getStock() < cartItem.getQuantity()) {
				throw new RuntimeException("Insufficient stock for product: " + product.getName());
			}

			product.setStock(product.getStock() - cartItem.getQuantity());
			productRepository.save(product);

			// Create order item without builder
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(product.getPrice());

			return orderItem;
		}).collect(Collectors.toList());

		order.setOrderItems(orderItems);

		double totalAmount = orderItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
		order.setTotalAmount(totalAmount);

		return orderRepository.save(order);
	}

	public List<Order> getUserOrders(Long userId) {
		return orderRepository.findByUserId(userId);
	}

	public List<Order> getAllOrders() {
		return orderRepository.findAllByOrderByOrderDateDesc();
	}

	public Order getOrderById(Long id) {
		return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
	}

	@Transactional
	public Order updateOrderStatus(Long id, OrderStatus status) {
		Order order = getOrderById(id);
		order.setStatus(status);
		return orderRepository.save(order);
	}
}
