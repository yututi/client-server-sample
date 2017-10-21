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

	/**
	 * Spring起動時、JavaFxも起動させる。
	 * @param args
	 */
	public static void main(String[] args) {
		context = SpringApplication.run(ClientApplication.class, args);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoaderService loader = context.getBean(FXMLLoaderService.class);

		// 初期画面表示
		Parent root = loader.loadRoot();
		Scene rootScene = new Scene(root);
		primaryStage.setScene(rootScene);
		primaryStage.show();
	}

	/**
	 * JavaFx終了時、Springも終了させる。
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() {
		context.close();
	}
}
