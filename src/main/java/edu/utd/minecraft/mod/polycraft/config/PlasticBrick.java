package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class PlasticBrick extends SourcedConfig<PolymerPellets> {

	public static final ConfigRegistry<PlasticBrick> registry = new ConfigRegistry<PlasticBrick>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PlasticBrick.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				registry.register(new PlasticBrick(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], // blockGameID
						line[2], // itemGameID
						line[3], // name
						PolymerPellets.registry.get(line[4]), // L-Bricks
						Integer.parseInt(line[6]), // width
						Integer.parseInt(line[7]) // length
				));
			}
	}

	public final int width, length;
	public final String itemName;
	public final String itemGameID;

	public PlasticBrick(final int[] version, final String blockGameID, final String itemGameID, final String name, final PolymerPellets polymer, final int width, final int length) {
		super(version, blockGameID, name, polymer);
		this.width = width;
		this.length = length;
		this.itemGameID = itemGameID;
		this.itemName = name + " Item";
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size, 15); // 15 is white by default
	}

	public ItemStack getItemStack(int size, int metadata) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size, metadata);
	}

	// public List<String> PROPERTY_NAMES = ImmutableList.of("Bounce Height");
	//
	// @Override
	// public List<String> getPropertyNames() {
	// return PROPERTY_NAMES;
	// }

	// @Override
	// public List<String> getPropertyValues() {
	// return ImmutableList.of(PolycraftMod.numFormat.format(bounceHeight));
	// }
}