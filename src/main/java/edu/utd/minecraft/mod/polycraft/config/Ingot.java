package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Ingot extends SourcedConfig {

	public static final ConfigRegistry<Ingot> registry = new ConfigRegistry<Ingot>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Ingot.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new Ingot(
						line[0], //gameID
						line[1], //name
						Config.find(line[2], line[3]), //source
						Integer.parseInt(line[4]) //moldDamagePerUse
				));
	}

	public final int moldDamagePerUse;

	public Ingot(final String gameID, final String name, final Config source, final int moldDamagePerUse) {
		super(gameID, name, source);
		this.moldDamagePerUse = moldDamagePerUse;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getItem(this), size);
	}
}
