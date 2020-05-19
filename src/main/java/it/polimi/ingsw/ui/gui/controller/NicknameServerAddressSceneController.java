package it.polimi.ingsw.ui.gui.controller;

import com.sun.tools.javac.Main;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Constants;
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
import javafx.stage.Stage;

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

	// triggers for server messages
	private static NicknameServerAddressSceneController currentObject;
	private boolean messageCanBeSent = true;
	private boolean connectedToServer = false;
	private String nameChosen;
	private GameState gameState;

	public static SceneController getInstance() {
		return currentObject;
	}

	public void initialize() {
		currentObject = this;
		MainGuiController.getInstance().setSceneController(this);
		gameState = MainGuiController.getInstance().getGameState();
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
		} else if (nickname.split(" ").length > 1 || nickname.length() > Constants.MAX_NICKNAME_LEN) {
			nicknameError();
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

			if (MainGuiController.getInstance().connectToServer(serverAddress)) {
				MainGuiController.getInstance().sendMessage(netSetupMessage);
			} else {
				serverAddressError(1);
				messageCanBeSent = true;
				button_exit.getScene().setCursor(Cursor.DEFAULT);
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
	public void nicknameError() {
		// TODO: draw the error in the function (invalid nickname)
		messageCanBeSent = true;
	}
	public void serverAddressError(int i) {
		if (i == 0) {
			// TODO: server inserted isn't an ip, show this
		} else if (i == 1) {
			// TODO: server inserted doesn't support this game, show this
		}
	}
	public void waitError() {
		// TODO: say the user to wait (user has clicked again on next before an arrive of a response from server)
	}


	/* **********************************************
	 *												*
	 *		METHODS CALLED BY MAIN CONTROLLER		*
	 * 												*
	 ************************************************/
	@Override
	public void fatalError() {
		// TODO: what to do here?
	}
	@Override
	public void deposeMessage(NetObject message) throws IOException {
		switch (message.message) {
			case Constants.SETUP_OUT_CONNWORKED, Constants.SETUP_OUT_CONNFINISH -> {
				nextFXML = FXMLLoader.load(getClass().getResource("/fxml/lobby.fxml"));
				nextScene = new Scene(nextFXML);
				if (MainGuiController.getInstance().getSceneController() == this) {
					MainGuiController.getInstance().setSceneController(ChooseNumPlayerSceneController.getInstance());
				}
				gameState.setPlayer(nameChosen);
				currentStage = (Stage) button_exit.getScene().getWindow();
				currentStage.setScene(nextScene);
			}
			case Constants.SETUP_CREATE -> {
				creatorFXML = FXMLLoader.load(getClass().getResource("/fxml/choose_numPlayer.fxml"));
				creatorScene = new Scene(creatorFXML);
				if (MainGuiController.getInstance().getSceneController() == this) {
					MainGuiController.getInstance().setSceneController(ChooseNumPlayerSceneController.getInstance());
				}
				gameState.setPlayer(nameChosen);
				currentStage = (Stage) button_exit.getScene().getWindow();
				currentStage.setScene(creatorScene);
			}
			case Constants.SETUP_OUT_CONNFAILED -> {
				messageCanBeSent = true;
				nameChosen = null;
				button_exit.getScene().setCursor(Cursor.DEFAULT);
				nicknameError();
			}
		}
	}
}
