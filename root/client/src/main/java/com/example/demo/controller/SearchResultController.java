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

	// 検索結果1ページあたりに表示する項目数
	@Value("${views.searchresult.cellsize}")
	private int cellSize;

	// 検索結果のキャッシュ
	// 描画スレッドからしかアクセスしないのでHashMapを使う
	private Map<Integer, List<ObservableUserInfo>> cachedTabelData = new HashMap<>();

	public void initialize(UserInfo request) {
		// 検索結果表示用のテーブル生成
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

		// FIXME 初回表示時に先頭ページへの遷移が2回発生する。setPageCountのせいでイベントが無駄に発火されている？
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

					// 取得成功時はレスポンスの内容をテーブルに設定
					.onSuccess(response -> {

						List<UserInfo> infolist = response.getContent();
						List<ObservableUserInfo> obsList = new ArrayList<>(infolist.size());

						for (UserInfo info : infolist) {
							obsList.add(ObservableUserInfo.create(info));
						}

						table.getItems().clear();
						table.getItems().addAll(obsList);
						pagenation.setPageCount(response.getPageInfo().getMaxPage());
						pagenation.setDisable(false);

						// キャッシュする
						cachedTabelData.put(page, obsList);
					})
					// 通信失敗、またはエラーが返却された場合はエラーダイアログ表示
					.onFailure((response, http) -> {
						new HttpErrorDialog(http).showAndWait();
					}).onError(e -> {
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
