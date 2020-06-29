package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.*;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * This class implements the choosing starter player scene of the GUI.
 */
public class ChooseStarterSceneController implements SceneController {
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
	@FXML
	private ImageView icon_errorFatalBG;
	@FXML
	private ImageView icon_errorFatal;
	@FXML
	private ImageView icon_error;

	Image buttonNextPressed = new Image("/img/home_next_btn_pressed.png");
	Image buttonNext = new Image("/img/home_next_btn.png");
	Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	Image buttonExit = new Image("/img/home_exit_btn.png");

	Image buttonSelect = new Image("/img/choose_starter_optionBtn.png");
	Image buttonSelectPressed = new Image("/img/choose_starter_selectedBtn.png");
	Image errorFatalBG = new Image("/img/errorFatal_background.png");
	Image errorFatal = new Image("/img/error_fatal.png");
	Image errorSomeoneDisconnected = new Image("/img/message_someoneDisconnected.png");
	Image errorChooseStarter = new Image("/img/error_chooseStarter.png");

	// objects used to change scene
	private Parent previousFXML;
	private Parent nextFXML;
	private Scene previousScene;
	private Scene nextScene;
	private Stage currentStage;

	// triggers for server messages
	private GameState gameState;
	private int selectedPlayer = -1;
	private boolean finished = false;

	public void initialize(){
		MainGuiController.getInstance().setSceneController(this);
		gameState = MainGuiController.getInstance().getGameState();

		if(gameState.getPlayerNumber() == 2) {
			text_3.setDisable(true);
			player_3rd.setVisible(false);
			button_select3.setVisible(false);
			button_select3.setDisable(true);
		}
		text_1.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
		text_1.setText(gameState.getPlayers().get(0));
		text_2.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
		text_2.setText(gameState.getPlayers().get(1));
		if (player_3rd.isVisible()) {
			text_3.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 34));
			text_3.setText(gameState.getPlayers().get(2));
		} else {
			((AnchorPane)button_exit.getParent()).getChildren().remove(text_3);
			((AnchorPane)button_exit.getParent()).getChildren().remove(player_3rd);
			((AnchorPane)button_exit.getParent()).getChildren().remove(button_select3);
		}

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
	 * This method handles the mouse click on the first player displayed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedSelect1(MouseEvent mouseEvent) {
		button_select1.setImage(buttonSelectPressed);
		button_select2.setImage(buttonSelect);
		button_select3.setImage(buttonSelect);
		selectedPlayer = 0;
	}

	/**
	 * This method handles the mouse click on the second player displayed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedSelect2(MouseEvent mouseEvent) {
		button_select1.setImage(buttonSelect);
		button_select2.setImage(buttonSelectPressed);
		button_select3.setImage(buttonSelect);
		selectedPlayer = 1;
	}

	/**
	 * This method handles the mouse click on the third player displayed.
	 * @param mouseEvent the MouseEvent that allows to analyze the information of the mouse click
	 */
	public void mousePressedSelect3(MouseEvent mouseEvent) {
		button_select1.setImage(buttonSelect);
		button_select2.setImage(buttonSelect);
		button_select3.setImage(buttonSelectPressed);
		selectedPlayer = 2;
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
			if (selectedPlayer == -1) {
				wrongPlayerSelection();
			} else {
				// the challenger sends to the server the information about the starter player
				((ImageView) mouseEvent.getTarget()).getScene().setCursor(Cursor.WAIT);
				NetDivinityChoice godMessage = new NetDivinityChoice(Constants.GODS_IN_START_PLAYER, gameState.getPlayer(), gameState.getPlayers().get(selectedPlayer), true);
				MainGuiController.getInstance().sendMessage(godMessage);
			}
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
	 * This method displays a pop up message which notify the player to choose a starter player.
	 */
	public void wrongPlayerSelection() {
		icon_error.toFront();
		moveImage(icon_error, errorChooseStarter, 600, 212, 198, 212, 198, 212, 211, 212, 211, 212, 198, 212, 198,212, 600, 212, 700, 1000, 1000, 500);
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
			case Constants.GODS_STARTER -> {
				gameState.setStarter(((NetDivinityChoice)message).getPlayer());
			}
			case Constants.GENERAL_PHASE_UPDATE -> {
				gameState.advancePhase();
			}
			case Constants.TURN_PLAYERTURN -> {
				gameState.setActivePlayer(((NetGameSetup)message).player);

				nextFXML = FXMLLoader.load(getClass().getResource("/fxml/map.fxml"));
				nextScene = new Scene(nextFXML);
				currentStage = (Stage) button_exit.getScene().getWindow();
				currentStage.setScene(nextScene);
			}
			case Constants.GENERAL_SETUP_DISCONNECT -> {
				finished = true;
				gameCantContinue(0);
			}
		}
	}
}
