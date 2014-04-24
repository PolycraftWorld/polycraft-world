package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Mineral extends Config {

	public static final ConfigRegistry<Mineral> registry = new ConfigRegistry<Mineral>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Mineral.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new Mineral(line[0]));
	}

	public Mineral(final String name) {
		super(name);
	}
}
