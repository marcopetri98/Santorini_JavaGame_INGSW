package it.polimi.ingsw.ui.gui.controller;

import com.sun.tools.javac.Main;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Constants;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class NicknameServerAddressSceneController implements SceneController {
	@FXML
	private TextField textField_nickname;
	@FXML
	private TextField textField_address;
	@FXML
	private ImageView button_next;
	@FXML
	private ImageView button_exit;
	@FXML
	private ImageView icon_error;
	@FXML
	private ImageView icon_errorFatalBG;
	@FXML
	private ImageView icon_errorFatal;
	@FXML
	private AnchorPane anchorPane;

	private Image buttonNextPressed = new Image("/img/home_next_btn_pressed.png");
	private Image buttonNext = new Image("/img/home_next_btn.png");
	private Image buttonExitPressed = new Image("/img/home_exit_btn_pressed.png");
	private Image buttonExit = new Image("/img/home_exit_btn.png");
	private Parent previousFXML;
	private Parent nextFXML;
	private Parent creatorFXML;
	private Scene previousScene;
	private Scene nextScene;
	private Scene creatorScene;
	private Stage currentStage;
	private Image errorNickname = new Image("/img/error_invalidNickname.png");
	private Image errorNicknameSame = new Image("/img/error_invalidNickname_same.png");
	private Image errorIP = new Image("/img/error_invalidIP.png");
	private Image errorSupportIp = new Image("/img/error_connectIP.png");
	private Image errorWait = new Image("/img/error_wait.png");
	private Image errorFatalBG = new Image("/img/errorFatal_background.png");
	private Image errorFatal = new Image("/img/error_fatal.png");

	// triggers for server messages
	private boolean messageCanBeSent = true;
	private boolean connectedToServer = false;
	private String nameChosen;
	private GameState gameState;

	public void initialize() {
		MainGuiController.getInstance().setSceneController(this);
		gameState = MainGuiController.getInstance().getGameState();
		icon_error.setImage(null);
		icon_errorFatal.setImage(null);
		icon_errorFatalBG.setImage(null);
		icon_errorFatalBG.setDisable(true);
		icon_errorFatal.setDisable(true);
	}

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

	private void moveImage(ImageView imageView, Image image, int x1_1, int y1_1, int x2_1, int y2_1, int x1_2, int y1_2, int x2_2, int y2_2, int x1_3, int y1_3, int x2_3, int y2_3, int x1_4, int y1_4, int x2_4, int y2_4, int duration1, int duration2, int duration3, int duration4) {
		imageView.setImage(image);

		Line line1 = new Line();
		Line line2 = new Line();
		Line line3 = new Line();
		Line line4 = new Line();

		SequentialTransition sequential = new SequentialTransition(setLine(imageView, line1, x1_1, y1_1, x2_1, y2_1, duration1), setLine(imageView, line2, x1_2, y1_2, x2_2, y2_2, duration2), setLine(imageView, line3, x1_3, y1_3, x2_3, y2_3, duration3), setLine(imageView, line4, x1_4, y1_4, x2_4, y2_4, duration4));
		sequential.play();
	}


	/* **********************************************
	 *												*
	 *			HANDLERS OF USER INTERACTION		*
	 * 												*
	 ************************************************/
	public void mousePressedNext(MouseEvent mouseEvent) throws IOException {
		button_next.setImage(buttonNextPressed);
	}
	public void mouseReleasedNext(MouseEvent mouseEvent) {
		button_next.setImage(buttonNext);

		AnchorPane anchorPane = (AnchorPane) ((ImageView)mouseEvent.getTarget()).getParent();
		TextField nicknameField = (TextField) anchorPane.lookup("#textField_nickname");
		TextField serverAddressField = (TextField) anchorPane.lookup("#textField_address");
		String nickname = nicknameField.getText();
		String serverAddress = serverAddressField.getText();
		String[] split;

		if (!messageCanBeSent) {
			waitError();
		} else if (nickname.split(" ").length > 1 || nickname.length() > Constants.MAX_NICKNAME_LEN || nickname.length() < Constants.MIN_NICKNAME_LEN) {
			nicknameError(0);
		} else if (serverAddress.split("\\.").length != 4) {
			serverAddressError(0);
		} else if (!Constants.isNumber(serverAddress.split("\\.")[0]) || !Constants.isNumber(serverAddress.split("\\.")[1]) || !Constants.isNumber(serverAddress.split("\\.")[2]) || !Constants.isNumber(serverAddress.split("\\.")[3])) {
			serverAddressError(0);
		} else if (Integer.parseInt(serverAddress.split("\\.")[0]) > 255 || Integer.parseInt(serverAddress.split("\\.")[0]) < 0 || Integer.parseInt(serverAddress.split("\\.")[1]) > 255 || Integer.parseInt(serverAddress.split("\\.")[1]) < 0 || Integer.parseInt(serverAddress.split("\\.")[2]) > 255 || Integer.parseInt(serverAddress.split("\\.")[2]) < 0 || Integer.parseInt(serverAddress.split("\\.")[3]) > 255 || Integer.parseInt(serverAddress.split("\\.")[3]) < 0) {
			serverAddressError(0);
		} else {
			// here the data inserted by the user are correct and a request to the server can be sent
			messageCanBeSent = false;
			nameChosen = nickname;
			((ImageView)mouseEvent.getTarget()).getScene().setCursor(Cursor.WAIT);
			NetSetup netSetupMessage = new NetSetup(Constants.SETUP_IN_PARTICIPATE,nickname);

			if (!connectedToServer) {
				if (MainGuiController.getInstance().connectToServer(serverAddress)) {
					connectedToServer = true;
					MainGuiController.getInstance().sendMessage(netSetupMessage);
				} else {
					serverAddressError(1);
					messageCanBeSent = true;
					button_exit.getScene().setCursor(Cursor.DEFAULT);
				}
			} else {
				MainGuiController.getInstance().sendMessage(netSetupMessage);
			}
		}
	}
	public void mousePressedExit(MouseEvent mouseEvent) throws IOException {
		button_exit.setImage(buttonExitPressed);
		previousFXML = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
		previousScene = new Scene(previousFXML);
	}
	public void mouseReleasedExit(MouseEvent mouseEvent) throws IOException {
		button_exit.setImage(buttonExit);

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
	 *
	 * @param type 0 if it receives an error from the gui, 1 if it receives an error from the server
	 */
	public void nicknameError(int type) {
		if(type == 0) {
			icon_errorFatal.setDisable(false);
			moveImage(icon_error, errorNickname, 600, 212, 198, 212, 198, 212, 220, 212, 220, 212, 198, 212, 198, 212, 600, 212, 700, 1000, 1000, 500);
			button_next.toFront();
			textField_nickname.toFront();
			textField_address.toFront();
		} else {
			icon_errorFatal.setDisable(false);
			moveImage(icon_error, errorNicknameSame, 600, 212, 198, 212, 198, 212, 220, 212, 220, 212, 198, 212, 198,212, 600, 212, 700, 1000, 1000, 500);
			button_next.toFront();
			textField_nickname.toFront();
			textField_address.toFront();
		}
		messageCanBeSent = true;
	}
	public void serverAddressError(int i) {
		icon_errorFatal.setDisable(false);
		if (i == 0) {
			moveImage(icon_error, errorIP, 600, 212, 198, 212, 198, 212, 220, 212, 220, 212, 198, 212, 198,212, 600, 212, 700, 1000, 1000, 500);
			button_next.toFront();
			textField_nickname.toFront();
			textField_address.toFront();
		} else if (i == 1) {
			moveImage(icon_error, errorSupportIp, 600, 212, 198, 212, 198, 212, 220, 212, 220, 212, 198, 212, 198,212, 600, 212, 700, 1000, 1000, 500);
			button_next.toFront();
			textField_nickname.toFront();
			textField_address.toFront();
		}
	}
	public void waitError() {
		icon_errorFatalBG.setDisable(false);
		icon_errorFatal.setDisable(false);
		moveImage(icon_error, errorWait, 600, 212, 198, 212, 198, 212, 220, 212, 220, 212, 198, 212, 198,212, 600, 212, 700, 1000, 1000, 500);
		button_next.toFront();
		button_exit.toFront();
		textField_nickname.toFront();
		textField_address.toFront();
	}


	/* **********************************************
	 *												*
	 *		METHODS CALLED BY MAIN CONTROLLER		*
	 * 												*fad
	 ************************************************/
	@Override
	public void fatalError() {

	}
	@Override
	public void deposeMessage(NetObject message) throws IOException {
		switch (message.message) {
			case Constants.SETUP_OUT_CONNWORKED, Constants.SETUP_OUT_CONNFINISH -> {
				gameState.setPlayerNumber(((NetSetup) message).number);
				gameState.setPlayer(nameChosen);
				nextFXML = FXMLLoader.load(getClass().getResource("/fxml/lobby.fxml"));
				nextScene = new Scene(nextFXML);
				currentStage = (Stage) button_exit.getScene().getWindow();
				currentStage.setScene(nextScene);
			}
			case Constants.SETUP_CREATE -> {
				gameState.setPlayer(nameChosen);
				creatorFXML = FXMLLoader.load(getClass().getResource("/fxml/choose_numPlayer.fxml"));
				creatorScene = new Scene(creatorFXML);
				currentStage = (Stage) button_exit.getScene().getWindow();
				currentStage.setScene(creatorScene);
			}
			case Constants.SETUP_OUT_CONNFAILED -> {
				messageCanBeSent = true;
				nameChosen = null;
				button_exit.getScene().setCursor(Cursor.DEFAULT);
				nicknameError(1);
			}
		}
	}
}
