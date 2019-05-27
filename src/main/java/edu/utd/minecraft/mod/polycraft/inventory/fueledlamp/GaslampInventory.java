package edu.utd.minecraft.mod.polycraft.inventory.fueledlamp;

import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;

public class GaslampInventory extends FueledLampInventory {

	private static Inventory config;

	public static void register(final Inventory config) {
		GaslampInventory.config = config;
		config.containerType = PolycraftContainerType.GASLAMP;
		PolycraftInventory.register(new FueledLampBlock(config, GaslampInventory.class));
	}

	public GaslampInventory() {
		super(PolycraftContainerType.GASLAMP, config);
	}

	@Override
	protected BlockLight.Source addLightSource(final int heatIntensity) {
		return BlockLight.addSource(worldObj, new BlockLight.Source(
				worldObj, pos.getX(), pos.getY(), pos.getZ(),
				(int) Math.floor(heatIntensity * rangePerHeatIntensity),
				true));
	}
}
