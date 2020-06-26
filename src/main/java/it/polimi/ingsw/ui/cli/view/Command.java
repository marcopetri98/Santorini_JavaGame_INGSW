package it.polimi.ingsw.ui.cli.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is the class used to get commands of the user with the CLI.
 */
public class Command {
	public String commandType;
	private String[] otherValues;

	/**
	 * It creates a command with the given words
	 * @param words words wrote by the user
	 */
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
				for (int i = 0; i < otherValues.length; i++) {
					otherValues[i] = words[i+1];
				}
			}
		}
	}

	/**
	 * It returns the number of the parameters of the command
	 * @return the number of parameters of the command
	 */
	public int getNumParameters() {
		if (otherValues == null) {
			return 0;
		} else {
			return otherValues.length;
		}
	}
	/**
	 * It returns the parameter with the index specified in the parameter
	 * @param i an integer greater than 0
	 * @return the parameter at that index
	 * @throws IndexOutOfBoundsException if {@code i} is less than 0 or greater than the index of the last element
	 */
	public String getParameter(int i) {
		if (i < otherValues.length) {
			return otherValues[i];
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	/**
	 * This methods converts parameters of the commands to a list and returns it.
	 * @return list of parameters
	 */
	public List<String> getParameterList() {
		List<String> returnList = new ArrayList<>();
		for (int i = 0; i < otherValues.length; i++) {
			returnList.add(otherValues[i]);
		}
		return returnList;
	}
}
