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
import com.example.demo.model.PageableUserInfoResponse;
import com.example.demo.model.UserInfo;

@RestController
public class UsersController {

	@Autowired
	private UserManagementService userManagementService;

	// ID指定のユーザー個別検索
	@GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody()
	public UserInfo getByUserId(@PathVariable Long id) {

		UserInfo info = userManagementService.findById(id);

		return info;
	}

	// ユーザーページ検索
	@GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public PageableUserInfoResponse getUsers(@RequestParam int page, @RequestParam int size,
			@RequestParam String name) {

		return userManagementService.findPageByNameLike(name, page, size);
	}

	// ユーザー新規登録
	@PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserInfo> postUsers(@RequestBody UserInfo info) {

		userManagementService.register(info);

		// TODO created 201にする
		return ResponseEntity.noContent().build();
	}
}
