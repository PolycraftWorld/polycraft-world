package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class MinecraftBlock extends Config {

	public static final ConfigRegistry<MinecraftBlock> registry = new ConfigRegistry<MinecraftBlock>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, MinecraftBlock.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				final int[] version = PolycraftMod.getVersionNumeric(line[0]);
				if (version != null)
					registry.register(new MinecraftBlock(
							version, //version
							line[1], //name
							Integer.parseInt(line[5]) //id
					));
			}
	}

	public final int id;

	public MinecraftBlock(final int[] version, final String name, final int id) {
		super(version, name);
		this.id = id;
	}
}