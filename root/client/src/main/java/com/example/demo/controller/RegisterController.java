package com.example.demo.controller;

import java.util.concurrent.atomic.AtomicReference;

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

	// 選択されているGenderを保持する
	private AtomicReference<Gender> gender = new AtomicReference<>();

	@FXML
	public void initialize() {

		// トグルのイベント設定
		tglOther.setToggleGroup(group);
		tglFemale.setToggleGroup(group);
		tglMale.setToggleGroup(group);

		tglOther.setOnAction(e -> {
			gender.set(Gender.Other);
		});
		tglFemale.setOnAction(e -> {
			gender.set(Gender.Female);
		});
		tglMale.setOnAction(e -> {
			gender.set(Gender.Male);
		});

		// トグル初期選択
		tglMale.setSelected(true);

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
		
		// 全入力コンポーネントを非活性にする
		registerPane.getChildren().forEach(child -> child.setDisable(true));

		// 入力情報チェック
		boolean isValid = validateInput();
		if (!isValid) {
			// 入力情報に不備があれば入力コンポーネントを活性化して終了。
			registerPane.getChildren().forEach(child -> child.setDisable(false));
			return;
		}

		UserInfo info = UserInfo.builder()
				.age(Integer.valueOf(age.getText()))
				.gender(gender.get())
				.name(name.getText())
				.build();

		UserInfoRequest request = UserInfoRequest.create(info);

		// HTTP:POST
		requestExchanger.request(request)
				// 登録成功時はコンポーネントを非活性にしたままにして２重登録を防ぐ
				.onSuccess(response -> {
					register.setText("registered");
				})
				//　登録に失敗したらエラーダイアログを表示
				.onFailure((response, httpstatus) -> {
					new HttpErrorDialog(httpstatus).showAndWait();
					registerPane.getChildren().forEach(child -> child.setDisable(false));
				})
				//
				.onError((e) -> {
					new ExceptionDialog(e).showAndWait();
					registerPane.getChildren().forEach(child -> child.setDisable(false));
				})
				.post();
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
