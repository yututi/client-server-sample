package com.example.demo.model.restcommunication;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.restrequest.RequestPage;
import com.example.demo.restrequest.PageableResponse;
import com.example.demo.restrequest.PageableRestRequest;
import com.example.demo.restrequest.RestRequest;

import javafx.application.Platform;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * RestなHTTP通信を行うクラス
 * </p>
 * メソッドチェーンを利用して、通信成功/失敗時の動作を定義する。
 * 
 * <pre>
 * {@code
 * RequestExchanger.request(reqObj)
 * 
 * //HTTPステータスコードが200番台のとき
 * .onSuccess((response) -> {
 *　　// 成功時の処理
 * })
 * 
 * //HTTPステータスコードが２００番台以外のとき
 * .onFailure((response, httpstatus) -> {
 *  // 失敗時の処理
 * })
 * 
 * //通信時にエラーが発生したとき
 * .onError((exception) -> {
 *  // エラー発生時の処理
 * })
 * 
 * // HTTPリクエスト送信
 * .post(); 
 * }
 * </pre>
 * 
 * @author tsuchiya
 *
 */
@Service
public class RequestExchanger {

	@Autowired
	private RestCommunicationService communicationService;

	/**
	 * <p>
	 * SingleRequestExchangerBuilderを生成する。
	 * </p>
	 * SingleRequestExchangerBuilderのメソッドチェインを利用してコールバックの定義や通信の実行を行う。<br>
	 * 
	 * @param request
	 * @return SingleRequestExchangerBuilder
	 */
	public <T> SingleRequestExchangerBuilder<T> request(RestRequest<T> request) {
		return new SingleRequestExchangerBuilder<>(request, communicationService);
	}

	/**
	 * <p>
	 * PageableRequestExchangerBuilderを生成する。
	 * </p>
	 * PageableRequestExchangerBuilderのメソッドチェインを利用してコールバックの定義や通信の実行を行う。<br>
	 * 
	 * @param request
	 * @return PageableRequestExchangerBuilder
	 */
	public <T, R extends PageableResponse<T>> PageableRequestExchangerBuilder<T, R> requestPage(
			PageableRestRequest<T, R> request, int page, int sizePerPage) {
		RequestPage pageable = new RequestPage(page, sizePerPage);
		return new PageableRequestExchangerBuilder<>(request, pageable, communicationService);
	}

	/**
	 * 
	 * @author tsuchiya
	 *
	 * @param <T>
	 */
	@RequiredArgsConstructor
	public static class SingleRequestExchangerBuilder<T> {
		private final RestRequest<T> request;
		private final RestCommunicationService communicationService;

		private List<Consumer<T>> onSuccessCallBacks = new LinkedList<>();
		private List<BiConsumer<T, HttpStatus>> onFailureCallBacks = new LinkedList<>();
		private List<Consumer<Exception>> onErrorCallBacks = new LinkedList<>();

		/**
		 * <p>
		 * HTTPステータスコードが200番台の応答が返ってきた場合に呼ばれるコールバック
		 * </p>
		 * 描画スレッドで実行される。
		 * 
		 * @param callback
		 * @return
		 */
		public SingleRequestExchangerBuilder<T> onSuccess(Consumer<T> callback) {
			Objects.requireNonNull(callback);
			onSuccessCallBacks.add(callback);
			return this;
		}

		/**
		 * <p>
		 * HTTPステータスコードが200番台以外の応答が返ってきた場合に呼ばれるコールバック
		 * </p>
		 * 描画スレッドで実行される。
		 * 
		 * @param callback
		 * @return
		 */
		public SingleRequestExchangerBuilder<T> onFailure(BiConsumer<T, HttpStatus> callBack) {
			Objects.requireNonNull(callBack);
			onFailureCallBacks.add(callBack);
			return this;
		}

		/**
		 * <p>
		 * エラーが発生した場合に呼ばれるコールバック
		 * </p>
		 * 描画スレッドで実行される。
		 * 
		 * @param callBack
		 * @return
		 */
		public SingleRequestExchangerBuilder<T> onError(Consumer<Exception> callBack) {
			Objects.requireNonNull(callBack);
			onErrorCallBacks.add((e) -> Platform.runLater(() -> callBack.accept(e)));
			return this;
		}

		/**
		 * HTTP:GETリクエストを送信する。
		 */
		public void get() {
			communicationService.getAndCallBack(request, responseHandler::accept, onErrorCallBacks);
		}

		/**
		 * HTTP:PUTリクエストを送信する。
		 */
		public void put() {
			communicationService.putAndCallBack(request, responseHandler::accept, onErrorCallBacks);
		}

		/**
		 * HTTP:POSTリクエストを送信する。
		 */
		public void post() {
			communicationService.postAndCallBack(request, responseHandler::accept, onErrorCallBacks);
		}

		private Consumer<ResponseEntity<T>> responseHandler = (responseEntity) -> {
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
		private final RequestPage pageable;
		private final RestCommunicationService communicationService;

		private List<Consumer<R>> onSuccessCallBacks = new LinkedList<>();
		private List<BiConsumer<R, HttpStatus>> onFailureCallBacks = new LinkedList<>();
		private List<Consumer<Exception>> onErrorCallBacks = new LinkedList<>();

		/**
		 * <p>
		 * HTTPステータスコードが200番台の応答が返ってきた場合に呼ばれるコールバック
		 * </p>
		 * 描画スレッドで実行される。
		 * 
		 * @param callback
		 * @return
		 */
		public PageableRequestExchangerBuilder<T, R> onSuccess(Consumer<R> callback) {
			Objects.requireNonNull(callback);
			onSuccessCallBacks.add(callback);
			return this;
		}

		/**
		 * <p>
		 * HTTPステータスコードが200番台以外の応答が返ってきた場合に呼ばれるコールバック
		 * </p>
		 * 描画スレッドで実行される。
		 * 
		 * @param callback
		 * @return
		 */
		public PageableRequestExchangerBuilder<T, R> onFailure(BiConsumer<R, HttpStatus> callBack) {
			Objects.requireNonNull(callBack);
			onFailureCallBacks.add(callBack);
			return this;
		}

		/**
		 * <p>
		 * エラーが発生した場合に呼ばれるコールバック
		 * </p>
		 * 描画スレッドで実行される。
		 * 
		 * @param callBack
		 * @return
		 */
		public PageableRequestExchangerBuilder<T, R> onError(Consumer<Exception> callBack) {
			Objects.requireNonNull(callBack);
			onErrorCallBacks.add((e) -> Platform.runLater(() -> callBack.accept(e)));
			return this;
		}

		/**
		 * HTTP:GETリクエストを送信する。
		 */
		public void get() {
			communicationService.getPageAndCallBack(request, pageable, responseHandler::accept, onErrorCallBacks);
		}

		private Consumer<ResponseEntity<R>> responseHandler = (responseEntity) -> {

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
