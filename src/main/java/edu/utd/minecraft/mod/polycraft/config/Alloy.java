package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Alloy extends Config {

	public static final ConfigRegistry<Alloy> registry = new ConfigRegistry<Alloy>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Alloy.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new Alloy(line[0]));
	}

	public Alloy(final String name) {
		super(name);
	}
}