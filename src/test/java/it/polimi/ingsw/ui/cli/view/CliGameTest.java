/*package it.polimi.ingsw.ui.cli.view;

import it.polimi.ingsw.core.Map;
import it.polimi.ingsw.core.Move;
import it.polimi.ingsw.core.Player;
import it.polimi.ingsw.core.gods.Apollo;
import it.polimi.ingsw.core.gods.GodCard;
import it.polimi.ingsw.core.state.GamePhase;
import it.polimi.ingsw.core.state.Phase;
import it.polimi.ingsw.core.state.Turn;
import it.polimi.ingsw.network.game.NetMap;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.exceptions.NoMoveException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CliGameTest {
	Map map;
	Player player;
	NetMap netMap;
	private CliGame cligame;



	@Before
	public void testSetup(){
		map = new Map();
		cligame = new CliGame();
	}

	@Test
	public void start() {
	}



	@Test
	public void basicDrawMap() {
		netMap = new NetMap(map);
		cligame.setNetMap(netMap);
		cligame.drawMap();
	}

	@Test
	public void drawMap1() {
		player = new Player("Pippo");
		player.setPlayerColor(Color.RED);
		Player opponent = new Player("Pluto");
		opponent.setPlayerColor(Color.BLUE);
		GodCard apollo = new Apollo(player);

		int x=0, y=0, x1=3, y1=3;
		int h=1, k=1, h1=4, k1=4;
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		netMap = new NetMap(map);
		cligame.setNetMap(netMap);
		cligame.drawMap();
	}

	@Test
	public void drawPossibilities() {
		player = new Player("Pippo");
		player.setPlayerColor(Color.RED);
		Player opponent = new Player("Pluto");
		opponent.setPlayerColor(Color.BLUE);
		GodCard apollo = new Apollo(player);
		Turn turn = new Turn();
		while(turn.getGamePhase() != GamePhase.MOVE){
			turn.advance();
		}
		List<Move> moves = null;

		int x=0, y=0, x1=3, y1=3;
		int h=1, k=1, h1=4, k1=4;
		map.getCell(x, y).setWorker(player.getWorker1());
		player.getWorker1().setPos(map.getCell(x, y));
		map.getCell(x1, y1).setWorker(player.getWorker2());
		player.getWorker2().setPos(map.getCell(x1, y1));
		map.getCell(h, k).setWorker(opponent.getWorker1());
		opponent.getWorker1().setPos(map.getCell(h, k));
		map.getCell(h1, k1).setWorker(opponent.getWorker2());
		opponent.getWorker2().setPos(map.getCell(h1, k1));

		try {
			moves = new ArrayList<>();
			moves = apollo.checkMove(map, player.getWorker1(), turn);
		}
		catch (NoMoveException nme){
			nme.printStackTrace();
		}

		if(moves != null){
			for(Move m : moves){
				cligame.addNetMove(m);
			}
		}

		while(!cligame.getPhase().getGamePhase().equals(GamePhase.MOVE)){
			cligame.phase.advance();
		}
		netMap = new NetMap(map);
		cligame.setNetMap(netMap);
		cligame.drawPossibilities();
	}


}*/