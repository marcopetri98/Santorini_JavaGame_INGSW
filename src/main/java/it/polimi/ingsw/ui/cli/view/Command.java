package it.polimi.ingsw.ui.cli.view;

public class Command {
	public String commandType;
	private String[] otherValues;

	public Command(String[] words) {
		if (words == null) {
			commandType = null;
			otherValues = null;
		} else {
			commandType = words[0];
			if (words.length <= 1) {
				otherValues = null;
			} else {
				otherValues = new String[words.length-1];
				for (int i = 1; i < otherValues.length; i++) {
					otherValues[i] = words[i];
				}
			}
		}
	}

	//eventually setters and getters; constructor...
	public int getNumParameters() {
		return otherValues.length;
	}
	public String getParameter(int i) {
		if (i < otherValues.length) {
			return otherValues[i];
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
}
