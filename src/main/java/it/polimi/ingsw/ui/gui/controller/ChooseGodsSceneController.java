package it.polimi.ingsw.ui.gui.controller;

import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ChooseGodsSceneController {

	@FXML
	private ImageView button_exit;

	@FXML
	private ImageView card_3;

	@FXML
	private ImageView description;

	@FXML
	private ImageView card_1;

	@FXML
	private ImageView button_next;

	@FXML
	private ImageView card_2;

	@FXML
	private Text text_player;

	Image cardApollo = new Image("/img/gods/card_apollo.png");
	Image cardApolloPressed = new Image("/img/gods/card_apollo_pressed.png");
	Image descriptionApollo = new Image("/img/gods/description_apollo.png");
	Image cardArtemis = new Image("/img/gods/card_artemis.png");
	Image cardArtemisPressed = new Image("/img/gods/card_artemis_pressed.png");
	Image descriptionArtemis = new Image("/img/gods/description_artemis.png");
	Image cardAthena = new Image("/img/gods/card_athena.png");
	Image cardAthenaPressed = new Image("/img/gods/card_athena_pressed.png");
	Image descriptionAthena = new Image("/img/gods/description_athena.png");
	Image cardAtlas = new Image("/img/gods/card_atlas.png");
	Image cardAtlasPressed = new Image("/img/gods/card_atlas_pressed.png");
	Image descriptionAtlas = new Image("/img/gods/description_atlas.png");
	Image cardDemeter = new Image("/img/gods/card_demeter.png");
	Image cardDemeterPressed = new Image("/img/gods/card_demeter_pressed.png");
	Image descriptionDemeter = new Image("/img/gods/description_demeter.png");
	Image cardHephaestus = new Image("/img/gods/card_hephaestus.png");
	Image cardHephaestusPressed = new Image("/img/gods/card_hephaestus_pressed.png");
	Image descriptionHephaestus = new Image("/img/gods/description_hephaestus.png");
	Image cardMinotaur = new Image("/img/gods/card_minotaur.png");
	Image cardMinotaurPressed = new Image("/img/gods/card_minotaur_pressed.png");
	Image descriptionMinotaur = new Image("/img/gods/description_minotaur.png");
	Image cardPan = new Image("/img/gods/card_pan.png");
	Image cardPanPressed = new Image("/img/gods/card_pan_pressed.png");
	Image descriptionPan = new Image("/img/gods/description_pan.png");
	Image cardPrometheus = new Image("/img/gods/card_prometheus.png");
	Image cardPrometheusPressed = new Image("/img/gods/card_prometheus_pressed.png");
	Image descriptionPrometheus = new Image("/img/gods/description_prometheus.png");

	Image buttonNextPressed = new Image("/img/home_next_btn_pressed.png");
	Image buttonNext = new Image("/img/home_next_btn.png");
	Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	Image buttonExit = new Image("/img/home_exit_btn.png");

	private int numPlayer = 2; //TODO:.........
	private String namePlayer = "sdfhjsjdflksdflksdf"; //only as example. //max 21

	private boolean pressed = false;


	public void initialize(){
		description.setImage(null);
		parsingPlayersCard();
		text_player.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
		text_player.setText(namePlayer);
	}

	public void parsingPlayersCard(){
		//TODO...If a card received is equals cardApollo, then card1.setImage(cardApollo)... example
		card_1.setImage(cardApollo);
		card_2.setImage(cardArtemis);
		card_3.setImage(cardAtlas);
	}

	private String setNamePlayer() {
		//TODO:...
		return namePlayer;
	}

	/**
	 * parsing the card I entered with mouseEvent
	 * @param card the card I entered in
	 * @return the correct description card
	 */
	private Image parsingCard(ImageView card){
		if(card.getImage().equals(cardApollo) || card.getImage().equals(cardApolloPressed) ){
			return descriptionApollo;
		} else if(card.getImage().equals(cardArtemis) || card.getImage().equals(cardArtemisPressed)  ){
			return descriptionArtemis;
		} else if(card.getImage().equals(cardAthena) || card.getImage().equals(cardAthenaPressed)){
			return descriptionAthena;
		} else if(card.getImage().equals(cardAtlas) || card.getImage().equals(cardAtlasPressed)){
			return descriptionAtlas;
		} else if(card.getImage().equals(cardDemeter) || card.getImage().equals(cardDemeterPressed)){
			return descriptionDemeter;
		} else if(card.getImage().equals(cardHephaestus) || card.getImage().equals(cardHephaestusPressed)){
			return descriptionHephaestus;
		} else if(card.getImage().equals(cardMinotaur) || card.getImage().equals(cardMinotaurPressed)){
			return descriptionMinotaur;
		} else if(card.getImage().equals(cardPan) || card.getImage().equals(cardPanPressed)){
			return descriptionPan;
		} else if(card.getImage().equals(cardPrometheus) || card.getImage().equals(cardPrometheusPressed)){
			return descriptionPrometheus;
		}
		return null;
	}

	private void pressingCard(MouseEvent mouseEvent) {
		ImageView card = (ImageView) mouseEvent.getTarget();
		if (!pressed) {
			if (card.getImage().equals(cardApollo)) {
				card.setImage(cardApolloPressed);
				pressed = true;
			} else if (card.getImage().equals(cardArtemis)) {
				card.setImage(cardArtemisPressed);
				pressed = true;
			} else if (card.getImage().equals(cardAthena)) {
				card.setImage(cardAthenaPressed);
				pressed = true;
			} else if (card.getImage().equals(cardAtlas)) {
				card.setImage(cardAtlasPressed);
				pressed = true;
			} else if (card.getImage().equals(cardDemeter)) {
				card.setImage(cardDemeterPressed);
				pressed = true;
			} else if (card.getImage().equals(cardHephaestus)) {
				card.setImage(cardHephaestusPressed);
				pressed = true;
			} else if (card.getImage().equals(cardMinotaur)) {
				card.setImage(cardMinotaurPressed);
				pressed = true;
			} else if (card.getImage().equals(cardPan)) {
				card.setImage(cardPanPressed);
				pressed = true;
			} else if (card.getImage().equals(cardPrometheus)) {
				card.setImage(cardPrometheusPressed);
				pressed = true;
			}
		} else {
			if (card.getImage().equals(cardApolloPressed)) {
				card.setImage(cardApollo);
				pressed = false;
			} else if (card.getImage().equals(cardArtemisPressed)) {
				card.setImage(cardArtemis);
				pressed = false;
			} else if (card.getImage().equals(cardAthenaPressed)) {
				card.setImage(cardAthena);
				pressed = false;
			} else if (card.getImage().equals(cardAtlasPressed)) {
				card.setImage(cardAtlas);
				pressed = false;
			} else if (card.getImage().equals(cardDemeterPressed)) {
				card.setImage(cardDemeter);
				pressed = false;
			} else if (card.getImage().equals(cardHephaestusPressed)) {
				card.setImage(cardHephaestus);
				pressed = false;
			} else if (card.getImage().equals(cardMinotaurPressed)) {
				card.setImage(cardMinotaur);
				pressed = false;
			} else if (card.getImage().equals(cardPanPressed)) {
				card.setImage(cardPan);
				pressed = false;
			} else if (card.getImage().equals(cardPrometheusPressed)) {
				card.setImage(cardPrometheus);
				pressed = false;
			}
		}
	}

	private void mouseEnterCard(ImageView imageView, Image image){
		imageView.setImage(image);
		Line line = new Line();
		line.setStartX(182);
		line.setStartY(500);
		line.setEndX(182);
		line.setEndY(174);
		PathTransition transition = new PathTransition();
		transition.setNode(imageView);
		transition.setDuration(Duration.millis(450));
		transition.setPath(line);
		transition.setCycleCount(1);
		transition.play();
	}

	private void mouseExitCard(ImageView imageView){
		Line line = new Line();
		line.setStartX(182);
		line.setStartY(174);
		line.setEndX(182);
		line.setEndY(500);
		PathTransition transition = new PathTransition();
		transition.setNode(imageView);
		transition.setDuration(Duration.millis(450));
		transition.setPath(line);
		transition.setCycleCount(1);
		transition.play();
	}


	public void mouseEnteredCard1(MouseEvent mouseEvent) {
		mouseEnterCard(description, parsingCard(card_1));
	}

	public void mouseExitedCard1(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	public void mouseEnteredCard2(MouseEvent mouseEvent) {
		mouseEnterCard(description, parsingCard(card_2));
	}

	public void mouseExitedCard2(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	public void mouseEnteredCard3(MouseEvent mouseEvent) {
		mouseEnterCard(description, parsingCard(card_3));
	}

	public void mouseExitedCard3(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	public void mousePressedCard1(MouseEvent mouseEvent) {
		pressingCard(mouseEvent);
	}

	public void mousePressedCard2(MouseEvent mouseEvent) {
		pressingCard(mouseEvent);
	}

	public void mousePressedCard3(MouseEvent mouseEvent) {
		pressingCard(mouseEvent);
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
