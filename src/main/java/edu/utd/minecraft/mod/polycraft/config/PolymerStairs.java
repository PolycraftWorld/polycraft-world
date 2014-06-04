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
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], // blockGameID
						line[2], // itemGameID
						line[3], // name
						PolymerBlock.registry.get(line[4]), // source
						Integer.parseInt(line[6]) // bounceHeight
				));
	}

	public final int bounceHeight;
	public final String blockStairsGameID;
	public final String blockStairsName;
	public final String itemStairsName;
	public final String itemStairsGameID;

	public PolymerStairs(final int[] version, final String blockGameID, final String itemGameID, final String name, final PolymerBlock source, final int bounceHeight) {
		super(version, blockGameID, name, source);
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