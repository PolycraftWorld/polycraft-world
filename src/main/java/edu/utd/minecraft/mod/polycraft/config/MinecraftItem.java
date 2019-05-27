package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class MinecraftItem extends Config {

	public static final ConfigRegistry<MinecraftItem> registry = new ConfigRegistry<MinecraftItem>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, MinecraftItem.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				final int[] version = PolycraftMod.getVersionNumeric(line[0]);
				if (version != null)
					registry.register(new MinecraftItem(
							version, //version
							line[1], //name
							Integer.parseInt(line[5]) //id
					));
			}
	}

	public final int id;

	public MinecraftItem(final int[] version, final String name, final int id) {
		super(version, name);
		this.id = id;
	}

	public ItemStack getItemStack() {
		return new ItemStack(PolycraftRegistry.getItem(name));
	}
}