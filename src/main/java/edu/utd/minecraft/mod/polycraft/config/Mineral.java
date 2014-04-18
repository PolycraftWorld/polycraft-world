package edu.utd.minecraft.mod.polycraft.config;

public class Mineral extends Entity {

	public static final EntityRegistry<Mineral> registry = new EntityRegistry<Mineral>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, Mineral.class.getSimpleName().toLowerCase(), extension, delimeter))
			registry.register(new Mineral(line[0]));
	}

	public Mineral(final String name) {
		super(name);
	}
}
