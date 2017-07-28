package edu.utd.minecraft.mod.polycraft.inventory.oilderrick;

import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class OilDerrickGui extends PolycraftInventoryGui<OilDerrickInventory> {

	private static final String OIL_DERRICK = "Oil Derrick";
	private static final String OIL_HARVESTED = "Drums Extracted: ";
	private static final String INVALID_BLOCK = "Not placed on an oil block";
	private static final String TAPPED_OUT = "No more oil at this location";

	public OilDerrickGui(final OilDerrickInventory inventory, final InventoryPlayer playerInventory) {
		// 133 (y-size), false (allow user input)
		super(inventory, playerInventory, 133, false);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		if (inventory.hasWorldObj()) {
			int color = 0x9E0300; // Red color.
			World world = inventory.getWorldObj();
			final Block oreBlock = world.getBlock(inventory.xCoord, inventory.yCoord - 1, inventory.zCoord);
			if (oreBlock != null && oreBlock instanceof BlockOre)
				if (((BlockOre) oreBlock).ore.gameID.equals(inventory.getSpawnFromOre().gameID))
					if (world.getBlockMetadata(inventory.xCoord, inventory.yCoord - 1, inventory.zCoord) > 0)
						color = 0x129E00; // Green color.
			this.fontRendererObj.drawString(OIL_DERRICK, 62, 6, color);
		}
	}
}
