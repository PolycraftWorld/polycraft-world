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
						line[0], // blockGameID
						line[1], // itemGameID
						line[2], // name
						PolymerPellets.registry.get(line[3]), // polymerPellets
						Integer.parseInt(line[5]) // bounceHeight
				));
	}

	public final int bounceHeight;
	public final String itemName;
	public final String itemGameID;

	public PolymerBlock(final String blockGameID, final String itemGameID, final String name, final PolymerPellets source, final int bounceHeight) {
		super(blockGameID, name, source);
		this.bounceHeight = bounceHeight;
		this.itemGameID = itemGameID;
		this.itemName = name + " Item";
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size, 15); //15 is white by default
	}

	public ItemStack getItemStack(int size, int metadata) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size, metadata);
	}
}