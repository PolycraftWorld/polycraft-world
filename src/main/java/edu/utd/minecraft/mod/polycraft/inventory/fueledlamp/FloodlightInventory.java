package edu.utd.minecraft.mod.polycraft.inventory.fueledlamp;

import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;

public class FloodlightInventory extends FueledLampInventory {

	private static Inventory config;

	public static void register(final Inventory config) {
		FloodlightInventory.config = config;
		config.containerType = PolycraftContainerType.FLOODLIGHT;
		PolycraftInventory.register(new FueledLampBlock(config, FloodlightInventory.class));
	}

	public FloodlightInventory() {
		super(PolycraftContainerType.FLOODLIGHT, config);
	}

	@Override
	protected BlockLight.Source addLightSource(final int heatIntensity) {
		return BlockLight.addSource(worldObj, new BlockLight.Source(
				worldObj, xCoord, yCoord, zCoord,
				(int) Math.floor(heatIntensity * rangePerHeatIntensity),
				false));
	}
}
