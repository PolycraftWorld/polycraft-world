package edu.utd.minecraft.mod.polycraft.config;

public class Ingot extends SourcedEntity {

	public static final EntityRegistry<Ingot> registry = new EntityRegistry<Ingot>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, Ingot.class.getSimpleName().toLowerCase(), extension, delimeter))
			registry.register(new Ingot(
					line[0],
					Entity.find(line[1], line[2]) //source
			));
	}

	public Ingot(final String name, final Entity source) {
		super(name, source);
	}
}
