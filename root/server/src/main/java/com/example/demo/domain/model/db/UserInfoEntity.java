package com.example.demo.domain.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.example.demo.model.Gender;
import com.example.demo.model.UserInfo;

import lombok.Data;

@Table(name = "USER", indexes = @Index(columnList = "name"))
@Entity
@Data
public class UserInfoEntity {
	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "gender")
	private Integer gender;

	@Column(name = "age")
	private Integer age;

	public static UserInfoEntity createFromJsonObj(UserInfo info) {
		UserInfoEntity entity = new UserInfoEntity();
		entity.setId(info.getId());
		entity.setName(info.getName());
		entity.setAge(info.getAge());
		entity.setGender((info.getGender() == null) ? null : info.getGender().getId());
		return entity;
	}

	public UserInfo toJsonObj() {
		UserInfo info = UserInfo.builder()
				.id(id)
				.name(name)
				.gender(Gender.getById(gender))
				.age(age)
				.build();
		return info;
	}
}
