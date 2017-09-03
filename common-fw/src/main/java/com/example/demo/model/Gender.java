package com.example.demo.model;

import java.util.NoSuchElementException;

/**
 * 性別
 * DBにはIDとして登録するので文字列<->idの変換を行うクラス。
 * @author tsuchiya
 *
 */
public enum Gender {
	Male(1), Female(2), Other(3);
	private int id;

	Gender(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static Gender getById(int id) {
		for (Gender gender : Gender.values()) {
			if (gender.id == id) {
				return gender;
			}
		}
		throw new NoSuchElementException(String.valueOf(id));
	}
}
