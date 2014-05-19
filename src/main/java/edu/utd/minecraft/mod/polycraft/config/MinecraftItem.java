package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class MinecraftItem extends Config {

	public static final ConfigRegistry<MinecraftItem> registry = new ConfigRegistry<MinecraftItem>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, MinecraftItem.class.getSimpleName().toLowerCase()))
			if (line.length > 4 && !"0".equals(line[0]))
				registry.register(new MinecraftItem(
						line[0], // name
						Integer.parseInt(line[4]) // id
				));
	}

	public final int id;

	public MinecraftItem(final String name, final int id) {
		super(name);
		this.id = id;
	}

	public ItemStack getItemStack() {
		return new ItemStack(PolycraftRegistry.getItem(name));
	}
}