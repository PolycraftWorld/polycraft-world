package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class Electronics extends GameIdentifiedConfig {

	public static final ConfigRegistry<Electronics> registry = new ConfigRegistry<Electronics>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Electronics.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new Electronics(
						PolycraftMod.getVersionNumeric(line[index++]),
						line[index++], //gameID
						WaferItem.registry.get(line[index++]), //wafer
						line[index++], //moldtype
						line[index++], //name
						Integer.parseInt(line[index++]), //layers
						Integer.parseInt(line[index++]) //chipsPerWafer

				));
			}
	}

	public final int chipsPerWafer;
	public final int layers;
	public final String moldType;
	public final WaferItem wafer;

	public Electronics(final int[] version, final String gameID, final WaferItem wafer, final String moldType,
			final String name, final int layers, final int chipsPerWafer) {
		super(version, gameID, name);
		this.wafer = wafer;
		this.moldType = moldType;
		this.layers = layers;
		this.chipsPerWafer = chipsPerWafer;

	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Chips Per Wafer", "Photolithography Layers", "Mold Type");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(
				PolycraftMod.numFormat.format(this.chipsPerWafer),
				PolycraftMod.numFormat.format(this.layers),
				this.moldType);
	}
}