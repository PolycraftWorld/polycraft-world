package edu.utd.minecraft.mod.polycraft.config;

public class SourcedEntity<S extends Entity> extends Entity {

	public final S source;

	public SourcedEntity(final String name, final S source) {
		super(name);
		this.source = source;
	}
}
