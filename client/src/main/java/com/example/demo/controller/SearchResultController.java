package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.example.demo.model.UserInfo;
import com.example.demo.model.UserInfoRequest;
import com.example.demo.model.fxcomponents.ExceptionDialog;
import com.example.demo.model.fxcomponents.HttpErrorDialog;
import com.example.demo.model.restcommunication.RequestExchanger;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;

public class SearchResultController {

	@FXML
	private Pagination pagenation;

	@Autowired
	private RequestExchanger requestExchanger;
	
	@Value("${views.searchresult.cellsize}")
	private int cellSize;

	private Map<Integer, List<ObservableUserInfo>> cachedTabelData = new HashMap<>();

	public void initialize(UserInfo request) {
		TableView<ObservableUserInfo> table = new TableView<>();
		table.setPrefHeight(30);
		table.prefHeightProperty().bind(Bindings.size(table.getItems()).multiply(table.getFixedCellSize()).add(30));
		TableColumn<ObservableUserInfo, String> col1 = new TableColumn<>("name");
		TableColumn<ObservableUserInfo, Integer> col2 = new TableColumn<>("age");
		TableColumn<ObservableUserInfo, String> col3 = new TableColumn<>("gender");
		col1.setCellValueFactory(new PropertyValueFactory<ObservableUserInfo, String>("name"));
		col2.setCellValueFactory(new PropertyValueFactory<ObservableUserInfo, Integer>("age"));
		col3.setCellValueFactory(new PropertyValueFactory<ObservableUserInfo, String>("gender"));

		table.getColumns().add(col1);
		table.getColumns().add(col2);
		table.getColumns().add(col3);
		
		// TODO なぜか初回表示時に0ページへの遷移が2回発生する。PageCountのせい？
		pagenation.setPageFactory((page) -> {
			pagenation.setDisable(true);

			// キャッシュにあればそれを表示
			if (cachedTabelData.containsKey(page)) {
				table.getItems().clear();
				table.getItems().addAll(cachedTabelData.get(page));
				pagenation.setDisable(false);
				return table;
			}

			UserInfoRequest req = new UserInfoRequest(request);

			// キャッシュになければサーバーから取得
			requestExchanger.requestPage(req, page, cellSize)
			.onSuccess(response -> {

				List<UserInfo> infolist = response.getContent();
				List<ObservableUserInfo> obsList = new ArrayList<>(infolist.size());

				for (UserInfo info : infolist) {
					obsList.add(ObservableUserInfo.create(info));
				}

				cachedTabelData.put(page, obsList);

				table.getItems().clear();
				table.getItems().addAll(obsList);
				pagenation.setPageCount(response.getPageInfo().getMaxPage());
				pagenation.setDisable(false);
			})
			.onFailure((response, http) -> {
				new HttpErrorDialog(http).showAndWait();
			})
			.onError(e -> {
				new ExceptionDialog(e).showAndWait();
			}).get();

			return table;
		});
	}

	// javaFxのTableView用構造体。TableViewで表示させるために、Property型のフィールドと、名前が要素名+PropertyのGetterが必要
	@AllArgsConstructor
	public static class ObservableUserInfo {
		private final SimpleStringProperty nameProperty;
		private final SimpleIntegerProperty ageProperty;
		private final SimpleStringProperty genderProperty;

		public static ObservableUserInfo create(UserInfo info) {
			return new ObservableUserInfo(new SimpleStringProperty(info.getName()),
					new SimpleIntegerProperty(info.getAge()), new SimpleStringProperty(info.getGender().toString()));
		}

		public SimpleStringProperty nameProperty() {
			return nameProperty;
		}

		public SimpleIntegerProperty ageProperty() {
			return ageProperty;
		}

		public SimpleStringProperty genderProperty() {
			return genderProperty;
		}
	}
}
