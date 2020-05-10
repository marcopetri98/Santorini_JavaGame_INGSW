package it.polimi.ingsw.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

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

	public void mousePressedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNextPressed);
	}

	public void mouseReleasedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNext);
	}

	public void mousePressedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExitPressed);
	}

	public void mouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);
	}

	//String name = texField_nickname.getText(); //after "Next" is clicked, i suppose...


}
