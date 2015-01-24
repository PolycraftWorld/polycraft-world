package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class PolymerBlock extends SourcedConfig<PolymerPellets> {

	public static final ConfigRegistry<PolymerBlock> registry = new ConfigRegistry<PolymerBlock>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerBlock.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				registry.register(new PolymerBlock(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], // blockGameID
						line[2], // itemGameID
						line[3], // name
						PolymerPellets.registry.get(line[4]), // polymerPellets
						Integer.parseInt(line[6]) // bounceHeight
				));
			}
	}

	public final int bounceHeight;
	public final String itemName;
	public final String itemGameID;

	public PolymerBlock(final int[] version, final String blockGameID, final String itemGameID, final String name, final PolymerPellets source, final int bounceHeight) {
		super(version, blockGameID, name, source);
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