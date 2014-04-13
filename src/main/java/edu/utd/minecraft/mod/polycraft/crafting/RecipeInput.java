package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * An input to a recipe.  The position may or may not be fixed (use RecipeSlot.ANY to indicate
 * that the input can occur anywhere).  The inputs represent a "one-of" item; the recipe must
 * have exactly one of the items specified in the input.  This is useful for example with
 * fuel inputs, where coal, charcoal, etc. are all valid, but only one of them is needed.
 */
public final class RecipeInput {
	public final Collection<ItemStack> inputs;
	public final ContainerSlot slot;
	
	private RecipeInput(final ContainerSlot slot, final Iterable<ItemStack> items) {
		Preconditions.checkNotNull(slot);		
		Preconditions.checkNotNull(items);
		inputs = ImmutableList.copyOf(items);
		this.slot = slot;
		// Must have at least 1 input item
		Preconditions.checkArgument(inputs.size() != 0);
		for (final ItemStack item : items) {
			Preconditions.checkNotNull(item);
			// No item stack size can be zero
			Preconditions.checkArgument(item.stackSize != 0);
			Preconditions.checkNotNull(item.getItem());
			// Item must have a unique name  TODO: is this the right name?
			Preconditions.checkNotNull(item.getItem().getUnlocalizedName());
			Preconditions.checkArgument(!"item.null".equals(item.getItem().getUnlocalizedName()));
		}
	}

	/**
	 * Returns the itemstack referenced by the RecipeComponent
	 */
	public ItemStack get(final RecipeComponent input) {
		if (!slot.equals(RecipeSlot.ANY) && slot.getSlotIndex() != input.slot.getSlotIndex()){
			return null;
		}
		
		for (final ItemStack stack : inputs) {
			// TODO: is unlocalized name the right comparison, or can be items be used?
			if (stack.getItem().getUnlocalizedName().equals(input.itemStack.getItem().getUnlocalizedName())) {
				return stack;
			}
		}
		return null;
	}
	
	/**
	 * @return true if the itemstack is contained by the RecipeInput
	 */
	public boolean contains(final ItemStack itemStack) {
		for (final ItemStack stack : inputs) {
			// TODO: is unlocalized name the right comparison, or can be items be used?
			if (stack.getItem().getUnlocalizedName().equals(
					itemStack.getItem().getUnlocalizedName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return true if the single input is valid for the RecipeInput.
	 */
	public boolean contains(final RecipeComponent input) {
		if (!slot.equals(RecipeSlot.ANY) && slot.getSlotIndex() != input.slot.getSlotIndex()){
			return false;
		}		
		return contains(input.itemStack);
	}
	
	/**
	 * Creates a new recipe input that requires the item stack placed at any slot.
	 */
	public static RecipeInput shapelessInput(final ItemStack item) {		
		return new RecipeInput(RecipeSlot.ANY, ImmutableList.of(item));
	}
	
	/**
	 * Creates a new recipe input that requires the item stack at the specified slot.
	 */
	public static RecipeInput shapedInput(final ContainerSlot slot, final ItemStack item) {		
		return new RecipeInput(slot, ImmutableList.of(item));
	}
	
	/**
	 * Create a new shapeless recipe input that is valid for any one of the items in the
	 * iterable list
	 */
	public static RecipeInput shapelessAnyOneOf(final Iterable<ItemStack> items) {
		return new RecipeInput(RecipeSlot.ANY, items);
	}
	
	/**
	 *  Create a new recipe input at the specified slot that is valid for any of the one
	 * items in the iterable list.
	 */
	public static RecipeInput shapedAnyOneOf(final ContainerSlot slot, final Iterable<ItemStack> items) {
		return new RecipeInput(slot, items);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inputs == null) ? 0 : inputs.iterator().next().getItem().getUnlocalizedName().hashCode());
		result = prime * result + ((inputs == null) ? 0 : inputs.iterator().next().stackSize);
		result = prime * result + ((slot == null) ? 0 : slot.hashCode());
		return result;
	}

	private boolean compareItemStacksTo(RecipeInput other) {
		ItemStack item1 = this.inputs.iterator().next();
		ItemStack item2 = other.inputs.iterator().next();
		
		if (!item1.getItem().getUnlocalizedName().equals(item2.getItem().getUnlocalizedName())) {
			return false;
		}
		if (!(item1.stackSize == item2.stackSize)) {
			return false;
		}
		return true;
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
		RecipeInput other = (RecipeInput) obj;
		if (inputs == null) {
			if (other.inputs != null) {
				return false;
			}
		} else if (!compareItemStacksTo(other)) {
			return false;
		}
		if (slot == null) {
			if (other.slot != null) {
				return false;
			}
		} else if (!slot.equals(other.slot)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.slot.toString() + ", inputs=" + this.inputs.toString();
	}
}
