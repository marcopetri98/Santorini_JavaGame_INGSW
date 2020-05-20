package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.NetGaming;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Constants;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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

	Image buttonNextPressed = new Image("/img/home_next_btn_pressed.png");
	Image buttonNext = new Image("/img/home_next_btn.png");
	Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	Image buttonExit = new Image("/img/home_exit_btn.png");
	Image button2 = new Image("/img/num2_btn.png");
	Image button2Pressed = new Image("/img/num2_btn_pressed.png");
	Image button3 = new Image("/img/num3_btn.png");
	Image button3Pressed = new Image("/img/num3_btn_pressed.png");

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
	}

	/* **********************************************
	 *												*
	 *			HANDLERS OF USER INTERACTION		*
	 * 												*
	 ************************************************/
	// FIXME: pressing the button can be collapsed into a unique function
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
		// TODO: error when user tries to go forward with next without having pressed buttons
	}

	/* **********************************************
	 *												*
	 *		METHODS CALLED BY MAIN CONTROLLER		*
	 * 												*
	 ************************************************/
	@Override
	public void fatalError() {
		// TODO: what to do here?
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
