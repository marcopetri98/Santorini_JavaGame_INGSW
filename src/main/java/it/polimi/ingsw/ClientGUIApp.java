package it.polimi.ingsw;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.ClientMessageListener;
import it.polimi.ingsw.ui.gui.controller.MainGuiController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * This class is the base App class for GUI clients, it starts the GUI client, the client lives until the player doesn't close the game.
 */
public class ClientGUIApp extends Application {
	public static void main(String[] args) {
		ClientMessageListener listener;
		MainGuiController guiController;

		guiController = MainGuiController.getInstance();
		listener = new ClientMessageListener(guiController);
		guiController.setListener(listener);
		listener.start();
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/lobby.fxml")); //in this moment, I'm using this as a scene tester
		Scene menu = new Scene(root);
		stage.setScene(menu);
		stage.setTitle("Santorini");
		stage.getIcons().add(new Image("/img/icon_logo.png"));
		stage.setResizable(false);
		stage.show();
		stage.setOnCloseRequest(windowEvent -> {
			Platform.exit();
			System.exit(0);
		});
	}
}
