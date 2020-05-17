package it.polimi.ingsw.ui.gui.controller;

import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class ChooseGodsFirstSceneController {


	@FXML
	private ImageView button_exit;

	@FXML
	private ImageView card_apollo;

	@FXML
	private ImageView card_artemis;

	@FXML
	private ImageView card_minotaur;

	@FXML
	private ImageView card_pan;

	@FXML
	private ImageView card_prometheus;

	@FXML
	private ImageView card_demeter;

	@FXML
	private ImageView card_hephaestus;

	@FXML
	private ImageView button_next;

	@FXML
	private ImageView description;

	@FXML
	private ImageView card_athena;

	@FXML
	private ImageView card_atlas;

	private boolean pressedApollo = false;
	private boolean pressedArtemis = false;
	private boolean pressedAthena = false;
	private boolean pressedAtlas = false;
	private boolean pressedDemeter = false;
	private boolean pressedHephaestus = false;
	private boolean pressedMinotaur = false;
	private boolean pressedPan = false;
	private boolean pressedPrometheus = false;

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



	public void initialize(){
		description.setImage(null);
	}


	public void mouseEnteredApollo(MouseEvent mouseEvent) {
		mouseEnterCard(description, descriptionApollo);
	}

	public void mouseExitedApollo(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	public void mousePressedApollo(MouseEvent mouseEvent) {
		pressingCard(pressedApollo, card_apollo, cardApolloPressed, cardApollo);
	}

	public void mouseReleasedApollo(MouseEvent mouseEvent) {

	}

	public void mouseEnteredArtemis(MouseEvent mouseEvent) {
		mouseEnterCard(description, descriptionArtemis);
	}

	public void mouseExitedArtemis(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	public void mousePressedArtemis(MouseEvent mouseEvent) {
		pressingCard(pressedArtemis, card_artemis, cardArtemisPressed, cardArtemis);
	}

	public void mouseReleasedArtemis(MouseEvent mouseEvent) {
	}

	public void mouseEnteredAthena(MouseEvent mouseEvent) {
		mouseEnterCard(description, descriptionAthena);
	}

	public void mouseExitedAthena(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	public void mousePressedAthena(MouseEvent mouseEvent) {
		pressingCard(pressedAthena, card_athena, cardAthenaPressed, cardAthena);
	}

	public void mouseReleasedAthena(MouseEvent mouseEvent) {
	}

	public void mouseEnteredAtlas(MouseEvent mouseEvent) {
		mouseEnterCard(description, descriptionAtlas);
	}

	public void mouseExitedAtlas(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	public void mousePressedAtlas(MouseEvent mouseEvent) {
		pressingCard(pressedAtlas, card_atlas, cardAtlasPressed, cardAtlas);
	}

	public void mouseReleasedAtlas(MouseEvent mouseEvent) {
	}

	public void mouseEnteredDemeter(MouseEvent mouseEvent) {
		mouseEnterCard(description, descriptionDemeter);
	}

	public void mouseExitedDemeter(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	public void mousePressedDemeter(MouseEvent mouseEvent) {
		pressingCard(pressedDemeter, card_demeter, cardDemeterPressed, cardDemeter);
	}

	public void mouseReleasedDemeter(MouseEvent mouseEvent) {
	}

	public void mouseEnteredHephaestus(MouseEvent mouseEvent) {
		mouseEnterCard(description, descriptionHephaestus);
	}

	public void mouseExitedHephaestus(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	public void mousePressedHephaestus(MouseEvent mouseEvent) {
		pressingCard(pressedHephaestus, card_hephaestus, cardHephaestusPressed, cardHephaestus);
	}

	public void mouseReleasedHephaestus(MouseEvent mouseEvent) {
	}

	public void mouseEnteredMinotaur(MouseEvent mouseEvent) {
		mouseEnterCard(description, descriptionMinotaur);
	}

	public void mouseExitedMinotaur(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	public void mousePressedMinotaur(MouseEvent mouseEvent) {
		pressingCard(pressedMinotaur, card_minotaur, cardMinotaurPressed, cardMinotaur);
	}

	public void mouseReleasedMinotaur(MouseEvent mouseEvent) {
	}

	public void mouseEnteredPan(MouseEvent mouseEvent) {
		mouseEnterCard(description, descriptionPan);
	}

	public void mouseExitedPan(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	public void mousePressedPan(MouseEvent mouseEvent) {
		pressingCard(pressedPan, card_pan, cardPanPressed, cardPan);
	}

	public void mouseReleasedPan(MouseEvent mouseEvent) {
	}

	public void mouseEnteredPrometheus(MouseEvent mouseEvent) {
		mouseEnterCard(description, descriptionPrometheus);
	}

	public void mouseExitedPrometheus(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	public void mousePressedPrometheus(MouseEvent mouseEvent) {
		pressingCard(pressedPrometheus, card_prometheus, cardPrometheusPressed, cardPrometheus);
	}

	public void mouseReleasedPrometheus(MouseEvent mouseEvent) {
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

	private void pressingCard(boolean pressed, ImageView imageView, Image imagePressed, Image image){
		if(!pressed) {
			imageView.setImage(imagePressed);
			if(imagePressed.equals(cardApolloPressed)){
				pressedApollo = true;
			} else if(imagePressed.equals(cardArtemisPressed)){
				pressedArtemis = true;
			} else if(imagePressed.equals(cardAthenaPressed)){
				pressedAthena = true;
			} else if(imagePressed.equals(cardAtlasPressed)){
				pressedAtlas = true;
			} else if(imagePressed.equals(cardDemeterPressed)){
				pressedDemeter = true;
			} else if(imagePressed.equals(cardHephaestusPressed)){
				pressedHephaestus = true;
			} else if(imagePressed.equals(cardMinotaurPressed)){
				pressedMinotaur = true;
			} else if(imagePressed.equals(cardPanPressed)){
				pressedPan = true;
			} else if(imagePressed.equals(cardPrometheusPressed)){
				pressedPrometheus = true;
			}
		} else{
			imageView.setImage(image);
			if(image.equals(cardApollo)){
				pressedApollo = false;
			} else if(image.equals(cardArtemis)){
				pressedArtemis = false;
			} else if(image.equals(cardAthena)){
				pressedAthena = false;
			} else if(image.equals(cardAtlas)){
				pressedAtlas = false;
			} else if(image.equals(cardDemeter)){
				pressedDemeter = false;
			} else if(image.equals(cardHephaestus)){
				pressedHephaestus = false;
			} else if(image.equals(cardMinotaur)){
				pressedMinotaur = false;
			} else if(image.equals(cardPan)){
				pressedPan = false;
			} else if(image.equals(cardPrometheus)){
				pressedPrometheus = false;
			}
		}
	}
}
