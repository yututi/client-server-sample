package com.example.demo.jfx;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * fxmlローダーを利用してNodeの生成を行う。
 * @author tsuchiya
 *
 */
@Component
public class FXMLLoaderService {

	@Value("${views.fxmlname.root}")
	private Path rootfxml;

	@Autowired
	private VCMapper vcMapper;

	@Autowired
	private ConfigurableApplicationContext context;

	private boolean initialized;

	/**
	 * 指定されたパスのfxmlをロードする
	 * @param path
	 * @return
	 */
	public <T extends Node> T load(Path path) {
		return loadCore(path);
	}

	/**
	 * 最初に起動するfxmlをロードする。
	 * @return
	 */
	public Parent loadRoot() {
		if (initialized) {
			throw new UnsupportedOperationException("attempt to load root fxml twice");
		}
		if (rootfxml == null) {
			throw new IllegalStateException("property rootfxml is null or empty");
		}

		initialized = true;
		return loadCore(rootfxml);
	}

	private <T extends Node> T loadCore(Path path) {

		// Mapperからコントローラのクラスインスタンスの取得を試みる
		Class<?> controllerClass = vcMapper.getControllerClass(path);

		FXMLLoader loader = new FXMLLoader();

		// コントローラを生成できた場合、インスタンスを生成してローダーに設定
		if (controllerClass != null) {
			Object controller = instantiateQuietly(controllerClass);
			// autowireする。
			context.getAutowireCapableBeanFactory().autowireBean(controller);
			loader.setController(controller);
		}
		// コントローラを生成できなかった場合、fxmlファイルにfx:controller属性が定義されていることを期待し、ControllerFactoryを設定。
		else {
			loader.setControllerFactory(context::getBean);
		}
		try {
			T node = loader.load(new ClassPathResource(path.toString()).getInputStream());
			node.setUserData(loader.getController());
			return node;
		} catch (IOException e) {
			throw new Error("failed to load fxml:" + path, e);
		}
	}

	private static Object instantiateQuietly(Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new Error("controller must has public no-args constructor :" + clazz.getSimpleName(), e);
		}
	}
}
