package edu.utd.minecraft.mod.polycraft.inventory.fueledlamp;

import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class SpotlightInventory extends FueledLampInventory {

	private static Inventory config;

	public static void register(final Inventory config) {
		SpotlightInventory.config = config;
		PolycraftInventory.register(new FueledLampBlock(config, SpotlightInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	public SpotlightInventory() {
		super(PolycraftContainerType.SPOTLIGHT, config);
	}

	@Override
	protected BlockLight.Source addLightSource(final int heatIntensity) {
		return BlockLight.addSource(worldObj, new BlockLight.Source(
				worldObj, xCoord, yCoord, zCoord,
				(int) Math.floor(heatIntensity * rangePerHeatIntensity),
				worldObj.getBlockMetadata(xCoord, yCoord, zCoord)));
	}
}
