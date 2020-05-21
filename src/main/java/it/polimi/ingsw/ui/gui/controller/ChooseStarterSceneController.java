package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.*;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class ChooseStarterSceneController implements SceneController {
	@FXML
	private ImageView button_exit;
	@FXML
	private ImageView button_select3;
	@FXML
	private ImageView button_select2;
	@FXML
	private ImageView player_3rd;
	@FXML
	private ImageView button_select1;
	@FXML
	private ImageView button_next;
	@FXML
	private Text text_1;
	@FXML
	private Text text_2;
	@FXML
	private Text text_3;

	Image buttonNextPressed = new Image("/img/home_next_btn_pressed.png");
	Image buttonNext = new Image("/img/home_next_btn.png");
	Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	Image buttonExit = new Image("/img/home_exit_btn.png");

	Image buttonSelect = new Image("/img/choose_starter_optionBtn.png");
	Image buttonSelectPressed = new Image("/img/choose_starter_selectedBtn.png");

	private int numPlayer = 2; //TODO:.........
	private String player1 = "Pippo";
	private String player2 = "Pluto";
	private String player3 = "Paperino";

	// objects used to change scene
	private Parent previousFXML;
	private Parent nextFXML;
	private Scene previousScene;
	private Scene nextScene;
	private Stage currentStage;

	// triggers for server messages
	private GameState gameState;
	private int selectedPlayer = -1;

	public void initialize(){
		MainGuiController.getInstance().setSceneController(this);
		gameState = MainGuiController.getInstance().getGameState();

		if(gameState.getPlayerNumber() == 2) {
			text_3.setDisable(true);
			player_3rd.setVisible(false);
			button_select3.setVisible(false);
			button_select3.setDisable(true);
		}
		text_1.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
		text_1.setText(gameState.getPlayers().get(0));
		text_2.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
		text_2.setText(gameState.getPlayers().get(1));
		if (player_3rd.isVisible()) {
			text_3.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
			text_3.setText(gameState.getPlayers().get(2));
		} else {
			((AnchorPane)button_exit.getParent()).getChildren().remove(text_3);
			((AnchorPane)button_exit.getParent()).getChildren().remove(player_3rd);
			((AnchorPane)button_exit.getParent()).getChildren().remove(button_select3);
		}
	}

	/* **********************************************
	 *												*
	 *			HANDLERS OF USER INTERACTION		*
	 * 												*
	 ************************************************/
	// TODO: maybe these 3 can be collapsed only in one?
	public void mousePressedSelect1(MouseEvent mouseEvent) {
		button_select1.setImage(buttonSelectPressed);
		button_select2.setImage(buttonSelect);
		button_select3.setImage(buttonSelect);
		selectedPlayer = 0;
	}
	public void mousePressedSelect2(MouseEvent mouseEvent) {
		button_select1.setImage(buttonSelect);
		button_select2.setImage(buttonSelectPressed);
		button_select3.setImage(buttonSelect);
		selectedPlayer = 1;
	}
	public void mousePressedSelect3(MouseEvent mouseEvent) {
		button_select1.setImage(buttonSelect);
		button_select2.setImage(buttonSelect);
		button_select3.setImage(buttonSelectPressed);
		selectedPlayer = 2;
	}
	public void mousePressedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNextPressed);
	}
	public void mouseReleasedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNext);

		if (selectedPlayer == -1) {
			wrongPlayerSelection();
		} else {
			// the challenger sends to the server the information about the starter player
			((ImageView)mouseEvent.getTarget()).getScene().setCursor(Cursor.WAIT);
			NetDivinityChoice godMessage = new NetDivinityChoice(Constants.GODS_IN_START_PLAYER,gameState.getPlayer(),gameState.getPlayers().get(selectedPlayer),true);
			MainGuiController.getInstance().sendMessage(godMessage);
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
	public void wrongPlayerSelection() {
		// TODO: prompt to the user that he must choose a starter before clicking next
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
			case Constants.GODS_STARTER -> {
				gameState.setStarter(((NetDivinityChoice)message).getPlayer());
			}
			case Constants.GENERAL_PHASE_UPDATE -> {
				gameState.advancePhase();
			}
			case Constants.TURN_PLAYERTURN -> {
				gameState.setActivePlayer(((NetGameSetup)message).player);

				nextFXML = FXMLLoader.load(getClass().getResource("/fxml/map.fxml"));
				nextScene = new Scene(nextFXML);
				currentStage = (Stage) button_exit.getScene().getWindow();
				currentStage.setScene(nextScene);
			}
			case Constants.GENERAL_SETUP_DISCONNECT -> {
				// TODO: implement the disconnection shutdown after someone quit the game
			}
		}
	}
}
