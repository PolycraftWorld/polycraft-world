package edu.utd.minecraft.mod.polycraft.crafting;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemStack;

public class RecipeSlot implements ContainerSlot {
	private static final int ANY_RECIPE_SLOT_INDEX = -1;
	
	public static final RecipeSlot ANY = new RecipeSlot();
	
	public final int slotIndex;
	
	private RecipeSlot() {
		slotIndex = ANY_RECIPE_SLOT_INDEX;
	}
	
	public RecipeSlot(int slotIndex) {
		Preconditions.checkArgument(slotIndex >= 0);
		this.slotIndex = slotIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return this.slotIndex == ((RecipeSlot)obj).slotIndex;
	}
	
	@Override
	public int hashCode() {
		return slotIndex;
	}

	@Override
	public int getSlotIndex() {
		return this.slotIndex;
	}
	
	public String toString() {
		return "Slot=" + this.slotIndex;
	}
}
