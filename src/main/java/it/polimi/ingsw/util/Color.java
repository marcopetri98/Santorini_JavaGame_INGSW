package it.polimi.ingsw.util;

import java.io.Serializable;

/**
 * This is a class which represent a rgb color.
 */
public class Color implements Serializable {
	public static final Color BLACK = new Color(0,0,0);
	public static final Color WHITE = new Color(255,255,255);
	public static final Color RED = new Color(255,0,0);
	public static final Color GREEN = new Color(0,255,0);
	public static final Color BLUE = new Color(0,0,255);
	private final int r;
	private final int g;
	private final int b;

	/**
	 * Creates a rgb color with given parameters.
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	public Color(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	/**
	 * Creates a color with the given color name if it is supported.
	 * @param colorName color name
	 * @throws IllegalArgumentException if the color name is not supported
	 */
	public Color(String colorName) throws IllegalArgumentException {
		switch (colorName.toUpperCase()) {
			case "BLACK" -> {
				r = 0; g = 0; b = 0;
			}
			case "WHITE" -> {
				r = 255; g = 255; b = 255;
			}
			case "RED" -> {
				r = 255; g = 0; b = 0;
			}
			case "GREEN" -> {
				r = 0; g = 255; b = 0;
			}
			case "BLUE" -> {
				r = 0; g = 0; b = 255;
			}
			default -> throw new IllegalArgumentException();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Color) {
			Color other = (Color)obj;
			return r == other.r && g == other.g && b == other.b;
		} else {
			return false;
		}
	}
}
