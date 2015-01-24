package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Compound extends Config {

	public static final ConfigRegistry<Compound> registry = new ConfigRegistry<Compound>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Compound.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new Compound(
						PolycraftMod.getVersionNumeric(line[index++]), //version
						line[index++], //name
						line[index++], //formula
						Matter.State.valueOf(line[index++]), //state
						line.length > index ? line[index++] : "", //uses
						line.length > index ? line[index++] : "" //sources
				));
			}
	}

	public final String formula;
	public final Matter.State state;
	public final String uses;
	public final String sources;

	public Compound(final int[] version, final String name, final String formula, final Matter.State state, final String uses, final String sources) {
		super(version, name);
		this.formula = formula;
		this.state = state;
		this.uses = uses;
		this.sources = sources;
	}
}