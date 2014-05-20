package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringUtils;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class CustomObject extends GameIdentifiedConfig {

	public static final ConfigRegistry<CustomObject> registry = new ConfigRegistry<CustomObject>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, CustomObject.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new CustomObject(
						line[0], //gameID
						line[1], //name
						line.length > 5 ? line[5] : null, //maxStackSize
						line, 8 //params
				));
	}

	public final int maxStackSize;;

	public CustomObject(final String gameID, final String name, final String maxStackSize, final String[] params, final int paramsOffset) {
		super(gameID, name, params, paramsOffset);
		this.maxStackSize = StringUtils.isEmpty(maxStackSize) ? 0 : Integer.parseInt(maxStackSize);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
}