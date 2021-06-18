package com.epam.libraryconfigclient.client.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.epam.libraryconfigclient.model.User;

@RestController
@RequestMapping("/user-service")
@RefreshScope
public class UserServiceConfigClient {

	@Autowired
	@Lazy
	private RestTemplate template;
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Value("${user.service.welcome}")
	private String welcomeUrl;
	
	@Value("${user.service.findAllUsers}")
	private String findAllUsersUrl;
	
	@Value("${user.service.findUserById: No user found}")
	private String findUserByIdUrl;
	
	@Value("${user.service.addNewUser}")
	private String addNewUserUrl;
	
	@Value("${user.service.updateExistingUser}")
	private String updateExistingUserUrl;
	
	@Value("${user.service.deleteUserById}")
	private String deleteUserByIdUrl;
	
	@GetMapping("/")
	public String welcomeUserService() {
		return template.getForObject(welcomeUrl, String.class);
	}
	
	@GetMapping("/users")
	public List<User> findAllUsers() {
		List<User> users = new ArrayList<User>();
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(MediaType.APPLICATION_JSON);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(mediaTypes);
		HttpEntity<User> httpEntity = new HttpEntity<User>(null, headers);
		try {
			ResponseEntity<User[]> responseEntity = template.exchange(findAllUsersUrl, HttpMethod.GET, httpEntity, User[].class);
			User[] result = responseEntity.getBody();
			for(User user : result) {
				users.add(user);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return users;
	}
	
	@GetMapping("/users/{userId}")
	public User findUserById(@PathVariable long userId) {
		User user = null;
		try {
			user = template.getForObject(findUserByIdUrl, User.class, userId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return user;
	}
	
	@PostMapping(path = "/users", consumes = "application/json")
	public User addNewUser(@RequestBody User user) {
		User result = null;
		try {
			User newUser = new User(0, user.getUserName(), user.getUserPhone(), user.getUserAddress());
			result = template.postForObject(addNewUserUrl, newUser, User.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@PutMapping(path = "/users", consumes = "application/json")
	public User updateExistingUser(@RequestBody User user) {
		User updatedUser = null;
		try {
			long userId = user.getUserId();
			template.put(updateExistingUserUrl, user);
			updatedUser = findUserById(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updatedUser;
	}
	
	@DeleteMapping("/users/{userId}")
	public void deleteUserById(@PathVariable long userId) {
		try {
			template.delete(deleteUserByIdUrl, userId);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
