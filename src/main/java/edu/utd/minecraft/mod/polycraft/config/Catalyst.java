package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class Catalyst extends SourcedConfig {

	public static final ConfigRegistry<Catalyst> registry = new ConfigRegistry<Catalyst>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Catalyst.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new Catalyst(
						PolycraftMod.getVersionNumeric(line[index++]), //version
						line[index++], //gameID
						line[index++], //name
						Config.find(line[index++], line[index++]) //source
				));
			}
	}

	public Catalyst(final int[] version, final String gameID, final String name, final Config source) {
		super(version, gameID, name, source);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
}