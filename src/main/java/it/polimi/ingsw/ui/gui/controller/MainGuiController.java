package it.polimi.ingsw.ui.gui.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainGuiController extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/loading.fxml")); //in this moment, I'm using this as a scene tester
		Scene menu = new Scene(root);
		stage.setScene(menu);
		stage.setTitle("Santorini");
		stage.getIcons().add(new Image("/img/icon_logo.png"));
		stage.setResizable(false);
		stage.show();
	}

	public static void main(String[] args){
		launch(args);
	}
}
