package edu.utd.minecraft.mod.polycraft.config;

public abstract class SourcedConfig<S extends Config> extends GameIdentifiedConfig {

	public final S source;

	public SourcedConfig(final int[] version, final String gameID, final String name, final S source) {
		super(version, gameID, name);
		this.source = source;
	}

	public SourcedConfig(final int[] version, final String gameID, final String name, final S source, final String[] paramNames, final String[] paramValues, final int paramsOffset) {
		super(version, gameID, name, paramNames, paramValues, paramsOffset);
		this.source = source;
	}
}
