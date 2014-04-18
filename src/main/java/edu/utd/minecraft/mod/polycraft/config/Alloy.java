package edu.utd.minecraft.mod.polycraft.config;

public class Alloy extends Entity {

	public static final EntityRegistry<Alloy> registry = new EntityRegistry<Alloy>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, Alloy.class.getSimpleName().toLowerCase(), extension, delimeter))
			registry.register(new Alloy(line[0]));
	}

	public Alloy(final String name) {
		super(name);
	}
}