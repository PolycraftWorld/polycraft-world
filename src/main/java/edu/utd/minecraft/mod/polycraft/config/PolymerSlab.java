package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class PolymerSlab extends SourcedConfig<Polymer> {

	public static final ConfigRegistry<PolymerSlab> registry = new ConfigRegistry<PolymerSlab>();

	public static void registerFromResource(final String directory, final String extension, final String delimeter) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerSlab.class.getSimpleName().toLowerCase(), extension, delimeter))
			if (line.length > 0)
				registry.register(new PolymerSlab(
						line[0], //itemSlabGameID
						line[1], //itemDoubleSlabGameID
						line[2], //blockSlabGameID
						line[3], //blockDoubleSlabGameID
						line[4], //name
						(Polymer) Config.find(line[5], line[6]), //source
						Integer.parseInt(line[7]) //bounceHeight
				));
	}

	public final int bounceHeight;

	public final String itemSlabGameID;
	public final String itemDoubleSlabGameID;
	public final String itemSlabName;
	public final String itemDoubleSlabName;
	public final String blockSlabGameID;
	public final String blockDoubleSlabGameID;
	public final String blockSlabName;
	public final String blockDoubleSlabName;

	public PolymerSlab(final String itemSlabGameID, final String itemDoubleSlabGameID, final String blockSlabGameID, final String blockDoubleSlabGameID, final String name, final Polymer source, final int bounceHeight) {
		super(itemSlabGameID, name, source);
		this.bounceHeight = bounceHeight;

		this.itemSlabGameID = itemSlabGameID;
		this.itemDoubleSlabGameID = itemDoubleSlabGameID;
		this.blockSlabGameID = blockSlabGameID;
		this.blockDoubleSlabGameID = blockDoubleSlabGameID;
		this.blockSlabName = name;
		this.blockDoubleSlabName = "Double " + blockSlabName;
		this.itemSlabName = blockSlabName + " Item";
		this.itemDoubleSlabName = blockDoubleSlabName + " Item";
	}
}