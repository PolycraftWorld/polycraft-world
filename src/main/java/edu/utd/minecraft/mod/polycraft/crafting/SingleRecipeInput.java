package edu.utd.minecraft.mod.polycraft.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

// A RecipeInput with only a single entry.  Used for searching for and
// finding recipes.
public class SingleRecipeInput implements Comparable<SingleRecipeInput> {
	public final ItemStack itemStack;
	public final ContainerSlot slot;
	
	public SingleRecipeInput(int slotIndex, Item item, int stackCount) {
		this(new RecipeSlot(slotIndex), new ItemStack(item, stackCount));
	}
	
	public SingleRecipeInput(int slotIndex, ItemStack stack) {
		this(new RecipeSlot(slotIndex), stack);
	}
	
	public SingleRecipeInput(ContainerSlot slot, ItemStack stack) {
		this.itemStack = stack;
		this.slot = slot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + itemStack.getItem().getUnlocalizedName().hashCode();
		result = prime * result + slot.getSlotIndex();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SingleRecipeInput other = (SingleRecipeInput) obj;
		if (!other.itemStack.getItem().getUnlocalizedName().equals(
				this.itemStack.getItem().getUnlocalizedName())) {
			return false;
		}
		return this.slot.equals(other.slot);
	}

	@Override
	public int compareTo(SingleRecipeInput o) {
		if (slot.getSlotIndex() == o.slot.getSlotIndex()) {
			return this.itemStack.getItem().getUnlocalizedName().compareTo(
					o.itemStack.getItem().getUnlocalizedName());
		}
		return Integer.compare(slot.getSlotIndex(), o.slot.getSlotIndex());
	}

	@Override
	public String toString() {
		return slot.toString() + ": " + itemStack.getItem().getUnlocalizedName() + " (" + itemStack.stackSize + ")";
	}
}
