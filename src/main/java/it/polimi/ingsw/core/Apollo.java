package it.polimi.ingsw.core;
import it.polimi.ingsw.exceptions.NoBuildException;

import java.util.List;
import java.util.ArrayList;

public class Apollo implements GodCard{
    private int typeGod = 0;
    private Player owner;
    int numPlayer = 4;
    String name = "Apollo";
    String description;
    List<Move> moves;
    List<Build> builds;

    public Apollo(Player player){
        this.owner = player;
    }

    public Apollo(){
        this.owner = null;
        this.moves = null;
        this.builds = null;
    }

    public int getNumPlayer(){
        return numPlayer;
    }
    public Player getOwner(){
        return owner;
    }
    public int getTypeGod(){
        return typeGod;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }

    public List<Build> checkBuild(Map m, Worker w, int type) throws NoBuildException {
        throw new NoBuildException();
    }

    public List<Move> checkMove(Map m, Worker w, int type){   //worker->activeworker
        int y = w.getPos().getY();
        int x = w.getPos().getX();
        moves = new ArrayList<>();
        for(int i = -1; i <= 1; i++){   //i->x   j->y     x1, y1 all the cells where I MAY move
            int x1 = x + i;
            for(int j = -1; j <= 1; j++){
                int y1 = y + j;

                if(x != x1 || y != y1){ //I shall not move where I am already
                    if(0 <= x1 && x1 <= 4 && 0 <= y1 && y1 <= 4){   //Check that I am inside the map
                        if(-1 <= (x1-x) && (x1-x) <= 1 && -1 <= (y1-y) && (y1-y) <=1){  //Check that distance from original is cell <= 1: useless?
                            if(m.getCell(x1, y1).getBuilding().getLevel() - m.getCell(x, y).getBuilding().getLevel() <= 1){ //Check height difference
                                if(!m.getCell(x1, y1).getBuilding().getDome()){   //Check there is NO dome
                                    if(owner.getWorker1().getPos().getX() != x1 && owner.getWorker1().getPos().getY() != y1 && owner.getWorker2().getPos().getX() != x1 && owner.getWorker2().getPos().getY() != y1){   //Check there is no OWNER worker on cell
                                        //Checks for opponent's workers because of Apollo's power
                                        //TODO: la typeMove di un worker che non comporta il movimento di un altro worker dev'essere 1?!?!
                                        if(m.getCell(x1, y1).getWorker() != null){
                                            Move newMove = new Move(1, m.getCell(x, y), m.getCell(x1, y1), w);
                                            newMove.setCondition(new Move(1, m.getCell(x1, y1), m.getCell(x, y), m.getCell(x1, y1).getWorker()));
                                            moves.add(newMove);
                                        }
                                        else{
                                            moves.add(new Move(1, m.getCell(x, y), m.getCell(x1, y1), w));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return moves;
    }
}