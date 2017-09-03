package com.example.demo.model.fxcomponents;

import org.springframework.http.HttpStatus;

import javafx.scene.control.Alert;

public class HttpErrorDialog extends Alert {

	public HttpErrorDialog(HttpStatus httpstatus) {
		super(AlertType.INFORMATION);
		setTitle("Information Dialog");
		setHeaderText(null);
		setContentText("HTTP STATUS = " + httpstatus.toString() + ". " + httpstatus.getReasonPhrase());
	}
}
