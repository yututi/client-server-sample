package com.example.demo.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.model.db.UserInfoEntity;

public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long> {
	public UserInfoEntity findFirstByNameEquals(String name);
}
