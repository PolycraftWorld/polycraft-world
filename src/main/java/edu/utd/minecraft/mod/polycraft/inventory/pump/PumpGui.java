package edu.utd.minecraft.mod.polycraft.inventory.pump;

import net.minecraft.entity.player.InventoryPlayer;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;

public class PumpGui extends PolycraftInventoryGui<PumpInventory> {

	public PumpGui(final PumpInventory inventory, final InventoryPlayer playerInventory) {
		super(inventory, playerInventory, 166, false);
	}

	private static final String fuelName = "Fuel Type: ";
	private static final String fuelIntensity = "Heat Intensity: ";
	private static final String fuelRemaining = "Fuel: %1$,.1f%% | Flow: %2$,d";
	private static final int maxTicksPerEpoch = (int) Math.pow(2,15); //this appears twice in the code...dubious

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		final int fuelTicksRemaining = inventory.getState(PumpState.FuelTicksRemaining);
		final int fuelTicksRemainingEpochs = inventory.getState(PumpState.FuelTicksRemainingEpochs);
		if (fuelTicksRemaining > 0 || fuelTicksRemainingEpochs >0) {
			final Fuel fuel = Fuel.getFuel(inventory.getState(PumpState.FuelIndex));
			this.fontRendererObj.drawString(fuelName + fuel.name, 8, 40, 4210752);
			this.fontRendererObj.drawString(fuelIntensity + inventory.getState(PumpState.FuelHeatIntensity), 8, 50, 4210752);
			this.fontRendererObj.drawString(String.format(fuelRemaining, ((double) 
					(fuelTicksRemaining + fuelTicksRemainingEpochs*maxTicksPerEpoch) / 
					(double) (inventory.getState(PumpState.FuelTicksTotal) + 
							(inventory.getState(PumpState.FuelTicksTotalEpochs)*maxTicksPerEpoch))) * 100, inventory.getState(PumpState.FlowTicksRemaining)), 8, 60, 4210752);
		}
	}
}
