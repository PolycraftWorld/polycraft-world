package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class PolymerStairs extends SourcedConfig<PolymerBlock> {

	public static final ConfigRegistry<PolymerStairs> registry = new ConfigRegistry<PolymerStairs>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerStairs.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new PolymerStairs(
						line[0], // blockGameID
						line[1], // itemGameID
						line[2], // name
						PolymerBlock.registry.get(line[3]), // source
						Integer.parseInt(line[5]) // bounceHeight
				));
	}

	public final int bounceHeight;
	public final String blockStairsGameID;
	public final String blockStairsName;
	public final String itemStairsName;
	public final String itemStairsGameID;

	public PolymerStairs(final String blockGameID, final String itemGameID, final String name, final PolymerBlock source, final int bounceHeight) {
		super(blockGameID, name, source);
		this.bounceHeight = bounceHeight;
		this.blockStairsGameID = blockGameID;
		this.itemStairsGameID = itemGameID;
		this.blockStairsName = name;
		this.itemStairsName = blockStairsName + " Item";

	}

	@Override
	public ItemStack getItemStack(final int size) {
		return new ItemStack(PolycraftRegistry.getItem(itemStairsName), size);
	}
}