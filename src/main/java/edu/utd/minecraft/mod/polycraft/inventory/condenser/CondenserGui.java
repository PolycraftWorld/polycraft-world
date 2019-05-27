package edu.utd.minecraft.mod.polycraft.inventory.condenser;

import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpInventory;
import net.minecraft.entity.player.InventoryPlayer;

public class CondenserGui extends PolycraftInventoryGui<CondenserInventory> {

	private static final String CONDENSER = "Condenser";

	public CondenserGui(final CondenserInventory inventory, final InventoryPlayer playerInventory) {
		super(inventory, playerInventory, 133, false);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		if (inventory.hasWorldObj() && inventory.blockNeighborsWater()) {
			// Change to blue if by water.
			this.fontRendererObj.drawString(CONDENSER, 61, 6, 0x00039E);
		}
	}
}
