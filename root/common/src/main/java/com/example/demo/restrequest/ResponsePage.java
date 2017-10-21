package com.example.demo.restrequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponsePage {
	private int current;
	private int maxPage;
}
