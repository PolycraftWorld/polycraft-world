package edu.utd.minecraft.mod.polycraft.config;

public class PolymerSlab extends SourcedEntity<Polymer> {

	public static final EntityRegistry<PolymerSlab> registry = new EntityRegistry<PolymerSlab>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, PolymerSlab.class.getSimpleName().toLowerCase(), extension, delimeter))
			registry.register(new PolymerSlab(
					line[0],
					(Polymer) Entity.find(line[1], line[2]), //source
					Integer.parseInt(line[3])
					));
	}

	public final int bounceHeight;

	public final String itemNameSlab;
	public final String itemNameDoubleSlab;
	public final String blockNameSlab;
	public final String blockNameDoubleSlab;

	public PolymerSlab(final String name, final Polymer source, final int bounceHeight) {
		super(name, source);
		this.bounceHeight = bounceHeight;

		this.blockNameSlab = name;
		this.blockNameDoubleSlab = "Double " + blockNameSlab;
		this.itemNameSlab = blockNameSlab + " Item";
		this.itemNameDoubleSlab = blockNameDoubleSlab + " Item";
	}
}