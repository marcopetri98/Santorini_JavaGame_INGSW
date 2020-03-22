package it.polimi.ingsw.ui;

// necessary imports of Java SE for ServerController class
import java.util.Observable;
import java.util.Observer;

public abstract class GraphicInterface extends Observable implements Observer, Runnable {
	// preparation methods
	public abstract void setupView();

	// view update methods
	public abstract void showModel();
}
