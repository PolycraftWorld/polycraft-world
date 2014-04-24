package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Compound extends Config {

	public static final ConfigRegistry<Compound> registry = new ConfigRegistry<Compound>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Compound.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new Compound(
						line[0], //name
						line.length > 1 ? line[1] : "", //formula
						line.length > 2 ? line[2] : "", //uses
						line.length > 3 ? line[3] : "" //sources
				));
	}

	public final String formula;
	public final String uses;
	public final String sources;

	public Compound(final String name, final String formula, final String uses, final String sources) {
		super(name);
		this.formula = formula;
		this.uses = uses;
		this.sources = sources;
	}
}