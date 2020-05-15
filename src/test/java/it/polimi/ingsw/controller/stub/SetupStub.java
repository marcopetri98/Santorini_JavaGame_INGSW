package it.polimi.ingsw.controller.stub;

import it.polimi.ingsw.controller.SetupManager;
import it.polimi.ingsw.core.Game;
import it.polimi.ingsw.network.objects.NetColorPreparation;
import it.polimi.ingsw.network.objects.NetDivinityChoice;
import it.polimi.ingsw.network.objects.NetGameSetup;
import it.polimi.ingsw.util.exceptions.BadRequestException;
import it.polimi.ingsw.util.exceptions.WrongPhaseException;

public class SetupStub extends SetupManager {
	private boolean generateOrderCalled;
	private boolean changeColorCalled;
	private boolean handleGodMessageCalled;
	private boolean positionWorkersCalled;

	public SetupStub(Game g) {
		super(g);
		generateOrderCalled = false;
		changeColorCalled = false;
		handleGodMessageCalled = false;
		positionWorkersCalled = false;
	}

	public void resetCalls() {
		generateOrderCalled = false;
		changeColorCalled = false;
		handleGodMessageCalled = false;
		positionWorkersCalled = false;
	}
	public boolean isGenerateOrderCalled() {
		return generateOrderCalled;
	}
	public boolean isChangeColorCalled() {
		return changeColorCalled;
	}
	public boolean isHandleGodMessageCalled() {
		return handleGodMessageCalled;
	}
	public boolean isPositionWorkersCalled() {
		return positionWorkersCalled;
	}

	@Override
	public void generateOrder() {
		generateOrderCalled = true;
	}
	@Override
	public void changeColor(NetColorPreparation playerColors) throws BadRequestException, WrongPhaseException {
		changeColorCalled = true;
	}
	@Override
	public void handleGodMessage(NetDivinityChoice request) throws BadRequestException, WrongPhaseException {
		handleGodMessageCalled = true;
	}
	@Override
	public void positionWorkers(NetGameSetup positions) throws BadRequestException {
		positionWorkersCalled = true;
	}

}
