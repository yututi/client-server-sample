package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.example.demo.model.Gender;
import com.example.demo.model.UserInfo;
import com.example.demo.model.UserInfoRequest;
import com.example.demo.model.fxcomponents.ExceptionDialog;
import com.example.demo.model.fxcomponents.HttpErrorDialog;
import com.example.demo.model.restcommunication.RequestExchanger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RegisterController {

	private ToggleGroup group = new ToggleGroup();

	@FXML
	private RadioButton tglOther;

	@FXML
	private RadioButton tglFemale;

	@FXML
	private RadioButton tglMale;

	@FXML
	private TextField name;

	@FXML
	private TextField age;

	@FXML
	private Button clear;

	@FXML
	private Button register;

	@FXML
	private BorderPane registerPane;

	@Autowired
	private RequestExchanger requestExchanger;

	@Autowired
	private TextFormatter<String> numberFormatter;

	private UserInfo info = new UserInfo();

	@FXML
	public void initialize() {

		// トグルのイベント設定
		tglOther.setToggleGroup(group);
		tglFemale.setToggleGroup(group);
		tglMale.setToggleGroup(group);

		tglOther.setOnAction(e -> {
			info.setGender(Gender.Other);
		});
		tglFemale.setOnAction(e -> {
			info.setGender(Gender.Female);
		});
		tglMale.setOnAction(e -> {
			info.setGender(Gender.Male);
		});

		// 初期選択
		tglMale.setSelected(true);
		info.setGender(Gender.Male);

		// 年齢テキストフィールドには数字しか入力できないようにする
		age.setTextFormatter(numberFormatter);
	}

	@FXML
	void onClearAction(ActionEvent event) {
		name.clear();
		age.clear();
	}

	@FXML
	void onRegisterAction(ActionEvent event) {
		registerPane.getChildren().forEach(child -> child.setDisable(true));

		// 入力情報チェック
		boolean isValid = validateInput();
		if (!isValid) {
			registerPane.getChildren().forEach(child -> child.setDisable(false));
			return;
		}

		// リクエスト生成
		info.setName(name.getText());
		info.setAge(Integer.valueOf(age.getText()));
		UserInfoRequest request = UserInfoRequest.create(info);

		// サーバーへリクエストをPOST
		requestExchanger.request(request)
				//
				.onSuccess(response -> {
					register.setText("registered");
				})
				//
				.onFailure((response, httpstatus) -> {
					new HttpErrorDialog(httpstatus).showAndWait();
					registerPane.getChildren().forEach(child -> child.setDisable(false));
				})
				//
				.onError((e) -> {
					new ExceptionDialog(e).showAndWait();
					registerPane.getChildren().forEach(child -> child.setDisable(false));
				}).post();
	}

	private boolean validateInput() {
		boolean valid = true;
		if (name.getText().isEmpty()) {
			name.setPromptText("名前を入力してください。");
			valid = false;
		}

		if (age.getText().isEmpty()) {
			age.setPromptText("年齢を入力してください。");
			valid = false;
		}
		return valid;
	}

}
