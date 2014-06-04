package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class InternalObject extends GameIdentifiedConfig {

	public static final ConfigRegistry<InternalObject> registry = new ConfigRegistry<InternalObject>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, InternalObject.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new InternalObject(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //gameID
						line[2] //name
				));
	}

	public InternalObject(final int[] version, final String gameID, final String name) {
		super(version, gameID, name);
	}

	@Override
	public ItemStack getItemStack(int size) {
		throw new Error("Not implemented");
	}
}