package com.example.demo.configuration;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {

	// @Valueの型解決用bean
	@Bean
	public static CustomEditorConfigurer getCustomEditorConfigurer() {

		CustomEditorConfigurer configurer = new CustomEditorConfigurer();
		Map<Class<?>, Class<? extends PropertyEditor>> map = new HashMap<>();

		// Pathの解決
		map.put(Path.class, PathPropertyEditor.class);

		configurer.setCustomEditors(map);
		return configurer;
	}

	public static class PathPropertyEditor extends PropertyEditorSupport {
		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			this.setValue(text == null ? null : Paths.get(text));
		}
	}

	// バックグラウンド処理用のワーカースレッドプール
	@Bean
	public ExecutorService getExecutorService() {
		return Executors.newCachedThreadPool();
	}
}
