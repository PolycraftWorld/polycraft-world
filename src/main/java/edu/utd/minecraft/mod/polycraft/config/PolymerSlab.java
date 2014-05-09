package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class PolymerSlab extends SourcedConfig<PolymerBlock> {

	public static final ConfigRegistry<PolymerSlab> registry = new ConfigRegistry<PolymerSlab>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerSlab.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new PolymerSlab(
						line[0], //itemSlabGameID
						line[1], //itemDoubleSlabGameID
						line[2], //blockSlabGameID
						line[3], //blockDoubleSlabGameID
						line[4], //name
						PolymerBlock.registry.get(line[5]), //source
						Integer.parseInt(line[7]) //bounceHeight
				));
	}

	public final int bounceHeight;
	public final String itemSlabGameID;
	public final String itemDoubleSlabGameID;
	public final String itemSlabName;
	public final String itemDoubleSlabName;
	public final String blockSlabGameID;
	public final String blockDoubleSlabGameID;
	public final String blockSlabName;
	public final String blockDoubleSlabName;

	public PolymerSlab(final String itemSlabGameID, final String itemDoubleSlabGameID, final String blockSlabGameID, final String blockDoubleSlabGameID,
			final String name, final PolymerBlock source, final int bounceHeight) {
		super(itemSlabGameID, name, source);
		this.bounceHeight = bounceHeight;
		this.itemSlabGameID = itemSlabGameID;
		this.itemDoubleSlabGameID = itemDoubleSlabGameID;
		this.blockSlabGameID = blockSlabGameID;
		this.blockDoubleSlabGameID = blockDoubleSlabGameID;
		this.blockSlabName = name;
		this.blockDoubleSlabName = "Double " + blockSlabName;
		this.itemSlabName = blockSlabName + " Item";
		this.itemDoubleSlabName = blockDoubleSlabName + " Item";
	}

	@Override
	public ItemStack getItemStack(final int size) {
		return new ItemStack(PolycraftRegistry.getItem(itemSlabName), size);
	}
}