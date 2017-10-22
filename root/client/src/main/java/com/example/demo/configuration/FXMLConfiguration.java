package com.example.demo.configuration;

import java.util.regex.Pattern;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.example.demo.jfx.NameBasedVCMapper;
import com.example.demo.jfx.VCMapper;
import com.example.demo.model.TabPaneAccessor;

import javafx.scene.control.TextFormatter;

@Configuration
public class FXMLConfiguration {

	// FxmlファイルとコントローラをマッピングするBean
	@Bean
	public VCMapper getMapper() {
		return new NameBasedVCMapper();
	}

	// Home画面のタブへアクセスするためのショートカットBean
	@Bean
	public TabPaneAccessor getTabAccessor() {
		return new TabPaneAccessor();
	}

	public static final Pattern notNumberPattern = Pattern.compile("[^0-9]+");

	// テキストに数字しか入力できないようにするフォーマッタ。スレッドアンセーフなのか1インスタンスを複数のコントローラに設定できないのでスコープはプロトタイプ
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	@Bean(name="numberFormatter")
	public TextFormatter<String> getNumTextFormatter() {
		return new TextFormatter<>(change -> {
			String newStr = notNumberPattern.matcher(change.getText()).replaceAll("");
			int diffcount = change.getText().length() - newStr.length();
			change.setAnchor(change.getAnchor() - diffcount);
			change.setCaretPosition(change.getCaretPosition() - diffcount);
			change.setText(newStr);
			return change;
		});
	}
}
