package com.ecommerce.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {
	@Autowired
	private OrderService orderService;

	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderRequest) {
		try {
			Long userId = Long.valueOf(orderRequest.get("userId").toString());
			String shippingAddress = (String) orderRequest.get("shippingAddress");

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> items = (List<Map<String, Object>>) orderRequest.get("items");

			List<CartItem> cartItems = items.stream().map(item -> {
				CartItem cartItem = new CartItem();
				cartItem.setProductId(Long.valueOf(item.get("productId").toString()));
				cartItem.setQuantity(Integer.valueOf(item.get("quantity").toString()));
				return cartItem;
			}).toList();

			Order order = orderService.createOrder(userId, cartItems, shippingAddress);
			return ResponseEntity.ok(order);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/user/{userId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
		return ResponseEntity.ok(orderService.getUserOrders(userId));
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Order>> getAllOrders() {
		return ResponseEntity.ok(orderService.getAllOrders());
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
		return ResponseEntity.ok(orderService.getOrderById(id));
	}

	@PutMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
		OrderStatus status = OrderStatus.valueOf(statusMap.get("status"));
		return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
	}
}
