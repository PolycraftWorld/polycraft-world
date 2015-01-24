package edu.utd.minecraft.mod.polycraft.crafting;

import com.google.common.base.Preconditions;

/**
 * A slot for materials in a recipe.
 */
public class RecipeSlot implements ContainerSlot {
	private static final int ANY_RECIPE_SLOT_INDEX = -1;
	
	/**
	 * A RecipeSlot representing any possible position in the inputs or outputs.
	 */
	public static final RecipeSlot ANY = new RecipeSlot();
	
	public final int slotIndex;
	
	private RecipeSlot() {
		slotIndex = ANY_RECIPE_SLOT_INDEX;
	}
	
	public RecipeSlot(final int slotIndex) {
		Preconditions.checkArgument(slotIndex >= 0);
		this.slotIndex = slotIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}	
		return this.slotIndex == ((ContainerSlot)obj).getSlotIndex();
	}
	
	@Override
	public int hashCode() {
		return slotIndex;
	}

	@Override
	public int getSlotIndex() {
		return this.slotIndex;
	}
	
	@Override
	public String toString() {
		return "Slot=" + this.slotIndex;
	}

	@Override
	public SlotType getSlotType() {
		// Recipe slots have no slot type
		return null;
	}

	@Override
	public int getRelativeX() {
		return -1;
	}

	@Override
	public int getRelativeY() {
		return -1;
	}
}
