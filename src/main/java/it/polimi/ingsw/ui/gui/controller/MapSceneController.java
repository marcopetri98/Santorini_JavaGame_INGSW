package it.polimi.ingsw.ui.gui.controller;

import it.polimi.ingsw.network.objects.NetGaming;
import it.polimi.ingsw.network.objects.NetObject;
import it.polimi.ingsw.network.objects.NetSetup;
import it.polimi.ingsw.ui.gui.viewModel.GameState;
import it.polimi.ingsw.util.Color;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

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
	private ImageView cell_00;
	@FXML
	private ImageView cell_01;
	@FXML
	private ImageView cell_02;
	@FXML
	private ImageView cell_03;
	@FXML
	private ImageView cell_04;
	@FXML
	private ImageView cell_10;
	@FXML
	private ImageView cell_11;
	@FXML
	private ImageView cell_12;
	@FXML
	private ImageView cell_13;
	@FXML
	private ImageView cell_14;
	@FXML
	private ImageView cell_20;
	@FXML
	private ImageView cell_21;
	@FXML
	private ImageView cell_22;
	@FXML
	private ImageView cell_23;
	@FXML
	private ImageView cell_24;
	@FXML
	private ImageView cell_30;
	@FXML
	private ImageView cell_31;
	@FXML
	private ImageView cell_32;
	@FXML
	private ImageView cell_33;
	@FXML
	private ImageView cell_34;
	@FXML
	private ImageView cell_40;
	@FXML
	private ImageView cell_41;
	@FXML
	private ImageView cell_42;
	@FXML
	private ImageView cell_43;
	@FXML
	private ImageView cell_44;
	@FXML
	private GridPane gridPane_cells;
	@FXML
	private Text text_player;

	private boolean pressedIconExit = false;
	private boolean pressedButtonBuild = false;
	private boolean pressedButtonDome = false;
	private boolean pressedBuild1Red = false;
	private boolean pressedBuild2Red = false;
	private boolean pressedBuild3Red = false;
	private boolean pressedBuild1Green = false;
	private boolean pressedBuild2Green = false;
	private boolean pressedBuild3Green = false;
	private boolean pressedBuild1Blue = false;
	private boolean pressedBuild2Blue = false;
	private boolean pressedBuild3Blue = false;
	private int setWorkers = 0;
	private boolean pressedSetWorker1 = false;
	private boolean pressedSetWorker2 = false;

	private boolean pressedWorker1 = false;     //??????????
	private boolean pressedWorker2 = false;  //??????????

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

	ImageView workerSelected = null;

	// objects used to change scene
	private Parent previousFXML;
	private Parent nextFXML;
	private Scene previousScene;
	private Scene nextScene;
	private Stage currentStage;

	// triggers for server messages
	private GameState gameState;

	public void initialize() {
		MainGuiController.getInstance().getGameState().getColors().get(MainGuiController.getInstance().getGameState().getPlayer());
		gameState = MainGuiController.getInstance().getGameState();

		description_god.setImage(null);
		initializeCells();
		initializeAnimations();
		setActivePlayer();
		button_build.setDisable(true);
		button_dome.setDisable(true);
	}
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
	}
	private void initializeCells() {
		cell_00.setImage(blank);
		cell_10.setImage(blank);
		cell_20.setImage(blank);
		cell_30.setImage(blank);
		cell_40.setImage(blank);
		cell_01.setImage(blank);
		cell_11.setImage(blank);
		cell_21.setImage(blank);
		cell_31.setImage(blank);
		cell_41.setImage(blank);
		cell_02.setImage(blank);
		cell_12.setImage(blank);
		cell_22.setImage(blank);
		cell_32.setImage(blank);
		cell_42.setImage(blank);
		cell_03.setImage(blank);
		cell_13.setImage(blank);
		cell_23.setImage(blank);
		cell_33.setImage(blank);
		cell_43.setImage(blank);
		cell_04.setImage(blank);
		cell_14.setImage(blank);
		cell_24.setImage(blank);
		cell_34.setImage(blank);
		cell_44.setImage(blank);
	}

	// initialize method
	public Image colorPlayer() {
		if (gameState.getColors().get(gameState.getPlayer()).equals(Color.BLUE)) {
			return workerBlue;
		} else if (gameState.getColors().get(gameState.getPlayer()).equals(Color.GREEN)) {
			return workerGreen;
		} else {
			return workerRed;
		}
	}
	// active player information data
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
	public void mousePressedIconExit(MouseEvent mouseEvent) {
		if (!pressedIconExit) {
			icon_exit.setImage(iconExitPressed);
			slidingImage(box_exit, boxExit, 300, 47, 127, 47, 500);
			slidingImage(button_exit, buttonExit, 200, 24, 36, 24, 500);
			pressedIconExit = true;
		} else {
			icon_exit.setImage(iconExit);
			slidingImage(box_exit, boxExit, 127, 47, 310, 47, 500);
			slidingImage(button_exit, buttonExit, 36, 24, 220, 24, 500);
			pressedIconExit = false;
		}
	}
	public void mousePressedExit(MouseEvent mouseEvent) throws IOException {
		button_exit.setImage(buttonExitPressed);
		previousFXML = FXMLLoader.load(getClass().getResource("/fxml/menu.fxml"));
		previousScene = new Scene(previousFXML);
	}
	public void mouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);

		NetGaming netSetup = new NetGaming(Constants.GENERAL_DISCONNECT);
		MainGuiController.getInstance().sendMessage(netSetup);
		MainGuiController.getInstance().refresh();
		MainGuiController.getInstance().setSceneController(null);
		currentStage = (Stage) button_exit.getScene().getWindow();
		currentStage.setScene(previousScene);
	}
	public void mousePressedBuild(MouseEvent mouseEvent) {
		if (!pressedButtonBuild) {
			button_build.setImage(buttonBuildPressed);
			pressedButtonBuild = true;
			button_dome.setDisable(true);

		} else {
			button_build.setImage(buttonBuild);
			button_dome.setDisable(false);
			pressedButtonBuild = false;
		}
	}
	public void mousePressedDome(MouseEvent mouseEvent) {
		if (!pressedButtonDome) {
			button_dome.setImage(buttonDomePressed);
			pressedButtonDome = true;
			button_build.setDisable(true);
		} else {
			button_dome.setImage(buttonDome);
			pressedButtonDome = false;
			button_build.setDisable(false);
		}
	}
	public void mouseEnteredGodCard(MouseEvent mouseEvent) {
		slidingImage(description_god, descriptionGodCard(), 129, 400, 129, 125, 450);
	}
	public void mouseExitedGodCard(MouseEvent mouseEvent) {
		slidingImage(description_god, descriptionGodCard(), 129, 125, 129, 400, 450);
	}

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
		}
	}
	public void mousePressedCell(MouseEvent mouseEvent) {
		ImageView pressedCell = (ImageView) mouseEvent.getTarget();
		if(colorPlayer().equals(workerRed)){
			movingWorker(pressedCell, workerRed, workerRedPressed, build1Red, build2Red, build3Red, build1RedPressed, build2RedPressed, build3RedPressed);
		} else if(colorPlayer().equals(workerGreen)){
			movingWorker(pressedCell, workerGreen, workerGreenPressed, build1Green, build2Green, build3Green, build1GreenPressed, build2GreenPressed, build3GreenPressed);
		} else if(colorPlayer().equals(workerBlue)){
			movingWorker(pressedCell, workerBlue, workerBluePressed, build1Blue, build2Blue, build3Blue, build1BluePressed, build2BluePressed, build3BluePressed);
		}
		pressingCell(pressedCell, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}
	public void mousePressedSetWorker(MouseEvent mouseEvent) {
		button_dome.setDisable(true);
		button_build.setDisable(true);
		if (colorPlayer().equals(workerRed)) {
			if (((ImageView) mouseEvent.getTarget()).getImage().equals(workerRed)) {
				workerSelected = ((ImageView) mouseEvent.getTarget());
				workerSelected.setImage(workerRedPressed);
				if (((ImageView) mouseEvent.getTarget()).getId().equals("worker1")) {
					pressedWorker1 = true;
					worker2.setImage(workerRed); //not pressed
					pressedWorker2 = false;
				} else { //worker2
					pressedWorker2 = true;
					worker1.setImage(workerRed);
					pressedWorker1 = false;
				}
			} else {
				workerSelected = null; //deselect
				((ImageView) mouseEvent.getTarget()).setImage(workerRed);
				if (((ImageView) mouseEvent.getTarget()).getId().equals("worker1")) {
					pressedWorker1 = false;
					pressedWorker2 = false;
				} else {
					pressedWorker2 = false;
					pressedWorker1 = false;
				}
			}
		} else if (colorPlayer().equals(workerGreen)) {
			if (((ImageView) mouseEvent.getTarget()).getImage().equals(workerGreen)) {
				workerSelected = ((ImageView) mouseEvent.getTarget());
				workerSelected.setImage(workerGreenPressed);
				if (((ImageView) mouseEvent.getTarget()).getId().equals("worker1")) {
					pressedWorker1 = true;
					worker2.setImage(workerGreen);
					pressedWorker2 = false;
				} else {
					pressedWorker2 = true;
					worker1.setImage(workerGreen);
					pressedWorker1 = false;
				}
			} else {
				workerSelected = null;
				((ImageView) mouseEvent.getTarget()).setImage(workerGreen);
				if (((ImageView) mouseEvent.getTarget()).getId().equals("worker1")) {
					pressedWorker1 = false;
					pressedWorker2 = false;
				} else {
					pressedWorker2 = false;
					pressedWorker1 = false;
				}
			}
		} else if (colorPlayer().equals(workerBlue)) {
			if (((ImageView) mouseEvent.getTarget()).getImage().equals(workerBlue)) {
				workerSelected = ((ImageView) mouseEvent.getTarget());
				workerSelected.setImage(workerBluePressed);
				if (((ImageView) mouseEvent.getTarget()).getId().equals("worker1")) {
					pressedWorker1 = true;
					worker2.setImage(workerBlue);
					pressedWorker2 = false;
				} else {
					pressedWorker2 = true;
					worker1.setImage(workerBlue);
					pressedWorker1 = false;
				}
			} else {
				workerSelected = null;
				((ImageView) mouseEvent.getTarget()).setImage(workerBlue);
				if (((ImageView) mouseEvent.getTarget()).getId().equals("worker1")) {
					pressedWorker1 = false;
					pressedWorker2 = false;
				} else {
					pressedWorker2 = false;
					pressedWorker1 = false;
				}
			}
		}
	}

	/* **********************************************
	 *												*
	 *			METHODS FOR USER INTERACTION		*
	 * 												*
	 ************************************************/
	/**
	 * This function moves through a line path an image.
	 *
	 * @param imageView the imageView I want to move
	 * @param image     the png of the imageView
	 * @param x1        the x coordinate at the beginning
	 * @param y1        the y coordinate at the beginning
	 * @param x2        the x coordinate at the end
	 * @param y2        the y coordinate at the end
	 * @param duration  time of the transition, in milliseconds
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
	private void pressingCell(ImageView cell, Image blank, Image build1, Image build2, Image build3, Image buildDome, ImageView button_build, Image buttonBuild, ImageView button_dome, Image buttonDome) {
		if (pressedButtonBuild) {
			if (cell.getImage().equals(blank)) {
				cell.setImage(build1);
				button_build.setImage(buttonBuild);
				button_dome.setDisable(false);
				pressedButtonBuild = false;
			} else if (cell.getImage().equals(build1)) {
				cell.setImage(build2);
				button_build.setImage(buttonBuild);
				button_dome.setDisable(false);
				pressedButtonBuild = false;
			} else if (cell.getImage().equals(build2)) {
				cell.setImage(build3);
				button_build.setImage(buttonBuild);
				button_dome.setDisable(false);
				pressedButtonBuild = false;
			} else {
				button_build.setImage(buttonBuild);
				button_dome.setDisable(false);
				pressedButtonBuild = false;
			}
			button_dome.setImage(buttonDome);
			pressedButtonDome = false;
			button_build.setDisable(false);
		}

		if (pressedButtonDome) {
			if (cell.getImage().equals(build3)) { //TODO: remember atlas...
				cell.setImage(buildDome);
				button_dome.setImage(buttonDome);
				pressedButtonDome = false;
				button_build.setDisable(false);
			} else {
				button_dome.setImage(buttonDome);
				pressedButtonDome = false;
				button_build.setDisable(false);
			}
		}
	}
	private void swapWorkers(ImageView cellPressed, Image worker, Image workerPressed, Image build1Worker, Image build1WorkerPressed, Image build2Worker, Image build2WorkerPressed, Image build3Worker, Image build3WorkerPressed) {
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
		} else if (workerSelected.getImage().equals(build1Worker)) {
			workerSelected.setImage(build1WorkerPressed);
		} else if (workerSelected.getImage().equals(build2Worker)) {
			workerSelected.setImage(build2WorkerPressed);
		} else if (workerSelected.getImage().equals(build3Worker)) {
			workerSelected.setImage(build3WorkerPressed);
		}
	}
	private void pressWorker(ImageView cellPressed, Image worker, Image workerPressed, Image build1Worker, Image build1WorkerPressed, Image build2Worker, Image build2WorkerPressed, Image build3Worker, Image build3WorkerPressed) {
		if (cellPressed.getImage().equals(worker)) {
			workerSelected = cellPressed;
			workerSelected.setImage(workerPressed);
		} else if (cellPressed.getImage().equals(build1Worker)) {
			workerSelected = cellPressed;
			workerSelected.setImage(build1WorkerPressed);
		} else if (cellPressed.getImage().equals(build2Worker)) {
			workerSelected = cellPressed;
			workerSelected.setImage(build2WorkerPressed);
		} else if (cellPressed.getImage().equals(build3Worker)) {
			workerSelected = cellPressed;
			workerSelected.setImage(build3WorkerPressed);
		}
	}
	private void unPressWorker(ImageView cellPressed, Image worker, Image workerPressed, Image build1Worker, Image build1WorkerPressed, Image build2Worker, Image build2WorkerPressed, Image build3Worker, Image build3WorkerPressed) {
		if (cellPressed.getImage().equals(workerPressed)) {
			workerSelected = cellPressed;
			workerSelected.setImage(worker);
			workerSelected = null;
		} else if (cellPressed.getImage().equals(build1WorkerPressed)) {
			workerSelected = cellPressed;
			workerSelected.setImage(build1Worker);
			workerSelected = null;
		} else if (cellPressed.getImage().equals(build2WorkerPressed)) {
			workerSelected = cellPressed;
			workerSelected.setImage(build2Worker);
			workerSelected = null;
		} else if (cellPressed.getImage().equals(build3WorkerPressed)) {
			workerSelected = cellPressed;
			workerSelected.setImage(build3Worker);
			workerSelected = null;
		}
	}
	private void placeWorker(ImageView cellPressed, Image worker, Image workerPressed, Image build1Worker, Image build1WorkerPressed, Image build2Worker, Image build2WorkerPressed, Image build3Worker, Image build3WorkerPressed) {
		if (workerSelected != null) { //if you are placing a worker
			// TODO: controlling checkmove...
			if (cellPressed.getImage().equals(blank)) {
				cellPressed.setImage(worker);
				if (workerSelected.getId().equals("worker1") || workerSelected.getId().equals("worker2")) {
					((AnchorPane) workerSelected.getParent()).getChildren().remove(workerSelected);
					++setWorkers;
					if (setWorkers == 2) {
						slidingImage(box_workers, boxWorkers, 159, 122, 450, 122, 750);
						button_build.setDisable(false);
						button_dome.setDisable(false);
					}
					workerSelected = null;
				} else {
					if(workerSelected.getImage().equals(workerPressed)) {
						workerSelected.setImage(blank);
						workerSelected = null;
					} else if(workerSelected.getImage().equals(build1WorkerPressed)) {
						workerSelected.setImage(build1);
						workerSelected = null;
					} else if(workerSelected.getImage().equals(build2WorkerPressed)) {
						workerSelected.setImage(build2);
						workerSelected = null;
					} else if(workerSelected.getImage().equals(build3WorkerPressed)) {
						workerSelected.setImage(build3);
						workerSelected = null;
					}
				}
			} else if (cellPressed.getImage().equals(build1)) {
				cellPressed.setImage(build1Worker);
				if (workerSelected.getImage().equals(workerPressed)) {
					workerSelected.setImage(blank);
					workerSelected = null;
				} else if (workerSelected.getImage().equals(build1WorkerPressed)) {
					workerSelected.setImage(build1);
					workerSelected = null;
				} else if (workerSelected.getImage().equals(build2WorkerPressed)) {
					workerSelected.setImage(build2);
					workerSelected = null;
				} else if (workerSelected.getImage().equals(build3WorkerPressed)) {
					workerSelected.setImage(build3);
					workerSelected = null;
				}
			} else if (cellPressed.getImage().equals(build2)) {
				cellPressed.setImage(build2Worker);
				if (workerSelected.getImage().equals(workerPressed)) {
					workerSelected.setImage(blank);
					workerSelected = null;
				} else if (workerSelected.getImage().equals(build1WorkerPressed)) {
					workerSelected.setImage(build1);
					workerSelected = null;
				} else if (workerSelected.getImage().equals(build2WorkerPressed)) {
					workerSelected.setImage(build2);
					workerSelected = null;
				} else if (workerSelected.getImage().equals(build3WorkerPressed)) {
					workerSelected.setImage(build3);
					workerSelected = null;
				}
			} else if (cellPressed.getImage().equals(build3)) {
				cellPressed.setImage(build3Worker);
				if (workerSelected.getImage().equals(workerPressed)) {
					workerSelected.setImage(blank);
					workerSelected = null;
				} else if (workerSelected.getImage().equals(build1WorkerPressed)) {
					workerSelected.setImage(build1);
					workerSelected = null;
				} else if (workerSelected.getImage().equals(build2WorkerPressed)) {
					workerSelected.setImage(build2);
					workerSelected = null;
				} else if (workerSelected.getImage().equals(build3WorkerPressed)) {
					workerSelected.setImage(build3);
					workerSelected = null;
				}
			}
		}
	}

	/* **********************************************
	 *												*
	 *		EVENTS AFTER SERVER MESSAGE				*
	 * 												*
	 ************************************************/
	private void setActivePlayer() {
		text_player.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/LillyBelle.ttf"), 18));
		text_player.setText(gameState.getActivePlayer());
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
		// TODO: implement
	}
}