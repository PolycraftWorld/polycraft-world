package edu.utd.minecraft.mod.polycraft.scoreboards;

public class Team {
	private String name;
	private int id;

	public Team(String name) {
		this.name = name;
	}

	public Team(String name, int id) {
		this.name = name;
		this.id = id;
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
}