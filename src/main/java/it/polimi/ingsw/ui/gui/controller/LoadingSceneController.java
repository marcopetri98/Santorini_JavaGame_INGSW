package it.polimi.ingsw.ui.gui.controller;

import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LoadingSceneController {

	@FXML
	private ImageView icon_zeus;

	@FXML
	private ImageView button_exit;

	Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	Image buttonExit = new Image("/img/home_exit_btn.png");

	/**
	 * moving icon_zeus
	 */
	public void initialize(){
		PathTransition transition = new PathTransition();
		transition.setNode(icon_zeus);
		transition.setDuration(Duration.millis(3500));
		transition.setPath(new Circle(327, 230, 12)); //x,y,radius (pixels)
		transition.setCycleCount(PathTransition.INDEFINITE);
		transition.play();
	}

	public void mousePressedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExitPressed);
	}

	public void mouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);
	}
}
