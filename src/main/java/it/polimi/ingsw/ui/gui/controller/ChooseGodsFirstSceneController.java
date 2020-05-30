package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
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
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
	}

	/* **********************************************
	 *												*
	 *			HANDLERS OF USER INTERACTION		*
	 * 												*
	 ************************************************/
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
	public void mouseExitedCard(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}
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
	public void mousePressedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNextPressed);
	}
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
	public void mousePressedExit(MouseEvent mouseEvent) throws IOException {
		button_exit.setImage(buttonExitPressed);
		previousFXML = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
		previousScene = new Scene(previousFXML);
	}
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
	public void wrongSelectOfGods() {
		// TODO: say to the user that he selected a wrong amount of gods
	}
	/**
	 *
	 * @param reason 0 if a player disconnected during the setup, 1 if the server has crashed
	 */
	private void gameCantContinue(int reason) {
		// TODO: print to the player that the server has crashed or a player disconnected in the setup and the game cannot continue for this reason
		// 		 now a player can only quit and cannot do anything
	}

	/* **********************************************
	 *												*
	 *		METHODS CALLED BY MAIN CONTROLLER		*
	 * 												*
	 ************************************************/
	@Override
	public void fatalError() {
		finished = true;
		gameCantContinue(1);
	}
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
