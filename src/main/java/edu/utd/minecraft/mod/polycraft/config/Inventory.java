package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Inventory extends GameIdentifiedConfig {

	public static final ConfigRegistry<Inventory> registry = new ConfigRegistry<Inventory>();

	public static void registerFromResource(final String directory, final String extension, final String delimeter) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Inventory.class.getSimpleName().toLowerCase(), extension, delimeter))
			if (line.length > 0)
				registry.register(new Inventory(
						line[0], //gameID
						line[1] //name
				));
	}

	public Inventory(final String gameID, final String name) {
		super(gameID, name);
	}
}