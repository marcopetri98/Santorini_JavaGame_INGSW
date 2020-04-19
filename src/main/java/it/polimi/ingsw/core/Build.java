package it.polimi.ingsw.core;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.game.NetBuild;

public class Build {
	public final int typeBuild;
	public final Cell cell;
	public final Worker worker;
	public final boolean dome;
	private Build other;

	public Build(Worker w, Cell c, boolean d, int t){
		this.worker = w;
		this.cell = c;
		this.dome = d;
		this.typeBuild = t;
	}

	// class setters
	public void setCondition(Build o){
		this.other = o;
	}

	// class getters
	public int getTypeBuild(){ return typeBuild; }
	public Cell getCell(){ return cell; }
	public Worker getWorker(){ return worker; }
	public boolean isDome(){ return dome; }
	public Build getOther(){ return other; }
	public boolean isSameAs(NetBuild playerBuild) {
		return cell.map.getY(cell) == playerBuild.cellY && cell.map.getX(cell) == playerBuild.cellX && worker.workerID == playerBuild.workerID && ((cell.getBuilding().getLevel() == playerBuild.level-1 && cell.getBuilding().getLevel() < 3) || dome == playerBuild.dome) && ((other == null && playerBuild.other == null) || (other != null && playerBuild.other != null && other.isSameAs(playerBuild.other)));
	}

	// overridden methods
	// TODO: rimuovere perché sbagliata (chiedere perché è stata scritta)
	public Build clone(){
		return new Build(this.getWorker(), this.getCell(), this.isDome(), this.getTypeBuild());
	}
	//TODO: fixare perché sbagliata
	public boolean equals(Build o){
		if(this.cell == o.getCell() && this. worker == o.getWorker() && this.dome == o.isDome() && this.typeBuild == o.getTypeBuild()) return true;
		else return false;
	}

}