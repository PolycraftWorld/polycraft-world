package edu.utd.minecraft.mod.polycraft.config;

public class Compound extends Entity {

	public static final EntityRegistry<Compound> registry = new EntityRegistry<Compound>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, Compound.class.getSimpleName().toLowerCase(), extension, delimeter))
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