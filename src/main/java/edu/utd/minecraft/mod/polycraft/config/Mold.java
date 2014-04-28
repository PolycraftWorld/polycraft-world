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
						PolymerObject.registry.get(line[3]), //polymerObject
						Integer.parseInt(line[4]) //craftingMaxDamage
				));
	}

	public final PolymerObject polymerObject;
	public final int craftingMaxDamage;

	public Mold(final String gameID, final String name, final PolymerObject polymerObject, final int craftingMaxDamage) {
		super(gameID, name);
		this.polymerObject = polymerObject;
		this.craftingMaxDamage = craftingMaxDamage;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getItem(this), size);
	}
}
