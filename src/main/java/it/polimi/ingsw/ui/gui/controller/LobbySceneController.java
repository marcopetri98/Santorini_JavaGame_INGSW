package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetLobbyPreparation;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Constants;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class LobbySceneController implements SceneController {
	@FXML
	private ImageView button_exit;
	@FXML
	private ImageView player_3rd;
	@FXML
	private Text text_1;
	@FXML
	private Text text_2;
	@FXML
	private Text text_3;

	private int numPlayer;


	private Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	private Image buttonExit = new Image("/img/home_exit_btn.png");

	// objects used to change scene
	private Parent previousFXML;
	private Parent nextFXML;
	private Scene previousScene;
	private Scene nextScene;
	private Stage currentStage;

	// triggers for server messages
	private GameState gameState;

	public void initialize(){
		MainGuiController.getInstance().setSceneController(this);
		gameState = MainGuiController.getInstance().getGameState();

		numPlayer = gameState.getPlayerNumber();
		if(numPlayer == 2){
			text_3.setDisable(true);
			player_3rd.setVisible(false);
		}
		setupNames(false);
	}

	/* **********************************************
	 *												*
	 *			HANDLERS OF USER INTERACTION		*
	 * 												*
	 ************************************************/
	public void mousePressedExit(MouseEvent mouseEvent) throws IOException {
		button_exit.setImage(buttonExitPressed);
		previousFXML = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
		previousScene = new Scene(previousFXML);
	}
	public void mouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);

		NetLobbyPreparation netSetup = new NetLobbyPreparation(Constants.GENERAL_DISCONNECT);
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
	private void setupNames(boolean finished) {
		List<String> playerNames = gameState.getPlayers();

		if (!finished) {
			text_1.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
			text_1.setText(playerNames.size() > 0 ? playerNames.get(0) : gameState.getPlayer());
			text_2.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
			text_2.setText(playerNames.size() > 1 ? playerNames.get(1) : "waiting for players...");
			if (player_3rd.isVisible()) {
				text_3.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
				text_3.setText(playerNames.size() > 2 ? playerNames.get(2) : "waiting for players...");
			}
		} else {
			text_1.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
			text_1.setText("1. "+playerNames.get(0));
			text_2.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
			text_2.setText("2. "+playerNames.get(1));
			if (player_3rd.isVisible()) {
				text_3.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
				text_3.setText("3. "+playerNames.get(2));
			}
		}
	}
	/**
	 *
	 */
	private void gameCantContinue() {
		// TODO: print to the player that the server has crashed, in fact in lobby players can disconnect without causing the end of the lobby, game isn't started already
		// 		 now a player can only quit and cannot do anything
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
			case Constants.LOBBY_INFO -> {
				// updates player names
				gameState.setPlayers(((NetLobbyPreparation)message).getPlayersList());
				setupNames(false);
			}
			case Constants.LOBBY_TURN -> {
				// update the lobby with player order
				gameState.setPlayers(((NetLobbyPreparation)message).getPlayersList());
				setupNames(true);
				// eliminate exit button because the game is starting
				((AnchorPane) button_exit.getParent()).getChildren().remove(button_exit);
			}
			case Constants.GENERAL_PHASE_UPDATE -> {
				gameState.advancePhase();
			}
			case Constants.TURN_PLAYERTURN -> {
				gameState.setActivePlayer(((NetColorPreparation)message).player);
				// sets the next scene
				nextFXML = FXMLLoader.load(getClass().getResource("/fxml/choose_colorV2.fxml"));
				nextScene = new Scene(nextFXML);
				PauseTransition waitReadPlayers = new PauseTransition(Duration.seconds(5.0));
				waitReadPlayers.setOnFinished((event) -> {
					currentStage = (Stage) text_1.getScene().getWindow();
					currentStage.setScene(nextScene);
				});
				waitReadPlayers.play();
			}
		}
	}
}
