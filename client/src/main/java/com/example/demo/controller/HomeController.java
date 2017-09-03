package com.example.demo.controller;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.example.demo.jfx.FXMLLoaderService;
import com.example.demo.model.TabPaneAccessor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class HomeController {

	@Value("${views.fxmlname.register}")
	Path registerfxml;

	@Value("${views.fxmlname.search}")
	Path searchfxml;

	@Autowired
	private FXMLLoaderService loader;

	@FXML
	private TabPane multipurposeTab;
	
	@Autowired
	private TabPaneAccessor tabAccesspr;

	@FXML
	public void initialize() {
		tabAccesspr.init(multipurposeTab);
	}

	@FXML
	void onRegisterAction(ActionEvent event) {
		Tab tab = new Tab("register");
		Node node = loader.load(registerfxml);
		tab.setContent(node);
		tabAccesspr.addAndFocus(tab);
	}

	@FXML
	void onSearchAction(ActionEvent event) {
		Tab tab = new Tab("search");
		Node node = loader.load(searchfxml);
		tab.setContent(node);
		tabAccesspr.addAndFocus(tab);
	}
}
