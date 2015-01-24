package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class PolymerSlab extends SourcedConfig<PolymerBlock> {

	public static final ConfigRegistry<PolymerSlab> registry = new ConfigRegistry<PolymerSlab>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerSlab.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new PolymerSlab(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //itemSlabGameID
						line[2], //itemDoubleSlabGameID
						line[3], //blockSlabGameID
						line[4], //blockDoubleSlabGameID
						line[5], //name
						PolymerBlock.registry.get(line[6]), //source
						Integer.parseInt(line[8]) //bounceHeight
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

	public PolymerSlab(final int[] version, final String itemSlabGameID, final String itemDoubleSlabGameID, final String blockSlabGameID, final String blockDoubleSlabGameID,
			final String name, final PolymerBlock source, final int bounceHeight) {
		super(version, itemSlabGameID, name, source);
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

	public List<String> PROPERTY_NAMES = ImmutableList.of("Bounce Height");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(PolycraftMod.numFormat.format(bounceHeight));
	}
}