package com.example.demo.model.restcommunication;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.restrequest.Pageable;
import com.example.demo.restrequest.PageableResponse;
import com.example.demo.restrequest.PageableRestRequest;
import com.example.demo.restrequest.RestRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RestCommunicationService {

	@Autowired
	private RestTemplate template;

	@Autowired
	private ExecutorService executorService;

	protected <T> ResponseEntity<T> put(RestRequest<T> request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<T> data = new HttpEntity<>(request.getData(), headers);
		return template.exchange(request.getUrl(), HttpMethod.PUT, data, request.getResponseType());
	}

	protected <T> ResponseEntity<T> get(RestRequest<T> request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> data = new HttpEntity<>(headers);
		return template.exchange(request.getIdentifiedUrl(), HttpMethod.GET, data, request.getResponseType());
	}

	// UriComponentsBuilderのためにベースURLが必要
	@Value("${serverside.baseurl}")
	String baseurl;

	protected <T, R extends PageableResponse<T>> ResponseEntity<R> get(PageableRestRequest<T, R> request,
			Pageable page) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseurl + request.getUrl());

		request.getParam().forEach((k, v) -> builder.queryParam(k, v));
		builder.queryParam("page", page.getCurrentPage());
		builder.queryParam("size", page.getSizePerPage());

		// TODO queryにsortByとsortHowを追加する？

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> data = new HttpEntity<>(headers);
		return template.exchange(builder.build().encode().toUri(), HttpMethod.GET, data,
				request.getPageableResponseType());
	}

	protected <T> ResponseEntity<T> post(RestRequest<T> request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<T> data = new HttpEntity<>(request.getData(), headers);
		return template.exchange(request.getUrl(), HttpMethod.POST, data, request.getResponseType());
	}

	public <T> Future<ResponseEntity<T>> getFuture(RestRequest<T> request) {
		return executorService.submit(() -> get(request));
	}

	public <T> Future<ResponseEntity<T>> putFuture(RestRequest<T> request) {
		return executorService.submit(() -> put(request));
	}

	public <T> Future<ResponseEntity<T>> postFuture(RestRequest<T> request) {
		return executorService.submit(() -> post(request));
	}

	public <T> void getAndCallBack(RestRequest<T> request, Consumer<ResponseEntity<T>> callBack,
			List<Consumer<Exception>> errorHandler) {
		executorService.submit(handle(() -> callBack.accept(get(request)), errorHandler));
	}

	public <T, R extends PageableResponse<T>> void getPageAndCallBack(PageableRestRequest<T, R> request, Pageable page,
			Consumer<ResponseEntity<R>> callBack, List<Consumer<Exception>> errorHandler) {
		executorService.submit(handle(() -> callBack.accept(get(request, page)), errorHandler));
	}

	public <T> void putAndCallBack(RestRequest<T> request, Consumer<ResponseEntity<T>> callBack,
			List<Consumer<Exception>> errorHandler) {
		executorService.submit(handle(() -> callBack.accept(put(request)), errorHandler));
	}

	public <T> void postAndCallBack(RestRequest<T> request, Consumer<ResponseEntity<T>> callBack,
			List<Consumer<Exception>> errorHandler) {
		executorService.submit(handle(() -> callBack.accept(post(request)), errorHandler));
	}

	private Runnable handle(Runnable task, List<Consumer<Exception>> errorHandler) {
		return () -> {
			try {
				task.run();
			} catch (Exception e) {
				log.error("server connection error", e);
				errorHandler.forEach(handler -> handler.accept(e));
			}
		};
	}
}
