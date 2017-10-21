package com.example.demo.restrequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pageable {
	private int currentPage;
	private int sizePerPage;
}