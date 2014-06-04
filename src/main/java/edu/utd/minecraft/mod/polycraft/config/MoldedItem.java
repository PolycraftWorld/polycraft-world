package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringUtils;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class MoldedItem extends SourcedConfig<Mold> {

	public static final ConfigRegistry<MoldedItem> registry = new ConfigRegistry<MoldedItem>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, MoldedItem.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new MoldedItem(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //gameID
						line[2], //name
						Mold.registry.get(line[3]), //source
						PolymerPellets.registry.get(line[4]), //polymerPellets
						Integer.parseInt(line[6]), //craftingPellets
						Float.parseFloat(line[7]), //craftingDurationSeconds
						line[8], //craftingDurationSeconds
						line, 10 //params
				));
	}

	public final PolymerPellets polymerPellets;
	public final int craftingPellets;
	public final float craftingDurationSeconds;
	public final int maxStackSize;

	public MoldedItem(final int[] version, final String gameID, final String name, final Mold source, final PolymerPellets polymerPellets,
			final int craftingPellets, final float craftingDurationSeconds, final String maxStackSize, final String[] params, final int paramsOffset) {
		super(version, gameID, name, source, params, paramsOffset);
		this.polymerPellets = polymerPellets;
		this.craftingPellets = craftingPellets;
		this.craftingDurationSeconds = craftingDurationSeconds;
		this.maxStackSize = StringUtils.isEmpty(maxStackSize) ? 0 : Integer.parseInt(maxStackSize);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
}