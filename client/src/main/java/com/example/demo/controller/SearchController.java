package com.example.demo.controller;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.example.demo.jfx.FXMLLoaderService;
import com.example.demo.model.TabPaneAccessor;
import com.example.demo.model.UserInfo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class SearchController {

	@FXML
	private TextField name;

	@FXML
	private VBox searchVbox;

	@Autowired
	private TabPaneAccessor tabAccessor;

	@Value("${views.fxmlname.searchresult}")
	private Path searchResultFxml;
	@Autowired
	private FXMLLoaderService loader;

	@FXML
	public void onSearchAction(ActionEvent event) {
		UserInfo info = new UserInfo();
		info.setName(name.getText());

		Node result = loader.load(searchResultFxml);

		SearchResultController controller = (SearchResultController) result.getUserData();

		controller.initialize(info);
		Tab resultTab = new Tab("result");
		resultTab.setContent(result);
		tabAccessor.addAndFocus(resultTab);
	}
}
