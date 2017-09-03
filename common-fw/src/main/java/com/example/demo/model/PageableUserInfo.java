package com.example.demo.model;

import java.util.List;

import com.example.demo.restrequest.PageableResponse;
import com.example.demo.restrequest.ResponsePage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageableUserInfo implements PageableResponse<UserInfo> {
	private List<UserInfo> content;
	private ResponsePage pageInfo;

	public static PageableUserInfo create(List<UserInfo> info, int currentPage, int maxPage) {
		return new PageableUserInfo(info, new ResponsePage(currentPage, maxPage));
	}
}
