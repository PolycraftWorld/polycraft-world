package edu.utd.minecraft.mod.polycraft.config;

public abstract class SourcedConfig<S extends Config> extends GameIdentifiedConfig {

	public final S source;

	public SourcedConfig(final int[] version, final String gameID, final String name, final S source) {
		super(version, gameID, name);
		this.source = source;
	}

	public SourcedConfig(final int[] version, final String gameID, final String name, final S source, final String[] params, final int paramsOffset) {
		super(version, gameID, name, params, paramsOffset);
		this.source = source;
	}
}
