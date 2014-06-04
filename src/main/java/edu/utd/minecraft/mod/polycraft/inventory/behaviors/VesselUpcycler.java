package edu.utd.minecraft.mod.polycraft.inventory.behaviors;

import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;

public class VesselUpcycler extends InventoryBehavior {

	private final int upcycleTicksStart;
	private int upcycleTicksCurrent = 0;

	public VesselUpcycler() {
		this(PolycraftMod.convertSecondsToGameTicks(5));
	}

	public VesselUpcycler(final int upcycleTicks) {
		this.upcycleTicksStart = upcycleTicks;
	}

	@Override
	public boolean updateEntity(final PolycraftInventory inventory, final World world) {
		if (inventory.getWorldObj() != null && !inventory.getWorldObj().isRemote) {
			if (upcycleTicksCurrent == 0) {
				upcycleTicksCurrent = upcycleTicksStart;
				//don't upcycle heated inventories that are currently processing
				if (inventory instanceof HeatedInventory) {
					if (((HeatedInventory) inventory).isHeated() && inventory.canProcess())
						return false;
				}
				ItemVessel.upcycle(inventory);
			}
			else
				upcycleTicksCurrent--;
		}
		return false;
	}
}
