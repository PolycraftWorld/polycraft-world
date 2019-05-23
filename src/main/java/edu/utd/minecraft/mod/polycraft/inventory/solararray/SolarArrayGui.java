package edu.utd.minecraft.mod.polycraft.inventory.solararray;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SolarArrayGui extends PolycraftInventoryGui<SolarArrayInventory> {

	private static final String SOLAR_ARRAY = "Solar Array";

	public SolarArrayGui(final SolarArrayInventory inventory, final InventoryPlayer playerInventory) {
		super(inventory, playerInventory, 166, false);
	}

	/**
	 * Change the overlay color of "Solar Array" to red if it is night time or
	 * raining and yellow if it is clear day and there is water loaded.
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		if (inventory.hasWorldObj()) {
			World world = inventory.getWorld();
			int color = -1;
			if (world.getWorldInfo().getWorldTime() % 24000 > 12000 || world.isRaining())
				color = 0x9E0300; // Red color.
			else {
				int xWeight;
				int zWeight;
				if (inventory.getBlockMetadata() == EnumFacing.NORTH.ordinal()) {
					xWeight = -1;
					zWeight = 1;
				} else if (inventory.getBlockMetadata() == EnumFacing.EAST.ordinal()) {
					xWeight = -1;
					zWeight = -1;
				} else if (inventory.getBlockMetadata() == EnumFacing.SOUTH.ordinal()) {
					xWeight = 1;
					zWeight = -1;
				} else if (inventory.getBlockMetadata() == EnumFacing.WEST.ordinal()) {
					xWeight = 1;
					zWeight = 1;
				} else
					return; // unexpected condition
				for (int x = 0; x < 8; x++) {
					for (int z = 0; z < 8; z++) {
						if (!inventory.getWorld().canBlockSeeSky(inventory.getPos().add(xWeight * x, 0, (zWeight * z))))
							return;

					}
				}

				// make sure there are no collision blocks above us in the
				// center because they dont block the light; only test a few
				// areas to make faster
				// Funny enough, all of this code runs fast enough for gui
				// refreshes.
				for (int y = 1; y < 7; y++) {
					if (inventory.getWorld().getBlockState(inventory.getPos().add(xWeight, y,zWeight)).getBlock() == PolycraftMod.blockCollision)
						return;
					if (inventory.getWorld().getBlockState(inventory.getPos().add(xWeight * 4, y,zWeight * 4)).getBlock() == PolycraftMod.blockCollision)
						return;
					if (inventory.getWorld().getBlockState(inventory.getPos().add(xWeight * 7, y,zWeight * 7)).getBlock() == PolycraftMod.blockCollision)
						return;
					if (inventory.getWorld().getBlockState(inventory.getPos().add(xWeight, y,zWeight * 7)).getBlock() == PolycraftMod.blockCollision)
						return;
					if (inventory.getWorld().getBlockState(inventory.getPos().add(xWeight * 7, y,zWeight)).getBlock() == PolycraftMod.blockCollision)
						return;
				}

				ItemStack inputStack1 = inventory.getStackInSlot(inventory.slotIndexInput1);
				ItemStack inputStack2 = inventory.getStackInSlot(inventory.slotIndexInput2);
				ItemStack inputStack3 = inventory.getStackInSlot(inventory.slotIndexInput3);
				String vialWater = "iP";

				boolean match1 = inputStack1 != null && inputStack1.getItem() instanceof ItemVessel
						&& ((ItemVessel) inputStack1.getItem()).config.gameID.equals(vialWater);
				boolean match2 = inputStack2 != null && inputStack2.getItem() instanceof ItemVessel
						&& ((ItemVessel) inputStack2.getItem()).config.gameID.equals(vialWater);
				boolean match3 = inputStack3 != null && inputStack3.getItem() instanceof ItemVessel
						&& ((ItemVessel) inputStack3.getItem()).config.gameID.equals(vialWater);
				if (match1 || match2 || match3)
					color = 0x9E9E12; // Yellow color.
			}
			if (color > 0)
				this.fontRendererObj.drawString(SOLAR_ARRAY, 58, 6, color);
		}
	}
}
