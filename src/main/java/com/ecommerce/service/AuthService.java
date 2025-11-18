package com.ecommerce.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtUtil;

@Service
public class AuthService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	public Map<String, Object> register(User user) {
		if (userRepository.existsByUsername(user.getUsername())) {
			throw new RuntimeException("Username already exists");
		}
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new RuntimeException("Email already exists");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if (user.getRole() == null) {
			user.setRole(Role.CUSTOMER);
		}
		User savedUser = userRepository.save(user);

		UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
		String token = jwtUtil.generateToken(userDetails);

		Map<String, Object> response = new HashMap<>();
		response.put("token", token);
		response.put("user", getUserInfo(savedUser));
		return response;
	}

	public Map<String, Object> login(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		String token = jwtUtil.generateToken(userDetails);

		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		Map<String, Object> response = new HashMap<>();
		response.put("token", token);
		response.put("user", getUserInfo(user));
		return response;
	}

	private Map<String, Object> getUserInfo(User user) {
		Map<String, Object> userInfo = new HashMap<>();
		userInfo.put("id", user.getId());
		userInfo.put("username", user.getUsername());
		userInfo.put("email", user.getEmail());
		userInfo.put("fullName", user.getFullName());
		userInfo.put("role", user.getRole());
		userInfo.put("address", user.getAddress());
		userInfo.put("phone", user.getPhone());
		return userInfo;
	}
}
