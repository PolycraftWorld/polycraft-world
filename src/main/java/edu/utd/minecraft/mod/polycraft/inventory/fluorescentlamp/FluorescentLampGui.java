package edu.utd.minecraft.mod.polycraft.inventory.fluorescentlamp;

import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import net.minecraft.entity.player.InventoryPlayer;

public class FluorescentLampGui extends PolycraftInventoryGui<FluorescentLampInventory> {

	private static final String REMOTE_POWER = "Powered by generator";
	private static final String fuelName = "Fuel: ";
	private static final String fuelIntensity = "Heat Intensity: ";
	private static final String fuelRemaining = "Remaining: %1$,.1f%%";
	private static final int maxTicksPerEpoch = (int) Math.pow(2, 15);

	public FluorescentLampGui(final FluorescentLampInventory inventory, final InventoryPlayer playerInventory) {
		super(inventory, playerInventory, 166, false);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		final int fuelTicksRemaining = inventory.getState(FluorescentLampState.FuelTicksRemaining);
		final int fuelTicksRemainingEpochs = inventory.getState(FluorescentLampState.FuelTicksRemainingEpochs);
		if (fuelTicksRemaining > 0 || fuelTicksRemainingEpochs > 0) {
			final Fuel fuel = Fuel.getFuel(inventory.getState(FluorescentLampState.FuelIndex));
			this.fontRendererObj.drawString(fuelName + fuel.name, 8, 40, 4210752);
			this.fontRendererObj.drawString(fuelIntensity + inventory.getState(FluorescentLampState.FuelHeatIntensity),
					8, 50, 4210752);
			this.fontRendererObj.drawString(String.format(fuelRemaining,
					((double) (fuelTicksRemaining + fuelTicksRemainingEpochs * maxTicksPerEpoch) / (double) (inventory
							.getState(FluorescentLampState.FuelTicksTotal)
							+ (inventory.getState(FluorescentLampState.FuelTicksTotalEpochs) * maxTicksPerEpoch)))
							* 100),
					8, 60, 4210752);
		}
	}
}
