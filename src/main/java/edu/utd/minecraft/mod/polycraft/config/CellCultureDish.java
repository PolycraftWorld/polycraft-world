package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class CellCultureDish extends SourcedConfig {

	public static final ConfigRegistry<CellCultureDish> registry = new ConfigRegistry<CellCultureDish>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, CellCultureDish.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new CellCultureDish(
						PolycraftMod.getVersionNumeric(line[index++]), //version
						line[index++], //gameID dish
						line[index++], //name
						DNASampler.registry.get(line[index++])
						));
			}
	}

	public CellCultureDish(final int[] version, final String gameID, final String name, final Config source) {
		super(version, gameID, name, source);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
}