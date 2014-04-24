package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class CustomObject extends GameIdentifiedConfig {

	public static final ConfigRegistry<CustomObject> registry = new ConfigRegistry<CustomObject>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, CustomObject.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new CustomObject(
						line[0], //gameID
						line[1] //name
				));
	}

	public CustomObject(final String gameID, final String name) {
		super(gameID, name);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getItem(this), size);
	}
}