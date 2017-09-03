package com.example.demo.model.restcommunication;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.restrequest.Pageable;
import com.example.demo.restrequest.PageableResponse;
import com.example.demo.restrequest.PageableRestRequest;
import com.example.demo.restrequest.RestRequest;

import javafx.application.Platform;
import lombok.RequiredArgsConstructor;

@Service
public class RequestExchanger {

	@Autowired
	private RestCommunicationService communicationService;

	/**
	 * <p>
	 * リクエストビルダーを生成する。このメソッドではサーバーとの通信を行わない
	 * </p>
	 * リクエストビルダーのメソッドチェインを利用してコールバックの定義や通信の実行を行う。<br>
	 * 
	 * 
	 * @param request
	 * @return
	 */
	public <T> SingleRequestExchangerBuilder<T> request(RestRequest<T> request) {
		return new SingleRequestExchangerBuilder<>(request, communicationService);
	}

	public <T, R extends PageableResponse<T>> PageableRequestExchangerBuilder<T, R> requestPage(
			PageableRestRequest<T, R> request, int page, int sizePerPage) {
		Pageable pageable = new Pageable(page, sizePerPage);
		return new PageableRequestExchangerBuilder<>(request, pageable, communicationService);
	}

	@RequiredArgsConstructor
	public static class SingleRequestExchangerBuilder<T> {
		private final RestRequest<T> request;
		private final RestCommunicationService communicationService;

		private List<Consumer<T>> onSuccessCallBacks = new LinkedList<>();
		private List<BiConsumer<T, HttpStatus>> onFailureCallBacks = new LinkedList<>();
		private List<Consumer<Exception>> onErrorCallBacks = new LinkedList<>();

		public SingleRequestExchangerBuilder<T> onSuccess(Consumer<T> callback) {
			onSuccessCallBacks.add(callback);
			return this;
		}

		public SingleRequestExchangerBuilder<T> onFailure(BiConsumer<T, HttpStatus> callBack) {
			onFailureCallBacks.add(callBack);
			return this;
		}

		public SingleRequestExchangerBuilder<T> onError(Consumer<Exception> callBack) {
			onErrorCallBacks.add((e) -> Platform.runLater(() -> callBack.accept(e)));
			return this;
		}

		public void get() {
			communicationService.getAndCallBack(request, singleResponseHandler::accept, onErrorCallBacks);
		}

		public void put() {
			communicationService.putAndCallBack(request, singleResponseHandler::accept, onErrorCallBacks);
		}

		public void post() {
			communicationService.postAndCallBack(request, singleResponseHandler::accept, onErrorCallBacks);
		}

		private Consumer<ResponseEntity<T>> singleResponseHandler = (responseEntity) -> {
			HttpStatus httpStatus = responseEntity.getStatusCode();
			T response = responseEntity.getBody();
			if (httpStatus.is2xxSuccessful()) {
				onSuccessCallBacks.forEach(callBack -> Platform.runLater(() -> callBack.accept(response)));
			} else {
				onFailureCallBacks.forEach(callBack -> Platform.runLater(() -> callBack.accept(response, httpStatus)));
			}
		};

	}

	@RequiredArgsConstructor
	public static class PageableRequestExchangerBuilder<T, R extends PageableResponse<T>> {
		private final PageableRestRequest<T, R> request;
		private final Pageable pageable;
		private final RestCommunicationService communicationService;

		private List<Consumer<R>> onSuccessCallBacks = new LinkedList<>();
		private List<BiConsumer<R, HttpStatus>> onFailureCallBacks = new LinkedList<>();
		private List<Consumer<Exception>> onErrorCallBacks = new LinkedList<>();

		public PageableRequestExchangerBuilder<T, R> onSuccess(Consumer<R> callback) {
			onSuccessCallBacks.add(callback);
			return this;
		}

		public PageableRequestExchangerBuilder<T, R> onFailure(BiConsumer<R, HttpStatus> callBack) {
			onFailureCallBacks.add(callBack);
			return this;
		}

		public PageableRequestExchangerBuilder<T, R> onError(Consumer<Exception> callBack) {
			onErrorCallBacks.add((e) -> Platform.runLater(() -> callBack.accept(e)));
			return this;
		}

		public void get() {
			communicationService.getPageAndCallBack(request, pageable, singleResponseHandler::accept, onErrorCallBacks);
		}

		private Consumer<ResponseEntity<R>> singleResponseHandler = (responseEntity) -> {

			HttpStatus httpStatus = responseEntity.getStatusCode();
			R typedResponse = responseEntity.getBody();
			if (httpStatus.is2xxSuccessful()) {
				onSuccessCallBacks.forEach(callBack -> Platform.runLater(() -> callBack.accept(typedResponse)));
			} else {
				onFailureCallBacks
						.forEach(callBack -> Platform.runLater(() -> callBack.accept(typedResponse, httpStatus)));
			}
		};

	}

}
