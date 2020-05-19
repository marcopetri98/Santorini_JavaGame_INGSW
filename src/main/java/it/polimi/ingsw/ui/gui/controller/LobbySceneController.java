package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;

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
	private String player1 = "Pippo";
	private String player2 = "Pluto";
	private String player3 = "Paperino";


	private Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	private Image buttonExit = new Image("/img/home_exit_btn.png");

	// triggers for server messages
	private static LobbySceneController currentObject;
	private GameState gameState;

	public static SceneController getInstance() {
		return currentObject;
	}

	public void initialize(){
		currentObject = this;
		MainGuiController.getInstance().setSceneController(this);
		gameState = MainGuiController.getInstance().getGameState();

		numPlayer = gameState.getPlayerNumber();
		if(numPlayer == 2){
			text_3.setDisable(true);
			player_3rd.setVisible(false);
		}
		text_1.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
		text_1.setText(player1);
		text_2.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
		text_2.setText(player2);
		if(player_3rd.isVisible()) {
			text_3.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
			text_3.setText(player3);
		}

	}

	/* **********************************************
	 *												*
	 *			HANDLERS OF USER INTERACTION		*
	 * 												*
	 ************************************************/
	public void mousePressedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExitPressed);
	}
	public void mouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);
	}

	/* **********************************************
	 *												*
	 *		METHODS CALLED BY MAIN CONTROLLER		*
	 * 												*
	 ************************************************/
	@Override
	public void fatalError() {

	}
	@Override
	public void deposeMessage(NetObject message) throws IOException {

	}
}
