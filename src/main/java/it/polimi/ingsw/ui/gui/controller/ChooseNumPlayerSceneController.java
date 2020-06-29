package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.NetGaming;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Constants;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class ChooseNumPlayerSceneController implements SceneController {
	@FXML
	private ImageView button_2;
	@FXML
	private ImageView button_exit;
	@FXML
	private ImageView button_3;
	@FXML
	private ImageView button_next;
	@FXML
	private ImageView icon_errorFatalBG;
	@FXML
	private ImageView icon_errorFatal;
	@FXML
	private ImageView icon_error;

	Image buttonNextPressed = new Image("/img/home_next_btn_pressed.png");
	Image buttonNext = new Image("/img/home_next_btn.png");
	Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	Image buttonExit = new Image("/img/home_exit_btn.png");
	Image button2 = new Image("/img/num2_btn.png");
	Image button2Pressed = new Image("/img/num2_btn_pressed.png");
	Image button3 = new Image("/img/num3_btn.png");
	Image button3Pressed = new Image("/img/num3_btn_pressed.png");
	Image errorFatalBG = new Image("/img/errorFatal_background.png");
	Image errorFatal = new Image("/img/error_fatal.png");
	Image errorNumPlayer = new Image("/img/error_numPlayer.png");

	// objects used to change scene
	private Parent previousFXML;
	private Parent nextFXML;
	private Scene previousScene;
	private Scene nextScene;
	private Stage currentStage;

	// triggers for server messages
	private static ChooseNumPlayerSceneController currentObject;
	private int numPlayers = 0;
	private GameState gameState;

	public static SceneController getInstance() {
		return currentObject;
	}

	public void initialize() {
		currentObject = this;
		MainGuiController.getInstance().setSceneController(this);
		gameState = MainGuiController.getInstance().getGameState();

		button_2.setImage(button2);
		button_3.setImage(button3);

		icon_errorFatal.toBack();
		icon_errorFatalBG.toBack();
		icon_errorFatal.setImage(null);
		icon_error.toBack();
		icon_error.setImage(null);
	}

	private void fadeImage(ImageView imageView, Image image){
		imageView.setImage(image);
		FadeTransition ft = new FadeTransition(Duration.millis(2500), imageView);
		ft.setFromValue(0);
		ft.setToValue(1);
		ft.setCycleCount(1);
		ft.play();
	}

	private void slidingImage(ImageView imageView, Image image, int x1, int y1, int x2, int y2, int duration) {
		imageView.setImage(image);
		Line line = new Line();
		line.setStartX(x1);
		line.setStartY(y1);
		line.setEndX(x2);
		line.setEndY(y2);
		PathTransition transition = new PathTransition();
		transition.setNode(imageView);
		transition.setDuration(Duration.millis(duration));
		transition.setPath(line);
		transition.setCycleCount(1);
		transition.play();
	}

	private void moveImage(ImageView imageView, Image image, int x1_1, int y1_1, int x2_1, int y2_1, int x1_2, int y1_2, int x2_2, int y2_2, int x1_3, int y1_3, int x2_3, int y2_3, int x1_4, int y1_4, int x2_4, int y2_4, int duration1, int duration2, int duration3, int duration4) {
		imageView.setImage(image);

		Line line1 = new Line();
		Line line2 = new Line();
		Line line3 = new Line();
		Line line4 = new Line();

		SequentialTransition sequential = new SequentialTransition(setLine(imageView, line1, x1_1, y1_1, x2_1, y2_1, duration1), setLine(imageView, line2, x1_2, y1_2, x2_2, y2_2, duration2), setLine(imageView, line3, x1_3, y1_3, x2_3, y2_3, duration3), setLine(imageView, line4, x1_4, y1_4, x2_4, y2_4, duration4));
		sequential.play();
	}

	private Transition setLine(ImageView imageView, Line line, int x1, int y1, int x2, int y2, int duration){
		line.setStartX(x1);
		line.setStartY(y1);
		line.setEndX(x2);
		line.setEndY(y2);
		PathTransition transition = new PathTransition();
		transition.setNode(imageView);
		transition.setDuration(Duration.millis(duration));
		transition.setPath(line);
		transition.setCycleCount(1);

		return transition;
	}
	/* **********************************************
	 *												*
	 *			HANDLERS OF USER INTERACTION		*
	 * 												*
	 ************************************************/
	public void mousePressedButton2(MouseEvent mouseEvent) {
		if (button_2.getImage().equals(button2)) {
			numPlayers = 2;
			button_2.setImage(button2Pressed);
			button_3.setImage(button3);
		} else {
			numPlayers = 0;
			button_2.setImage(button2);
		}
	}
	public void mouseReleasedButton2(MouseEvent mouseEvent) {
	}
	public void mousePressedButton3(MouseEvent mouseEvent) {
		if (button_3.getImage().equals(button3)) {
			numPlayers = 3;
			button_3.setImage(button3Pressed);
			button_2.setImage(button2);
		} else {
			numPlayers = 0;
			button_3.setImage(button3);
		}
	}
	public void mouseReleasedButton3(MouseEvent mouseEvent) {
	}
	public void mousePressedNext(MouseEvent mouseEvent) throws IOException {
		button_next.setImage(buttonNextPressed);
	}
	public void mouseReleasedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNext);

		if (numPlayers != 2 && numPlayers != 3) {
			selectNumber();
		} else {
			((ImageView)mouseEvent.getTarget()).getScene().setCursor(Cursor.WAIT);
			NetSetup netSetup = new NetSetup(Constants.SETUP_IN_SETUPNUM, gameState.getPlayer(), numPlayers);
			MainGuiController.getInstance().sendMessage(netSetup);
		}
	}
	public void mousePressedExit(MouseEvent mouseEvent) throws IOException {
		button_exit.setImage(buttonExitPressed);
		previousFXML = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
		previousScene = new Scene(previousFXML);
	}
	public void mouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);

		NetSetup netSetup = new NetSetup(Constants.GENERAL_DISCONNECT);
		MainGuiController.getInstance().sendMessage(netSetup);
		MainGuiController.getInstance().refresh();
		MainGuiController.getInstance().setSceneController(null);
		currentStage = (Stage) button_exit.getScene().getWindow();
		currentStage.setScene(previousScene);
	}

	/* **********************************************
	 *												*
	 *		EVENTS AFTER SERVER MESSAGE				*
	 * 												*
	 ************************************************/
	public void selectNumber() {
		icon_error.toFront();
		moveImage(icon_error, errorNumPlayer, 600, 212, 198, 212, 198, 212, 211, 212, 211, 212, 198, 212, 198,212, 600, 212, 700, 1000, 1000, 500);
		button_next.toFront();
	}
	/**
	 *
	 */
	private void gameCantContinue() {
		fadeImage(icon_errorFatalBG, errorFatalBG);
		slidingImage(icon_errorFatal, errorFatal, 650, 0, 650, 325, 1250);
		icon_errorFatalBG.toFront();
		icon_errorFatal.toFront();
		button_exit.toFront();
	}

	/* **********************************************
	 *												*
	 *		METHODS CALLED BY MAIN CONTROLLER		*
	 * 												*
	 ************************************************/
	@Override
	public void fatalError() {
		gameCantContinue();
	}
	@Override
	public void deposeMessage(NetObject message) throws IOException {
		switch (message.message) {
			case Constants.SETUP_CREATE_WORKED -> {
				String[] players = new String[]{gameState.getPlayer()};
				gameState.setPlayerNumber(((NetSetup) message).number);
				gameState.setPlayers(players);
				nextFXML = FXMLLoader.load(getClass().getResource("/fxml/lobby.fxml"));
				nextScene = new Scene(nextFXML);
				currentStage = (Stage) button_exit.getScene().getWindow();
				currentStage.setScene(nextScene);
			}
		}
	}
}
