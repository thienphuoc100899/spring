package com.quizsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quizsystem.model.User;
import com.quizsystem.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public void save(User user) {
		userRepository.save(user);
	}
	
	public User getUserByEmail(String email) {
		User user = new User();
		user = userRepository.getUserByEmail(email);
		return user;
	}
}
