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

/**
 * Crafting behavior that emulates the regular crafting style behavior.
 */
public class CraftingBehavior<I extends PolycraftInventory> extends InventoryBehavior<I> {
	private boolean isUpdating = false;

	@Override
	public boolean setInventorySlotContents(I inventory, ContainerSlot slot, ItemStack item) {
		if (!isUpdating) {
			if (!slot.getSlotType().equals(SlotType.OUTPUT)) {
				updateOutputsForRecipe(inventory, PolycraftMod.recipeManagerRuntime.findRecipe(inventory.getContainerType(), inventory.getMaterials()));
			}
		}
		return false;
	}

	@Override
	public boolean onPickupFromSlot(I inventory, EntityPlayer player, ContainerSlot slot, ItemStack item) {
		if (item == null || item.getItem() == null)
			return false;

		try {
			if (slot.getSlotType().equals(SlotType.OUTPUT)) {
				// Output slot -- clear / decrement the inputs
				isUpdating = true;
				inventory.craftItems(false);
			}
		} finally {
			isUpdating = false;
		}
		updateOutputsForRecipe(inventory, PolycraftMod.recipeManagerRuntime.findRecipe(inventory.getContainerType(), inventory.getMaterials()));
		return false;
	}

	protected void updateOutputsForRecipe(final I inventory, final PolycraftRecipe recipe) {
		for (ContainerSlot slot : inventory.getOutputSlots()) {
			if (inventory.getStackInSlot(slot) != null) {
				inventory.setStackInSlot(slot, null);
			}
		}
		if (recipe == null || !inventory.canProcess())
			return;

		for (final RecipeComponent output : recipe.getOutputs(inventory)) {
			final ItemStack desiredResult = output.itemStack.copy();
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
