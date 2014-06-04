package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

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
						line.length <= 9 || line[9].isEmpty() ? null : line[9].split(","), //paramNames
						line, 10 //paramValues
				));
	}

	public final PolymerPellets polymerPellets;
	public final int craftingPellets;
	public final float craftingDurationSeconds;
	public final int maxStackSize;

	public MoldedItem(final int[] version, final String gameID, final String name, final Mold source, final PolymerPellets polymerPellets,
			final int craftingPellets, final float craftingDurationSeconds, final String maxStackSize, final String[] paramNames, final String[] paramValues, final int paramsOffset) {
		super(version, gameID, name, source, paramNames, paramValues, paramsOffset);
		this.polymerPellets = polymerPellets;
		this.craftingPellets = craftingPellets;
		this.craftingDurationSeconds = craftingDurationSeconds;
		this.maxStackSize = StringUtils.isEmpty(maxStackSize) ? 0 : Integer.parseInt(maxStackSize);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Polymer Pellets", "Crafting Pellets", "Crafting Duration (sec)", "Max Stack Size");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(
				polymerPellets.name,
				PolycraftMod.numFormat.format(craftingPellets),
				PolycraftMod.numFormat.format(craftingDurationSeconds),
				PolycraftMod.numFormat.format(maxStackSize));
	}
}