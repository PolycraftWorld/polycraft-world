package edu.utd.minecraft.mod.polycraft.config;

public class PolymerPellets extends SourcedEntity<Polymer> {

	public static final EntityRegistry<PolymerPellets> registry = new EntityRegistry<PolymerPellets>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, PolymerPellets.class.getSimpleName().toLowerCase(), extension, delimeter))
			registry.register(new PolymerPellets(
					line[0],
					(Polymer) Entity.find(line[1], line[2]) //source
			));
	}

	public PolymerPellets(final String name, final Polymer source) {
		super(name, source);
	}
}