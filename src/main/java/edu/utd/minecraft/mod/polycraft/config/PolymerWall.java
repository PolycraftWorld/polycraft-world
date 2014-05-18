package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class PolymerWall extends SourcedConfig<PolymerBlock> {

	public static final ConfigRegistry<PolymerWall> registry = new ConfigRegistry<PolymerWall>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerWall.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new PolymerWall(
						line[0], // blockGameID
						line[1], // itemGameID
						line[2], // name
						PolymerBlock.registry.get(line[3]), // source
						Integer.parseInt(line[5]) // bounceHeight
				));
	}

	public final int bounceHeight;
	public final String blockWallGameID;
	public final String blockWallName;
	public final String itemWallName;
	public final String itemWallGameID;

	public PolymerWall(final String blockGameID, final String itemGameID, final String name, final PolymerBlock source, final int bounceHeight) {
		super(blockGameID, name, source);
		this.bounceHeight = bounceHeight;
		this.blockWallGameID = blockGameID;
		this.itemWallGameID = itemGameID;
		this.blockWallName = name;
		this.itemWallName = blockWallName + " Item";

	}

	@Override
	public ItemStack getItemStack(final int size) {
		return new ItemStack(PolycraftRegistry.getItem(itemWallName), size);
	}
}