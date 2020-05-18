package it.polimi.ingsw.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ChooseNumPlayerSceneController {

	@FXML
	private ImageView button_2;

	@FXML
	private ImageView button_exit;

	@FXML
	private ImageView button_3;

	@FXML
	private ImageView button_next;

	Image buttonNextPressed = new Image("/img/home_next_btn_pressed.png");
	Image buttonNext = new Image("/img/home_next_btn.png");
	Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	Image buttonExit = new Image("/img/home_exit_btn.png");
	Image button2 = new Image("/img/num2_btn.png");
	Image button2Pressed = new Image("/img/num2_btn_pressed.png");
	Image button3 = new Image("/img/num3_btn.png");
	Image button3Pressed = new Image("/img/num3_btn_pressed.png");


	public void mousePressedButton2(MouseEvent mouseEvent) {
		button_2.setImage(button2Pressed);
		button_3.setImage(button3);
	}

	public void mouseReleasedButton2(MouseEvent mouseEvent) {
	}

	public void mousePressedButton3(MouseEvent mouseEvent) {
		button_3.setImage(button3Pressed);
		button_2.setImage(button2);
	}

	public void mouseReleasedButton3(MouseEvent mouseEvent) {
	}

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
}
