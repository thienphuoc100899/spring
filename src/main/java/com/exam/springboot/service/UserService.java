package com.exam.springboot.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.exam.springboot.model.User;
import com.exam.springboot.web.dto.UserRegistrationDto;

public interface UserService extends UserDetailsService{
	User save(UserRegistrationDto registrationDto);
}
