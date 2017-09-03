package com.example.demo.restrequest;

public interface PageableRestRequest<T, R extends PageableResponse<T>> extends RestRequest<T> {

	Class<R> getPageableResponseType();
}
