package it.polimi.ingsw.ui.gui.controller;

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
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class ChooseGodsSceneController implements SceneController {
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
	private HBox god_cards_list;
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
	Image cardApolloDisabled = new Image("/img/gods/card_apollo_disabled.png");
	Image cardArtemisDisabled = new Image("/img/gods/card_artemis_disabled.png");
	Image cardAthenaDisabled = new Image("/img/gods/card_athena_disabled.png");
	Image cardAtlasDisabled = new Image("/img/gods/card_atlas_disabled.png");
	Image cardDemeterDisabled = new Image("/img/gods/card_demeter_disabled.png");
	Image cardHephaestusDisabled = new Image("/img/gods/card_hephaestus_disabled.png");
	Image cardMinotaurDisabled = new Image("/img/gods/card_minotaur_disabled.png");
	Image cardPanDisabled = new Image("/img/gods/card_pan_disabled.png");
	Image cardPrometheusDisabled = new Image("/img/gods/card_prometheus_disabled.png");

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
	private String godName = null;

	// triggers for server messages
	private GameState gameState;
	private boolean pressed = false;

	private String god;


	public void initialize(){
		MainGuiController.getInstance().setSceneController(this);
		gameState = MainGuiController.getInstance().getGameState();

		description.setImage(null);
		setChoosingPlayer();
		parsingPlayersCard();
	}

	public void parsingPlayersCard() {
		setGodImage(card_1,gameState.getGodsName().get(0));
		setGodImage(card_2,gameState.getGodsName().get(1));
		if (gameState.getPlayers().size() == 3) {
			setGodImage(card_3,gameState.getGodsName().get(2));
		} else {
			god_cards_list.getChildren().remove(card_3);
		}
	}

	/* **********************************************
	 *												*
	 *			HANDLERS OF USER INTERACTION		*
	 * 												*
	 ************************************************/
	public void mouseEnteredCard(MouseEvent mouseEvent) {
		ImageView entered = (ImageView) mouseEvent.getTarget();

		if (entered.equals(card_1)) {
			mouseEnterCard(description, parsingCard(card_1));
		} else if (entered.equals(card_2)) {
			mouseEnterCard(description, parsingCard(card_2));
		} else if (entered.equals(card_3)) {
			mouseEnterCard(description, parsingCard(card_3));
		}
	}
	public void mouseExitedCard(MouseEvent mouseEvent) {
		mouseExitCard(description);
	}
	public void mousePressedCard(MouseEvent mouseEvent) {
		ImageView card = (ImageView) mouseEvent.getTarget();
		if (!pressed) {
			if (card.getImage().equals(cardApollo)) {
				card.setImage(cardApolloPressed);
				godName = Constants.APOLLO;
				pressed = true;
			} else if (card.getImage().equals(cardArtemis)) {
				card.setImage(cardArtemisPressed);
				godName = Constants.ARTEMIS;
				pressed = true;
			} else if (card.getImage().equals(cardAthena)) {
				card.setImage(cardAthenaPressed);
				godName = Constants.ATHENA;
				pressed = true;
			} else if (card.getImage().equals(cardAtlas)) {
				card.setImage(cardAtlasPressed);
				godName = Constants.ATLAS;
				pressed = true;
			} else if (card.getImage().equals(cardDemeter)) {
				card.setImage(cardDemeterPressed);
				godName = Constants.DEMETER;
				pressed = true;
			} else if (card.getImage().equals(cardHephaestus)) {
				card.setImage(cardHephaestusPressed);
				godName = Constants.HEPHAESTUS;
				pressed = true;
			} else if (card.getImage().equals(cardMinotaur)) {
				card.setImage(cardMinotaurPressed);
				godName = Constants.MINOTAUR;
				pressed = true;
			} else if (card.getImage().equals(cardPan)) {
				card.setImage(cardPanPressed);
				godName = Constants.PAN;
				pressed = true;
			} else if (card.getImage().equals(cardPrometheus)) {
				card.setImage(cardPrometheusPressed);
				godName = Constants.PROMETHEUS;
				pressed = true;
			}
		} else {
			if (card.getImage().equals(cardApolloPressed)) {
				card.setImage(cardApollo);
				godName = null;
				pressed = false;
			} else if (card.getImage().equals(cardArtemisPressed)) {
				card.setImage(cardArtemis);
				godName = null;
				pressed = false;
			} else if (card.getImage().equals(cardAthenaPressed)) {
				card.setImage(cardAthena);
				godName = null;
				pressed = false;
			} else if (card.getImage().equals(cardAtlasPressed)) {
				card.setImage(cardAtlas);
				godName = null;
				pressed = false;
			} else if (card.getImage().equals(cardDemeterPressed)) {
				card.setImage(cardDemeter);
				godName = null;
				pressed = false;
			} else if (card.getImage().equals(cardHephaestusPressed)) {
				card.setImage(cardHephaestus);
				godName = null;
				pressed = false;
			} else if (card.getImage().equals(cardMinotaurPressed)) {
				card.setImage(cardMinotaur);
				godName = null;
				pressed = false;
			} else if (card.getImage().equals(cardPanPressed)) {
				card.setImage(cardPan);
				godName = null;
				pressed = false;
			} else if (card.getImage().equals(cardPrometheusPressed)) {
				card.setImage(cardPrometheus);
				godName = null;
				pressed = false;
			}
		}
	}
	public void mousePressedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNextPressed);
	}
	public void mouseReleasedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNext);

		if (godName == null) {
			wrongSelectOfGod();
		} else {
			// the user selected a god, so the request of the selection is sent to the server
			((ImageView)mouseEvent.getTarget()).getScene().setCursor(Cursor.WAIT);
			NetDivinityChoice godMessage = new NetDivinityChoice(Constants.GODS_IN_CHOICE,gameState.getPlayer(),godName,false);
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
	private void setGodImage(ImageView card, String godName) {
		if (godName.equals(Constants.APOLLO)) {
			card.setImage(cardApollo);
		}else if (godName.equals(Constants.ARTEMIS)) {
			card.setImage(cardArtemis);
		} else if (godName.equals(Constants.ATHENA)) {
			card.setImage(cardAthena);
		} else if (godName.equals(Constants.ATLAS)) {
			card.setImage(cardAtlas);
		} else if (godName.equals(Constants.DEMETER)) {
			card.setImage(cardDemeter);
		}  else if (godName.equals(Constants.HEPHAESTUS)) {
			card.setImage(cardHephaestus);
		} else if (godName.equals(Constants.MINOTAUR)) {
			card.setImage(cardMinotaur);
		} else if (godName.equals(Constants.PAN)) {
			card.setImage(cardPan);
		} else if (godName.equals(Constants.PROMETHEUS)) {
			card.setImage(cardPrometheus);
		}
	}

	/* **********************************************
	 *												*
	 *		EVENTS AFTER SERVER MESSAGE				*
	 * 												*
	 ************************************************/
	public void wrongSelectOfGod() {
		// TODO: say to the user that he has not selected a god
	}
	public void setChoosingPlayer() {
		text_player.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
		text_player.setText(gameState.getActivePlayer());
	}
	public void disableGod(String name) {
		for(String item : gameState.getGods().values()){
			if(item.equals(name)){
				switch (name.toUpperCase()){
					case Constants.APOLLO -> {
						if (card_1.getImage().equals(cardApollo)) {
							card_2.setImage(cardApolloDisabled);
						} else if (card_2.getImage().equals(cardApollo)) {
							card_2.setImage(cardApolloDisabled);
						}
						if(gameState.getPlayerNumber() == 3) {
							if(card_3.getImage().equals(cardApollo)) {
								card_3.setImage(cardApolloDisabled);
							}
						}
					}
					case Constants.ARTEMIS -> {
						if (card_1.getImage().equals(cardArtemis)) {
							card_2.setImage(cardArtemisDisabled);
						} else if (card_2.getImage().equals(cardArtemis)) {
							card_2.setImage(cardArtemisDisabled);
						}
						if(gameState.getPlayerNumber() == 3) {
							if(card_3.getImage().equals(cardArtemis)) {
								card_3.setImage(cardArtemisDisabled);
							}
						}
					}
					case Constants.ATHENA -> {
						if (card_1.getImage().equals(cardAthena)) {
							card_2.setImage(cardAthenaDisabled);
						} else if (card_2.getImage().equals(cardAthena)) {
							card_2.setImage(cardAthenaDisabled);
						}
						if(gameState.getPlayerNumber() == 3) {
							if(card_3.getImage().equals(cardAthena)) {
								card_3.setImage(cardAthenaDisabled);
							}
						}
					}
					case Constants.ATLAS -> {
						if (card_1.getImage().equals(cardAtlas)) {
							card_2.setImage(cardAtlasDisabled);
						} else if (card_2.getImage().equals(cardAtlas)) {
							card_2.setImage(cardAtlasDisabled);
						}
						if(gameState.getPlayerNumber() == 3) {
							if(card_3.getImage().equals(cardAtlas)) {
								card_3.setImage(cardAtlasDisabled);
							}
						}
					}
					case Constants.DEMETER -> {
						if (card_1.getImage().equals(cardDemeter)) {
							card_2.setImage(cardDemeterDisabled);
						} else if (card_2.getImage().equals(cardDemeter)) {
							card_2.setImage(cardDemeterDisabled);
						}
						if(gameState.getPlayerNumber() == 3) {
							if(card_3.getImage().equals(cardDemeter)) {
								card_3.setImage(cardDemeterDisabled);
							}
						}
					}
					case Constants.HEPHAESTUS -> {
						if (card_1.getImage().equals(cardHephaestus)) {
							card_2.setImage(cardHephaestusDisabled);
						} else if (card_2.getImage().equals(cardHephaestus)) {
							card_2.setImage(cardHephaestusDisabled);
						}
						if(gameState.getPlayerNumber() == 3) {
							if(card_3.getImage().equals(cardHephaestus)) {
								card_3.setImage(cardHephaestusDisabled);
							}
						}
					}
					case Constants.MINOTAUR -> {
						if (card_1.getImage().equals(cardMinotaur)) {
							card_2.setImage(cardMinotaurDisabled);
						} else if (card_2.getImage().equals(cardMinotaur)) {
							card_2.setImage(cardMinotaurDisabled);
						}
						if(gameState.getPlayerNumber() == 3) {
							if(card_3.getImage().equals(cardMinotaur)) {
								card_3.setImage(cardMinotaurDisabled);
							}
						}
					}
					case Constants.PAN -> {
						if (card_1.getImage().equals(cardPan)) {
							card_2.setImage(cardPanDisabled);
						} else if (card_2.getImage().equals(cardPan)) {
							card_2.setImage(cardPanDisabled);
						}
						if(gameState.getPlayerNumber() == 3) {
							if(card_3.getImage().equals(cardPan)) {
								card_3.setImage(cardPanDisabled);
							}
						}
					}
					case Constants.PROMETHEUS -> {
						if (card_1.getImage().equals(cardPrometheus)) {
							card_2.setImage(cardPrometheusDisabled);
						} else if (card_2.getImage().equals(cardPrometheus)) {
							card_2.setImage(cardPrometheusDisabled);
						}
						if(gameState.getPlayerNumber() == 3) {
							if(card_3.getImage().equals(cardPrometheus)) {
								card_3.setImage(cardPrometheusDisabled);
							}
						}
					}
				}
			}
		}
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
			case Constants.GODS_CHOICES -> {
				button_exit.getScene().setCursor(Cursor.DEFAULT);
				gameState.setGods(((NetDivinityChoice)message).getPlayerGodMap());
				for (String god : gameState.getGods().values()) {
					if (god.equals(godName)) {
						godName = null;
						pressed = false;
					}
					disableGod(god);
				}
			}
			case Constants.GENERAL_PHASE_UPDATE -> {
				gameState.advancePhase();
			}
			case Constants.TURN_PLAYERTURN -> {
				gameState.setActivePlayer(((NetDivinityChoice)message).getPlayer());

				// if all players have selected the god they must change window
				if (gameState.getGods().size() == gameState.getPlayerNumber()) {
					// if the player is now the active player he is the challenger, he must choose the starter, the others must go in wait for it
					if (gameState.getPlayer().equals(gameState.getActivePlayer())) {
						nextFXML = FXMLLoader.load(getClass().getResource("/fxml/choose_starter.fxml"));
						nextScene = new Scene(nextFXML);
						currentStage = (Stage) button_next.getScene().getWindow();
						currentStage.setScene(nextScene);
					} else {
						nextFXML = FXMLLoader.load(getClass().getResource("/fxml/loading.fxml"));
						nextScene = new Scene(nextFXML);
						currentStage = (Stage) button_next.getScene().getWindow();
						currentStage.setScene(nextScene);
					}
				}
			}
			case Constants.GENERAL_SETUP_DISCONNECT -> {
				// TODO: implement the disconnection shutdown after someone quit the game
			}
		}
	}
}
