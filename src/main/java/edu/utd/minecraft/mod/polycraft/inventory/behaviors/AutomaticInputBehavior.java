package edu.utd.minecraft.mod.polycraft.inventory.behaviors;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
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
		if (inventory.getWorld() != null && !inventory.getWorld().isRemote) {
			if (cooldownTicksCurrent == 0) {
				cooldownTicksCurrent = cooldownTicksStart;
				//attemptAutomaticInput(inventory); //TODO: fix this: walter commented out to test 3D inputs/outputs
			}
			else
				cooldownTicksCurrent--;
		}
		return false;
	}

	private void attemptAutomaticInput(final I inventory) {
		IInventory inputInventory = null;
		final int directionFacing = inventory.getBlockMetadata() & 7;
		for (final EnumFacing direction : EnumFacing.values()) {
			if (direction != null) {
				if (directional && (directionFacing == direction.ordinal() || direction == EnumFacing.DOWN))
					continue;
				inputInventory = PolycraftMod.getInventoryAt(inventory.getWorld(),
						inventory.getPos().offset(direction));
				if (inputInventory != null) {
					if (inputInventory instanceof ISidedInventory) {
						ISidedInventory isidedinventory = (ISidedInventory) inputInventory;
						int[] aint = isidedinventory.getSlotsForFace(EnumFacing.getFront(0));
						if (aint != null)
							for (int k = 0; k < aint.length; ++k)
								if (InventoryHelper.transfer(inventory, isidedinventory, aint[k], EnumFacing.DOWN, 1))
									return;
					}
				}
			}
		}
	}
}
