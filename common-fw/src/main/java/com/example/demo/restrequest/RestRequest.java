package com.example.demo.restrequest;

import java.util.Map;

public interface RestRequest<T> {

	/**
	 * ベースURL以降のurlを返却する。
	 * 
	 * @return url
	 */
	String getUrl();

	/**
	 * クエリ付GETを行う際のクエリパラメータを返却する。
	 * 
	 * @return
	 */
	Map<String, String> getParam();

	/**
	 * リソースを一意に特定するURLを返却する。
	 * 
	 * @return
	 */
	String getIdentifiedUrl();

	/**
	 * PUT/POSTを行うためのデータを返却する
	 * 
	 * @return
	 */
	T getData();

	/**
	 * レスポンスjsonを格納するクラスの型を返却する。
	 * 
	 * @return
	 */
	Class<T> getResponseType();

}