package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class PolymerObject extends Config {

	public static final ConfigRegistry<PolymerObject> registry = new ConfigRegistry<PolymerObject>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerObject.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new PolymerObject(
						PolycraftMod.getVersionNumeric(line[index++]), //version
						line[1] //name
				));
			}
	}

	public PolymerObject(final int[] version, final String name) {
		super(version, name);
	}
}