package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.Set;

import net.minecraft.item.ItemStack;

public interface PolycraftTileEntityContainer {
	/**
	 * @return the collection of valid input slots
	 */
	public Collection<ContainerSlot> getInputSlots();

	/**
	 * @return the collection of valid output slots
	 */
	public Collection<ContainerSlot> getOutputSlots();

	/**
	 * @return the set of materials currently in the input set.
	 */
	public Set<RecipeComponent> getMaterials();

	/**
	 * @return the total size of the inventory.
	 */
	int getSizeInventory();

	/**
	 * @return the itemstack at the specified slot, or null of it does not contain any itemstack.
	 */
	public ItemStack getStackInSlot(ContainerSlot slot);

	/**
	 * Clears the contents at the specified slot.
	 */
	public void clearSlotContents(ContainerSlot slot);

	/**
	 * Sets the itemstack at the specified slot. If null, the contents are cleared.
	 */
	public void setStackInSlot(ContainerSlot slot, ItemStack stack);
}
