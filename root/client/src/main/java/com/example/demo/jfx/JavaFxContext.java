package com.example.demo.jfx;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.fxml.FXMLLoader;
import lombok.Getter;

@Deprecated
public class JavaFxContext {

	@Autowired
	private FXMLLoader loader;

	@Getter
	@Value("${javafx.rootfxml}")
	private String rootfxml;

	@Autowired
	protected ConfigurableApplicationContext context;

	@PostConstruct
	public void start() {
		loader.setControllerFactory(context::getBean);
	}


}
