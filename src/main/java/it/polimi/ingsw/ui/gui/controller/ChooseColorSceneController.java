package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Color;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;


public class ChooseColorSceneController implements SceneController {
	@FXML
	private ImageView button_green;
	@FXML
	private ImageView button_red;
	@FXML
	private ImageView button_blue;
	@FXML
	private ImageView button_next;
	@FXML
	private ImageView button_exit;
	@FXML
	private Text text_player;
	@FXML
	private ImageView icon_errorFatalBG;
	@FXML
	private ImageView icon_errorFatal;
	@FXML
	private ImageView icon_error;

	private Image buttonNextPressed = new Image("/img/home_next_btn_pressed.png");
	private Image buttonNext = new Image("/img/home_next_btn.png");
	private Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	private Image buttonExit = new Image("/img/home_exit_btn.png");
	private Image buttonRedPressed = new Image("/img/color_btn_coral_pressed.png");
	private Image buttonRed = new Image("/img/color_red_btn.png");
	private Image buttonGreenPressed = new Image("/img/color_btn_green_pressed.png");
	private Image buttonGreen = new Image("/img/color_green_btn.png");
	private Image buttonBluePressed = new Image("/img/color_btn_blue_pressed.png");
	private Image buttonBlue = new Image("/img/color_blue_btn.png");
	private Image buttonColorDisabled = new Image("/img/color_disable.png");
	private Image errorFatalBG = new Image("/img/errorFatal_background.png");
	private Image errorFatal = new Image("/img/error_fatal.png");
	private Image errorSomeoneDisconnected = new Image("/img/message_someoneDisconnected.png");
	private Image errorWaitTurn = new Image("/img/error_waitTurn.png");

	// objects used to change scene
	private Parent previousFXML;
	private Parent nextFXML;
	private Scene previousScene;
	private Scene nextScene;
	private Stage currentStage;
	private boolean redLocked = false, greenLocked = false, blueLocked = false;
	private boolean challengerReceived = false;
	private boolean finished = false;
	private Color choice = null;

	// triggers for server messages
	private GameState gameState;

	public void initialize(){
		MainGuiController.getInstance().setSceneController(this);
		gameState = MainGuiController.getInstance().getGameState();
		setChoosingPlayer();

		button_blue.setImage(buttonBlue);
		button_red.setImage(buttonRed);
		button_green.setImage(buttonGreen);

		icon_errorFatal.toBack();
		icon_errorFatalBG.toBack();
		icon_errorFatal.setImage(null);
		icon_error.toBack();
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
	public void mousePressedColor(MouseEvent mouseEvent) {
		ImageView buttonPressed = (ImageView) mouseEvent.getTarget();

		if (buttonPressed.getImage().equals(buttonBlue) || buttonPressed.getImage().equals(buttonBluePressed)) {
			if (buttonPressed.getImage().equals(buttonBlue)) {
				button_blue.setImage(buttonBluePressed);
				choice = Color.BLUE;
				if (!redLocked) {
					button_red.setImage(buttonRed);
				}
				if (!greenLocked) {
					button_green.setImage(buttonGreen);
				}
			} else {
				choice = null;
				button_blue.setImage(buttonBlue);
			}
		} else if (buttonPressed.getImage().hashCode() == buttonRed.hashCode() || buttonPressed.getImage().hashCode() == buttonRedPressed.hashCode()) {
			if (buttonPressed.getImage().equals(buttonRed)) {
				button_red.setImage(buttonRedPressed);
				choice = Color.RED;
				if (!blueLocked) {
					button_blue.setImage(buttonBlue);
				}
				if (!greenLocked) {
					button_green.setImage(buttonGreen);
				}
			} else {
				choice = null;
				button_red.setImage(buttonRed);
			}
		} else if (buttonPressed.getImage().equals(buttonGreen) || buttonPressed.getImage().equals(buttonGreenPressed)) {
			if (buttonPressed.getImage().equals(buttonGreen)) {
				button_green.setImage(buttonGreenPressed);
				choice = Color.GREEN;
				if (!blueLocked) {
					button_blue.setImage(buttonBlue);
				}
				if (!redLocked) {
					button_red.setImage(buttonRed);
				}
			} else {
				choice = null;
				button_green.setImage(buttonGreen);
			}
		}
	}

	public void mousePressedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNextPressed);
	}
	public void mouseReleasedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNext);

		if (choice != null && !finished) {
			if (!gameState.getPlayer().equals(gameState.getActivePlayer())) {
				// user is trying to choose the color in others turn
				chooseInYourTurnError();
			} else {
				// user is trying to choose a color in its turn, a message of choose will be sent
				((ImageView)mouseEvent.getTarget()).getScene().setCursor(Cursor.WAIT);
				NetColorPreparation colorMsg = new NetColorPreparation(Constants.COLOR_IN_CHOICE,gameState.getPlayer(),choice);
				MainGuiController.getInstance().sendMessage(colorMsg);
			}
		}
	}
	public void mousePressedExit(MouseEvent mouseEvent) throws IOException {
		button_exit.setImage(buttonExitPressed);
		previousFXML = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
		previousScene = new Scene(previousFXML);
	}
	public void mouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);

		if (!finished) {
			NetColorPreparation netSetup = new NetColorPreparation(Constants.GENERAL_DISCONNECT);
			MainGuiController.getInstance().sendMessage(netSetup);
		}
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
	private void chooseInYourTurnError() {
		icon_error.toFront();
		moveImage(icon_error, errorWaitTurn, 600, 212, 198, 212, 198, 212, 211, 212, 211, 212, 198, 212, 198,212, 600, 212, 700, 1000, 1000, 500);
		button_next.toFront();
	}
	private void setChoosingPlayer() {
		text_player.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
		text_player.setText(gameState.getActivePlayer());
	}
	/**
	 *
	 * @param reason 0 if a player disconnected during the setup, 1 if the server has crashed
	 */
	private void gameCantContinue(int reason) {
		if(reason == 0){
			fadeImage(icon_errorFatalBG, errorFatalBG);
			slidingImage(icon_errorFatal, errorSomeoneDisconnected, 650, 0, 650, 325, 1250);
			icon_errorFatalBG.toFront();
			icon_errorFatal.toFront();
			button_exit.toFront();
		} else {
			fadeImage(icon_errorFatalBG, errorFatalBG);
			slidingImage(icon_errorFatal, errorFatal, 650, 0, 650, 325, 1250);
			icon_errorFatalBG.toFront();
			icon_errorFatal.toFront();
			button_exit.toFront();
		}
	}

	/* **********************************************
	 *												*
	 *		METHODS CALLED BY MAIN CONTROLLER		*
	 * 												*
	 ************************************************/
	@Override
	public void fatalError() {
		finished = true;
		gameCantContinue(1);
	}
	@Override
	public void deposeMessage(NetObject message) throws IOException {
		switch (message.message) {
			case Constants.COLOR_CHOICES -> {
				button_exit.getScene().setCursor(Cursor.DEFAULT);
				gameState.setColors(((NetColorPreparation)message).getPlayerColorsMap());
				for (Color color : gameState.getColors().values()) {
					if (color.equals(Color.BLUE)) {
						if (choice != null && choice.equals(Color.BLUE)) {
							choice = null;
						}
						button_blue.setImage(buttonColorDisabled);
						blueLocked = true;
					} else if (color.equals(Color.RED)) {
						if (choice != null && choice.equals(Color.RED)) {
							choice = null;
						}
						button_red.setImage(buttonColorDisabled);
						redLocked = true;
					} else if (color.equals(Color.GREEN)) {
						if (choice != null && choice.equals(Color.GREEN)) {
							choice = null;
						}
						button_green.setImage(buttonColorDisabled);
						greenLocked = true;
					}
				}
			}
			case Constants.GENERAL_PHASE_UPDATE -> {
				gameState.advancePhase();
			}
			case Constants.GODS_CHALLENGER -> {
				challengerReceived = true;
			}
			case Constants.TURN_PLAYERTURN -> {
				if (!challengerReceived) {
					gameState.setActivePlayer(((NetColorPreparation)message).player);
					setChoosingPlayer();
				} else {
					gameState.setActivePlayer(((NetDivinityChoice)message).player);
					if (gameState.getPlayer().equals(gameState.getActivePlayer())) {
						// the player is the challenger
						nextFXML = FXMLLoader.load(getClass().getResource("/fxml/choose_gods_first.fxml"));
						nextScene = new Scene(nextFXML);
						currentStage = (Stage) button_next.getScene().getWindow();
						currentStage.setScene(nextScene);
					} else {
						// the player isn't the challenger
						nextFXML = FXMLLoader.load(getClass().getResource("/fxml/loading.fxml"));
						nextScene = new Scene(nextFXML);
						currentStage = (Stage) button_next.getScene().getWindow();
						currentStage.setScene(nextScene);
					}
				}
			}
			case Constants.GENERAL_SETUP_DISCONNECT -> {
				finished = true;
				gameCantContinue(0);
			}
		}
	}
}
