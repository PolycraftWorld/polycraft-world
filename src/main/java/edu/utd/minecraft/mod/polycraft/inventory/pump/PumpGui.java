package edu.utd.minecraft.mod.polycraft.inventory.pump;

import net.minecraft.entity.player.InventoryPlayer;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpInventory.FlowNetwork;

public class PumpGui extends PolycraftInventoryGui<PumpInventory> {

	private final FlowNetwork flowNetwork;
	public PumpGui(final PumpInventory inventory, final InventoryPlayer playerInventory) {
		super(inventory, playerInventory, 210, false);
		flowNetwork = inventory.getFlowNetwork();
	}

	private static final String fuelName = "Fuel Type: ";
	private static final String fuelIntensity = "Heat Intensity: ";
	private static final String fuelRemaining = "Fuel: %1$,.1f%% | Flow: %2$,d";
	private static final String sourceDistance = "Source: %1$,d Pipes";
	private static final String defaultTargetDistance = "Default Target: %1$,d Pipes";
	private static final String regulatedTargets = "Regulated Targets: %1$,d";
	private static final String totalPipes = "Total Pipes: %1$,d";
	private static final int maxTicksPerEpoch = (int) Math.pow(2,15); //this appears twice in the code...dubious

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		final int fuelTicksRemaining = inventory.getState(PumpState.FuelTicksRemaining);
		final int fuelTicksRemainingEpochs = inventory.getState(PumpState.FuelTicksRemainingEpochs);
		final int color = flowNetwork.isValid() ? 0x129E00 : 0x9E0300;
		int offset = 30;
		if (fuelTicksRemaining > 0 || fuelTicksRemainingEpochs > 0) {
			final Fuel fuel = Fuel.getFuel(inventory.getState(PumpState.FuelIndex));
			this.fontRendererObj.drawString(fuelName + fuel.name, 8, offset += 10, color);
			this.fontRendererObj.drawString(fuelIntensity + inventory.getState(PumpState.FuelHeatIntensity), 8, offset += 10, color);
			this.fontRendererObj.drawString(String.format(fuelRemaining, ((double) 
					(fuelTicksRemaining + fuelTicksRemainingEpochs*maxTicksPerEpoch) / 
					(double) (inventory.getState(PumpState.FuelTicksTotal) + 
							(inventory.getState(PumpState.FuelTicksTotalEpochs)*maxTicksPerEpoch))) * 100, inventory.getState(PumpState.FlowTicksRemaining)), 8, offset += 10, color);
		}
		else {
			this.fontRendererObj.drawString("Add fuel to start pumping!", 8, offset += 10, color);
		}
		if (flowNetwork.isValid()) {
			this.fontRendererObj.drawString(String.format(sourceDistance, flowNetwork.source.distanceFromPump), 8, offset += 10, color);
			this.fontRendererObj.drawString(String.format(defaultTargetDistance, flowNetwork.defaultTarget.distanceFromPump), 8, offset += 10, color);
			if (flowNetwork.regulatedTargets.size() > 0)
				this.fontRendererObj.drawString(String.format(regulatedTargets, flowNetwork.regulatedTargets.size()), 8, offset += 10, color);
		}
		else {
			if (flowNetwork.source == null)
				this.fontRendererObj.drawString("Invalid source", 8, offset += 10, color);
			else if (flowNetwork.defaultTarget == null)
				this.fontRendererObj.drawString("Invalid default target", 8, offset += 10, color);
			else
				this.fontRendererObj.drawString("Invalid regulated target(s)", 8, offset += 10, color);
		}
	}
}
