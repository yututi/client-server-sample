package com.example.demo.model.restcommunication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

public class RestTemplateFactory extends AbstractFactoryBean<RestTemplate> {

	@Value("${serverside.baseurl}")
	private String baseUrl;

	@Value("${serverside.connecttimeout:3000}")
	private int ctimeout;

	@Value("${serverside.readtimeout:3000}")
	private int rtimeout;

	@Value("${serverside.username}")
	private String username;

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
