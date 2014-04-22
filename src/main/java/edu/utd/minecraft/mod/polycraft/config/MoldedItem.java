package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class MoldedItem extends SourcedConfig<Mold> {

	public static final ConfigRegistry<MoldedItem> registry = new ConfigRegistry<MoldedItem>();

	public static void registerFromResource(final String directory, final String extension, final String delimeter) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, MoldedItem.class.getSimpleName().toLowerCase(), extension, delimeter))
			if (line.length > 0)
				registry.register(new MoldedItem(
						line[0], //gameID
						line[1], //name
						Mold.registry.get(line[2]), //mold
						Polymer.registry.get(line[3]), //polymer
						Integer.parseInt(line[4]) //pellets
				));
	}

	public final Polymer polymer;
	public final int pellets;

	public MoldedItem(final String gameID, final String name, final Mold mold, final Polymer polymer, final int pellets) {
		super(gameID, name, mold);
		this.polymer = polymer;
		this.pellets = pellets;
	}
}