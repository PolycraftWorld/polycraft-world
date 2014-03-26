package edu.utd.minecraft.mod.polycraft.config;

public abstract class Entity {

	protected static final String getSafeName(final String name) {
		return name.replaceAll("[^_A-Za-z0-9]", "_").toLowerCase();
	}

	public final String gameName;
	public final String name;

	public Entity(final String gameName, final String name) {
		this.gameName = gameName;
		this.name = name;
	}

	public String export(final String delimiter) {
		return name;
	}
}