package com.example.demo.restrequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestPage {
	private int currentPage;
	private int sizePerPage;
}