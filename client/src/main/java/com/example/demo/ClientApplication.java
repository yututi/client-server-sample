package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import com.example.demo.jfx.FXMLLoaderService;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication(exclude = { EmbeddedServletContainerAutoConfiguration.class, WebMvcAutoConfiguration.class })
public class ClientApplication extends Application {

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(ClientApplication.class, args);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoaderService loader = context.getBean(FXMLLoaderService.class);

		Parent root = loader.loadRoot();

		Scene rootScene = new Scene(root);
		primaryStage.setScene(rootScene);
		primaryStage.show();
	}

	@Override
	public void stop() {
		context.close();
	}
}
