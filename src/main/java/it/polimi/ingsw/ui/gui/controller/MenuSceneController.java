package it.polimi.ingsw.ui.gui.controller;

import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * This class implements the menu scene of the GUI.
 */
public class MenuSceneController {
	@FXML
	private ImageView button_exit;
	@FXML
	private ImageView logo_santorini;
	@FXML
	private ImageView button_play;

	Image buttonPlayPressed = new Image("/img/home_play_btn_pressed.png");
	Image buttonPlay = new Image("/img/home_play_btn.png");
	Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	Image buttonExit = new Image("/img/home_exit_btn.png");
	Parent nextFXML;
	Stage nextStage;
	Scene nextScene;

	/**
	 * moving santorini_logo
	 */
	public void initialize() {
		PathTransition transition = new PathTransition();
		transition.setNode(logo_santorini);
		transition.setDuration(Duration.millis(3500));
		transition.setPath(new Circle(355, 130, 7)); //x,y,radius (pixels)
		transition.setCycleCount(PathTransition.INDEFINITE);
		transition.play();
	}

	/**
	 * This method handles the mouse click on a play button: making it pressed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedPlay(MouseEvent mouseEvent) {
		button_play.setImage(buttonPlayPressed);
	}

	/**
	 * This method handles the mouse release on a play button: making it unpressed and changing the scene.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mouseReleasedPlay(MouseEvent mouseEvent) throws IOException {
		button_play.setImage(buttonPlay);

		nextStage = (Stage) ((Node)mouseEvent.getTarget()).getScene().getWindow();
		nextFXML = FXMLLoader.load(getClass().getResource("/fxml/nickname_serverAddress.fxml"));
		nextScene = new Scene(nextFXML);
		nextStage.setScene(nextScene);
	}

	/**
	 * This method handles the mouse click on a exit button: making it pressed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExitPressed);
		nextStage = (Stage) ((Node)mouseEvent.getTarget()).getScene().getWindow();
	}

	/**
	 * This method handles the mouse release on a exit button: making it unpressed and returning to the home scene.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);
		nextStage.close();
		System.exit(0);
	}

}
