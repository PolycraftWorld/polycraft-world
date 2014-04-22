package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Interface for defining custom behavior of crafting containers.
 */
public abstract class InventoryBehavior {
	public InventoryBehavior() {
	}
	
	/**
	 * Called when the block is activated via right click.
	 * Return true to end processing; false to propagate to other behaviors.
	 */
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are, PolycraftInventory inventory) {
		return false;
	}
	
	/**
	 * Called when the block is broken.
	 * Return true to end processing; false to propagate to other behaviors.
	 */
	public boolean breakBlock(World world, int x, int y, int z, Block block, PolycraftInventory inventory) {
		return false;
	}
	
	/**
	 * Called during display update.
	 * Return true to end processing; false to propagate to other behaviors.
	 */
	public boolean randomDisplayTick(World world, int x, int y, int z, Random random, PolycraftInventory inventory) {
		return true;
	}
	
	/**
	 * Called during entity update.
	 * Return true to end processing; false to propagate to other behaviors.
	 */
	public boolean updateEntity(PolycraftInventory inventory) {
		return true;
	}
	
	/**
	 * Called to determine if an item is valid for the slot. Return true or false to end processing and return
	 * that value; return null to propagate to other behaviors.
	 */
	public Boolean isItemValidForSlot(int slotIndex, ItemStack stack, PolycraftInventory inventory) {
		return null;
	}
}
