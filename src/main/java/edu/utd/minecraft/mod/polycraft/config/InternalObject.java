package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class InternalObject extends GameIdentifiedConfig {

	public static final ConfigRegistry<InternalObject> registry = new ConfigRegistry<InternalObject>();

	public static void registerFromResource(final String directory, final String extension, final String delimeter) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, InternalObject.class.getSimpleName().toLowerCase(), extension, delimeter))
			if (line.length > 0)
				registry.register(new InternalObject(
						line[0], //gameID
						line[1] //name
				));
	}

	public InternalObject(final String gameID, final String name) {
		super(gameID, name);
	}

	@Override
	public ItemStack getItemStack(int size) {
		throw new Error("Not implemented");
	}
}