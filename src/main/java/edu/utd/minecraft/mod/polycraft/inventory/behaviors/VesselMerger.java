package edu.utd.minecraft.mod.polycraft.inventory.behaviors;

import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;

public class VesselMerger extends InventoryBehavior {

	private final int mergeTicksStart;
	private int mergeTicksCurrent = 0;

	public VesselMerger() {
		this(PolycraftMod.convertSecondsToGameTicks(5));
	}

	public VesselMerger(final int mergeTicks) {
		this.mergeTicksStart = mergeTicks;
	}

	@Override
	public boolean updateEntity(final PolycraftInventory inventory, final World world) {
		if (inventory.getWorldObj() != null && !inventory.getWorldObj().isRemote) {
			if (mergeTicksCurrent == 0) {
				mergeTicksCurrent = mergeTicksStart;
				//don't merge heated inventories that are currently processing
				if (inventory instanceof HeatedInventory) {
					if (((HeatedInventory) inventory).isHeated() && inventory.canProcess())
						return false;
				}
				ItemVessel.merge(inventory);
			}
			else
				mergeTicksCurrent--;
		}
		return false;
	}
}
