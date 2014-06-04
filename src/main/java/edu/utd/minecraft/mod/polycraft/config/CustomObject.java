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
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //gameID
						line[2], //name
						line.length > 7 ? line[7] : null, //maxStackSize
						line.length > 8 ? line[8].split(",") : null, //paramNames
						line, 9 //params
				));
	}

	public final int maxStackSize;;

	public CustomObject(final int[] version, final String gameID, final String name, final String maxStackSize, final String[] paramNames, final String[] paramValues, final int paramsOffset) {
		super(version, gameID, name, paramNames, paramValues, paramsOffset);
		this.maxStackSize = StringUtils.isEmpty(maxStackSize) ? 0 : Integer.parseInt(maxStackSize);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
}