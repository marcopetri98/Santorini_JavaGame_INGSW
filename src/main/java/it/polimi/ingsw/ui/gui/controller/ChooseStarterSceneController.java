package it.polimi.ingsw.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ChooseStarterSceneController {

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

	public void initialize(){
		if(numPlayer == 2){
			text_3.setDisable(true);
			player_3rd.setVisible(false);
			button_select3.setVisible(false);
			button_select3.setDisable(true);
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


	public void mousePressedSelect1(MouseEvent mouseEvent) {
		button_select1.setImage(buttonSelectPressed);
		button_select2.setImage(buttonSelect);
		button_select3.setImage(buttonSelect);
	}

	public void mousePressedSelect2(MouseEvent mouseEvent) {
		button_select1.setImage(buttonSelect);
		button_select2.setImage(buttonSelectPressed);
		button_select3.setImage(buttonSelect);
	}

	public void mousePressedSelect3(MouseEvent mouseEvent) {
		button_select1.setImage(buttonSelect);
		button_select2.setImage(buttonSelect);
		button_select3.setImage(buttonSelectPressed);
	}

	public void mousePressedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNextPressed);
	}

	public void mouseReleasedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNext);
	}

	public void mousePressedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExitPressed);
	}

	public void mouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);
	}
}
