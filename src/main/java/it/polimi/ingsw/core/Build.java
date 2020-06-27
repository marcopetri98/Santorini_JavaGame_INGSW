package it.polimi.ingsw.core;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.game.NetBuild;

public class Build {
	public TypeBuild typeBuild;
	public final Cell cell;
	public final Worker worker;
	public final boolean dome;
	private Build other;

	public Build(Worker w, Cell c, boolean d, TypeBuild t){
		this.worker = w;
		this.cell = c;
		this.dome = d;
		this.typeBuild = t;
	}

	// class setters
	public void setCondition(Build o){
		this.other = o;
	}
	public void setTypeBuild(TypeBuild t){ this.typeBuild = t; }

	// class getters
	public TypeBuild getTypeBuild(){ return typeBuild; }
	public Cell getCell(){ return cell; }
	public Worker getWorker(){ return worker; }
	public boolean isDome(){ return dome; }
	public Build getOther(){ return other; }
	public boolean isSameAs(NetBuild playerBuild) {
		return cell.map.getY(cell) == playerBuild.cellY && cell.map.getX(cell) == playerBuild.cellX && worker.workerID == playerBuild.workerID && ((cell.getBuilding().getLevel() == playerBuild.level-1 && cell.getBuilding().getLevel() < 3) || dome == playerBuild.dome) && ((other == null && playerBuild.other == null) || (other != null && playerBuild.other != null && other.isSameAs(playerBuild.other)));
	}
	public Build copy(){
		return new Build(this.getWorker(), this.getCell(), this.isDome(), this.getTypeBuild());
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof Build){
			Build b = (Build) obj;
			if(this.typeBuild == b.getTypeBuild() && this.cell == b.getCell() && this.dome == b.isDome() && this.worker == b.getWorker() && ((this.other == null && this.other == b.getOther()) || (this.other != null && b.getOther() != null && this.other.equals(b.getOther())))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}