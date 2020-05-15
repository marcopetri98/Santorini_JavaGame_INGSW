package it.polimi.ingsw.ui.cli.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
				for (int i = 0; i < otherValues.length; i++) {
					otherValues[i] = words[i+1];
				}
			}
		}
	}

	//eventually setters and getters; constructor...
	public int getNumParameters() {
		if (otherValues == null) {
			return 0;
		} else {
			return otherValues.length;
		}
	}
	public String getParameter(int i) {
		if (i < otherValues.length) {
			return otherValues[i];
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	public List<String> getParameterList() {
		List<String> returnList = new ArrayList<>();
		for (int i = 0; i < otherValues.length; i++) {
			returnList.add(otherValues[i]);
		}
		return returnList;
	}
}
