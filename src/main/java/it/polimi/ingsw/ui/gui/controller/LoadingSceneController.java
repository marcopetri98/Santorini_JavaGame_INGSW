package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.core.state.GodsPhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.Constants;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoadingSceneController implements SceneController {
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

	// objects used to change scene
	private Parent previousFXML;
	private Parent nextFXML;
	private Scene previousScene;
	private Scene nextScene;
	private Stage currentStage;

	// triggers for server messages
	private GameState gameState;

	public void initialize() {
		MainGuiController.getInstance().setSceneController(this);
		gameState = MainGuiController.getInstance().getGameState();
		if (gameState.getTurn().getPhase() == Phase.GODS && gameState.getTurn().getGodsPhase() == GodsPhase.CHALLENGER_CHOICE) {
			loading_background.setImage(loadingGods);
			icon.setImage(logoGods);
		} else if (gameState.getTurn().getPhase() == Phase.GODS && gameState.getTurn().getGodsPhase() == GodsPhase.STARTER_CHOICE) {
			loading_background.setImage(loadingStarter);
			icon.setImage(logoStarter);
		} else {
			loading_background.setImage(loading);
			icon.setImage(logoLoading);
		}

		PathTransition transition = new PathTransition();
		transition.setNode(icon);
		transition.setDuration(Duration.millis(3500));
		transition.setPath(new Circle(630, 362, 12)); //x,y,radius (pixels)
		transition.setCycleCount(PathTransition.INDEFINITE);
		transition.play();
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

		NetSetup netSetup = new NetSetup(Constants.GENERAL_DISCONNECT);
		MainGuiController.getInstance().sendMessage(netSetup);
		MainGuiController.getInstance().refresh();
		MainGuiController.getInstance().setSceneController(null);
		currentStage = (Stage) button_exit.getScene().getWindow();
		currentStage.setScene(previousScene);
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
			case Constants.GODS_GODS -> {
				gameState.setGods(((NetDivinityChoice)message).getPlayerGodMap());
			}
			case Constants.GENERAL_PHASE_UPDATE -> {
				gameState.advancePhase();
			}
			case Constants.TURN_PLAYERTURN -> {
				gameState.setActivePlayer(((NetDivinityChoice)message).player);
				nextFXML = FXMLLoader.load(getClass().getResource("/fxml/choose_gods.fxml"));
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
