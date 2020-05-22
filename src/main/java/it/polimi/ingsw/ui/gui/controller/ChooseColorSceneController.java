package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.Constants;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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

	// objects used to change scene
	private Parent previousFXML;
	private Parent nextFXML;
	private Scene previousScene;
	private Scene nextScene;
	private Stage currentStage;
	private boolean redLocked = false, greenLocked = false, blueLocked = false;
	private boolean challengerReceived = false;
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
	public void mouseReleasedColor(MouseEvent mouseEvent) {
		// TODO: il the release event necessary?
	}
	public void mousePressedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNextPressed);
	}
	public void mouseReleasedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNext);

		if (choice != null) {
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

		NetColorPreparation netSetup = new NetColorPreparation(Constants.GENERAL_DISCONNECT);
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
	private void chooseInYourTurnError() {
		// TODO: implement graphic effect to say the user he can choose a color only in its turn
	}
	private void setChoosingPlayer() {
		text_player.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
		text_player.setText(gameState.getActivePlayer());
	}

	/* **********************************************
	 *												*
	 *		METHODS CALLED BY MAIN CONTROLLER		*
	 * 												*
	 ************************************************/
	@Override
	public void fatalError() {
		// TODO: server has crashed, show it to the client
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
				button_next.getScene().setCursor(Cursor.WAIT);
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
				// TODO: implement the disconnection shutdown after someone quit the game
			}
		}
	}
}
