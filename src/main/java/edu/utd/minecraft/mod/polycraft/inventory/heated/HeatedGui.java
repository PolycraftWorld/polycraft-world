package edu.utd.minecraft.mod.polycraft.inventory.heated;

import net.minecraft.entity.player.InventoryPlayer;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;

public class HeatedGui<I extends HeatedInventory> extends PolycraftInventoryGui<I> {

	public static class ProgressDisplayOffsets {
		public final int heatX;
		public final int heatY;
		public final int processingX;
		public final int processingY;

		public ProgressDisplayOffsets(final int heatX, final int heatY, final int processingX, final int processingY) {
			this.heatX = heatX;
			this.heatY = heatY;
			this.processingX = processingX;
			this.processingY = processingY;
		}
	}

	private final ProgressDisplayOffsets progressDisplayOffsets;

	public HeatedGui(final I inventory, final InventoryPlayer playerInventory, final ProgressDisplayOffsets progressDisplayOffsets) {
		super(inventory, playerInventory);
		this.progressDisplayOffsets = progressDisplayOffsets;
	}

	public HeatedGui(final I inventory, final InventoryPlayer playerInventory, final ProgressDisplayOffsets progressDisplayOffsets, final int ySize) {
		super(inventory, playerInventory, ySize);
		this.progressDisplayOffsets = progressDisplayOffsets;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);

		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		int i1;
		if (inventory.isHeated()) {
			i1 = inventory.getHeatSourceTimeRemainingScaled(12);
			this.drawTexturedModalRect(k + progressDisplayOffsets.heatX, l + progressDisplayOffsets.heatY - i1, 176, 12 - i1, 14, i1 + 2);
		}

		i1 = inventory.getProcessingProgressScaled(24);
		this.drawTexturedModalRect(k + progressDisplayOffsets.processingX, l + progressDisplayOffsets.processingY, 176, 14, i1 + 1, 16);
	}
}
