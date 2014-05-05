package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class MoldedItem extends SourcedConfig<Mold> {

	public static final ConfigRegistry<MoldedItem> registry = new ConfigRegistry<MoldedItem>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, MoldedItem.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new MoldedItem(
						line[0], //gameID
						line[1], //name
						Mold.registry.get(line[2]), //source
						PolymerPellets.registry.get(line[3]), //polymerPellets
						Integer.parseInt(line[5]), //craftingPellets
						Float.parseFloat(line[6]), //craftingDurationSeconds
						line, 8 //params
				));
	}

	public final PolymerPellets polymerPellets;
	public final int craftingPellets;
	public final float craftingDurationSeconds;

	public MoldedItem(final String gameID, final String name, final Mold source, final PolymerPellets polymerPellets, final int craftingPellets, final float craftingDurationSeconds, final String[] params, final int paramsOffset) {
		super(gameID, name, source, params, paramsOffset);
		this.polymerPellets = polymerPellets;
		this.craftingPellets = craftingPellets;
		this.craftingDurationSeconds = craftingDurationSeconds;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getItem(this), size);
	}
}