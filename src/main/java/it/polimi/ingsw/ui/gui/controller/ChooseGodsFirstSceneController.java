package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Constants;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class implements the choosing color scene of the GUI only for the challenger.
 */
public class ChooseGodsFirstSceneController implements SceneController {
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
	@FXML
	private ImageView icon_errorFatalBG;
	@FXML
	private ImageView icon_errorFatal;
	@FXML
	private ImageView icon_error;

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
	Image errorFatalBG = new Image("/img/errorFatal_background.png");
	Image errorFatal = new Image("/img/error_fatal.png");
	Image errorSomeoneDisconnected = new Image("/img/message_someoneDisconnected.png");
	Image errorNumberCards = new Image("/img/error_numberCards.png");

	// objects used to change scene
	private Parent previousFXML;
	private Parent nextFXML;
	private Scene previousScene;
	private Scene nextScene;
	private Stage currentStage;
	private boolean finished = false;

	// triggers for server messages
	private GameState gameState;
	private boolean pressedApollo = false;
	private boolean pressedArtemis = false;
	private boolean pressedAthena = false;
	private boolean pressedAtlas = false;
	private boolean pressedDemeter = false;
	private boolean pressedHephaestus = false;
	private boolean pressedMinotaur = false;
	private boolean pressedPan = false;
	private boolean pressedPrometheus = false;
	private int cardPressed = 0;
	private Set<String> godNamesSelected = new TreeSet<>();

	public void initialize(){
		MainGuiController.getInstance().setSceneController(this);
		gameState = MainGuiController.getInstance().getGameState();
		description.setImage(null);


		card_apollo.setImage(cardApollo);
		card_artemis.setImage(cardArtemis);
		card_athena.setImage(cardAthena);
		card_atlas.setImage(cardAtlas);
		card_demeter.setImage(cardDemeter);
		card_hephaestus.setImage(cardHephaestus);
		card_minotaur.setImage(cardMinotaur);
		card_pan.setImage(cardPan);
		card_prometheus.setImage(cardPrometheus);

		icon_errorFatal.toBack();
		icon_errorFatalBG.toBack();
		icon_errorFatal.setImage(null);
		icon_error.toBack();
		icon_error.setImage(null);
	}

	/**
	 * This method creates a fade transition of an image.
	 * @param imageView the ImageView that has to be faded.
	 * @param image the Image to set in the ImageView.
	 */
	private void fadeImage(ImageView imageView, Image image){
		imageView.setImage(image);
		FadeTransition ft = new FadeTransition(Duration.millis(2500), imageView);
		ft.setFromValue(0);
		ft.setToValue(1);
		ft.setCycleCount(1);
		ft.play();
	}

	/**
	 * This method creates a slide transition of an image.
	 * @param imageView the ImageView that has to be slided.
	 * @param image the Image to set in the ImageView.
	 * @param x1 initial x coordinate.
	 * @param y1 initial y coordinate.
	 * @param x2 final x coordinate.
	 * @param y2 final y coordinate.
	 * @param duration duration of the transtion.
	 */
	private void slidingImage(ImageView imageView, Image image, int x1, int y1, int x2, int y2, int duration) {
		imageView.setImage(image);
		Line line = new Line();
		line.setStartX(x1);
		line.setStartY(y1);
		line.setEndX(x2);
		line.setEndY(y2);
		PathTransition transition = new PathTransition();
		transition.setNode(imageView);
		transition.setDuration(Duration.millis(duration));
		transition.setPath(line);
		transition.setCycleCount(1);
		transition.play();
	}

	/**
	 * This method is a combination of slide transition (4 transition: to left, then to right, then to left, then to right) to simulate a cloud fluctuation
	 * @param imageView the ImageView that has to be slided.
	 * @param image the Image to set in the ImageView.
	 * @param x1_1 first transition initial x coordinate.
	 * @param y1_1 first transition initial y coordinate.
	 * @param x2_1 first transition final x coordinate.
	 * @param y2_1 first transition final y coordinate.
	 * @param x1_2 second transition initial x coordinate.
	 * @param y1_2 second transition initial x coordinate.
	 * @param x2_2 second transition final x coordinate.
	 * @param y2_2 second transition final x coordinate.
	 * @param x1_3 third transition initial x coordinate.
	 * @param y1_3 third transition initial x coordinate.
	 * @param x2_3 third transition final x coordinate.
	 * @param y2_3 third transition final x coordinate.
	 * @param x1_4 fourth transition initial x coordinate.
	 * @param y1_4 fourth transition initial x coordinate.
	 * @param x2_4 fourth transition final x coordinate.
	 * @param y2_4 fourth transition final x coordinate.
	 * @param duration1 duration of the first transition.
	 * @param duration2 duration of the first transition.
	 * @param duration3 duration of the first transition.
	 * @param duration4 duration of the first transition.
	 */
	private void moveImage(ImageView imageView, Image image, int x1_1, int y1_1, int x2_1, int y2_1, int x1_2, int y1_2, int x2_2, int y2_2, int x1_3, int y1_3, int x2_3, int y2_3, int x1_4, int y1_4, int x2_4, int y2_4, int duration1, int duration2, int duration3, int duration4) {
		imageView.setImage(image);

		Line line1 = new Line();
		Line line2 = new Line();
		Line line3 = new Line();
		Line line4 = new Line();

		SequentialTransition sequential = new SequentialTransition(setLine(imageView, line1, x1_1, y1_1, x2_1, y2_1, duration1), setLine(imageView, line2, x1_2, y1_2, x2_2, y2_2, duration2), setLine(imageView, line3, x1_3, y1_3, x2_3, y2_3, duration3), setLine(imageView, line4, x1_4, y1_4, x2_4, y2_4, duration4));
		sequential.play();
	}

	/**
	 * This method creates the line path for a slide transition.
	 * @param imageView the ImageView that has to be slided.
	 * @param line the default line path.
	 * @param x1 initial x coordinate.
	 * @param y1 initial y coordinate.
	 * @param x2 final x coordinate.
	 * @param y2 final y coordinate.
	 * @param duration duration of the transtion.
	 * @return the line transition.
	 */
	private Transition setLine(ImageView imageView, Line line, int x1, int y1, int x2, int y2, int duration){
		line.setStartX(x1);
		line.setStartY(y1);
		line.setEndX(x2);
		line.setEndY(y2);
		PathTransition transition = new PathTransition();
		transition.setNode(imageView);
		transition.setDuration(Duration.millis(duration));
		transition.setPath(line);
		transition.setCycleCount(1);

		return transition;
	}

	/* **********************************************
	 *												*
	 *			HANDLERS OF USER INTERACTION		*
	 * 												*
	 ************************************************/

	/**
	 * This method handles the mouse entry on a god card, sliding up the description of that god.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mouseEnteredCard(MouseEvent mouseEvent) {
		ImageView cardEntered = (ImageView) mouseEvent.getTarget();

		if (cardEntered.getImage().equals(cardApollo) || cardEntered.getImage().equals(cardApolloPressed)) {
			mouseEnterCard(description,descriptionApollo);
		} else if (cardEntered.getImage().equals(cardArtemis) || cardEntered.getImage().equals(cardArtemisPressed)) {
			mouseEnterCard(description,descriptionArtemis);
		} else if (cardEntered.getImage().equals(cardAthena) || cardEntered.getImage().equals(cardAthenaPressed)) {
			mouseEnterCard(description,descriptionAthena);
		} else if (cardEntered.getImage().equals(cardAtlas) || cardEntered.getImage().equals(cardAtlasPressed)) {
			mouseEnterCard(description,descriptionAtlas);
		} else if (cardEntered.getImage().equals(cardDemeter) || cardEntered.getImage().equals(cardDemeterPressed)) {
			mouseEnterCard(description,descriptionDemeter);
		} else if (cardEntered.getImage().equals(cardHephaestus) || cardEntered.getImage().equals(cardHephaestusPressed)) {
			mouseEnterCard(description,descriptionHephaestus);
		} else if (cardEntered.getImage().equals(cardMinotaur) || cardEntered.getImage().equals(cardMinotaurPressed)) {
			mouseEnterCard(description,descriptionMinotaur);
		} else if (cardEntered.getImage().equals(cardPan) || cardEntered.getImage().equals(cardPanPressed)) {
			mouseEnterCard(description,descriptionPan);
		} else if (cardEntered.getImage().equals(cardPrometheus) || cardEntered.getImage().equals(cardPrometheusPressed)) {
			mouseEnterCard(description,descriptionPrometheus);
		}
	}

	/**
	 * This method handles the mouse entry on a god card, sliding down the description of that god.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mouseExitedCard(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}

	/**
	 * This method handles the mouse click on a god card, choosing that card.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedCard(MouseEvent mouseEvent) {
		ImageView cardEntered = (ImageView) mouseEvent.getTarget();

		if (cardEntered.getImage().equals(cardApollo) || cardEntered.getImage().equals(cardApolloPressed)) {
			pressingCard(pressedApollo, card_apollo, cardApolloPressed, cardApollo);
		} else if (cardEntered.getImage().equals(cardArtemis) || cardEntered.getImage().equals(cardArtemisPressed)) {
			pressingCard(pressedArtemis, card_artemis, cardArtemisPressed, cardArtemis);
		} else if (cardEntered.getImage().equals(cardAthena) || cardEntered.getImage().equals(cardAthenaPressed)) {
			pressingCard(pressedAthena, card_athena, cardAthenaPressed, cardAthena);
		} else if (cardEntered.getImage().equals(cardAtlas) || cardEntered.getImage().equals(cardAtlasPressed)) {
			pressingCard(pressedAtlas, card_atlas, cardAtlasPressed, cardAtlas);
		} else if (cardEntered.getImage().equals(cardDemeter) || cardEntered.getImage().equals(cardDemeterPressed)) {
			pressingCard(pressedDemeter, card_demeter, cardDemeterPressed, cardDemeter);
		} else if (cardEntered.getImage().equals(cardHephaestus) || cardEntered.getImage().equals(cardHephaestusPressed)) {
			pressingCard(pressedHephaestus, card_hephaestus, cardHephaestusPressed, cardHephaestus);
		} else if (cardEntered.getImage().equals(cardMinotaur) || cardEntered.getImage().equals(cardMinotaurPressed)) {
			pressingCard(pressedMinotaur, card_minotaur, cardMinotaurPressed, cardMinotaur);
		} else if (cardEntered.getImage().equals(cardPan) || cardEntered.getImage().equals(cardPanPressed)) {
			pressingCard(pressedPan, card_pan, cardPanPressed, cardPan);
		} else if (cardEntered.getImage().equals(cardPrometheus) || cardEntered.getImage().equals(cardPrometheusPressed)) {
			pressingCard(pressedPrometheus, card_prometheus, cardPrometheusPressed, cardPrometheus);
		}
	}

	/**
	 * This method creates the sliding up transition of the god card description
	 * @param imageView the ImageView that has to be faded.
	 * @param image the Image to set in the ImageView.
	 */
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

	/**
	 * This method creates the sliding down transition of the god card description
	 * @param imageView the ImageView that has to be faded.
	 */
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

	/**
	 * This method handles the mous click on a god card, making it pressed (selected) or unpressed (unselected).
	 * @param pressed boolean parameter to check if the card is pressed or not.
	 * @param imageView the ImageView interested.
	 * @param imagePressed the image pressed of a god card
	 * @param image the standard image (unpressed) of a god card.
	 */
	private void pressingCard(boolean pressed, ImageView imageView, Image imagePressed, Image image){
		if(!pressed) {
			if (cardPressed < gameState.getPlayerNumber()) {
				imageView.setImage(imagePressed);
				if (imagePressed.equals(cardApolloPressed)) {
					godNamesSelected.add(Constants.APOLLO);
					pressedApollo = true;
					cardPressed++;
				} else if (imagePressed.equals(cardArtemisPressed)) {
					godNamesSelected.add(Constants.ARTEMIS);
					pressedArtemis = true;
					cardPressed++;
				} else if (imagePressed.equals(cardAthenaPressed)) {
					godNamesSelected.add(Constants.ATHENA);
					pressedAthena = true;
					cardPressed++;
				} else if (imagePressed.equals(cardAtlasPressed)) {
					godNamesSelected.add(Constants.ATLAS);
					pressedAtlas = true;
					cardPressed++;
				} else if (imagePressed.equals(cardDemeterPressed)) {
					godNamesSelected.add(Constants.DEMETER);
					pressedDemeter = true;
					cardPressed++;
				} else if (imagePressed.equals(cardHephaestusPressed)) {
					godNamesSelected.add(Constants.HEPHAESTUS);
					pressedHephaestus = true;
					cardPressed++;
				} else if (imagePressed.equals(cardMinotaurPressed)) {
					godNamesSelected.add(Constants.MINOTAUR);
					pressedMinotaur = true;
					cardPressed++;
				} else if (imagePressed.equals(cardPanPressed)) {
					godNamesSelected.add(Constants.PAN);
					pressedPan = true;
					cardPressed++;
				} else if (imagePressed.equals(cardPrometheusPressed)) {
					godNamesSelected.add(Constants.PROMETHEUS);
					pressedPrometheus = true;
					cardPressed++;
				}
			}
		} else {
			imageView.setImage(image);
			if(image.equals(cardApollo)){
				godNamesSelected.remove(Constants.APOLLO);
				pressedApollo = false;
				cardPressed--;
			} else if(image.equals(cardArtemis)){
				godNamesSelected.remove(Constants.ARTEMIS);
				pressedArtemis = false;
				cardPressed--;
			} else if(image.equals(cardAthena)){
				godNamesSelected.remove(Constants.ATHENA);
				pressedAthena = false;
				cardPressed--;
			} else if(image.equals(cardAtlas)){
				godNamesSelected.remove(Constants.ATLAS);
				pressedAtlas = false;
				cardPressed--;
			} else if(image.equals(cardDemeter)){
				godNamesSelected.remove(Constants.DEMETER);
				pressedDemeter = false;
				cardPressed--;
			} else if(image.equals(cardHephaestus)){
				godNamesSelected.remove(Constants.HEPHAESTUS);
				pressedHephaestus = false;
				cardPressed--;
			} else if(image.equals(cardMinotaur)){
				godNamesSelected.remove(Constants.MINOTAUR);
				pressedMinotaur = false;
				cardPressed--;
			} else if(image.equals(cardPan)){
				godNamesSelected.remove(Constants.PAN);
				pressedPan = false;
				cardPressed--;
			} else if(image.equals(cardPrometheus)){
				godNamesSelected.remove(Constants.PROMETHEUS);
				pressedPrometheus = false;
				cardPressed--;
			}
		}
	}

	/**
	 * This method handles the mouse click on a next button, making it pressed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNextPressed);
	}

	/**
	 * This method handles the mouse release on a next button: making it unpressed and changing the scene.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mouseReleasedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNext);

		if (!finished) {
			if (cardPressed != gameState.getPlayerNumber()) {
				wrongSelectOfGods();
			} else {
				((ImageView) mouseEvent.getTarget()).getScene().setCursor(Cursor.WAIT);
				NetDivinityChoice godsMessage = new NetDivinityChoice(Constants.GODS_IN_GAME_GODS, gameState.getPlayer(), new ArrayList<>(godNamesSelected));
				MainGuiController.getInstance().sendMessage(godsMessage);
			}
		}
	}

	/**
	 * This method handles the mouse click on a exit button: making it pressed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 * @throws IOException if the fxml file can't be loaded
	 */
	public void mousePressedExit(MouseEvent mouseEvent) throws IOException {
		button_exit.setImage(buttonExitPressed);
		previousFXML = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
		previousScene = new Scene(previousFXML);
	}

	/**
	 * This method handles the mouse release on a exit button: making it unpressed and returning to the home scene.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);

		if (!finished) {
			NetDivinityChoice netSetup = new NetDivinityChoice(Constants.GENERAL_DISCONNECT);
			MainGuiController.getInstance().sendMessage(netSetup);
		}
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

	/**
	 * This method displays a pop up message which notify the player to choose the correct number of god cards.
	 */
	public void wrongSelectOfGods() {
		icon_error.toFront();
		moveImage(icon_error, errorNumberCards, 600, 212, 198, 212, 198, 212, 211, 212, 211, 212, 198, 212, 198,212, 600, 212, 700, 1000, 1000, 500);
		button_next.toFront();
	}

	/**
	 * This method displays a pop up message which notify the player according to notify parameter.
	 * @param reason 0 if a player disconnected during the setup, 1 if the server has crashed
	 */
	private void gameCantContinue(int reason) {
		if(reason == 0){
			fadeImage(icon_errorFatalBG, errorFatalBG);
			slidingImage(icon_errorFatal, errorSomeoneDisconnected, 650, 0, 650, 325, 1250);
			icon_errorFatalBG.toFront();
			icon_errorFatal.toFront();
			button_exit.toFront();
		} else {
			fadeImage(icon_errorFatalBG, errorFatalBG);
			slidingImage(icon_errorFatal, errorFatal, 650, 0, 650, 325, 1250);
			icon_errorFatalBG.toFront();
			icon_errorFatal.toFront();
			button_exit.toFront();
		}
	}

	/* **********************************************
	 *												*
	 *		METHODS CALLED BY MAIN CONTROLLER		*
	 * 												*
	 ************************************************/

	/**
	 * This methods handles an error from the server.
	 */
	@Override
	public void fatalError() {
		finished = true;
		gameCantContinue(1);
	}

	/**
	 * This methods handles messages from the server.
	 * @param message is the message arrived from the server
	 * @throws IOException if there has been an error handling the message
	 */
	@Override
	public void deposeMessage(NetObject message) throws IOException {
		switch (message.message) {
			case Constants.GODS_GODS -> {
				gameState.setGodsName(((NetDivinityChoice)message).getDivinities());
			}
			case Constants.GENERAL_PHASE_UPDATE -> {
				gameState.advancePhase();
			}
			case Constants.TURN_PLAYERTURN -> {
				gameState.setActivePlayer(((NetDivinityChoice)message).getPlayer());
				nextFXML = FXMLLoader.load(getClass().getResource("/fxml/choose_gods.fxml"));
				nextScene = new Scene(nextFXML);
				currentStage = (Stage) button_next.getScene().getWindow();
				currentStage.setScene(nextScene);
			}
			case Constants.GENERAL_SETUP_DISCONNECT -> {
				finished = true;
				gameCantContinue(0);
			}
		}
	}
}
