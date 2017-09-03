package com.example.demo.jfx;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * fxmlファイル名と同名のコントローラをマッピングする。
 * </p>
 * 
 * AAA.fxml -> com.example.demo.controller.AAAController
 * 
 * @author tsuchiya
 */
public class NameBasedVCMapper implements VCMapper {

	private static final String PACKAGE_AS_PREFIX = "com.example.demo.controller.";
	private static final String SUFFIX = "Controller";

	private ConcurrentMap<Path, Class<?>> cache = new ConcurrentHashMap<>();

	@Override
	public Class<?> getControllerClass(Path path) {
		path = path.normalize();

		if (!cache.containsKey(path)) {

			String fileName = path.getFileName().toString();

			fileName = fileName.substring(0, fileName.indexOf('.'));

			Class<?> controllerClass;
			try {
				controllerClass = Class.forName(PACKAGE_AS_PREFIX + fileName + SUFFIX);
			} catch (ClassNotFoundException e) {
				// mappingできなかったらnullを返却する。
				return null;
			}
			cache.putIfAbsent(path, controllerClass);
		}

		return cache.get(path);
	}

}
