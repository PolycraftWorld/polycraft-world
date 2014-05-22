package edu.utd.minecraft.mod.polycraft.inventory.fueledlamp;

import net.minecraft.entity.player.InventoryPlayer;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;

public class FueledLampGui extends PolycraftInventoryGui<FueledLampInventory> {

	public FueledLampGui(final FueledLampInventory inventory, final InventoryPlayer playerInventory) {
		super(inventory, playerInventory, 166, false);
	}

	private static final String fuelName = "Fuel: ";
	private static final String fuelIntensity = "Heat Intensity: ";
	private static final String fuelRemaining = "Remaining: %1$,.1f%%";

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		final int fuelTicksRemaining = inventory.getState(FueledLampState.FuelTicksRemaining);
		if (fuelTicksRemaining > 0) {
			final Fuel fuel = Fuel.getFuel(inventory.getState(FueledLampState.FuelIndex));
			this.fontRendererObj.drawString(fuelName + fuel.name, 8, 40, 4210752);
			this.fontRendererObj.drawString(fuelIntensity + inventory.getState(FueledLampState.FuelHeatIntensity), 8, 50, 4210752);
			this.fontRendererObj.drawString(String.format(fuelRemaining, ((double) fuelTicksRemaining / (double) inventory.getState(FueledLampState.FuelTicksTotal)) * 100), 8, 60, 4210752);
		}
	}
}
