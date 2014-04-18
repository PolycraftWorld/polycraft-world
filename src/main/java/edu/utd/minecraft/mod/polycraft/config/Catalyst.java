package edu.utd.minecraft.mod.polycraft.config;

public class Catalyst extends SourcedEntity {

	public static final EntityRegistry<Catalyst> registry = new EntityRegistry<Catalyst>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, Catalyst.class.getSimpleName().toLowerCase(), extension, delimeter))
			registry.register(new Catalyst(
					line[0],
					Entity.find(line[1], line[2]) //source
			));
	}

	public Catalyst(final String name, final Entity source) {
		super(name, source);
	}
}