package edu.utd.minecraft.mod.polycraft.config;

public class GameIdentifiedConfig<S extends Config> extends Config {

	public final String gameID;

	public GameIdentifiedConfig(final String gameID, final String name) {
		super(name);
		this.gameID = gameID;
	}
}
