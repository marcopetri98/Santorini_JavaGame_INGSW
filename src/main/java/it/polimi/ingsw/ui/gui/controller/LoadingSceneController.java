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
	private ImageView icon;

	@FXML
	private ImageView button_exit;

	@FXML
	private ImageView loading_background;

	Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	Image buttonExit = new Image("/img/home_exit_btn.png");

	Image logoGods = new Image("/img/loading_godspng.png");
	Image logoStarter = new Image("/img/loading_starterpng.png");
	Image logoLoading = new Image("/img/loading_zeuspng.png");
	Image loading = new Image("/img/loading_background.png");
	Image loadingGods = new Image("/img/loading_gods_background.png");
	Image loadingStarter = new Image("/img/loading_starter_background.png");




	public void initialize(){
		loading_background.setImage(phase());
		PathTransition transition = new PathTransition();
		icon.setImage(phaseLogo());
		transition.setNode(icon);
		transition.setDuration(Duration.millis(3500));
		transition.setPath(new Circle(630, 362, 12)); //x,y,radius (pixels)
		transition.setCycleCount(PathTransition.INDEFINITE);
		transition.play();
	}

	private Image phase(){
		//TODO:...
		return loading;
	}

	private Image phaseLogo(){
		//TODO:...
		return logoLoading;
	}

	public void mousePressedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExitPressed);
	}

	public void mouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);
	}
}
