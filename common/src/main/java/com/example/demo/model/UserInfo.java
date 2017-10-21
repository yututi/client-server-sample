package com.example.demo.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfo implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private Gender gender;

	private Integer age;

	@Override
	public UserInfo clone() {
		try {
			UserInfo clone = (UserInfo) super.clone();
			clone.id = id;
			clone.name = name;
			clone.gender = gender;
			clone.age = age;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
