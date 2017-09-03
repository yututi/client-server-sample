package com.example.demo.restrequest;

import java.util.List;

/**
 * ページネーションjson用構造体.
 * 
 * @author tsuchiya
 *
 * @param <T>
 */
public interface PageableResponse<T> {

	List<T> getContent();

	ResponsePage getPageInfo();
}
