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
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {
	@Autowired
	private OrderService orderService;

	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderRequest) {
		try {
			Long userId = Long.valueOf(orderRequest.get("userId").toString());
			String shippingAddress = (String) orderRequest.get("shippingAddress");

			// Support both "items" and "orderItems" field names for flexibility
			List<Map<String, Object>> items = null;

			if (orderRequest.containsKey("orderItems")) {
				items = (List<Map<String, Object>>) orderRequest.get("orderItems");
			} else if (orderRequest.containsKey("items")) {
				items = (List<Map<String, Object>>) orderRequest.get("items");
			} else {
				return ResponseEntity.badRequest().body(Map.of("error", "Missing items or orderItems in request"));
			}

			if (items == null || items.isEmpty()) {
				return ResponseEntity.badRequest().body(Map.of("error", "Cart items cannot be empty"));
			}

			List<CartItem> cartItems = items.stream().map(item -> {
				CartItem cartItem = new CartItem();
				cartItem.setProductId(Long.valueOf(item.get("productId").toString()));
				cartItem.setQuantity(Integer.valueOf(item.get("quantity").toString()));

				// Optional: set price if provided
				if (item.containsKey("price")) {
					cartItem.setPrice(Double.valueOf(item.get("price").toString()));
				}

				return cartItem;
			}).toList();

			Order order = orderService.createOrder(userId, cartItems, shippingAddress);

			if (order == null) {
				return ResponseEntity.badRequest().body(Map.of("error", "Failed to create order"));
			}

			return ResponseEntity.ok(order);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "Invalid data format: " + e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace(); // Log the error
			return ResponseEntity.badRequest().body(Map.of("error", "Order creation failed: " + e.getMessage()));
		}
	}

//	@GetMapping("/user/{userId}")
//	@PreAuthorize("isAuthenticated()")
//	public ResponseEntity<?> getUserOrders(@PathVariable Long userId) {
//		try {
//			List<Order> orders = orderService.getUserOrders(userId);
//
//			if (orders == null) {
//				return ResponseEntity.ok(List.of()); // Return empty list if null
//			}
//
//			return ResponseEntity.ok(orders);
//		} catch (Exception e) {
//			return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch user orders: " + e.getMessage()));
//		}
//	}
	@GetMapping("/user/{userId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> getUserOrders(@PathVariable Long userId) {
		try {
			System.out.println("=== Fetching orders for user: " + userId);

			List<Order> orders = orderService.getUserOrders(userId);

			System.out.println("=== Found " + orders.size() + " orders");
			System.out.println("=== Orders: " + orders);

			return ResponseEntity.ok(orders);
		} catch (Exception e) {
			System.err.println("=== ERROR: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch orders: " + e.getMessage()));
		}
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getAllOrders() {
		try {
			List<Order> orders = orderService.getAllOrders();
			return ResponseEntity.ok(orders);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch all orders: " + e.getMessage()));
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> getOrderById(@PathVariable Long id) {
		try {
			Order order = orderService.getOrderById(id);

			if (order == null) {
				return ResponseEntity.badRequest().body(Map.of("error", "Order not found"));
			}

			return ResponseEntity.ok(order);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch order: " + e.getMessage()));
		}
	}

	@PutMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
		try {
			String statusString = statusMap.get("status");

			if (statusString == null || statusString.isEmpty()) {
				return ResponseEntity.badRequest().body(Map.of("error", "Status cannot be empty"));
			}

			OrderStatus status = OrderStatus.valueOf(statusString);
			Order updatedOrder = orderService.updateOrderStatus(id, status);

			if (updatedOrder == null) {
				return ResponseEntity.badRequest().body(Map.of("error", "Order not found"));
			}

			return ResponseEntity.ok(updatedOrder);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "Invalid order status: " + e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "Failed to update order status: " + e.getMessage()));
		}
	}
}