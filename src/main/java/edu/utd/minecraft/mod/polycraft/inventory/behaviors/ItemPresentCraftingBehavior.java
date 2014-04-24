package edu.utd.minecraft.mod.polycraft.inventory.behaviors;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;

/**
 * Crafting behavior that requires a certain item to be present in certain a certain slot
 */
public class ItemPresentCraftingBehavior extends CraftingBehavior {

	private final Map<ContainerSlot, Item> itemsBySlot;

	public ItemPresentCraftingBehavior(final Map<ContainerSlot, Item> itemsBySlot) {
		this.itemsBySlot = itemsBySlot;
	}

	@Override
	public boolean updateEntity(final PolycraftInventory inventory) {
		PolycraftRecipe recipe = PolycraftMod.recipeManager.findRecipe(inventory.getContainerType(), inventory.getMaterials());
		if (recipe != null) {
			for (Entry<ContainerSlot, Item> itemSlot : itemsBySlot.entrySet()) {
				final ItemStack itemStack = inventory.getStackInSlot(itemSlot.getKey());
				if (itemStack == null || !itemStack.getItem().equals(itemSlot.getValue())) {
					recipe = null;
					break;
				}
			}
		}
		updateOutputsForRecipe(inventory, recipe);
		return true;
	}
}
