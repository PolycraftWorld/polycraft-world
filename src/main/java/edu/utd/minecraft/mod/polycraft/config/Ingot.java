package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Ingot extends SourcedConfig {

	public static final ConfigRegistry<Ingot> registry = new ConfigRegistry<Ingot>();

	public static void registerFromResource(final String directory, final String extension, final String delimeter) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Ingot.class.getSimpleName().toLowerCase(), extension, delimeter))
			if (line.length > 0)
				registry.register(new Ingot(
						line[0], //gameID
						line[1], //name
						Config.find(line[2], line[3]) //source
				));
	}

	public Ingot(final String gameID, final String name, final Config source) {
		super(gameID, name, source);
	}
}
