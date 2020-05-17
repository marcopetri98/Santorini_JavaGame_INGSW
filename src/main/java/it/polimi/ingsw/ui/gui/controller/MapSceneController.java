package it.polimi.ingsw.ui.gui.controller;

import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class MapSceneController {
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
	private boolean setupWorkers = false;
	private int setWorkers = 0;
	private boolean pressedSetWorker1 = false;
	private boolean pressedSetWorker2 = false;

	private boolean pressedWorker1 = false;	 //??????????
	private boolean pressedWorker2 = false;  //??????????

	boolean cellContainingWorkerPressed[][] = new boolean[4][4];
	ImageView workerSelected = null;

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




	public void initialize(){
		description_god.setImage(null);
		initializeCells();
		initializeAnimations();
	}

	private void initializeAnimations(){
		slidingImage(box_exit, boxExit, 300, 47, 300, 47, 500);
		slidingImage(button_exit, buttonExit,200, 24, 200, 24, 500 );
		slidingImage(column_left, columnLeft, 0, 360, 61, 360, 3300);
		slidingImage(column_right, columnRight, 150, 360, 63, 360, 3300);
		slidingImage(box_turn, boxTurn, 121, -250, 121, 218, 3300);
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

	private void initializeCells(){
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

	public Image colorPlayer(/*...*/){
		//TODO:
		//like: if(players.color == red) then return the image of the red worker, obviously
		return workerRed;
	}

	public Image godPlayer(){
		//TODO: same logic as colorPlayer...
		return new Image("/img/gods/card_apollo.png");
	}

	public Image descriptionGodCard(){
		//TODO:....
		return new Image("/img/gods/description_apollo.png");
	}

	/**
	 * This function moves through a line path an image.
	 * @param imageView the imageView I want to move
	 * @param image the png of the imageView
	 * @param x1 the x coordinate at the beginning
	 * @param y1 the y coordinate at the beginning
	 * @param x2 the x coordinate at the end
	 * @param y2 the y coordinate at the end
	 * @param duration time of the transition, in milliseconds
	 */
	private void slidingImage(ImageView imageView, Image image, int x1, int y1, int x2, int y2, int duration){
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

	public void mousePressedIconExit(MouseEvent mouseEvent) {
		if(!pressedIconExit){
			icon_exit.setImage(iconExitPressed);
			slidingImage(box_exit, boxExit, 300, 47, 127, 47, 500);
			slidingImage(button_exit, buttonExit,200, 24, 36, 24, 500 );
			pressedIconExit = true;
		} else {
			icon_exit.setImage(iconExit);
			slidingImage(box_exit, boxExit, 127, 47, 310, 47, 500);
			slidingImage(button_exit, buttonExit,36, 24, 220, 24, 500 );
			pressedIconExit = false;
		}
	}

	public void mousePressedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExitPressed);
	}

	public void MouseReleasedExit(MouseEvent mouseEvent) {
		button_exit.setImage(buttonExit);
	}

	public void mousePressedBuild(MouseEvent mouseEvent) {
		if(!pressedButtonBuild){
			button_build.setImage(buttonBuildPressed);
			pressedButtonBuild = true;
			button_dome.setDisable(true);

		} else {
			button_build.setImage(buttonBuild);
			button_dome.setDisable(false);
			pressedButtonBuild = false;
		}
	}

	public void mousePressedDome(MouseEvent mouseEvent) { //TODO: remember to implement atlas power: build only a dome.
		if(!pressedButtonDome){
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

	private void pressingCell(ImageView cell, Image blank, Image build1, Image build2, Image build3, Image buildDome, ImageView button_build, Image buttonBuild, ImageView button_dome, Image buttonDome){
		if(pressedButtonBuild){
			if(cell.getImage().equals(blank)){
				cell.setImage(build1);
				button_build.setImage(buttonBuild);
				button_dome.setDisable(false);
				pressedButtonBuild = false;
			} else if(cell.getImage().equals(build1)) {
				cell.setImage(build2);
				button_build.setImage(buttonBuild);
				button_dome.setDisable(false);
				pressedButtonBuild = false;
			} else if(cell.getImage().equals(build2)){
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

		if(pressedButtonDome){
			cell.setImage(buildDome);
			button_dome.setImage(buttonDome);
			pressedButtonDome = false;
			button_build.setDisable(false);
		}
	}

	public void mousePressedSetWorker(MouseEvent mouseEvent) {
		
	}

	private void setupWorkers(ImageView cell){
		if(cell.getImage().equals(blank)){
			if(pressedSetWorker1 && !pressedSetWorker2){
				cell.setImage(colorPlayer());
				worker1.setImage(null);
				worker1.setDisable(true);
				pressedSetWorker1 = false;
				++setWorkers;
			} else if(!pressedSetWorker1 && pressedSetWorker2){
				cell.setImage(colorPlayer());
				worker2.setImage(null);
				worker2.setDisable(true);
				pressedSetWorker2 = false;
				++setWorkers;
			}
		}
		if(setWorkers == 2) { //activate build buttons only if workers are placed
			button_dome.setDisable(false);
			button_build.setDisable(false);
		}
		if(worker1.isDisabled() && worker2.isDisabled() && !setupWorkers){
			slidingImage(box_workers, boxWorkers, 159, 122, 450, 122, 750);
			box_workers.setDisable(true);
			button_build.setDisable(false);
			button_dome.setDisable(false);
			setupWorkers = true;
		}
	} //OK


	private Boolean cellWorkerPressed(ImageView cell){
		if(cell.equals(cell_00)){
			return cellContainingWorkerPressed[0][0];
		} else if(cell.equals(cell_10)){
			return cellContainingWorkerPressed[1][0];
		} else if(cell.equals(cell_20)){
			return cellContainingWorkerPressed[2][0];
		} else if(cell.equals(cell_30)){
			return cellContainingWorkerPressed[3][0];
		} else if(cell.equals(cell_40)){
			return cellContainingWorkerPressed[4][0];
		} else if(cell.equals(cell_01)){
			return cellContainingWorkerPressed[0][1];
		} else if(cell.equals(cell_11)){
			return cellContainingWorkerPressed[1][1];
		} else if(cell.equals(cell_21)){
			return cellContainingWorkerPressed[2][1];
		} else if(cell.equals(cell_31)){
			return cellContainingWorkerPressed[3][1];
		} else if(cell.equals(cell_41)){
			return cellContainingWorkerPressed[4][1];
		} else if(cell.equals(cell_02)){
			return cellContainingWorkerPressed[0][2];
		} else if(cell.equals(cell_12)){
			return cellContainingWorkerPressed[1][2];
		} else if(cell.equals(cell_22)){
			return cellContainingWorkerPressed[2][2];
		} else if(cell.equals(cell_32)){
			return cellContainingWorkerPressed[3][2];
		} else if(cell.equals(cell_42)){
			return cellContainingWorkerPressed[4][2];
		} else if(cell.equals(cell_03)){
			return cellContainingWorkerPressed[0][3];
		} else if(cell.equals(cell_13)){
			return cellContainingWorkerPressed[1][3];
		} else if(cell.equals(cell_23)){
			return cellContainingWorkerPressed[2][3];
		} else if(cell.equals(cell_33)){
			return cellContainingWorkerPressed[3][3];
		} else if(cell.equals(cell_43)){
			return cellContainingWorkerPressed[4][3];
		} else if(cell.equals(cell_04)){
			return cellContainingWorkerPressed[0][4];
		} else if(cell.equals(cell_14)){
			return cellContainingWorkerPressed[1][4];
		} else if(cell.equals(cell_24)){
			return cellContainingWorkerPressed[2][4];
		} else if(cell.equals(cell_34)){
			return cellContainingWorkerPressed[3][4];
		} else if(cell.equals(cell_44)){
			return cellContainingWorkerPressed[4][4];
		} else return null;
	}


	private void pressingWorker(ImageView cell){
		if(cell.getImage().equals(build1Red) && !cellWorkerPressed(cell)){ //dovrei fare anche l'unpress..
			cell.setImage(build1RedPressed);
		} else if(cell.getImage().equals(build2Red) && !pressedBuild2Red){
			cell.setImage(build2RedPressed);
			pressedBuild2Red = true;
		} else if(cell.getImage().equals(build3Red) && !pressedBuild3Red){
			cell.setImage(build3RedPressed);
			pressedBuild3Red = true;
		} else if(cell.getImage().equals(build1Green) && !pressedBuild1Green){
			cell.setImage(build1GreenPressed);
			pressedBuild1Green = true;
		} else if(cell.getImage().equals(build2Green) && pressedBuild2Green){
			cell.setImage(build2GreenPressed);
			pressedBuild2Green = true;
		} else if(cell.getImage().equals(build3Green) && !pressedBuild3Green){
			cell.setImage(build3GreenPressed);
			pressedBuild3Green = true;
		} else if(cell.getImage().equals(build1Blue) && !pressedBuild1Blue){
			cell.setImage(build1BluePressed);
			pressedBuild1Blue = true;
		} else if(cell.getImage().equals(build2Blue) && !pressedBuild2Blue){
			cell.setImage(build2BluePressed);
			pressedBuild2Blue = true;
		} else if(cell.getImage().equals(build3Blue) && !pressedBuild3Blue){
			cell.setImage(build3BluePressed);
			pressedBuild3Blue = true;
		}
	}


	public void moveWorker(ImageView cell){
		if(setupWorkers) {
			if (pressedWorker1 && !pressedWorker2) {
				if (cell.getImage().equals(blank) && colorPlayer().equals(workerRed)) {     //moving in a blank cell
					cell.setImage(workerRed);
					pressedWorker1 = false;
				} else if (cell.getImage().equals(blank) && colorPlayer().equals(workerGreen)) {
					cell.setImage(workerGreen);
					pressedWorker1 = false;
				} else if (cell.getImage().equals(blank) && colorPlayer().equals(workerBlue)) {
					cell.setImage(workerBlue);
					pressedWorker1 = false;
				} else if (cell.getImage().equals(build1) && colorPlayer().equals(workerRed)) {
					cell.setImage(build1Red);
					pressedWorker1 = false;
				} else if (cell.getImage().equals(build1) && colorPlayer().equals(workerGreen)) {
					cell.setImage(build1Green);
					pressedWorker1 = false;
				} else if (cell.getImage().equals(build1) && colorPlayer().equals(workerBlue)) {
					cell.setImage(build1Blue);
					pressedWorker1 = false;
				} else if (cell.getImage().equals(build2) && colorPlayer().equals(workerRed)) {
					cell.setImage(build2Red);
					pressedWorker1 = false;
				} else if (cell.getImage().equals(build2) && colorPlayer().equals(workerGreen)) {
					cell.setImage(build2Green);
					pressedWorker1 = false;
				} else if (cell.getImage().equals(build2) && colorPlayer().equals(workerBlue)) {
					cell.setImage(build2Blue);
					pressedWorker1 = false;
				} else if (cell.getImage().equals(build3) && colorPlayer().equals(workerRed)) {
					cell.setImage(build3Red);
					pressedWorker1 = false;
				} else if (cell.getImage().equals(build3) && colorPlayer().equals(workerGreen)) {
					cell.setImage(build3Green);
					pressedWorker1 = false;
				} else if (cell.getImage().equals(build3) && colorPlayer().equals(workerBlue)) {
					cell.setImage(build3Blue);
					pressedWorker1 = false;
				}
			} else if (!pressedWorker1 && pressedWorker2) {
				if (cell.getImage().equals(blank) && colorPlayer().equals(workerRedPressed)) {     //moving in a blank cell
					cell.setImage(workerRed);
					pressedWorker2 = false;
				} else if (cell.getImage().equals(blank) && colorPlayer().equals(workerGreen)) {
					cell.setImage(workerGreen);
					pressedWorker2 = false;
				} else if (cell.getImage().equals(blank) && colorPlayer().equals(workerBlue)) {
					cell.setImage(workerBlue);
					pressedWorker2 = false;
				} else if (cell.getImage().equals(build1) && colorPlayer().equals(workerRed)) {
					cell.setImage(build1Red);
					pressedWorker2 = false;
				} else if (cell.getImage().equals(build1) && colorPlayer().equals(workerGreen)) {
					cell.setImage(build1Green);
					pressedWorker2 = false;
				} else if (cell.getImage().equals(build1) && colorPlayer().equals(workerBlue)) {
					cell.setImage(build1Blue);
					pressedWorker2 = false;
				} else if (cell.getImage().equals(build2) && colorPlayer().equals(workerRed)) {
					cell.setImage(build2Red);
					pressedWorker2 = false;
				} else if (cell.getImage().equals(build2) && colorPlayer().equals(workerGreen)) {
					cell.setImage(build2Green);
					pressedWorker2 = false;
				} else if (cell.getImage().equals(build2) && colorPlayer().equals(workerBlue)) {
					cell.setImage(build2Blue);
					pressedWorker2 = false;
				} else if (cell.getImage().equals(build3) && colorPlayer().equals(workerRed)) {
					cell.setImage(build3Red);
					pressedWorker2 = false;
				} else if (cell.getImage().equals(build3) && colorPlayer().equals(workerGreen)) {
					cell.setImage(build3Green);
					pressedWorker2 = false;
				} else if (cell.getImage().equals(build3) && colorPlayer().equals(workerBlue)) {
					cell.setImage(build3Blue);
					pressedWorker2 = false;
				}
			}
		}
	} //OK

	public void mousePressedCell(MouseEvent mouseEvent) {

//		selectWorker1(cell_00);
//		selectWorker2(cell_00);
		//moveWorker(cell_00);
		//pressingBuildWorker(cell_00); //only if there is already a build w/ worker...
		pressingCell(cell_00, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell10(MouseEvent mouseEvent) {
		setupWorkers(cell_10);
//		selectWorker1(cell_10);
//		selectWorker2(cell_10);
		//moveWorker(cell_10);
		//pressingBuildWorker(cell_10);
		pressingCell(cell_10, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell20(MouseEvent mouseEvent) {
		setupWorkers(cell_20);
		moveWorker(cell_20);
		//pressingBuildWorker(cell_20);
		pressingCell(cell_20, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell30(MouseEvent mouseEvent) {
		setupWorkers(cell_30);
		moveWorker(cell_30);
		//pressingBuildWorker(cell_30);
		pressingCell(cell_30, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell40(MouseEvent mouseEvent) {
		pressingCell(cell_40, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell01(MouseEvent mouseEvent) {
		pressingCell(cell_01, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell11(MouseEvent mouseEvent) {
		pressingCell(cell_11, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell21(MouseEvent mouseEvent) {
		pressingCell(cell_21, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell31(MouseEvent mouseEvent) {
		pressingCell(cell_31, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell41(MouseEvent mouseEvent) {
		pressingCell(cell_41, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell02(MouseEvent mouseEvent) {
		pressingCell(cell_02, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell12(MouseEvent mouseEvent) {
		pressingCell(cell_12, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell22(MouseEvent mouseEvent) {
		pressingCell(cell_22, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell32(MouseEvent mouseEvent) {
		pressingCell(cell_32, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell42(MouseEvent mouseEvent) {
		pressingCell(cell_42, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell03(MouseEvent mouseEvent) {
		pressingCell(cell_03, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell13(MouseEvent mouseEvent) {
		pressingCell(cell_13, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell23(MouseEvent mouseEvent) {
		pressingCell(cell_23, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell33(MouseEvent mouseEvent) {
		pressingCell(cell_33, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell43(MouseEvent mouseEvent) {
		pressingCell(cell_43, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell04(MouseEvent mouseEvent) {
		pressingCell(cell_04, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell14(MouseEvent mouseEvent) {
		pressingCell(cell_14, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell24(MouseEvent mouseEvent) {
		pressingCell(cell_24, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell34(MouseEvent mouseEvent) {
		pressingCell(cell_34, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	public void mousePressedCell44(MouseEvent mouseEvent) {
		pressingCell(cell_44, blank, build1, build2, build3, buildDome, button_build, buttonBuild, button_dome, buttonDome);
	}

	//	public void selectWorker1(ImageView cell) {
//		if(setupWorkers) { //if setup workers is already done...
//			if (colorPlayer().equals(workerRed) && !pressedWorker2) {
//				button_dome.setDisable(true);
//				button_build.setDisable(true);
//				if (!pressedWorker1 && cell.getImage().equals(workerRed)) {
//					cell.setImage(workerRedPressed);
//					pressedWorker1 = true;
//				} else if (pressedWorker1 && cell.getImage().equals(workerRedPressed)) {
//					cell.setImage(workerRed);
//					pressedWorker1 = false;
//				} else if (!pressedWorker1 && cell.getImage().equals(build1Red)) {
//					cell.setImage(build1RedPressed);
//					pressedWorker1 = true;
//				} else if (pressedWorker1 && cell.getImage().equals(build1RedPressed)) {
//					cell.setImage(build1RedPressed);
//					pressedWorker1 = false;
//				} else if (!pressedWorker1 && cell.getImage().equals(build2Red)) {
//					cell.setImage(build2RedPressed);
//					pressedWorker1 = true;
//				} else if (pressedWorker1 && cell.getImage().equals(build2RedPressed)) {
//					cell.setImage(build2Red);
//					pressedWorker1 = false;
//				} else if (!pressedWorker1 && cell.getImage().equals(build3Red)) {
//					cell.setImage(build3RedPressed);
//					pressedWorker1 = true;
//				} else if (pressedWorker1 && cell.getImage().equals(build3RedPressed)) {
//					cell.setImage(build3Red);
//					pressedWorker1 = false;
//				}
//			} else if (colorPlayer().equals(workerGreen) && !pressedWorker2) {
//				button_dome.setDisable(true);
//				button_build.setDisable(true);
//				if (!pressedWorker1 && cell.getImage().equals(workerGreen)) {
//					cell.setImage(workerGreenPressed);
//					pressedWorker1 = true;
//				} else if (pressedWorker1 && cell.getImage().equals(workerGreenPressed)) {
//					cell.setImage(workerGreen);
//					pressedWorker1 = false;
//				} else if (!pressedWorker1 && cell.getImage().equals(build1Green)) {
//					cell.setImage(build1GreenPressed);
//					pressedWorker1 = true;
//				} else if (pressedWorker1 && cell.getImage().equals(build1GreenPressed)) {
//					cell.setImage(build1Green);
//					pressedWorker1 = false;
//				} else if (!pressedWorker1 && cell.getImage().equals(build2Green)) {
//					cell.setImage(build2GreenPressed);
//					pressedWorker1 = true;
//				} else if (pressedWorker1 && cell.getImage().equals(build2GreenPressed)) {
//					cell.setImage(build2Green);
//					pressedWorker1 = false;
//				} else if (!pressedWorker1 && cell.getImage().equals(build3Green)) {
//					cell.setImage(build3GreenPressed);
//					pressedWorker1 = true;
//				} else if (pressedWorker1 && cell.getImage().equals(build3GreenPressed)) {
//					cell.setImage(build3Green);
//					pressedWorker1 = false;
//				}
//			} else if (colorPlayer().equals(workerBlue) && !pressedWorker2) {
//				button_dome.setDisable(true);
//				button_build.setDisable(true);
//				if (!pressedWorker1 && cell.getImage().equals(workerBlue)) {
//					cell.setImage(workerBluePressed);
//					pressedWorker1 = true;
//				} else if (pressedWorker1 && cell.getImage().equals(workerBluePressed)) {
//					cell.setImage(workerBlue);
//					pressedWorker1 = false;
//				} else if (!pressedWorker1 && cell.getImage().equals(build1Blue)) {
//					cell.setImage(build1BluePressed);
//					pressedWorker1 = true;
//				} else if (pressedWorker1 && cell.getImage().equals(build1BluePressed)) {
//					cell.setImage(build1Blue);
//					pressedWorker1 = false;
//				} else if (!pressedWorker1 && cell.getImage().equals(build2Blue)) {
//					cell.setImage(build2BluePressed);
//					pressedWorker1 = true;
//				} else if (pressedWorker1 && cell.getImage().equals(build2BluePressed)) {
//					cell.setImage(build2Blue);
//					pressedWorker1 = false;
//				} else if (!pressedWorker1 && cell.getImage().equals(build3Blue)) {
//					cell.setImage(build3BluePressed);
//					pressedWorker1 = true;
//				} else if (pressedWorker1 && cell.getImage().equals(build3BluePressed)) {
//					cell.setImage(build3Blue);
//					pressedWorker1 = false;
//				}
//			}
//		}
//	}
//
//	public void selectWorker2(ImageView cell) {
//		if(setupWorkers) {
//			button_dome.setDisable(true);
//			button_build.setDisable(true);
//			if (colorPlayer().equals(workerRed) && !pressedWorker1) {
//				if (!pressedWorker2 && cell.getImage().equals(workerRed)) {
//					cell.setImage(workerRedPressed);
//					pressedWorker2 = true;
//				} else if (pressedWorker2 && cell.getImage().equals(workerRedPressed)) {
//					cell.setImage(workerRed);
//					pressedWorker2 = false;
//				} else if (!pressedWorker2 && cell.getImage().equals(build1Red)) {
//					cell.setImage(build1RedPressed);
//					pressedWorker2 = true;
//				} else if (pressedWorker2 && cell.getImage().equals(build1RedPressed)) {
//					cell.setImage(build1RedPressed);
//					pressedWorker2 = false;
//				} else if (!pressedWorker2 && cell.getImage().equals(build2Red)) {
//					cell.setImage(build2RedPressed);
//					pressedWorker2 = true;
//				} else if (pressedWorker2 && cell.getImage().equals(build2RedPressed)) {
//					cell.setImage(build2Red);
//					pressedWorker2 = false;
//				} else if (!pressedWorker2 && cell.getImage().equals(build3Red)) {
//					cell.setImage(build3RedPressed);
//					pressedWorker2 = true;
//				} else if (pressedWorker2 && cell.getImage().equals(build3RedPressed)) {
//					cell.setImage(build3Red);
//					pressedWorker2 = false;
//				}
//			} else if (colorPlayer().equals(workerGreen) && !pressedWorker1) {
//				if (!pressedWorker2 && cell.getImage().equals(workerGreen)) {
//					cell.setImage(workerGreenPressed);
//					pressedWorker2 = true;
//				} else if (pressedWorker2 && cell.getImage().equals(workerGreenPressed)) {
//					cell.setImage(workerGreen);
//					pressedWorker2 = false;
//				} else if (!pressedWorker2 && cell.getImage().equals(build1Green)) {
//					cell.setImage(build1GreenPressed);
//					pressedWorker2 = true;
//				} else if (pressedWorker2 && cell.getImage().equals(build1GreenPressed)) {
//					cell.setImage(build1Green);
//					pressedWorker2 = false;
//				} else if (!pressedWorker2 && cell.getImage().equals(build2Green)) {
//					cell.setImage(build2GreenPressed);
//					pressedWorker2 = true;
//				} else if (pressedWorker2 && cell.getImage().equals(build2GreenPressed)) {
//					cell.setImage(build2Green);
//					pressedWorker2 = false;
//				} else if (!pressedWorker2 && cell.getImage().equals(build3Green)) {
//					cell.setImage(build3GreenPressed);
//					pressedWorker2 = true;
//				} else if (pressedWorker2 && cell.getImage().equals(build3GreenPressed)) {
//					cell.setImage(build3Green);
//					pressedWorker2 = false;
//				}
//			} else if (colorPlayer().equals(workerBlue) && !pressedWorker1) {
//				if (!pressedWorker2 && cell.getImage().equals(workerBlue)) {
//					cell.setImage(workerBluePressed);
//					pressedWorker2 = true;
//				} else if (pressedWorker2 && cell.getImage().equals(workerBluePressed)) {
//					cell.setImage(workerBlue);
//					pressedWorker2 = false;
//				} else if (!pressedWorker2 && cell.getImage().equals(build1Blue)) {
//					cell.setImage(build1BluePressed);
//					pressedWorker2 = true;
//				} else if (pressedWorker2 && cell.getImage().equals(build1BluePressed)) {
//					cell.setImage(build1Blue);
//					pressedWorker2 = false;
//				} else if (!pressedWorker2 && cell.getImage().equals(build2Blue)) {
//					cell.setImage(build2BluePressed);
//					pressedWorker2 = true;
//				} else if (pressedWorker2 && cell.getImage().equals(build2BluePressed)) {
//					cell.setImage(build2Blue);
//					pressedWorker2 = false;
//				} else if (!pressedWorker2 && cell.getImage().equals(build3Blue)) {
//					cell.setImage(build3BluePressed);
//					pressedWorker2 = true;
//				} else if (pressedWorker2 && cell.getImage().equals(build3BluePressed)) {
//					cell.setImage(build3Blue);
//					pressedWorker2 = false;
//				}
//			}
//		}
//	}

}
