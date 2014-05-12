package edu.utd.minecraft.mod.polycraft.inventory.behaviors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.util.LogUtil;

/**
 * Crafting behavior that emulates the regular crafting style behavior.
 */
public class CraftingBehavior extends InventoryBehavior {
	private boolean isUpdating = false;
	
	@Override
	public boolean setInventorySlotContents(PolycraftInventory inventory, ContainerSlot slot, ItemStack item) {
		System.out.println("CraftingBehavior::setInventorySlotContents slot=" + slot + ", item=" + LogUtil.toString(item) + ", isUpdating=" + isUpdating + ", isRemote="
					+ (inventory.hasWorldObj() ? inventory.getWorldObj().isRemote : "null"));
		if (!isUpdating) {
			if (slot.getSlotType().equals(SlotType.INPUT)){
				updateOutputsForRecipe(inventory, PolycraftMod.recipeManager.findRecipe(inventory.getContainerType(), inventory.getMaterials()));
			}
		}
		return false;
	}
	
	@Override
	public boolean onPickupFromSlot(PolycraftInventory inventory, EntityPlayer player, ContainerSlot slot, ItemStack item) {
		if (item == null || item.getItem() == null) {
			return true;
		}
		
		try {	
			if (slot.getSlotType().equals(SlotType.OUTPUT)) {
				// Output slot -- clear / decrement the inputs
				isUpdating = true;
				inventory.craftItems(true);
			}
		} finally {
			isUpdating = false;
		}
		updateOutputsForRecipe(inventory, PolycraftMod.recipeManager.findRecipe(inventory.getContainerType(), inventory.getMaterials()));

		return true;
	}

	protected void updateOutputsForRecipe(final PolycraftInventory inventory, final PolycraftRecipe recipe) {
		if (recipe == null) {
			for (ContainerSlot slot : inventory.getOutputSlots()) {
				if (inventory.getStackInSlot(slot) != null) {
					inventory.setStackInSlot(slot, null);
				}
			}
			return;
		}

		for (final RecipeComponent output : recipe.getOutputs()) {
			final ItemStack desiredResult = output.itemStack;
			final ItemStack currentResult = inventory.getStackInSlot(output.slot);
			if (currentResult != null) {
				if (!currentResult.isItemEqual(desiredResult)) {
					inventory.setStackInSlot(output.slot, desiredResult);
				}
			} else {
				inventory.setStackInSlot(output.slot, desiredResult);
			}
		}
	}
}
