package edu.utd.minecraft.mod.polycraft.scoreboards;

import java.awt.Color;

public class Team {
	private String name;
	private int id;
	private Color color;
	private static ColorEnum colorlist = ColorEnum.Blue;
	private static enum ColorEnum {
		Cyan,
		Blue,
		Red,
		Green,
		Orange;
		
		public ColorEnum next() {
		    if (ordinal() == values().length - 1)
		    	return values()[0];
		    return values()[ordinal() + 1];
		}
	}

	public Team(String name) {
		this.name = name;
		switch(colorlist) {
			case Blue:
				setColor(Color.BLUE);
				break;
			case Green:
				setColor(Color.GREEN);
				break;
			case Red:
				setColor(Color.RED);
				break;
			case Orange:
				setColor(Color.ORANGE);
				break;
			case Cyan:
				setColor(Color.CYAN);
			default:
				setColor(Color.PINK);
		}
		colorlist = colorlist.next();
	}

	public Team(String name, int id) {
		this.id = id;
		this.name = name;
		switch(colorlist) {
			case Blue:
				setColor(Color.BLUE);
				break;
			case Green:
				setColor(Color.GREEN);
				break;
			case Red:
				setColor(Color.RED);
				break;
			case Orange:
				setColor(Color.ORANGE);
				break;
			case Cyan:
				setColor(Color.CYAN);
			default:
				setColor(Color.PINK);
		}
		colorlist = colorlist.next();
	}

	public String getName() {
		return name;
	}

	public int getID() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		else if (o instanceof String) {
			return this.name.equals(o); // string comparison.
		} else if (o instanceof Team) {
			return this.name.equals(((Team) o).getName());
		} else
			return false;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}