package edu.utd.minecraft.mod.polycraft.config;

public abstract class Entity {

	public final String gameName;
	public final String name;

	public Entity(final String gameName, final String name) {
		this.gameName = gameName;
		this.name = name;
	}
}
