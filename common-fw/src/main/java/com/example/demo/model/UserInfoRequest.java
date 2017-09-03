package com.example.demo.model;

import java.util.HashMap;
import java.util.Map;

import com.example.demo.restrequest.PageableRestRequest;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserInfoRequest implements PageableRestRequest<UserInfo, PageableUserInfo> {

	private UserInfo info;

	@Override
	public String getUrl() {
		return "/users";
	}

	@Override
	public Class<UserInfo> getResponseType() {
		return UserInfo.class;
	}

	@Override
	public UserInfo getData() {
		return info;
	}

	@Override
	public String getIdentifiedUrl() {
		return getUrl() + "/" + String.valueOf(info.getId());
	}

	public static UserInfoRequest create(UserInfo info) {
		return new UserInfoRequest(info.clone());
	}

	@Override
	public Map<String, String> getParam() {
		Map<String, String> param = new HashMap<>();
		if (info.getName() != null)
			param.put("name", info.getName());
		if (info.getGender() != null)
			param.put("sex", String.valueOf(info.getGender()));
		if (info.getAge() != null)
			param.put("age", String.valueOf(info.getAge()));
		return param;
	}

	@Override
	public Class<PageableUserInfo> getPageableResponseType() {
		return PageableUserInfo.class;
	}
}
