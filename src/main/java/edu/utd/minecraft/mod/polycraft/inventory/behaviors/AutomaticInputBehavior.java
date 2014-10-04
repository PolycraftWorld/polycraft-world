package edu.utd.minecraft.mod.polycraft.inventory.behaviors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryHelper;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;

public class AutomaticInputBehavior<I extends PolycraftInventory & ISidedInventory> extends InventoryBehavior<I> {

	private final boolean directional;
	private final int cooldownTicksStart;
	private int cooldownTicksCurrent = 0;

	public AutomaticInputBehavior(final boolean directional, final int cooldownTicks) {
		this.directional = directional;
		this.cooldownTicksStart = cooldownTicks;
	}

	@Override
	public boolean updateEntity(final I inventory, final World world) {
		if (inventory.getWorldObj() != null && !inventory.getWorldObj().isRemote) {
			if (cooldownTicksCurrent == 0) {
				cooldownTicksCurrent = cooldownTicksStart;
				attemptAutomaticInput(inventory);
			}
			else
				cooldownTicksCurrent--;
		}
		return false;
	}

	private void attemptAutomaticInput(final I inventory) {
		IInventory inputInventory = null;
		final int directionFacing = inventory.getBlockMetadata() & 7;
		for (final ForgeDirection direction : ForgeDirection.values()) {
			if (direction != ForgeDirection.UNKNOWN) {
				if (directional && (directionFacing == direction.ordinal() || direction == ForgeDirection.DOWN))
					continue;
				inputInventory = PolycraftMod.getInventoryAt(inventory.getWorldObj(),
						inventory.xCoord + direction.offsetX,
						inventory.yCoord + direction.offsetY,
						inventory.zCoord + direction.offsetZ);
				if (inputInventory != null) {
					if (inputInventory instanceof ISidedInventory) {
						ISidedInventory isidedinventory = (ISidedInventory) inputInventory;
						int[] aint = isidedinventory.getAccessibleSlotsFromSide(0);
						if (aint != null)
							for (int k = 0; k < aint.length; ++k)
								if (InventoryHelper.transfer(inventory, isidedinventory, aint[k], 0))
									return;
					}
				}
			}
		}
	}
}
