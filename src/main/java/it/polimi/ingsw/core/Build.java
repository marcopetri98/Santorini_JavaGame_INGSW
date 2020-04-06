package it.polimi.ingsw.core;

public class Build {
	private int typeBuild;
	private Cell cell;
	private Worker worker;
	private boolean dome;
	private Build other;

	public Build(Worker w, Cell c, boolean d, int t){
		this.worker = w;
		this.cell = c;
		this.dome = d;
		this.typeBuild = t;
	}

	public void setCondition(Build o){
		this.other = o;
	}

	public int getTypeBuild(){ return typeBuild; }

	private Cell getCell(){ return cell; }

	private Worker getWorker(){ return worker; }

	private boolean isDome(){ return dome; }

	private Build getOther(){ return other; }

	//TODO: controllare se le condizioni sono corrette!!!
	public boolean equals(Build o){
		if(this.cell == o.getCell() && this. worker == o.getWorker() && this.dome == o.isDome() && this.typeBuild == o.getTypeBuild()) return true;
		else return false;
	}

}