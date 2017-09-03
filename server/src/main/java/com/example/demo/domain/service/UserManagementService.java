package com.example.demo.domain.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.example.demo.domain.model.db.UserInfoEntity;
import com.example.demo.domain.repository.UserInfoRepository;
import com.example.demo.model.PageableUserInfo;
import com.example.demo.model.UserInfo;

@Service
public class UserManagementService {

	@Autowired
	private UserInfoRepository repository;

	public Long register(UserInfo info) {
		if (info.getId() != null) {
			throw new IllegalStateException("id must be null but " + info.getId());
		}

		UserInfoEntity entity = UserInfoEntity.create(info);
		UserInfoEntity savedEntity = repository.saveAndFlush(entity);
		return savedEntity.getId();
	}

	public UserInfo findById(Long id) {
		UserInfoEntity entity = repository.findOne(id);

		return entity.toJsonObject();
	}

	public PageableUserInfo findAllByNameLike(UserInfo info, int page, int size) {

		UserInfoEntity example = UserInfoEntity.create(info);
		ExampleMatcher matcher = ExampleMatcher.matching().withStringMatcher(StringMatcher.CONTAINING);

		Page<UserInfoEntity> result = repository.findAll(Example.of(example, matcher),
				new PageRequest(page, size, Direction.ASC, "name"));

		return PageableUserInfo.create(
				result.getContent().stream().map(e -> e.toJsonObject()).collect(Collectors.toList()),
				result.getNumber(), result.getTotalPages());
	}
}
