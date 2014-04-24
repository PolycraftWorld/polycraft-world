package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class PolymerPellets extends SourcedConfig<Polymer> {

	public static final ConfigRegistry<PolymerPellets> registry = new ConfigRegistry<PolymerPellets>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerPellets.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new PolymerPellets(
						line[0], //gameID
						line[1], //name
						(Polymer) Config.find(line[2], line[3]) //source
				));
	}

	public PolymerPellets(final String gameID, final String name, final Polymer source) {
		super(gameID, name, source);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getItem(this), size);
	}
}