package com.example.demo;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.UserInfo;
import com.example.demo.model.UserInfoRequest;
import com.example.demo.model.restcommunication.RestCommunicationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientApplicationTests {

	@Autowired
	RestCommunicationService service;

	@Autowired
	RestTemplate template;

	@Test
	public void contextLoads() throws InterruptedException {
		MockRestServiceServer mockServer = MockRestServiceServer.bindTo(template).build();

		mockServer.expect(requestTo("http://localhost:8080/users")).andExpect(new RequestMatcher() {
			
			@Override
			public void match(ClientHttpRequest request) throws IOException, AssertionError {
			}
		}).andRespond(withSuccess());

		UserInfo info = UserInfo.builder().age(10).name("test").build();
		service.postAndCallBack(UserInfoRequest.create(info), System.out::println, new LinkedList<>());
		TimeUnit.SECONDS.sleep(5);
		mockServer.verify();
	}
}
