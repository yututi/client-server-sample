package com.example.demo.model.restcommunication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplateで行うHTTP通信にかかわる設定を行うファクトリクラス。
 * @author tsuchiya
 */
@Component
public class RestTemplateFactory extends AbstractFactoryBean<RestTemplate> {

	// サーバ側のベースURI
	@Value("${serverside.baseurl}")
	private String baseUrl;

	// TCPコネクション確立時のタイムアウト時間
	@Value("${serverside.connecttimeout:3000}")
	private int ctimeout;

	// HTTP通信のタイムアウト時間
	@Value("${serverside.readtimeout:3000}")
	private int rtimeout;

	// 認証用ユーザー名
	@Value("${serverside.username}")
	private String username;

	// 認証用パスワード
	@Value("${serverside.password}")
	private String password;

	@Override
	protected RestTemplate createInstance() throws Exception {
		return new RestTemplateBuilder()
				.basicAuthorization(username, password)
				.setConnectTimeout(ctimeout)
				.setReadTimeout(rtimeout)
				.rootUri(baseUrl)
				.build();
	}

	@Override
	public Class<?> getObjectType() {
		return RestTemplate.class;
	}
}
