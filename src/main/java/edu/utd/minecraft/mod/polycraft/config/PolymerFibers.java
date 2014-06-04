package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class PolymerFibers extends SourcedConfig<PolymerPellets> {

	public static final ConfigRegistry<PolymerFibers> registry = new ConfigRegistry<PolymerFibers>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerFibers.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new PolymerFibers(
						PolycraftMod.getVersionNumeric(line[index++]),
						line[index++], //gameID
						line[index++], //name
						PolymerPellets.registry.get(line[index++]) //source
				));
			}
	}

	public PolymerFibers(final int[] version, final String gameID, final String name, final PolymerPellets source) {
		super(version, gameID, name, source);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
}