package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.network.game.*;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.network.objects.NetGaming;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.Constants;
import it.polimi.ingsw.util.Pair;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;


/**
 * This class implements the choosing color map of the GUI.
 */
public class MapSceneController implements SceneController {
	@FXML
	private ImageView box_workers;
	@FXML
	private ImageView box_build;
	@FXML
	private ImageView column_right;
	@FXML
	private ImageView box_exit;
	@FXML
	private ImageView column_left;
	@FXML
	private ImageView worker1;
	@FXML
	private ImageView box_turn;
	@FXML
	private ImageView worker2;
	@FXML
	private ImageView icon_exit;
	@FXML
	private ImageView card_god;
	@FXML
	private ImageView clouds_left;
	@FXML
	private ImageView clouds_right;
	@FXML
	private ImageView button_exit;
	@FXML
	private ImageView button_dome;
	@FXML
	private ImageView button_build;
	@FXML
	private ImageView description_god;
	@FXML
	private ImageView cell_0_0;
	@FXML
	private ImageView cell_0_1;
	@FXML
	private ImageView cell_0_2;
	@FXML
	private ImageView cell_0_3;
	@FXML
	private ImageView cell_0_4;
	@FXML
	private ImageView cell_1_0;
	@FXML
	private ImageView cell_1_1;
	@FXML
	private ImageView cell_1_2;
	@FXML
	private ImageView cell_1_3;
	@FXML
	private ImageView cell_1_4;
	@FXML
	private ImageView cell_2_0;
	@FXML
	private ImageView cell_2_1;
	@FXML
	private ImageView cell_2_2;
	@FXML
	private ImageView cell_2_3;
	@FXML
	private ImageView cell_2_4;
	@FXML
	private ImageView cell_3_0;
	@FXML
	private ImageView cell_3_1;
	@FXML
	private ImageView cell_3_2;
	@FXML
	private ImageView cell_3_3;
	@FXML
	private ImageView cell_3_4;
	@FXML
	private ImageView cell_4_0;
	@FXML
	private ImageView cell_4_1;
	@FXML
	private ImageView cell_4_2;
	@FXML
	private ImageView cell_4_3;
	@FXML
	private ImageView cell_4_4;
	@FXML
	private ImageView possibleCell_0_0;
	@FXML
	private ImageView possibleCell_0_1;
	@FXML
	private ImageView possibleCell_0_2;
	@FXML
	private ImageView possibleCell_0_3;
	@FXML
	private ImageView possibleCell_0_4;
	@FXML
	private ImageView possibleCell_1_0;
	@FXML
	private ImageView possibleCell_1_1;
	@FXML
	private ImageView possibleCell_1_2;
	@FXML
	private ImageView possibleCell_1_3;
	@FXML
	private ImageView possibleCell_1_4;
	@FXML
	private ImageView possibleCell_2_0;
	@FXML
	private ImageView possibleCell_2_1;
	@FXML
	private ImageView possibleCell_2_2;
	@FXML
	private ImageView possibleCell_2_3;
	@FXML
	private ImageView possibleCell_2_4;
	@FXML
	private ImageView possibleCell_3_0;
	@FXML
	private ImageView possibleCell_3_1;
	@FXML
	private ImageView possibleCell_3_2;
	@FXML
	private ImageView possibleCell_3_3;
	@FXML
	private ImageView possibleCell_3_4;
	@FXML
	private ImageView possibleCell_4_0;
	@FXML
	private ImageView possibleCell_4_1;
	@FXML
	private ImageView possibleCell_4_2;
	@FXML
	private ImageView possibleCell_4_3;
	@FXML
	private ImageView possibleCell_4_4;
	@FXML
	private GridPane gridPane_cells;
	@FXML
	private Text text_player;
	@FXML
	private ImageView button_endTurn;
	@FXML
	private ImageView icon_error;
	@FXML
	private ImageView button_exit2;
	@FXML
	private ImageView BG_message;
	@FXML
	private ImageView button_watch;
	@FXML
	private ImageView icon_message;
	@FXML
	private Text text_playerMessage;
	@FXML
	private Text text_playerMessageLost;
	@FXML
	private ImageView button_undo;
	@FXML
	private ImageView button_info;
	@FXML
	private ImageView box_info;


	private boolean pressedIconExit = false;
	private boolean pressedButtonBuild = false;
	private boolean pressedButtonDome = false;
	private int setWorkers = 0;

	private boolean pressedWorker1 = false;
	private boolean pressedWorker2 = false;

	Image boxWorkers = new Image("/img/map/box_workers.png");
	Image boxBuild = new Image("/img/map/box_build.png");
	Image columnRight = new Image("/img/map/column_right.png");
	Image columnLeft = new Image("/img/map/column_left.png");
	Image boxExit = new Image("/img/map/box_exit.png");
	Image workerRed = new Image("/img/map/worker_red.png");
	Image workerRedPressed = new Image("/img/map/worker_red_pressed.png");
	Image workerGreen = new Image("/img/map/worker_green.png");
	Image workerGreenPressed = new Image("/img/map/worker_green_pressed.png");
	Image workerBlue = new Image("/img/map/worker_blue.png");
	Image workerBluePressed = new Image("/img/map/worker_blue_pressed.png");
	Image boxTurn = new Image("/img/map/box_turn.png");
	Image iconExit = new Image("/img/map/exit_icon.png");
	Image iconExitPressed = new Image("/img/map/exit_icon_pressed.png");
	Image cloudsLeft = new Image("/img/map/clouds_left.png");
	Image cloudsRight = new Image("/img/map/clouds_right.png");
	Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	Image buttonExit = new Image("/img/home_exit_btn.png");
	Image buttonBuild = new Image("/img/map/button_build.png");
	Image buttonBuildPressed = new Image("/img/map/button_build_pressed.png");
	Image buttonDome = new Image("/img/map/button_dome.png");
	Image buttonDomePressed = new Image("/img/map/button_dome_pressed.png");
	Image build1 = new Image("/img/map/build1.png");
	Image build2 = new Image("/img/map/build2.png");
	Image build3 = new Image("/img/map/build3.png");
	Image buildDome = new Image("/img/map/buildDome.png");
	Image blank = new Image("/img/map/cell_blank.png");
	Image build1Red = new Image("/img/map/build1_red.png");
	Image build1Green = new Image("/img/map/build1_green.png");
	Image build1Blue = new Image("/img/map/build1_blue.png");
	Image build2Red = new Image("/img/map/build2_red.png");
	Image build2Green = new Image("/img/map/build2_green.png");
	Image build2Blue = new Image("/img/map/build2_blue.png");
	Image build3Red = new Image("/img/map/build3_red.png");
	Image build3Green = new Image("/img/map/build3_green.png");
	Image build3Blue = new Image("/img/map/build3_blue.png");
	Image build1RedPressed = new Image("/img/map/build1_redPressed.png");
	Image build1GreenPressed = new Image("/img/map/build1_greenPressed.png");
	Image build1BluePressed = new Image("/img/map/build1_bluePressed.png");
	Image build2RedPressed = new Image("/img/map/build2_redPressed.png");
	Image build2GreenPressed = new Image("/img/map/build2_greenPressed.png");
	Image build2BluePressed = new Image("/img/map/build2_bluePressed.png");
	Image build3RedPressed = new Image("/img/map/build3_redPressed.png");
	Image build3GreenPressed = new Image("/img/map/build3_greenPressed.png");
	Image build3BluePressed = new Image("/img/map/build3_bluePressed.png");
	Image buttonEndTurn = new Image("/img/map/button_endTurn.png");
	Image buttonEndTurnPressed = new Image("/img/map/button_endTurn_pressed.png");
	Image buttonEndTurnDisabled = new Image("/img/map/button_endTurn_disabled.png");
	Image errorWait = new Image("/img/error_wait.png");
	Image errorMove = new Image("/img/error_move.png");
	Image errorBuild = new Image("/img/error_build.png");
	Image errorSelectWorker = new Image("/img/error_selectWorker.png");
	Image BGwatch = new Image("/img/passiveWatchFilter.png");
	Image iconLost = new Image("/img/message_lost.png");
	Image iconOtherLost = new Image("/img/message_otherLost.png");
	Image buttonWatch = new Image("/img/button_watch.png");
	Image buttonWatchPressed = new Image("/img/button_watch_pressed.png");
	Image iconWon = new Image("/img/message_won.png");
	Image iconOtherWon = new Image("/img/message_otherWon.png");
	Image iconDisconnected = new Image("/img/message_otherDisconnected.png");
	Image cellTarget = new Image("/img/map/cell_target.png");
	Image buttonUndo = new Image("/img/map/button_undo.png");
	Image buttonUndoPressed = new Image("/img/map/button_undo_pressed.png");
	Image buttonUndoDisabled = new Image("/img/map/button_undo_disabled.png");
	Image buttonUndo5 = new Image("/img/map/button_undo5.png");
	Image buttonUndo4 = new Image("/img/map/button_undo4.png");
	Image buttonUndo3 = new Image("/img/map/button_undo3.png");
	Image buttonUndo2 = new Image("/img/map/button_undo2.png");
	Image buttonUndo1 = new Image("/img/map/button_undo1.png");
	Image errorFatalBG = new Image("/img/errorFatal_background.png");
	Image errorFatal = new Image("/img/error_fatal.png");
	Image buttonInfo = new Image("/img/map/button_info.png");
	Image boxInfo = new Image("/img/map/box_info.png");
	Image errorSomeoneDisconnected = new Image("/img/message_someoneDisconnected.png");

	ImageView workerSelected = null;

	Timeline timeline = null; //for undo button

	// objects used to change scene
	private Parent previousFXML;
	private Parent nextFXML;
	private Scene previousScene;
	private Scene nextScene;
	private Stage currentStage;

	// triggers for server messages
	private GameState gameState;

	// variables for gui
	private NetMap beforeActionMap = null;
	private NetBuild beforePerformedBuild = null;
	private long actionTimestamp = 0;
	private Pair<Integer,Integer> worker1StartingPos;
	private Pair<Integer,Integer> worker2StartingPos;
	private boolean finished = false;
	private boolean waitingResponse = false;
	private boolean hasMoved = false;
	private boolean transitioningFromSetupToPlay = false;
	private boolean hasBuilt = false;
	private NetMove performedMove = null;
	private NetBuild performedBuild = null;

	public void initialize() {
		MainGuiController.getInstance().setSceneController(this);
		gameState = MainGuiController.getInstance().getGameState();

		button_endTurn.toBack();
		button_endTurn.setImage(null);
		button_undo.toBack();
		button_undo.setImage(null);
		description_god.setImage(null);
		icon_error.setImage(null);
		initializeCells();
		initializeAnimations();
		setActivePlayer();
		BG_message.toBack();
		icon_message.toBack();
		button_exit2.toBack();
		button_watch.toBack();
		icon_error.toBack();
		button_exit.toBack();
		button_build.toFront();
		button_dome.toFront();
		button_build.setDisable(true);
		button_dome.setDisable(true);
		text_playerMessage.toBack();
		icon_message.setImage(null);
		BG_message.setImage(null);
		button_watch.setImage(null);
		button_exit2.setImage(null);
		box_info.toBack();
		box_info.setImage(null);
		button_info.toFront();
		button_info.setImage(null);
		gridPane_cells.toFront();
	}

	/**
	 * This method initialize the undo button on the screen.
	 */
	private void timerInitialize(){
		button_undo.toFront();
		button_undo.setDisable(true);
		fadeImage(button_undo, buttonUndoDisabled, 0, 1, 1);
	}

	/**
	 * This method handles the 5 second countdown displayed on the undo button.
	 */
	private void timerCountdown(){
		if (timeline != null) {
			timeline.stop();
		}
		button_undo.setDisable(false);
		timeline = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(button_undo.imageProperty(), buttonUndo5)),
				new KeyFrame(Duration.seconds(1), new KeyValue(button_undo.imageProperty(), buttonUndo4)),
				new KeyFrame(Duration.seconds(2), new KeyValue(button_undo.imageProperty(), buttonUndo3)),
				new KeyFrame(Duration.seconds(3), new KeyValue(button_undo.imageProperty(), buttonUndo2)),
				new KeyFrame(Duration.seconds(4), new KeyValue(button_undo.imageProperty(), buttonUndo1)),
				new KeyFrame(Duration.seconds(5), new KeyValue(button_undo.imageProperty(), buttonUndoDisabled))
		);
		actionTimestamp = new Date().getTime();
		timeline.play();
	}

	/**
	 * This method creates a fade transition of an image.
	 * @param imageView the ImageView that has to be faded.
	 * @param image the Image to set in the ImageView.
	 */
	private void fadeImage(ImageView imageView, Image image, int from, int to, int flag){
		imageView.setImage(image);
		FadeTransition ft = new FadeTransition(Duration.millis(2500), imageView);
		ft.setFromValue(from);
		ft.setToValue(to);
		ft.setCycleCount(1);
		if(flag == 1){
			imageView.toFront();
		} else {
			imageView.toBack();
		}
		ft.play();
	}

	/**
	 * This method creates a fade transition on a text.
	 * @param text the Text object interested.
	 * @param from the initial fading percentage
	 * @param to the final fading percentage
	 * @param flag is 1 when the text has to be displayed on top of the screen, 1 if it has to be hidden.
	 * @param duration the duration of the transition.
	 * @return the transition.
	 */
	private Transition setFadeText(Text text, int from, int to, int flag, int duration){
		FadeTransition ft = new FadeTransition(Duration.millis(duration), text);
		ft.setFromValue(from);
		ft.setToValue(to);
		ft.setCycleCount(1);
		if(flag == 1){
			text.toFront();
		} else {
			text.toBack();
		}
		return ft;
	}

	/**
	 * This method creates a fade transition on a text.
	 * @param text the Text object interested.
	 * @param from the initial fading percentage
	 * @param to the final fading percentage
	 */
	private void fadeText(Text text, int from, int to){
		FadeTransition ft = new FadeTransition(Duration.millis(2500), text);
		ft.setFromValue(from);
		ft.setToValue(to);
		ft.setCycleCount(1);
		ft.play();
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
	 * This method handles all the inital backgorund animations.
	 */
	private void initializeAnimations() {
		slidingImage(box_exit, boxExit, 300, 47, 300, 47, 500);
		slidingImage(button_exit, buttonExit, 200, 24, 200, 24, 500);
		slidingImage(column_left, columnLeft, 0, 360, 61, 360, 3300);
		slidingImage(column_right, columnRight, 150, 360, 63, 360, 3300);
		slidingImage(box_turn, boxTurn, 121, -250, 121, 218, 3300);
		slidingImage(box_turn, boxTurn, 121, -250, 121, 218, 3300);
		slidingText(text_player, 77, -400, 77, -10, 3300);
		slidingImage(box_build, boxBuild, 121, 600, 121, 201, 3300);
		slidingImage(button_build, buttonBuild, 52, 500, 52, 44, 3300);
		slidingImage(button_dome, buttonDome, 67, 500, 67, 45, 3300);
		slidingImage(box_workers, boxWorkers, 280, 122, 159, 122, 3300);
		slidingImage(worker1, colorPlayer(), 280, 52, 60, 52, 3300);
		slidingImage(worker2, colorPlayer(), 280, 52, 60, 52, 3300);
		slidingImage(card_god, godPlayer(), 40, -500, 40, 52, 3300);
		slidingImage(clouds_left, cloudsLeft, 350, 359, -600, 359, 3200);
		slidingImage(clouds_right, cloudsRight, 400, 359, 1200, 359, 3200);
		fadeImage(button_info, buttonInfo, 0, 1, 1);
	}

	/**
	 * This method initilizes null all the cell imageView
	 */
	private void initializeCells() {
		cell_0_0.setImage(blank);
		cell_1_0.setImage(blank);
		cell_2_0.setImage(blank);
		cell_3_0.setImage(blank);
		cell_4_0.setImage(blank);
		cell_0_1.setImage(blank);
		cell_1_1.setImage(blank);
		cell_2_1.setImage(blank);
		cell_3_1.setImage(blank);
		cell_4_1.setImage(blank);
		cell_0_2.setImage(blank);
		cell_1_2.setImage(blank);
		cell_2_2.setImage(blank);
		cell_3_2.setImage(blank);
		cell_4_2.setImage(blank);
		cell_0_3.setImage(blank);
		cell_1_3.setImage(blank);
		cell_2_3.setImage(blank);
		cell_3_3.setImage(blank);
		cell_4_3.setImage(blank);
		cell_0_4.setImage(blank);
		cell_1_4.setImage(blank);
		cell_2_4.setImage(blank);
		cell_3_4.setImage(blank);
		cell_4_4.setImage(blank);
	}

	/**
	 *
	 * @return the color of the player.
	 */
	public Image colorPlayer() {
		if (gameState.getColors().get(gameState.getPlayer()).equals(Color.BLUE)) {
			return workerBlue;
		} else if (gameState.getColors().get(gameState.getPlayer()).equals(Color.GREEN)) {
			return workerGreen;
		} else {
			return workerRed;
		}
	}

	/**
	 *
	 * @return the godcard of the player.
	 */
	public Image godPlayer() {
		if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.APOLLO)) {
			return new Image("/img/gods/card_apollo.png");
		}else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.ARTEMIS)) {
			return new Image("/img/gods/card_artemis.png");
		} else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.ATHENA)) {
			return new Image("/img/gods/card_athena.png");
		} else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.ATLAS)) {
			return new Image("/img/gods/card_atlas.png");
		} else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.DEMETER)) {
			return new Image("/img/gods/card_demeter.png");
		}  else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.HEPHAESTUS)) {
			return new Image("/img/gods/card_hephaestus.png");
		} else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.MINOTAUR)) {
			return new Image("/img/gods/card_minotaur.png");
		} else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.PAN)) {
			return new Image("/img/gods/card_pan.png");
		} else {
			return new Image("/img/gods/card_prometheus.png");
		}
	}

	/**
	 *
	 * @return the description of the god card.
	 */
	public Image descriptionGodCard() {
		if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.APOLLO)) {
			return new Image("/img/gods/description_apollo.png");
		}else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.ARTEMIS)) {
			return new Image("/img/gods/description_artemis.png");
		} else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.ATHENA)) {
			return new Image("/img/gods/description_athena.png");
		} else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.ATLAS)) {
			return new Image("/img/gods/description_atlas.png");
		} else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.DEMETER)) {
			return new Image("/img/gods/description_demeter.png");
		}  else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.HEPHAESTUS)) {
			return new Image("/img/gods/description_hephaestus.png");
		} else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.MINOTAUR)) {
			return new Image("/img/gods/description_minotaur.png");
		} else if (gameState.getGods().get(gameState.getActivePlayer()).equals(Constants.PAN)) {
			return new Image("/img/gods/description_pan.png");
		} else {
			return new Image("/img/gods/description_prometheus.png");
		}
	}

	/* **********************************************
	 *												*
	 *			HANDLERS OF USER INTERACTION		*
	 * 												*
	 ************************************************/

	/**
	 * This method handles the mouse click on the little exit button: making appear the exit button or making disappear it.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedIconExit(MouseEvent mouseEvent) {
		if (!pressedIconExit) {
			button_exit.toFront();
			column_right.toFront();
			worker2.toFront();
			icon_exit.toFront();
			icon_exit.setImage(iconExitPressed);
			slidingImage(box_exit, boxExit, 300, 47, 127, 47, 500);
			slidingImage(button_exit, buttonExit, 200, 24, 36, 24, 500);
			pressedIconExit = true;
		} else {
			button_exit.toBack();
			icon_exit.setImage(iconExit);
			slidingImage(box_exit, boxExit, 127, 47, 310, 47, 500);
			slidingImage(button_exit, buttonExit, 36, 24, 220, 24, 500);
			pressedIconExit = false;
		}
	}

	/**
	 * This method handles the mouse click on a exit button: making it pressed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
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

		if (gameState.getTurn().getPhase() == Phase.PLAYERTURN) {
			NetGaming netSetup = new NetGaming(Constants.GENERAL_DISCONNECT);
			MainGuiController.getInstance().sendMessage(netSetup);
		} else {
			NetGameSetup netSetup = new NetGameSetup(Constants.GENERAL_DISCONNECT);
			MainGuiController.getInstance().sendMessage(netSetup);
		}
		MainGuiController.getInstance().refresh();
		MainGuiController.getInstance().setSceneController(null);
		currentStage = (Stage) button_exit.getScene().getWindow();
		currentStage.setScene(previousScene);
	}

	/**
	 * This method handles the mouse release on the block button: making it pressed or unpressed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedBuild(MouseEvent mouseEvent) {
		if (!pressedButtonBuild) {
			button_build.setImage(buttonBuildPressed);
			pressedButtonBuild = true;
			button_dome.setDisable(true);

			if (workerSelected != null) {
				NetCell movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
				colorCells(1, movingWorkerCell);
			}
		} else {
			button_build.setImage(buttonBuild);
			button_dome.setDisable(false);
			pressedButtonBuild = false;
			unColorCells();
		}
	}

	/**
	 * This method handles the mouse release on the dome button: making it pressed or unpressed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedDome(MouseEvent mouseEvent) {
		if (!pressedButtonDome) {
			button_dome.setImage(buttonDomePressed);
			pressedButtonDome = true;
			button_build.setDisable(true);

			if (workerSelected != null) {
				NetCell movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
				colorCells(2, movingWorkerCell);
			}
		} else {
			button_dome.setImage(buttonDome);
			pressedButtonDome = false;
			button_build.setDisable(false);
			unColorCells();
		}
	}

	/**
	 * This method handles the mouse entry on a god card, sliding up the description of that god.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mouseEnteredGodCard(MouseEvent mouseEvent) {
		slidingImage(description_god, descriptionGodCard(), 129, 400, 129, 125, 450);
	}

	/**
	 * This method handles the mouse entry on a god card, sliding down the description of that god.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mouseExitedGodCard(MouseEvent mouseEvent) {
		slidingImage(description_god, descriptionGodCard(), 129, 125, 129, 400, 450);
	}

	/**
	 * This method handles the mouse click on a cell of the map.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedCell(MouseEvent mouseEvent) {
		ImageView pressedCell = (ImageView) mouseEvent.getTarget();

		// if the player is clicking a cell on its turn it active possible actions
		if (gameState.getActivePlayer().equals(gameState.getPlayer()) && !waitingResponse && !finished && !hasMoved) {
			if (!pressedButtonDome && !pressedButtonBuild) {
				if (colorPlayer().equals(workerRed)) {
					movingWorker(pressedCell, workerRed, workerRedPressed, build1Red, build2Red, build3Red, build1RedPressed, build2RedPressed, build3RedPressed);
				} else if (colorPlayer().equals(workerGreen)) {
					movingWorker(pressedCell, workerGreen, workerGreenPressed, build1Green, build2Green, build3Green, build1GreenPressed, build2GreenPressed, build3GreenPressed);
				} else if (colorPlayer().equals(workerBlue)) {
					movingWorker(pressedCell, workerBlue, workerBluePressed, build1Blue, build2Blue, build3Blue, build1BluePressed, build2BluePressed, build3BluePressed);
				}
			}

			buildOnCell(pressedCell, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
		} else if (waitingResponse) {
			waitAction(0);
		} else if (!gameState.getActivePlayer().equals(gameState.getPlayer())) {
			waitAction(1);
		}
	}

	/**
	 * This method handles the mouse release on the end turn button: making it pressed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedEndTurn(MouseEvent mouseEvent) {
		button_endTurn.setImage(buttonEndTurnPressed);
	}

	/**
	 * This method handles the mouse release on the end turn button: making it unpressed and changing the phase turn or the turn.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mouseReleasedEndTurn(MouseEvent mouseEvent) {
		button_endTurn.setImage(buttonEndTurn);
		if (performedBuild != null) {
			timeline.stop();
			button_undo.setImage(buttonUndoDisabled);
			sendWorkerBuild();
		} else {
			timeline.stop();
			button_undo.setImage(buttonUndoDisabled);
			sendWorkerMove(performedMove);
			hasMoved = false;
		}
	}

	/**
	 * This method handles the mouse click on a exit button: making it pressed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedExit2(MouseEvent mouseEvent) throws IOException {
		button_exit2.setImage(buttonExitPressed);
	}

	/**
	 * This method handles the mouse release on a exit button: making it unpressed and returning to the home scene.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mouseReleasedExit2(MouseEvent mouseEvent) throws IOException {
		button_exit2.setImage(buttonExit);

		previousFXML = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
		previousScene = new Scene(previousFXML);
		MainGuiController.getInstance().refresh();
		MainGuiController.getInstance().setSceneController(null);
		currentStage = (Stage) button_exit.getScene().getWindow();
		currentStage.setScene(previousScene);
	}

	/**
	 * This method handles the mouse click on the watch button: making it pressed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedWatch(MouseEvent mouseEvent) {
		button_watch.setImage(buttonWatchPressed);
	}

	/**
	 * This method handles the mouse click on the watch button: allowing a player who has lost to watch the match
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mouseReleasedWatch(MouseEvent mouseEvent) {
		button_watch.setImage(buttonWatch);

		fadeImage(button_watch, buttonWatch, 1, 0, 0);
		slidingImage(icon_message, iconLost, 650, 325, 650, -350, 1250);
		button_exit2.toFront();
	}

	/**
	 * This method handles the mouse click on the undo button: making it pressed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click.
	 */
	public void mousePressedUndo(MouseEvent mouseEvent) {
		if(button_undo.getImage().equals(buttonUndo5) || button_undo.getImage().equals(buttonUndo4) || button_undo.getImage().equals(buttonUndo3) || button_undo.getImage().equals(buttonUndo2) || button_undo.getImage().equals(buttonUndo1)) {
			timeline.stop();
			button_undo.setImage(buttonUndoPressed);
		}
	}

	/**
	 * This method handles the mouse click on the undo button: allowing a player to undo the action he has just did.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mouseReleasedUndo(MouseEvent mouseEvent) {
		if(button_undo.getImage().equals(buttonUndoPressed)) {
			button_undo.setImage(buttonUndoDisabled);
			button_undo.setDisable(true);
			if (new Date().getTime()-actionTimestamp <= 5000) {
				boolean workerSelection = false;
				if (performedMove != null) {
					workerSelected = null;
				}
				performedMove = null;
				hasMoved = false;
				if (performedBuild != null) {
					workerSelection = true;
				}
				performedBuild = beforePerformedBuild;
				if (performedBuild == null) {
					button_endTurn.setImage(buttonEndTurnDisabled);
					button_endTurn.setDisable(true);
				}
				gameState.setMap(beforeActionMap);
				updateMap();

				if (workerSelection) {
					NetCell workerSel = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]),Integer.parseInt(workerSelected.getId().split("_")[2]));
					if (workerSel.getBuilding().getLevel() == 0) {
						workerSelected.setImage(colorPlayer().equals(workerRed) ? workerRedPressed : (colorPlayer().equals(workerGreen) ? workerGreenPressed : workerBluePressed));
					} else if (workerSel.getBuilding().getLevel() == 1) {
						workerSelected.setImage(colorPlayer().equals(workerRed) ? build1RedPressed : (colorPlayer().equals(workerGreen) ? build1GreenPressed: build1BluePressed));
					} else if (workerSel.getBuilding().getLevel() == 2) {
						workerSelected.setImage(colorPlayer().equals(workerRed) ? build2RedPressed : (colorPlayer().equals(workerGreen) ? build2GreenPressed : build2BluePressed));
					} else if (workerSel.getBuilding().getLevel() == 3) {
						workerSelected.setImage(colorPlayer().equals(workerRed) ? build3RedPressed : (colorPlayer().equals(workerGreen) ? build3GreenPressed : build3BluePressed));
					}

					if (pressedButtonBuild) {
						NetCell movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
						colorCells(1,movingWorkerCell);
					} else if (pressedButtonDome) {
						NetCell movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
						colorCells(2,movingWorkerCell);
					} else {
						unColorCells();
					}
				}
			}
		}
	}

	/**
	 * This method handles the mouse entry on the info button: sliding up the possibles actions info box.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void MouseEnteredInfo(MouseEvent mouseEvent) {
		box_info.toFront();
		slidingImage(box_info, boxInfo, 345, 400, 345, 130, 450);
	}

	/**
	 * This method handles the mouse entry on the info button: sliding down the possibles actions info box.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void MouseExitedInfo(MouseEvent mouseEvent) {
		slidingImage(box_info, boxInfo, 345, 130, 345, 400, 450);
	}

	/* **********************************************
	 *												*
	 *			METHODS FOR USER INTERACTION		*
	 * 												*
	 ************************************************/

	/**
	 * This method handles the move action of a worker.
	 * @param cellPressed the ImageView of the cell where the player wants to move his worker.
	 * @param worker Image of the worker.
	 * @param workerPressed Image of the pressed worker.
	 * @param build1Worker Image of the level 1 block with a worker on it.
	 * @param build2Worker Image of the level 2 block with a worker on it.
	 * @param build3Worker Image of the level 3 block with a worker on it.
	 * @param build1WorkerPressed Image of the level 1 block with a worker pressed on it.
	 * @param build2WorkerPressed Image of the level 2 block with a worker pressed on it.
	 * @param build3WorkerPressed Image of the level 3 block with a worker pressed on it.
	 */
	private void movingWorker(ImageView cellPressed, Image worker, Image workerPressed, Image build1Worker, Image build2Worker, Image build3Worker, Image build1WorkerPressed, Image build2WorkerPressed, Image build3WorkerPressed){
		if (cellPressed.getImage().equals(worker) || cellPressed.getImage().equals(build1Worker) || cellPressed.getImage().equals(build2Worker) || cellPressed.getImage().equals(build3Worker)) {
			if (!pressedButtonBuild && !pressedButtonDome) {
				if (setWorkers == 2) {
					if (workerSelected != null && (cellPressed.getImage().equals(worker) || cellPressed.getImage().equals(build1Worker) || cellPressed.getImage().equals(build2Worker) || cellPressed.getImage().equals(build3Worker))) {
						swapWorkers(cellPressed, worker, workerPressed, build1Worker, build1WorkerPressed, build2Worker, build2WorkerPressed, build3Worker, build3WorkerPressed);
					} else {
						pressWorker(cellPressed, worker, workerPressed, build1Worker, build1WorkerPressed, build2Worker, build2WorkerPressed, build3Worker, build3WorkerPressed);
					}
				}
			}
		} else if (cellPressed.getImage().equals(workerPressed) || cellPressed.getImage().equals(build1WorkerPressed) || cellPressed.getImage().equals(build2WorkerPressed) || cellPressed.getImage().equals(build3WorkerPressed)) {
			unPressWorker(cellPressed, worker, workerPressed, build1Worker, build1WorkerPressed, build2Worker, build2WorkerPressed, build3Worker, build3WorkerPressed);
		} else if (!cellPressed.getImage().equals(buildDome)) {
			placeWorker(cellPressed, worker, workerPressed, build1Worker, build1WorkerPressed, build2Worker, build2WorkerPressed, build3Worker, build3WorkerPressed);
		} else if (cellPressed.getImage().equals(buildDome)){
			wrongAction(0);
		}
	}

	/**
	 * This method handles the build action on a cell.
	 * @param cellPressed the imageView of the cell interested.
	 * @param blank a blank image used to initialize a cell.
	 * @param build1 Image of the level 1 block with
	 * @param build2 Image of the level 2 block with
	 * @param build3 Image of the level 3 block with
	 * @param buildDome Image of a building with a dome
	 * @param button_build ImageView of the block button.
	 * @param buttonBuild Image of th block button.
	 * @param button_dome ImageView of the dome button.
	 * @param buttonDome Image of th dome button.
	 */
	private void buildOnCell(ImageView cellPressed, Image blank, Image build1, Image build2, Image build3, Image buildDome, ImageView button_build, Image buttonBuild, ImageView button_dome, Image buttonDome) {
		if (workerSelected != null && (pressedButtonBuild || pressedButtonDome)) {
			// player is trying to build
			if (pressedButtonBuild) {
				NetBuild wantedBuild, complexBuild;
				NetMap map = gameState.getMap();
				NetCell cellClicked = map.getCell(Integer.parseInt(cellPressed.getId().split("_")[1]),Integer.parseInt(cellPressed.getId().split("_")[2]));
				NetCell workerSel = map.getCell(Integer.parseInt(workerSelected.getId().split("_")[1]),Integer.parseInt(workerSelected.getId().split("_")[2]));
				wantedBuild = new NetBuild(workerSel.getWorker().getWorkerID(), map.getX(cellClicked), map.getY(cellClicked), cellClicked.getBuilding().getLevel(), cellClicked.getBuilding().isDome());

				if (performedBuild == null) {
					if (gameState.getPossibleBuilds().contains(wantedBuild)) {
						beforePerformedBuild = null;
						performedBuild = wantedBuild;
						button_endTurn.setImage(buttonEndTurn);
						button_endTurn.setDisable(false);

						// player wants to build a building he can build, now the map is updated
						beforeActionMap = gameState.getMap();
						gameState.setMap(map.changeCell(cellClicked.setBuilding(cellClicked.getBuilding().setLevel(cellClicked.getBuilding().getLevel()+1)),map.getX(cellClicked),map.getY(cellClicked)));
						updateMap();
						hasBuilt = true;

						if (workerSel.getBuilding().getLevel() == 0) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? workerRedPressed : (colorPlayer().equals(workerGreen) ? workerGreenPressed : workerBluePressed));
						} else if (workerSel.getBuilding().getLevel() == 1) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? build1RedPressed : (colorPlayer().equals(workerGreen) ? build1GreenPressed: build1BluePressed));
						} else if (workerSel.getBuilding().getLevel() == 2) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? build2RedPressed : (colorPlayer().equals(workerGreen) ? build2GreenPressed : build2BluePressed));
						} else if (workerSel.getBuilding().getLevel() == 3) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? build3RedPressed : (colorPlayer().equals(workerGreen) ? build3GreenPressed : build3BluePressed));
						}
						timerCountdown();

						unColorCells();
						NetCell movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
						colorCells(1,movingWorkerCell);
					} else {
						wrongAction(1);
					}
				} else {
					// builds the NetBuild which is wanted to be performed by the player
					complexBuild = performedBuild.appendOther(wantedBuild);
					if (gameState.getPossibleBuilds().contains(complexBuild)) {
						beforePerformedBuild = performedBuild;
						performedBuild = complexBuild;
						button_endTurn.setImage(buttonEndTurn);
						button_endTurn.setDisable(false);

						// player wants to build a building he can build, now the map is updated
						beforeActionMap = gameState.getMap();
						gameState.setMap(map.changeCell(cellClicked.setBuilding(cellClicked.getBuilding().setLevel(cellClicked.getBuilding().getLevel()+1)),map.getX(cellClicked),map.getY(cellClicked)));
						updateMap();
						hasBuilt = true;

						if (workerSel.getBuilding().getLevel() == 0) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? workerRedPressed : (colorPlayer().equals(workerGreen) ? workerGreenPressed : workerBluePressed));
						} else if (workerSel.getBuilding().getLevel() == 1) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? build1RedPressed : (colorPlayer().equals(workerGreen) ? build1GreenPressed: build1BluePressed));
						} else if (workerSel.getBuilding().getLevel() == 2) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? build2RedPressed : (colorPlayer().equals(workerGreen) ? build2GreenPressed : build2BluePressed));
						} else if (workerSel.getBuilding().getLevel() == 3) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? build3RedPressed : (colorPlayer().equals(workerGreen) ? build3GreenPressed : build3BluePressed));
						}
						timerCountdown();

						unColorCells();
						NetCell movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
						colorCells(1,movingWorkerCell);
					} else {
						wrongAction(1);
					}
				}
			}

			// player is trying to build a dome
			if (pressedButtonDome) {
				NetBuild wantedBuild, complexBuild;
				NetMap map = gameState.getMap();
				NetCell cellClicked = map.getCell(Integer.parseInt(cellPressed.getId().split("_")[1]),Integer.parseInt(cellPressed.getId().split("_")[2]));
				NetCell workerSel = map.getCell(Integer.parseInt(workerSelected.getId().split("_")[1]),Integer.parseInt(workerSelected.getId().split("_")[2]));
				wantedBuild = new NetBuild(workerSel.getWorker().getWorkerID(),map.getX(cellClicked),map.getY(cellClicked),cellClicked.getBuilding().getLevel(),true);

				if (performedBuild == null) {
					if (gameState.getPossibleBuilds().contains(wantedBuild)) {
						beforePerformedBuild = null;
						performedBuild = wantedBuild;
						button_endTurn.setImage(buttonEndTurn);
						button_endTurn.setDisable(false);

						// player wants to build a building he can build, now the map is updated
						beforeActionMap = gameState.getMap();
						gameState.setMap(map.changeCell(cellClicked.setBuilding(cellClicked.getBuilding().setDome(true)),map.getX(cellClicked),map.getY(cellClicked)));
						updateMap();
						hasBuilt = true;

						if (workerSel.getBuilding().getLevel() == 0) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? workerRedPressed : (colorPlayer().equals(workerGreen) ? workerGreenPressed : workerBluePressed));
						} else if (workerSel.getBuilding().getLevel() == 1) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? build1RedPressed : (colorPlayer().equals(workerGreen) ? build1GreenPressed: build1BluePressed));
						} else if (workerSel.getBuilding().getLevel() == 2) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? build2RedPressed : (colorPlayer().equals(workerGreen) ? build2GreenPressed : build2BluePressed));
						} else if (workerSel.getBuilding().getLevel() == 3) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? build3RedPressed : (colorPlayer().equals(workerGreen) ? build3GreenPressed : build3BluePressed));
						}
						timerCountdown();

						unColorCells();
						NetCell movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
						colorCells(2,movingWorkerCell);
					} else {
						wrongAction(1);
					}
				} else {
					// builds the NetBuild which is wanted to be performed by the player
					complexBuild = performedBuild.appendOther(wantedBuild);
					if (gameState.getPossibleBuilds().contains(complexBuild)) {
						beforePerformedBuild = performedBuild;
						performedBuild = complexBuild;
						button_endTurn.setImage(buttonEndTurn);
						button_endTurn.setDisable(false);

						// player wants to build a building he can build, now the map is updated
						beforeActionMap = gameState.getMap();
						gameState.setMap(map.changeCell(cellClicked.setBuilding(cellClicked.getBuilding().setDome(true)),map.getX(cellClicked),map.getY(cellClicked)));
						updateMap();
						hasBuilt = true;

						if (workerSel.getBuilding().getLevel() == 0) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? workerRedPressed : (colorPlayer().equals(workerGreen) ? workerGreenPressed : workerBluePressed));
						} else if (workerSel.getBuilding().getLevel() == 1) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? build1RedPressed : (colorPlayer().equals(workerGreen) ? build1GreenPressed: build1BluePressed));
						} else if (workerSel.getBuilding().getLevel() == 2) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? build2RedPressed : (colorPlayer().equals(workerGreen) ? build2GreenPressed : build2BluePressed));
						} else if (workerSel.getBuilding().getLevel() == 3) {
							workerSelected.setImage(colorPlayer().equals(workerRed) ? build3RedPressed : (colorPlayer().equals(workerGreen) ? build3GreenPressed : build3BluePressed));
						}
						timerCountdown();

						unColorCells();
						NetCell movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
						colorCells(2,movingWorkerCell);
					} else {
						wrongAction(1);
					}
				}
			}
		} else {
			if (workerSelected == null && (pressedButtonDome || pressedButtonBuild)) {
				wrongAction(2);
			}
		}
	}

	/**
	 * This method handles the general move action of a worker.
	 * @param cellPressed the ImageView of the cell where the player wants to move his worker.
	 * @param worker Image of the worker.
	 * @param workerPressed Image of the pressed worker.
	 * @param build1Worker Image of the level 1 block with a worker on it.
	 * @param build2Worker Image of the level 2 block with a worker on it.
	 * @param build3Worker Image of the level 3 block with a worker on it.
	 * @param build1WorkerPressed Image of the level 1 block with a worker pressed on it.
	 * @param build2WorkerPressed Image of the level 2 block with a worker pressed on it.
	 * @param build3WorkerPressed Image of the level 3 block with a worker pressed on it.
	 */
	private void placeWorker(ImageView cellPressed, Image worker, Image workerPressed, Image build1Worker, Image build1WorkerPressed, Image build2Worker, Image build2WorkerPressed, Image build3Worker, Image build3WorkerPressed) {
		boolean possibleToPerform = false;
		NetCell netCellPressed, netWorkerCell, movingWorkerCell;
		NetMove performingMovement = null, currentMove;
		NetMap netMap;
		ImageView cellToChange;

		//if you are placing a worker
		if (workerSelected != null && !pressedButtonDome && !pressedButtonBuild && !hasBuilt) {
			// check if the action can be performed and set the variable possibleToPerform to indicate this
			if (gameState.getTurn().getPhase() == Phase.SETUP) {
				// evaluates if this is a possible move in setup phase
				if (gameState.getMap() != null) {
					netCellPressed = gameState.getMap().getCell(Integer.parseInt(cellPressed.getId().split("_")[1]), Integer.parseInt(cellPressed.getId().split("_")[2]));
					if (netCellPressed.getWorker() == null) {
						possibleToPerform = true;
					} else {
						possibleToPerform = false;
						wrongAction(0);
					}
				} else {
					possibleToPerform = true;
				}
			} else if (gameState.getTurn().getGamePhase() == GamePhase.BEFOREMOVE || gameState.getTurn().getGamePhase() == GamePhase.MOVE) {
				// evaluates if this is a possible move during player's turn
				netWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
				performingMovement = new NetMove(netWorkerCell.getWorker().getWorkerID(),Integer.parseInt(cellPressed.getId().split("_")[1]),Integer.parseInt(cellPressed.getId().split("_")[2]));

				// if there is at least one move of the same type of the move it can be performed
				if (gameState.containsLike(performingMovement) > 0) {
					for (int i = 0; i < gameState.getPossibleMoves().size(); i++) {
						if (gameState.getPossibleMoves().get(i).isLike(performingMovement)) {
							performedMove = gameState.getPossibleMoves().get(i);
							i = gameState.getPossibleMoves().size();
						}
					}
					possibleToPerform = true;
				} else {
					possibleToPerform = false;
				}
			}

			// effectuates the move
			if (possibleToPerform) {
				if (workerSelected.getId().equals("worker1") || workerSelected.getId().equals("worker2")) {
					if (workerSelected.getId().equals("worker1")) {
						worker1StartingPos = new Pair<>(Integer.parseInt(cellPressed.getId().split("_")[1]),Integer.parseInt(cellPressed.getId().split("_")[2]));
					} else {
						worker2StartingPos = new Pair<>(Integer.parseInt(cellPressed.getId().split("_")[1]),Integer.parseInt(cellPressed.getId().split("_")[2]));
					}
					cellPressed.setImage(worker);
					((AnchorPane) workerSelected.getParent()).getChildren().remove(workerSelected);
					workerSelected = null;
					++setWorkers;
					if (setWorkers == 2) {
						slidingImage(box_workers, boxWorkers, 159, 122, 450, 122, 750);
						button_endTurn.toFront();
						button_endTurn.setDisable(true);
						timerInitialize();
						fadeImage(button_endTurn, buttonEndTurnDisabled, 0, 1, 1);
						sendWorkerPositions();
					}
				} else if (performingMovement != null) {
					unColorCells();
					beforeActionMap = gameState.getMap();
					movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
					currentMove = performedMove;
					List<Pair<Integer,Integer>> cellsChanged = new ArrayList<>();
					while (currentMove != null) {
						if (currentMove.other == null || currentMove.other.workerID == currentMove.workerID) {
							// it performs a simple move
							netMap = gameState.getMap();
							netMap = netMap.changeCell(netMap.getCell(currentMove.cellX,currentMove.cellY).setWorker(movingWorkerCell.getWorker()),currentMove.cellX,currentMove.cellY);
							if (cellsChanged.isEmpty()) {
								netMap = netMap.changeCell(movingWorkerCell.setWorker(null),Integer.parseInt(workerSelected.getId().split("_")[1]),Integer.parseInt(workerSelected.getId().split("_")[2]));
							}
							gameState.setMap(netMap);

							// move is finished
							currentMove = null;
						} else {
							// it performs a conditioned move (when there is the necessity to move other workers)
							NetCell nowMoving = movingWorkerCell;

							netMap = gameState.getMap();
							movingWorkerCell = netMap.getCell(currentMove.cellX,currentMove.cellY);
							netMap = netMap.changeCell(nowMoving.setWorker(null),netMap.getX(nowMoving),netMap.getY(nowMoving));
							netMap = netMap.changeCell(netMap.getCell(currentMove.cellX,currentMove.cellY).setWorker(nowMoving.getWorker()),currentMove.cellX,currentMove.cellY);
							cellsChanged.add(new Pair<>(currentMove.cellX,currentMove.cellY));
							gameState.setMap(netMap);

							// move must propagate
							currentMove = currentMove.other;
						}
					}
					updateMap();
					hasMoved = true;
					button_endTurn.setImage(buttonEndTurn);
					button_endTurn.setDisable(false);
					timerCountdown();
				}
			} else {
				wrongAction(0);
			}
		} else {
			if (hasBuilt) {
				wrongAction(0);
			}
		}
	}

	/* **********************************************
	 *												*
	 *		CHANGES TO THE GAME MAP IMAGES			*
	 * 												*
	 ************************************************/

	/**
	 * This method handles the move action of a worker in a cell where is another worker.
	 * @param cellPressed the ImageView of the cell where the player wants to move his worker.
	 * @param worker Image of the worker.
	 * @param workerPressed Image of the pressed worker.
	 * @param build1Worker Image of the level 1 block with a worker on it.
	 * @param build2Worker Image of the level 2 block with a worker on it.
	 * @param build3Worker Image of the level 3 block with a worker on it.
	 * @param build1WorkerPressed Image of the level 1 block with a worker pressed on it.
	 * @param build2WorkerPressed Image of the level 2 block with a worker pressed on it.
	 * @param build3WorkerPressed Image of the level 3 block with a worker pressed on it.
	 */
	private void swapWorkers(ImageView cellPressed, Image worker, Image workerPressed, Image build1Worker, Image build1WorkerPressed, Image build2Worker, Image build2WorkerPressed, Image build3Worker, Image build3WorkerPressed) {
		NetCell movingWorkerCell;
		if (workerSelected.getImage().equals(workerPressed)) {
			workerSelected.setImage(worker);
		} else if (workerSelected.getImage().equals(build1WorkerPressed)) {
			workerSelected.setImage(build1Worker);
		} else if (workerSelected.getImage().equals(build2WorkerPressed)) {
			workerSelected.setImage(build2Worker);
		} else if (workerSelected.getImage().equals(build3WorkerPressed)) {
			workerSelected.setImage(build3Worker);
		}

		workerSelected = cellPressed;
		if (workerSelected.getImage().equals(worker)) {
			workerSelected.setImage(workerPressed);
			unColorCells();
			movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
			colorCells(0,movingWorkerCell);
		} else if (workerSelected.getImage().equals(build1Worker)) {
			workerSelected.setImage(build1WorkerPressed);
			unColorCells();
			movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
			colorCells(0,movingWorkerCell);
		} else if (workerSelected.getImage().equals(build2Worker)) {
			workerSelected.setImage(build2WorkerPressed);
			unColorCells();
			movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
			colorCells(0,movingWorkerCell);
		} else if (workerSelected.getImage().equals(build3Worker)) {
			workerSelected.setImage(build3WorkerPressed);
			unColorCells();
			movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
			colorCells(0,movingWorkerCell);
		}
	}

	/**
	 * This method handles the selection of a worker, making it pressed or unpressed.
	 * @param cellPressed the ImageView of the cell where the player wants to move his worker.
	 * @param worker Image of the worker.
	 * @param workerPressed Image of the pressed worker.
	 * @param build1Worker Image of the level 1 block with a worker on it.
	 * @param build2Worker Image of the level 2 block with a worker on it.
	 * @param build3Worker Image of the level 3 block with a worker on it.
	 * @param build1WorkerPressed Image of the level 1 block with a worker pressed on it.
	 * @param build2WorkerPressed Image of the level 2 block with a worker pressed on it.
	 * @param build3WorkerPressed Image of the level 3 block with a worker pressed on it.
	 */
	private void pressWorker(ImageView cellPressed, Image worker, Image workerPressed, Image build1Worker, Image build1WorkerPressed, Image build2Worker, Image build2WorkerPressed, Image build3Worker, Image build3WorkerPressed) {
		NetCell movingWorkerCell;

		gridPane_cells.toFront();
		if (cellPressed.getImage().equals(worker)) {
			workerSelected = cellPressed;
			workerSelected.setImage(workerPressed);
			movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
			colorCells(0,movingWorkerCell);
		} else if (cellPressed.getImage().equals(build1Worker)) {
			workerSelected = cellPressed;
			workerSelected.setImage(build1WorkerPressed);
			movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
			colorCells(0,movingWorkerCell);
		} else if (cellPressed.getImage().equals(build2Worker)) {
			workerSelected = cellPressed;
			workerSelected.setImage(build2WorkerPressed);
			movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
			colorCells(0,movingWorkerCell);
		} else if (cellPressed.getImage().equals(build3Worker)) {
			workerSelected = cellPressed;
			workerSelected.setImage(build3WorkerPressed);
			movingWorkerCell = gameState.getMap().getCell(Integer.parseInt(workerSelected.getId().split("_")[1]), Integer.parseInt(workerSelected.getId().split("_")[2]));
			colorCells(0,movingWorkerCell);
		}
	}

	/**
	 * This method handles the selection of a worker, making it unpressed.
	 * @param cellPressed the ImageView of the cell where the player wants to move his worker.
	 * @param worker Image of the worker.
	 * @param workerPressed Image of the pressed worker.
	 * @param build1Worker Image of the level 1 block with a worker on it.
	 * @param build2Worker Image of the level 2 block with a worker on it.
	 * @param build3Worker Image of the level 3 block with a worker on it.
	 * @param build1WorkerPressed Image of the level 1 block with a worker pressed on it.
	 * @param build2WorkerPressed Image of the level 2 block with a worker pressed on it.
	 * @param build3WorkerPressed Image of the level 3 block with a worker pressed on it.
	 */
	private void unPressWorker(ImageView cellPressed, Image worker, Image workerPressed, Image build1Worker, Image build1WorkerPressed, Image build2Worker, Image build2WorkerPressed, Image build3Worker, Image build3WorkerPressed) {
		if (cellPressed.getImage().equals(workerPressed)) {
			unColorCells();
			workerSelected = cellPressed;
			workerSelected.setImage(worker);
			workerSelected = null;
		} else if (cellPressed.getImage().equals(build1WorkerPressed)) {
			unColorCells();
			workerSelected = cellPressed;
			workerSelected.setImage(build1Worker);
			workerSelected = null;
		} else if (cellPressed.getImage().equals(build2WorkerPressed)) {
			unColorCells();
			workerSelected = cellPressed;
			workerSelected.setImage(build2Worker);
			workerSelected = null;
		} else if (cellPressed.getImage().equals(build3WorkerPressed)) {
			unColorCells();
			workerSelected = cellPressed;
			workerSelected.setImage(build3Worker);
			workerSelected = null;
		}
	}
	/**
	 * This function press a worker not set up on the map before in the setup phase before starting the game, this does not place it
	 * @param mouseEvent mouse's click
	 */
	public void mousePressedSetWorker(MouseEvent mouseEvent) {
		if (gameState.getTurn().getPhase() == Phase.SETUP) {
			Image workerNormal = colorPlayer();
			Image workerPressed = colorPlayer().equals(workerRed) ? workerRedPressed : (colorPlayer().equals(workerGreen) ? workerGreenPressed : workerBluePressed);

			if (((ImageView) mouseEvent.getTarget()).getImage().equals(workerNormal)) {
				workerSelected = ((ImageView) mouseEvent.getTarget());
				workerSelected.setImage(workerPressed);
				if (((ImageView) mouseEvent.getTarget()).getId().equals("worker1")) {
					pressedWorker1 = true;
					worker2.setImage(workerNormal); //not pressed
					pressedWorker2 = false;
				} else { //worker2
					pressedWorker2 = true;
					worker1.setImage(workerNormal);
					pressedWorker1 = false;
				}
			} else {
				workerSelected = null; //deselect
				((ImageView) mouseEvent.getTarget()).setImage(workerNormal);
				pressedWorker1 = false;
				pressedWorker2 = false;
			}
		}
	}

	/* **********************************************
	 *												*
	 *		ANIMATIONS FOR THE GAME MAP				*
	 * 												*
	 ************************************************/

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
	 * This method creates a slide transition of an image.
	 * @param imageView the ImageView that has to be slided.
	 * @param image the Image to set in the ImageView.
	 * @param x1 initial x coordinate.
	 * @param y1 initial y coordinate.
	 * @param x2 final x coordinate.
	 * @param y2 final y coordinate.
	 * @param duration duration of the transtion.
	 * @return the sliding transition
	 */
	private Transition setSlidingImage(ImageView imageView, Image image, int x1, int y1, int x2, int y2, int duration){
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
		return transition;
	}

	/**
	 * This method creates a slide transition of the name player text.
	 * @param text the Text object of the name player.
	 * @param x1 initial x coordinate.
	 * @param y1 initial y coordinate.
	 * @param x2 final x coordinate.
	 * @param y2 final y coordinate.
	 * @param duration duration of the transtion.
	 */
	private void slidingText(Text text, int x1, int y1, int x2, int y2, int duration) {
		Line line = new Line();
		line.setStartX(x1);
		line.setStartY(y1);
		line.setEndX(x2);
		line.setEndY(y2);
		PathTransition transition = new PathTransition();
		transition.setNode(text);
		transition.setDuration(Duration.millis(duration));
		transition.setPath(line);
		transition.setCycleCount(1);
		transition.play();
	}

	/* **********************************************
	 *												*
	 *		SUPPORT METHODS USED TO HANDLE INPUT	*
	 * 												*
	 ************************************************/
	/**
	 * This method get the imageview of the cell required.
	 * @param x the x coordinate of the cell.
	 * @param y the y coordinate of the cell.
	 * @return the imageview of the cell required.
	 */
	private ImageView getCell(int x, int y) {
		switch (x) {
			case 0 -> {
				switch (y) {
					case 0 -> { return cell_0_0; }
					case 1 -> { return cell_0_1; }
					case 2 -> { return cell_0_2; }
					case 3 -> { return cell_0_3; }
					case 4 -> { return cell_0_4; }
				}
			}
			case 1 -> {
				switch (y) {
					case 0 -> { return cell_1_0; }
					case 1 -> { return cell_1_1; }
					case 2 -> { return cell_1_2; }
					case 3 -> { return cell_1_3; }
					case 4 -> { return cell_1_4; }
				}
			}
			case 2 -> {
				switch (y) {
					case 0 -> { return cell_2_0; }
					case 1 -> { return cell_2_1; }
					case 2 -> { return cell_2_2; }
					case 3 -> { return cell_2_3; }
					case 4 -> { return cell_2_4; }
				}
			}
			case 3 -> {
				switch (y) {
					case 0 -> { return cell_3_0; }
					case 1 -> { return cell_3_1; }
					case 2 -> { return cell_3_2; }
					case 3 -> { return cell_3_3; }
					case 4 -> { return cell_3_4; }
				}
			}
			case 4 -> {
				switch (y) {
					case 0 -> { return cell_4_0; }
					case 1 -> { return cell_4_1; }
					case 2 -> { return cell_4_2; }
					case 3 -> { return cell_4_3; }
					case 4 -> { return cell_4_4; }
				}
			}
		}
		throw new AssertionError("Controller is trying to access a cell which does not exists");
	}

	/**
	 * This method get the imageview of the possible cell required.
	 * @param x the x coordinate of the cell.
	 * @param y the y coordinate of the cell.
	 * @return the imageview of the cell required.
	 */
	private ImageView getPossibleCell(int x, int y){
		switch (x) {
			case 0 -> {
				switch (y) {
					case 0 -> {
						return possibleCell_0_0;
					}
					case 1 -> {
						return possibleCell_0_1;
					}
					case 2 -> {
						return possibleCell_0_2;
					}
					case 3 -> {
						return possibleCell_0_3;
					}
					case 4 -> {
						return possibleCell_0_4;
					}
				}
			}
			case 1 -> {
				switch (y) {
					case 0 -> {
						return possibleCell_1_0;
					}
					case 1 -> {
						return possibleCell_1_1;
					}
					case 2 -> {
						return possibleCell_1_2;
					}
					case 3 -> {
						return possibleCell_1_3;
					}
					case 4 -> {
						return possibleCell_1_4;
					}
				}
			}
			case 2 -> {
				switch (y) {
					case 0 -> {
						return possibleCell_2_0;
					}
					case 1 -> {
						return possibleCell_2_1;
					}
					case 2 -> {
						return possibleCell_2_2;
					}
					case 3 -> {
						return possibleCell_2_3;
					}
					case 4 -> {
						return possibleCell_2_4;
					}
				}
			}
			case 3 -> {
				switch (y) {
					case 0 -> {
						return possibleCell_3_0;
					}
					case 1 -> {
						return possibleCell_3_1;
					}
					case 2 -> {
						return possibleCell_3_2;
					}
					case 3 -> {
						return possibleCell_3_3;
					}
					case 4 -> {
						return possibleCell_3_4;
					}
				}
			}
			case 4 -> {
				switch (y) {
					case 0 -> {
						return possibleCell_4_0;
					}
					case 1 -> {
						return possibleCell_4_1;
					}
					case 2 -> {
						return possibleCell_4_2;
					}
					case 3 -> {
						return possibleCell_4_3;
					}
					case 4 -> {
						return possibleCell_4_4;
					}
				}
			}
		}
		throw new AssertionError("Controller is trying to access a cell which does not exists");
	}

	/* **********************************************
	 *												*
	 *		MESSAGE BUILDERS TO SEND TO SERVER		*
	 * 												*
	 ************************************************/
	/**
	 * This method sends the information about the wanted position for client's workers' positions
	 */
	private void sendWorkerPositions() {
		button_exit.getScene().setCursor(Cursor.WAIT);
		waitingResponse = true;
		NetGameSetup workerPositions = new NetGameSetup(Constants.GAMESETUP_IN_PLACE,gameState.getPlayer(),worker1StartingPos,worker2StartingPos);
		MainGuiController.getInstance().sendMessage(workerPositions);
		workerSelected = null;
	}
	/**
	 * This method sends the information about the performed move of the player for the current round
	 * @param move the wanted move
	 */
	private void sendWorkerMove(NetMove move) {
		button_exit.getScene().setCursor(Cursor.WAIT);
		waitingResponse = true;
		button_build.setDisable(false);
		button_dome.setDisable(false);
		NetGaming movePerformed = new NetGaming(Constants.PLAYER_IN_MOVE,gameState.getPlayer(),move);
		MainGuiController.getInstance().sendMessage(movePerformed);
		workerSelected = null;
		performedMove = null;
	}
	/**
	 * This method sens the information about the performed build of the player for the current round
	 */
	private void sendWorkerBuild() {
		unColorCells();
		button_exit.getScene().setCursor(Cursor.WAIT);
		waitingResponse = true;
		button_build.setDisable(true);
		button_dome.setDisable(true);
		NetGaming buildPerformed = new NetGaming(Constants.PLAYER_IN_BUILD,gameState.getPlayer(),performedBuild);
		MainGuiController.getInstance().sendMessage(buildPerformed);
		beforePerformedBuild = null;
		performedBuild = null;
		workerSelected = null;
		pressedButtonDome = false;
		pressedButtonBuild = false;
		hasBuilt = false;
		button_endTurn.setDisable(true);
		button_endTurn.setImage(buttonEndTurnDisabled);
		button_dome.setImage(buttonDome);
		button_build.setImage(buttonBuild);
	}

	/* **********************************************
	 *												*
	 *		EVENTS AFTER SERVER MESSAGE				*
	 * 												*
	 ************************************************/

	/**
	 * This method displays the name of the active player.
	 */
	private void setActivePlayer() {
		text_player.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 18));
		text_player.setText(gameState.getActivePlayer());
	}

	/**
	 * Highlights cells for the selected action
	 * @param typeAction 0 is a move, 1 is a normal building, 2 is a dome
	 * @param workerCell cell where is the worker
	 */
	private void colorCells(int typeAction, NetCell workerCell) {
		if (workerCell != null) {
			NetWorker movingWorker = workerCell.worker;

			if (movingWorker != null) {
				if (typeAction == 0) {
					for (NetMove m : gameState.getPossibleMoves()) {
						if (m.workerID == movingWorker.workerID) {
							getPossibleCell(m.cellX, m.cellY).setImage(cellTarget);
						}
					}
				} else if (typeAction == 1) {
					if (performedBuild == null) {
						for (NetBuild b : gameState.getPossibleBuilds()) {
							if (b.workerID == movingWorker.workerID && !b.dome) {
								getPossibleCell(b.cellX, b.cellY).setImage(cellTarget);
							}
						}
					} else {
						unColorCells();
						for (NetBuild b : gameState.getPossibleBuilds()) {
							// only if there are not more than 2 concatenations
							if (b.isLikeStrong(performedBuild) && b.other != null && performedBuild.other == null && !b.other.dome) {
								getPossibleCell(b.other.cellX, b.other.cellY).setImage(cellTarget);
							}
						}
					}
				} else if (typeAction == 2) {
					if (performedBuild == null) {
						for (NetBuild b : gameState.getPossibleBuilds()) {
							if (b.workerID == movingWorker.workerID && b.dome) {
								getPossibleCell(b.cellX, b.cellY).setImage(cellTarget);
							}
						}
					} else {
						unColorCells();
						for (NetBuild b : gameState.getPossibleBuilds()) {
							// only if there are not more than 2 concatenations
							if (b.isLikeStrong(performedBuild) && b.other != null && performedBuild.other == null && b.other.dome) {
								getPossibleCell(b.other.cellX, b.other.cellY).setImage(cellTarget);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Remove highlighting from possible cells used for the action
	 */
	private void unColorCells() {
		for (int i = 0; i < Constants.MAP_SIDE; i++) {
			for (int j = 0; j < Constants.MAP_SIDE; j++) {
				getPossibleCell(i,j).setImage(blank);
			}
		}
	}

	/**
	 * This method updates the map when a new building has been built or a worker has moved.
	 */
	private void updateMap() {
		NetMap map = gameState.getMap();

		for (int i = 0; i < Constants.MAP_SIDE; i++) {
			for (int j = 0; j < Constants.MAP_SIDE; j++) {
				if (map.getCell(i,j).getWorker() == null) {
					// sets the correct image for cells where there isn't a worker
					if (map.getCell(i,j).getBuilding().isDome()) {
						getCell(i,j).setImage(buildDome);
					} else {
						switch (map.getCell(i, j).getBuilding().getLevel()) {
							case 0 -> {
								getCell(i,j).setImage(blank);
							}
							case 1 -> {
								getCell(i,j).setImage(build1);
							}
							case 2 -> {
								getCell(i,j).setImage(build2);
							}
							case 3 -> {
								getCell(i,j).setImage(build3);
							}
						}
					}
				} else {
					// sets the image for cells where there are workers
					Color playerColor = gameState.getColors().get(map.getCell(i,j).getWorker().getOwner());
					Image workerImg;

					switch (map.getCell(i, j).getBuilding().getLevel()) {
						case 0 -> {
							workerImg = playerColor.equals(Color.RED) ? workerRed : (playerColor.equals(Color.GREEN) ? workerGreen : workerBlue);
							getCell(i,j).setImage(workerImg);
						}
						case 1 -> {
							workerImg = playerColor.equals(Color.RED) ? build1Red : (playerColor.equals(Color.GREEN) ? build1Green : build1Blue);
							getCell(i,j).setImage(workerImg);
						}
						case 2 -> {
							workerImg = playerColor.equals(Color.RED) ? build2Red : (playerColor.equals(Color.GREEN) ? build2Green : build2Blue);
							getCell(i,j).setImage(workerImg);
						}
						case 3 -> {
							workerImg = playerColor.equals(Color.RED) ? build3Red : (playerColor.equals(Color.GREEN) ? build3Green : build3Blue);
							getCell(i,j).setImage(workerImg);
						}
					}
				}
			}
		}
	}
	/**
	 * This method displays a pop up message which notify the player according to "i" parameter.
	 * @param i 0 if it is a wrong move, 1 if is a wrong build, 2 user is trying to build without selecting a worker.
	 */
	private void wrongAction(int i) {
		if(i == 0){
			icon_error.toFront();
			moveImage(icon_error, errorMove, 600, 212, 198, 212, 198, 212, 211, 212, 211, 212, 198, 212, 198,212, 600, 212, 700, 1000, 1000, 500);
		} else if(i == 1) {
			icon_error.toFront();
			moveImage(icon_error, errorBuild, 600, 212, 198, 212, 198, 212, 211, 212, 211, 212, 198, 212, 198,212, 600, 212, 700, 1000, 1000, 500);
		} else {
			icon_error.toFront();
			moveImage(icon_error, errorSelectWorker, 600, 212, 198, 212, 198, 212, 211, 212, 211, 212, 198, 212, 198,212, 600, 212, 700, 1000, 1000, 500);
		}
	}
	/**
	 *This method displays a pop up message which notify the player according to "i" parameter.
	 * @param i 1 if he must wait its turn, 0 if he must wait server response
	 */
	private void waitAction(int i) {
		icon_error.toFront();
		moveImage(icon_error, errorWait, 600, 212, 198, 212, 198, 212, 211, 212, 211, 212, 198, 212, 198,212, 600, 212, 700, 1000, 1000, 500);
		icon_error.toBack();
	}

	/**
	 * This method displays on all client screens who has lost.
	 * @param name the name of the looser.
	 */
	private void playerLost(String name) {
		if (gameState.getPlayer().equals(name)) {
			fadeImage(BG_message, BGwatch, 0, 1, 0);
			slidingImage(icon_message, iconLost, 650, 0, 650, 325, 1250);
			BG_message.toFront();
			icon_message.toFront();
			button_exit2.toFront();
			button_watch.toFront();
			button_exit2.setImage(buttonExit);
			button_watch.setImage(buttonWatch);

		} else {
			fadeImage(icon_message, iconOtherLost, 0, 1, 1);
			text_playerMessageLost.toFront();
			SequentialTransition iconLoose = new SequentialTransition(setSlidingImage(icon_message, iconOtherLost, 650, 0, 650, 325, 1750), setFadeText(text_playerMessageLost,0,1,1, 1250), setFadeText(text_playerMessageLost,1,0,1, 1250), setSlidingImage(icon_message, iconOtherLost, 650, 325, 650, -500, 1750));
			iconLoose.play();

			text_playerMessageLost.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 18));
			text_playerMessageLost.setText(name);
		}
	}

	/**
	 * This method displays on all the client screens who has won.
	 * @param name the name of the winner.
	 */
	private void playerWon(String name) {
		if (gameState.getPlayer().equals(name)) {
			fadeImage(BG_message, BGwatch, 0, 1, 0);
			fadeImage(button_exit2, buttonExit, 0, 1, 1);
			slidingImage(icon_message, iconWon, 650, 0, 650, 325, 1250);
			BG_message.toFront();
			icon_message.toFront();
			button_exit2.toFront();
		} else {
			button_watch.toBack();
			fadeImage(BG_message, BGwatch, 0, 1, 1);
			slidingImage(icon_message, iconOtherWon, 650, 0, 650, 360, 1250);
			fadeImage(button_exit2, buttonExit, 0, 1, 1);
			BG_message.toFront();
			icon_message.toFront();
			button_exit2.toFront();
			text_playerMessage.toFront();
			fadeText(text_playerMessage, 0, 1);
			text_playerMessage.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 18));
			text_playerMessage.setText(name);
		}
	}

	/**
	 * This method displays on all the client screens who has disconnected.
	 * @param name the name of the player disconnected.
	 */
	private void playerDisconnected(String name) {
		if (!gameState.getPlayer().equals(name)) {
			fadeImage(BG_message, BGwatch, 0, 1, 1);
			fadeImage(icon_message, iconDisconnected, 0, 1, 1);
			text_playerMessageLost.toFront();
			SequentialTransition iconDisconnect = new SequentialTransition(setSlidingImage(icon_message, iconDisconnected, 650, 0, 650, 325, 1750), setFadeText(text_playerMessageLost, 0, 1, 1, 1250));
			iconDisconnect.play();

			text_playerMessageLost.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 18));
			text_playerMessageLost.setText(name);
			button_exit2.toFront();
			button_exit2.setImage(buttonExit);
		}
	}

	/**
	 * This method displays a pop up message which notify the player according to notify parameter.
	 * @param reason 0 if a player disconnected during the setup, 1 if the server has crashed
	 */
	private void gameCantContinue(int reason) {
		if(reason == 0){
			fadeImage(BG_message, errorFatalBG, 0, 1, 1);
			slidingImage(icon_message, errorSomeoneDisconnected, 650, 0, 650, 325, 1250);
			BG_message.toFront();
			icon_message.toFront();
			button_exit.toFront();
			button_exit2.toFront();
			button_exit2.setImage(buttonExit);
		} else {
			fadeImage(BG_message, errorFatalBG, 0, 1, 1);
			slidingImage(icon_message, errorFatal, 650, 0, 650, 325, 1250);
			BG_message.toFront();
			icon_message.toFront();
			button_exit.toFront();

			button_exit2.toFront();
			button_exit2.setImage(buttonExit);
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
			case Constants.GENERAL_PHASE_UPDATE -> {
				if (gameState.getTurn().getPhase() == Phase.SETUP) {
					transitioningFromSetupToPlay = true;
				}
				gameState.advancePhase();
				if (gameState.getTurn().getGamePhase() == GamePhase.BEFOREMOVE && gameState.getPlayer().equals(gameState.getActivePlayer())) {
					button_dome.setDisable(false);
					button_build.setDisable(false);
				} else if (gameState.getTurn().getGamePhase() == GamePhase.MOVE && gameState.getPlayer().equals(gameState.getActivePlayer())) {
					button_dome.setDisable(true);
					button_build.setDisable(true);
				} else if (gameState.getTurn().getGamePhase() == GamePhase.BUILD && gameState.getPlayer().equals(gameState.getActivePlayer())) {
					button_dome.setDisable(false);
					button_build.setDisable(false);
				}
				button_endTurn.setImage(buttonEndTurnDisabled);
				button_endTurn.setDisable(true);
			}
			case Constants.TURN_PLAYERTURN -> {
				button_exit.getScene().setCursor(Cursor.DEFAULT);
				if (gameState.getTurn().getPhase() == Phase.SETUP) {
					NetGameSetup gameSetupMessage = (NetGameSetup) message;
					gameState.setActivePlayer(gameSetupMessage.player);
				} else {
					NetGaming gamingMessage = (NetGaming) message;
					gameState.setActivePlayer(gamingMessage.player);
					if (transitioningFromSetupToPlay) {
						if (gameState.getPlayer().equals(gameState.getActivePlayer())) {
							button_dome.setDisable(false);
							button_build.setDisable(false);
						}
						transitioningFromSetupToPlay = false;
					}
				}
				setActivePlayer();
				button_endTurn.setImage(buttonEndTurnDisabled);
				card_god.setImage(godPlayer());
			}
			case Constants.GENERAL_GAMEMAP_UPDATE -> {
				button_exit.getScene().setCursor(Cursor.DEFAULT);
				waitingResponse = false;
				if (gameState.getTurn().getPhase() == Phase.SETUP) {
					NetGameSetup gameSetupMessage = (NetGameSetup) message;
					gameState.setMap(gameSetupMessage.gameMap);
				} else {
					NetGaming gamingMessage = (NetGaming) message;
					gameState.setMap(gamingMessage.gameMap);
				}
				updateMap();
			}
			case Constants.GENERAL_SETUP_DISCONNECT -> {
				NetGameSetup currentMsg = (NetGameSetup)message;
				finished = true;
				button_build.setDisable(true);
				button_dome.setDisable(true);
				button_endTurn.setDisable(true);
				button_undo.setDisable(true);
				gameCantContinue(0);
			}
			case Constants.GENERAL_PLAYER_DISCONNECTED -> {
				NetGaming netGaming = (NetGaming) message;
				gameState.removePlayer(netGaming.player);
				finished = true;
				button_build.setDisable(true);
				button_dome.setDisable(true);
				button_endTurn.setDisable(true);
				button_undo.setDisable(true);
				playerDisconnected(netGaming.player);
			}
			case Constants.GENERAL_WINNER -> {
				NetGaming netGaming = (NetGaming) message;
				finished = true;
				button_build.setDisable(true);
				button_dome.setDisable(true);
				button_endTurn.setDisable(true);
				button_undo.setDisable(true);
				playerWon(netGaming.player);
			}
			case Constants.GENERAL_DEFEATED -> {
				NetGaming netGaming = (NetGaming) message;
				gameState.removePlayer(netGaming.player);
				playerLost(netGaming.player);
			}
			case Constants.PLAYER_ACTIONS -> {
				gameState.setPossibleBuilds((NetGaming)message);
				gameState.setPossibleMoves((NetGaming)message);
			}
		}
	}

}