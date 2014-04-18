package edu.utd.minecraft.mod.polycraft.config;

public class PolymerFibers extends SourcedEntity<Polymer> {

	public static final EntityRegistry<PolymerFibers> registry = new EntityRegistry<PolymerFibers>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, PolymerFibers.class.getSimpleName().toLowerCase(), extension, delimeter))
			registry.register(new PolymerFibers(
					line[0],
					(Polymer) Entity.find(line[1], line[2]) //source
			));
	}

	public PolymerFibers(final String name, final Polymer source) {
		super(name, source);
	}
}