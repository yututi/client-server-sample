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

import com.example.demo.restrequest.RequestPage;
import com.example.demo.restrequest.PageableResponse;
import com.example.demo.restrequest.PageableRestRequest;
import com.example.demo.restrequest.RestRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * RestTemplateを利用し、ワーカースレッドでサーバとの通信を行う。
 * </p>
 * 
 * @author tsuchiya
 *
 */
@Service
@Slf4j
public class RestCommunicationService {

	@Autowired
	private RestTemplate template;

	@Autowired
	private ExecutorService executorService;

	// --- PUT ---

	// 同期PUT
	protected <T> ResponseEntity<T> put(RestRequest<T> request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<T> data = new HttpEntity<>(request.getData(), headers);
		return template.exchange(request.getUrl(), HttpMethod.PUT, data, request.getResponseType());
	}

	/**
	 * <p>非同期PUT</p>
	 * 指定されたRestRequestに基づくHTTP：PUT通信を行う。<br>
	 * 通信はワーカースレッド上で行われる。
	 * @param request
	 * @return 
	 */
	public <T> Future<ResponseEntity<T>> putFuture(RestRequest<T> request) {
		return executorService.submit(() -> put(request));
	}

	/**
	 * <p>非同期PUT(コールバック)</p>
	 * 指定されたRestRequestに基づくHTTP：PUT通信を行う。<br>
	 * 通信はワーカースレッド上で行われ、サーバーからの応答
	 * @param request
	 * @return 
	 */
	public <T> void putAndCallBack(RestRequest<T> request, Consumer<ResponseEntity<T>> callBack,
			List<Consumer<Exception>> errorHandler) {
		executorService.submit(handle(() -> callBack.accept(put(request)), errorHandler));
	}

	// --- GET ---

	// 同期GET
	protected <T> ResponseEntity<T> get(RestRequest<T> request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> data = new HttpEntity<>(headers);
		return template.exchange(request.getIdentifiedUrl(), HttpMethod.GET, data, request.getResponseType());
	}

	/**
	 * <p>非同期GET</p>
	 * 指定されたRestRequestに基づくHTTP：GET通信を行う。<br>
	 * 通信はワーカースレッド上で行われる。
	 * @param request
	 * @return 
	 */
	public <T> Future<ResponseEntity<T>> getFuture(RestRequest<T> request) {
		return executorService.submit(() -> get(request));
	}

	// 非同期GET(コールバック)
	public <T> void getAndCallBack(RestRequest<T> request, Consumer<ResponseEntity<T>> callBack,
			List<Consumer<Exception>> errorHandler) {
		executorService.submit(handle(() -> callBack.accept(get(request)), errorHandler));
	}

	// --- GET(ページング) ---

	// ページングGETで利用するUriComponentsBuilderのためにベースURLが必要
	@Value("${serverside.baseurl}")
	private String baseurl;

	/**
	 * <p>非同期GET(ページング)</p>
	 * 指定されたRestRequestに基づくHTTP：GET通信を行う。<br>
	 * 通信はワーカースレッド上で行われる。
	 * @param request
	 * @return 
	 */
	protected <T, R extends PageableResponse<T>> ResponseEntity<R> get(PageableRestRequest<T, R> request,
			RequestPage page) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseurl + request.getUrl());

		// クエリストリングの作成
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

	// 非同期GET(ページング)
	public <T, R extends PageableResponse<T>> void getPageAndCallBack(PageableRestRequest<T, R> request, RequestPage page,
			Consumer<ResponseEntity<R>> callBack, List<Consumer<Exception>> errorHandler) {
		executorService.submit(handle(() -> callBack.accept(get(request, page)), errorHandler));
	}
	
	// --- POST ---

	// 同期POST
	protected <T> ResponseEntity<T> post(RestRequest<T> request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<T> data = new HttpEntity<>(request.getData(), headers);
		return template.exchange(request.getUrl(), HttpMethod.POST, data, request.getResponseType());
	}

	/**
	 * <p>非同期POST</p>
	 * 指定されたRestRequestに基づくHTTP：POST通信を行う。<br>
	 * 通信はワーカースレッド上で行われる。
	 * @param request
	 * @return 
	 */
	public <T> Future<ResponseEntity<T>> postFuture(RestRequest<T> request) {
		return executorService.submit(() -> post(request));
	}

	// 非同期POST(コールバック)
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
