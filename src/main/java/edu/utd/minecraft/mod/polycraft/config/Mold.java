package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Mold extends GameIdentifiedConfig {

	public static final ConfigRegistry<Mold> registry = new ConfigRegistry<Mold>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Mold.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new Mold(
						line[0], //gameID
						line[1], //name
						Integer.parseInt(line[4]) //maxDamage
				));
	}

	public final int maxDamage;

	public Mold(final String gameID, final String name, final int maxDamage) {
		super(gameID, name);
		this.maxDamage = maxDamage;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getItem(this), size);
	}
}
