package it.polimi.ingsw.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LobbySceneController {

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

	private int numPlayer = 2; //TODO:.........
	private String player1 = "Pippo";
	private String player2 = "Pluto";
	private String player3 = "Paperino";


	Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	Image buttonExit = new Image("/img/home_exit_btn.png");

	public void initialize(){
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

	public void mousePressedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExitPressed);
	}

	public void mouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);
	}
}
