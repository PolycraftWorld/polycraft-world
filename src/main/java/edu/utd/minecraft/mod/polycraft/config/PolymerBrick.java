package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class PolymerBrick extends SourcedConfig<PolymerPellets> {

	public static final ConfigRegistry<PolymerBrick> registry = new ConfigRegistry<PolymerBrick>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerBrick.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				registry.register(new PolymerBrick(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], // blockGameID
						line[2], // itemGameID
						line[3], // tileEntityID
						line[4], // name
						PolymerPellets.registry.get(line[5]), // L-Bricks
						Integer.parseInt(line[7]), // width
						Integer.parseInt(line[8]), // length
						PolymerBrick.registry.get(line[9]), //subBlockname: null means return same item as one destroyed
						Mold.registry.get(line[10]),
						Integer.parseInt(line[11]), //craftingPellets
						Float.parseFloat(line[12]) //craftingDurationSeconds

				));
			}
	}

	public final int width, length;
	public final String itemName;
	public final String itemGameID;
	public final String tileEntityID;
	public final PolymerBrick subBrick;
	public final Mold brickMold;
	public final int craftingPellets;
	public final float craftingDurationSeconds;

	public PolymerBrick(final int[] version, final String blockGameID, final String itemGameID, final String tileEntityID, final String name,
			final PolymerPellets polymer, final int width, final int length, final PolymerBrick subBrick, final Mold brickMold,
			final int craftingPellets, final float craftingDurationSeconds) {
		super(version, blockGameID, name, polymer);
		this.width = width;
		this.length = length;
		this.itemGameID = itemGameID;
		this.tileEntityID = tileEntityID;
		this.itemName = name + " Item";
		this.subBrick = subBrick;
		this.brickMold = brickMold;
		this.craftingPellets = craftingPellets;
		this.craftingDurationSeconds = craftingDurationSeconds;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size, 0); // 0 is rainbow by default
	}

	public ItemStack getItemStack(int size, int metadata) {
		if (metadata <= 15)
			return new ItemStack(PolycraftRegistry.getBlock(this), size, metadata);
		else
			return new ItemStack(PolycraftRegistry.getBlock(this), size, 0);

	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Width", "Length");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(PolycraftMod.numFormat.format(width), PolycraftMod.numFormat.format(width));
	}

}