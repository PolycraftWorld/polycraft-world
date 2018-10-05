package edu.utd.minecraft.mod.polycraft.inventory.tierchest;

import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.inventory.tierchest.TierChestBlock;;

public class TierChestGui extends PolycraftInventoryGui<TierChestInventory> {
	
	private static final int[][] tappedCoordOffsets = new int[][] { new int[] { 1, 0 }, new int[] { 0, 1 },
			new int[] { 0, -1 }, new int[] { -1, 0 } };
	private static final String TIER_CHEST = "Tier Chest";
	
	public TierChestGui(final TierChestInventory inventory, final InventoryPlayer playerInventory) {
		// 133 (y-size), false (allow user input)
		super(inventory, playerInventory, 133, false);
	}
	
	/** TODO
	 * Change the overlay color of "Tier_Chest" to correct rareity,
	 * 
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		int color=0x000000;;
		this.fontRendererObj.drawString(TIER_CHEST, 62, 6, color);
	}
	
	
}
