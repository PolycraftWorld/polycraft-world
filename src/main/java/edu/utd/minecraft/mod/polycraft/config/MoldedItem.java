package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class MoldedItem extends SourcedConfig<Mold> {

	public static final ConfigRegistry<MoldedItem> registry = new ConfigRegistry<MoldedItem>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, MoldedItem.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				List<String> params = null;
				if (line.length > 8) {
					params = Lists.newArrayList();
					for (int i = 8; i < 11; i++)
						params.add(line[i]);
				}

				registry.register(new MoldedItem(
						line[0], //gameID
						line[1], //name
						Mold.registry.get(line[2]), //source
						PolymerPellets.registry.get(line[3]), //polymerPellets
						Integer.parseInt(line[5]), //craftingPellets
						Float.parseFloat(line[6]), //craftingDurationSeconds
						params
						));
			}
	}

	public final PolymerPellets polymerPellets;
	public final int craftingPellets;
	public final float craftingDurationSeconds;
	public final List<String> params;

	public MoldedItem(final String gameID, final String name, final Mold source, final PolymerPellets polymerPellets, final int craftingPellets, final float craftingDurationSeconds, final List<String> params) {
		super(gameID, name, source);
		this.polymerPellets = polymerPellets;
		this.craftingPellets = craftingPellets;
		this.craftingDurationSeconds = craftingDurationSeconds;
		this.params = params;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getItem(this), size);
	}

	public int getParamInteger(final int index) {
		return Integer.parseInt(params.get(index));
	}

	public float getParamFloat(final int index) {
		return Float.parseFloat(params.get(index));
	}
}