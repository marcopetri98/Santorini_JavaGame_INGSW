package it.polimi.ingsw.core;

// necessary imports from other packages of the project
import it.polimi.ingsw.network.game.NetBuild;

/**
 * This class represents a build of the workers in the game
 */
public class Build {
	public TypeBuild typeBuild;
	public final Cell cell;
	public final Worker worker;
	public final boolean dome;
	private Build other;

	/**
	 * Constructor of the class
	 * @param w the {@link Worker} involved in the build
	 * @param c the {@link Cell} in which the player can or will build
	 * @param d if true the build is a dome
	 * @param t the {@link TypeBuild}
	 */
	public Build(Worker w, Cell c, boolean d, TypeBuild t){
		this.worker = w;
		this.cell = c;
		this.dome = d;
		this.typeBuild = t;
	}

	// SETTERS

	/**
	 * Setter of the {@code other} {@link Build}
	 * @param o the {@code other} {@link Build}
	 */
	public void setCondition(Build o){
		this.other = o;
	}

	/**
	 * Setter of the {@link TypeBuild} for this {@link Build}
	 * @param t the {@link TypeBuild}
	 */
	public void setTypeBuild(TypeBuild t){ this.typeBuild = t; }

	// GETTERS

	/**
	 * Getter of the {@link TypeBuild}
	 * @return the {@link TypeBuild}
	 */
	public TypeBuild getTypeBuild(){ return typeBuild; }

	/**
	 * Getter of the {@link Cell} of this {@link Build}
	 * @return the {@link Cell}
	 */
	public Cell getCell(){ return cell; }

	/**
	 * Getter of the {@link Worker} of this {@link Build}
	 * @return the {@link Worker}
	 */
	public Worker getWorker(){ return worker; }

	/**
	 * Getter of the {@code dome} of this {@link Build}
	 * @return the {@code dome}
	 */
	public boolean isDome(){ return dome; }

	/**
	 * Getter of the {@code other} {@link Build}
	 * @return the {@code other} {@link Build}
	 */
	public Build getOther(){ return other; }

	/**
	 * Check if a {@link Build} and a {@link NetBuild} are the same
	 * @param playerBuild the {@link NetBuild}
	 * @return true if they are the same
	 */
	public boolean isSameAs(NetBuild playerBuild) {
		return cell.map.getY(cell) == playerBuild.cellY && cell.map.getX(cell) == playerBuild.cellX && worker.workerID == playerBuild.workerID && ((cell.getBuilding().getLevel() == playerBuild.level-1 && cell.getBuilding().getLevel() < 3) || dome == playerBuild.dome) && ((other == null && playerBuild.other == null) || (other != null && playerBuild.other != null && other.isSameAs(playerBuild.other)));
	}

	/**
	 * Method that copies this {@link Build}
	 * @return a copy of this {@link Build}
	 */
	public Build copy(){
		return new Build(this.getWorker(), this.getCell(), this.isDome(), this.getTypeBuild());
	}

	// OVERRIDDEN METHODS

	/**
	 * Overridden equals method
	 * @param obj the object to check
	 * @return true if they are the same
	 */
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