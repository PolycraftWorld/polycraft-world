package edu.utd.minecraft.mod.polycraft.inventory.treetap;

import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class TreeTapGui extends PolycraftInventoryGui<TreeTapInventory> {

	private static final int[][] tappedCoordOffsets = new int[][] { new int[] { 1, 0 }, new int[] { 0, 1 },
			new int[] { 0, -1 }, new int[] { -1, 0 } };
	private static final String TREE_TAP = "Tree Tap";

	public TreeTapGui(final TreeTapInventory inventory, final InventoryPlayer playerInventory) {
		// 133 (y-size), false (allow user input)
		super(inventory, playerInventory, 133, false);
	}

	/**
	 * Change the overlay color of "Tree Tap" to red there are no logs to tap,
	 * yellow if there is logs, and green if at least one log is Jungle Wood.
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		if (inventory.hasWorldObj()) {
			int color = 0x9E0300; // Red color.
			World world = inventory.getWorld();
			for (final int[] tappedCoordOffset : tappedCoordOffsets) {
				final int x =  tappedCoordOffset[0];
				final int z =  tappedCoordOffset[1];
				final Block treeBlock = world.getBlockState(inventory.getPos().add(x, 0, z)).getBlock();
				if (treeBlock != null && ((treeBlock instanceof BlockOldLog) || (treeBlock instanceof BlockNewLog))) {
					color = 0x9E9E12; // Yellow color.
					if ((world.getBlockState(inventory.getPos().add(x, 0, z)).getBlock().getMetaFromState(world.getBlockState(inventory.getPos().add(x, 0, z))) & 3) == 3) {
						color = 0x129E00; // Green color.
						break;
					}
				}
			}
			this.fontRendererObj.drawString(TREE_TAP, 65, 6, color);
		}
	}

}
