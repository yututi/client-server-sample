package com.example.demo.model;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.Getter;

public class TabPaneAccessor {

	@Getter
	private boolean init = false;

	private TabPane tabPane;

	public void init(TabPane pane) {
		checkNotInit();
		init = true;
		this.tabPane = pane;
	}

	public void addAndFocus(Tab tab) {
		this.tabPane.getTabs().add(tab);
		this.tabPane.getSelectionModel().select(tab);
	}

	private void checkNotInit() {
		if (isInit()) {
			throw new IllegalStateException("TabPaneAccessor alrady initialized");
		}
	}
}
