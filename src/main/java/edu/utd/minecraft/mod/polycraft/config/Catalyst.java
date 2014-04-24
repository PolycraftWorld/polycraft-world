package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Catalyst extends SourcedConfig {

	public static final ConfigRegistry<Catalyst> registry = new ConfigRegistry<Catalyst>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Catalyst.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new Catalyst(
						line[0], //gameID
						line[1], //name
						Config.find(line[2], line[3]) //source
				));
	}

	public Catalyst(final String gameID, final String name, final Config source) {
		super(gameID, name, source);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getItem(this), size);
	}
}