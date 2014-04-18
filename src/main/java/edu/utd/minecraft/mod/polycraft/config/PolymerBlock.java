package edu.utd.minecraft.mod.polycraft.config;

public class PolymerBlock extends SourcedEntity<Polymer> {

	public static final EntityRegistry<PolymerBlock> registry = new EntityRegistry<PolymerBlock>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, PolymerBlock.class.getSimpleName().toLowerCase(), extension, delimeter))
			registry.register(new PolymerBlock(
					line[0],
					(Polymer) Entity.find(line[1], line[2]), //source
					Integer.parseInt(line[3])
					));
	}

	public final int bounceHeight;

	public PolymerBlock(final String name, final Polymer source, final int bounceHeight) {
		super(name, source);
		this.bounceHeight = bounceHeight;
	}
}