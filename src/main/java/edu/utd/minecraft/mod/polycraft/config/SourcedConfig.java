package edu.utd.minecraft.mod.polycraft.config;

public class SourcedConfig<S extends Config> extends GameIdentifiedConfig {

	public final S source;

	public SourcedConfig(final String gameID, final String name, final S source) {
		super(gameID, name);
		this.source = source;
	}
}
