package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class MinecraftItem extends Config {

	public static final ConfigRegistry<MinecraftItem> registry = new ConfigRegistry<MinecraftItem>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, MinecraftItem.class.getSimpleName().toLowerCase()))
			if (line.length > 4 && !"0".equals(line[0]))
				registry.register(new MinecraftItem(
						line[0], //name
						Integer.parseInt(line[4]) //id
				));
	}

	public final int id;

	public MinecraftItem(final String name, final int id) {
		super(name);
		this.id = id;
	}
}