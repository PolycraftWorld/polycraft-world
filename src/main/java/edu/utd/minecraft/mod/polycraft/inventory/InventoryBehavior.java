package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;

/**
 * Interface for defining custom behavior of crafting containers.
 */
public abstract class InventoryBehavior<I extends PolycraftInventory> {
	public InventoryBehavior() {
	}

	/**
	 * Called when the block is activated via right click. Return true to end processing; false to propagate to other behaviors.
	 */
	public boolean onBlockActivated(I inventory, World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are) {
		return false;
	}

	/**
	 * Called when the block is broken. Return true to end processing; false to propagate to other behaviors.
	 */
	public boolean breakBlock(I inventory, World world, int x, int y, int z, Block block) {
		return false;
	}

	/**
	 * Called during display update. Return true to end processing; false to propagate to other behaviors.
	 */
	public boolean randomDisplayTick(I inventory, World world, int x, int y, int z, Random random) {
		return false;
	}

	/**
	 * Called during entity update. Return true to end processing; false to propagate to other behaviors.
	 */
	public boolean updateEntity(I inventory, World world) {
		return false;
	}

	/**
	 * Called to determine if an item is valid for the slot. Return true or false to end processing and return that value; return null to propagate to other behaviors.
	 */
	public Boolean isItemValidForSlot(I inventory, int slotIndex, ItemStack stack) {
		return null;
	}

	/**
	 * Return true to end processing; false to propagate to other behaviors.
	 */
	public boolean setInventorySlotContents(I inventory, ContainerSlot slot, ItemStack item) {
		return false;
	}

	/**
	 * Return true to end processing; false to propagate to other behaviors.
	 */
	public boolean onPickupFromSlot(I inventory, EntityPlayer player, ContainerSlot slot, ItemStack item) {
		return false;
	}
}
