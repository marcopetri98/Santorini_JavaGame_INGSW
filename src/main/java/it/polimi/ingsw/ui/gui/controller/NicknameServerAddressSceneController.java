package it.polimi.ingsw.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class NicknameServerAddressSceneController {

	@FXML
	private TextField textField_nickname;

	@FXML
	private TextField textField_address;

	@FXML
	private ImageView button_next;

	@FXML
	private ImageView button_exit;

	Image buttonNextPressed = new Image("/img/home_next_btn_pressed.png");
	Image buttonNext = new Image("/img/home_next_btn.png");
	Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	Image buttonExit = new Image("/img/home_exit_btn.png");
	Parent previousFXML;
	Stage nextStage;
	Scene previousScene;

	public void mousePressedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNextPressed);
	}
	public void mouseReleasedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNext);
	}

	public void mousePressedExit(MouseEvent mouseEvent) throws IOException {
		button_exit.setImage(buttonExitPressed);
		nextStage = (Stage) ((Node)mouseEvent.getTarget()).getScene().getWindow();
		previousFXML = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
		previousScene = new Scene(previousFXML);
	}
	public void mouseReleasedExit(MouseEvent mouseEvent) throws IOException {
		button_exit.setImage(buttonExit);
		nextStage.setScene(previousScene);
	}

	//String name = texField_nickname.getText(); //after "Next" is clicked, i suppose...


}
