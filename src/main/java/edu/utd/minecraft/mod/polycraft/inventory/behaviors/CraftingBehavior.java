package edu.utd.minecraft.mod.polycraft.inventory.behaviors;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;

/**
 * Crafting behavior that emulates the regular crafting style behavior.
 */
public class CraftingBehavior extends InventoryBehavior {

	// TODO: This is probably wrong - should update with user interaction, and not
	// on update tick?
	@Override
	public boolean updateEntity(PolycraftInventory inventory) {
		updateOutputsForRecipe(inventory, PolycraftMod.recipeManager.findRecipe(inventory.getContainerType(), inventory.getMaterials()));
		return true;
	}

	protected void updateOutputsForRecipe(PolycraftInventory inventory, PolycraftRecipe recipe) {
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
