package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class PolymerBlock extends SourcedConfig<PolymerPellets> {

	public static final ConfigRegistry<PolymerBlock> registry = new ConfigRegistry<PolymerBlock>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerBlock.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new PolymerBlock(
						line[0], //gameID
						line[1], //name
						PolymerPellets.registry.get(line[2]), //polymerPellets
						Integer.parseInt(line[4]) //bounceHeight
				));
	}

	public final int bounceHeight;

	public PolymerBlock(final String gameID, final String name, final PolymerPellets source, final int bounceHeight) {
		super(gameID, name, source);
		this.bounceHeight = bounceHeight;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size);
	}
}