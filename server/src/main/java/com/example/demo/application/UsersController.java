package com.example.demo.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.service.UserManagementService;
import com.example.demo.model.PageableUserInfo;
import com.example.demo.model.UserInfo;

@RestController
public class UsersController {

	@Autowired
	private UserManagementService userManagementService;

	@GetMapping("/users/{id}")
	@ResponseBody
	public UserInfo getByUserId(@PathVariable Long id) {
		UserInfo info = userManagementService.findById(id);

		return info;
	}

	@GetMapping("/users")
	@ResponseBody
	public PageableUserInfo getUsers(@RequestParam int page, @RequestParam int size, @RequestParam String name) {

		UserInfo info = new UserInfo();
		info.setName(name);

		return userManagementService.findAllByNameLike(info, page, size);
	}

	@PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserInfo> postUsers(@RequestBody UserInfo info) {

		System.out.println(info);

		Long id = userManagementService.register(info);

		// TODO created 201
		return ResponseEntity.ok().build();
	}
}
