package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class MinecraftBlock extends Config {

	public static final ConfigRegistry<MinecraftBlock> registry = new ConfigRegistry<MinecraftBlock>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, MinecraftBlock.class.getSimpleName().toLowerCase()))
			if (line.length > 4 && !"0".equals(line[0]))
				registry.register(new MinecraftBlock(
						line[0], //name
						Integer.parseInt(line[4]) //id
				));
	}

	public final int id;

	public MinecraftBlock(final String name, final int id) {
		super(name);
		this.id = id;
	}
}