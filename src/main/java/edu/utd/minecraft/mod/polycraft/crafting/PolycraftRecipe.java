package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Recipes for Polycraft Mod. Recipes can consist of shaped inputs, shapeless inputs, or both.
 */
public class PolycraftRecipe {
	private static Logger logger = LogManager.getLogger();

	/**
	 * Collection of collections that are shapeless. Recipe creation requires at least one item from every RecipeInput, and each of those items can be any one of the items specified within the RecipeInput's ItemStack values.
	 */
	private final Collection<RecipeInput> shapelessInputs = Lists.newArrayList();

	/**
	 * Map of fixed recipe inputs.
	 */
	private final Map<ContainerSlot, RecipeInput> shapedInputs = Maps.newHashMap();

	/**
	 * Collection of item outputs and their quantities.
	 */
	protected final Set<RecipeComponent> outputs;

	/**
	 * Container type required by this recipe.
	 */
	private final PolycraftContainerType containerType;

	/**
	 * The amount of experience given for crafting the recipe.
	 */
	private final double experience;

	/**
	 * Create a new recipe with the specified inputs and outputs.
	 */
	public PolycraftRecipe(final PolycraftContainerType containerType, final Iterable<RecipeInput> inputs,
			final Iterable<RecipeComponent> outputs) {
		this(containerType, inputs, outputs, 0);
	}

	/**
	 * Shifts the shaped inputs the amounts specified. Does not check if shifting is possible.
	 */
	private void shiftInputs(final int dX, final int dY) {
		Map<ContainerSlot, RecipeInput> newInputs = Maps.newHashMap();
		for (ContainerSlot usedSlot : this.shapedInputs.keySet()) {
			// Make sure the container slot is from the container itself, so relative x and y are right
			ContainerSlot containerUsedSlot = containerType.getContainerSlotByIndex(usedSlot);
			ContainerSlot inputSlot = containerType.getRelativeContainerSlot(SlotType.INPUT,
					containerUsedSlot.getRelativeX() - dX, containerUsedSlot.getRelativeY() - dY);
			newInputs.put(inputSlot, RecipeInput.shapedAnyOneOf(inputSlot, this.shapedInputs.get(usedSlot).inputs));
		}
		this.shapedInputs.clear();
		this.shapedInputs.putAll(newInputs);
	}

	private boolean canShiftInputs(final int dX, final int dY) {
		if (this.shapedInputs.size() == 0) {
			return false;
		}

		for (ContainerSlot usedSlot : this.shapedInputs.keySet()) {
			// Make sure the container slot is from the container itself, so relative x and y are right
			ContainerSlot containerUsedSlot = containerType.getContainerSlotByIndex(usedSlot);
			ContainerSlot inputSlot = containerType.getRelativeContainerSlot(SlotType.INPUT,
					containerUsedSlot.getRelativeX() - dX, containerUsedSlot.getRelativeY() - dY);
			if (inputSlot == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return true if the recipe consist only of shaped inputs.
	 */
	public boolean isShapedOnly() {
		return shapelessInputs.size() == 0;
	}

	/**
	 * Translates purely shaped recipes to the top-left; this makes it easier to find shaped recipes created anywhere within the input grid.
	 */
	private void adjustShapedRecipe() {
		if (!isShapedOnly()) {
			// Not applicable to recipes with shapeless inputs
			return;
		}

		while (canShiftInputs(1, 0)) {
			shiftInputs(1, 0);
		}
		while (canShiftInputs(0, 1)) {
			shiftInputs(0, 1);
		}
	}

	/**
	 * Create a new recipe with the specified inputs, outputs, and experience.
	 */
	public PolycraftRecipe(final PolycraftContainerType containerType, final Iterable<RecipeInput> inputs,
			final Iterable<RecipeComponent> outputs, final double experience) {
		Preconditions.checkArgument(experience >= 0, "Recipe crafting experience cannot be less than zero.");

		this.containerType = containerType;
		this.experience = experience;
		Preconditions.checkNotNull(inputs);
		Preconditions.checkNotNull(outputs);

		// Validate the outputs
		Set<ContainerSlot> slotMap = Sets.newHashSet();
		for (final RecipeComponent output : outputs) {
			// Outputs cannot be assigned to "any" slot
			Preconditions.checkArgument(!output.slot.equals(RecipeSlot.ANY));
			// Cannot have two outputs in the same slot
			Preconditions.checkArgument(!slotMap.contains(output.slot));
			slotMap.add(output.slot);
		}

		this.outputs = ImmutableSet.copyOf(outputs);
		Preconditions.checkArgument(this.outputs.size() != 0);
		for (final RecipeInput input : inputs) {
			if (input.slot.equals(RecipeSlot.ANY)) {
				// Input can go into any of the slots
				shapelessInputs.add(RecipeInput.shapelessAnyOneOf(input.inputs));
			} else {
				// Fixed input position
				if (shapedInputs.containsKey(input.slot)) {
					throw new IllegalArgumentException("A recipe item already exists at container slot " + input.slot);
				}
				shapedInputs.put(input.slot, input);
			}
		}

		// Must have at least 1 fixed input or 1 shapeless inputs
		Preconditions.checkArgument(this.shapedInputs.size() + this.shapelessInputs.size() != 0);

		adjustShapedRecipe();
	}

	/**
	 * @return the container type required by this recipe.
	 */
	public PolycraftContainerType getContainerType() {
		return this.containerType;
	}

	/**
	 * @return the inputs required by the recipe.
	 */
	public Collection<RecipeInput> getInputs() {
		List<RecipeInput> inputs = Lists.newArrayList();
		inputs.addAll(this.shapelessInputs);
		inputs.addAll(this.shapedInputs.values());
		return inputs;
	}

	/**
	 * @return outputs generated by the recipe.
	 */
	public Collection<RecipeComponent> getOutputs() {
		return this.outputs;
	}

	/**
	 * @return The number of inputs required by this recipe.
	 */
	public int getInputCount() {
		return this.shapedInputs.size() + this.shapelessInputs.size();
	}

	/**
	 * Creates a set of every possible combination of shaped recipes.
	 */
	public Collection<Set<RecipeComponent>> getShapedCombinations() {
		final List<Set<RecipeComponent>> shapedCombos = Lists.newArrayList();

		// Add combinations of the "one of any" in fixed slot positions
		for (final ContainerSlot slot : shapedInputs.keySet()) {
			if (shapedCombos.size() == 0) {
				// Initial set entry
				for (final ItemStack stack : shapedInputs.get(slot).inputs) {
					shapedCombos.add(ImmutableSet.of(new RecipeComponent(slot, stack)));
				}
			} else {
				List<Set<RecipeComponent>> newList = Lists.newArrayList();
				for (final ItemStack stack : shapedInputs.get(slot).inputs) {
					for (Set<RecipeComponent> existing : shapedCombos) {
						Set<RecipeComponent> newSet = Sets.newHashSet(existing);
						newSet.add(new RecipeComponent(slot, stack));
						newList.add(newSet);
					}
				}
				shapedCombos.clear();
				shapedCombos.addAll(newList);
			}
		}

		return shapedCombos;
	}

	/**
	 * Creates a set of every combination of shapeless recipes.
	 */
	public Collection<Set<RecipeComponent>> getShapelessCombinations() {
		List<Set<RecipeComponent>> shapelessCombos = Lists.newArrayList();

		// Add combinations for the shapeless inputs
		for (final RecipeInput input : shapelessInputs) {
			if (shapelessCombos.size() == 0) {
				// For the first element, create the initial array
				for (final ItemStack stack : input.inputs) {
					shapelessCombos.add(ImmutableSet.of(new RecipeComponent(input.slot, stack)));
				}
			} else {
				final List<Set<RecipeComponent>> newListList = Lists.newArrayList();
				for (final ItemStack stack : input.inputs) {
					for (final Set<RecipeComponent> oldList : shapelessCombos) {
						Set<RecipeComponent> newList = Sets.newHashSet(oldList);
						newList.add(new RecipeComponent(input.slot, stack));
						newListList.add(newList);
					}
				}
				shapelessCombos.clear();
				shapelessCombos = newListList;
			}
		}
		return shapelessCombos;
	}

	protected ItemStack getItemstackForInput(final RecipeComponent input, final Set<RecipeInput> usedInputs) {
		for (final RecipeInput recipeInput : shapedInputs.values()) {
			if (usedInputs != null && usedInputs.contains(recipeInput)) {
				continue;
			}
			ItemStack recipeStack = recipeInput.get(input);
			if (recipeStack != null && input.itemStack.stackSize >= recipeStack.stackSize) {
				usedInputs.add(recipeInput);
				return recipeStack;
			}
		}
		for (final RecipeInput recipeInput : shapelessInputs) {
			ItemStack recipeStack = recipeInput.get(input);
			if (recipeStack != null && input.itemStack.stackSize >= recipeStack.stackSize) {
				if (usedInputs != null && usedInputs.contains(recipeInput)) {
					continue;
				}
				usedInputs.add(recipeInput);
				return recipeStack;
			}
		}
		return null;
	}

	/**
	 * @return true if the inputs and stack sizes are valid for this recipe.
	 */
	public boolean areInputsValid(final Set<RecipeComponent> inputs) {
		if (inputs.size() != (shapelessInputs.size() + shapedInputs.size())) {
			return false;
		}

		Set<RecipeInput> usedSlots = Sets.newHashSet();
		for (final RecipeComponent input : inputs) {
			// Iterate over the shaped inputs looking for a match
			if (getItemstackForInput(input, usedSlots) == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Processes the inputs against the recipe, adding the results to the container's output and subtracting from the inputs.
	 */
	public void process(final Set<RecipeComponent> inputs, PolycraftTileEntityContainer container) {
		if (inputs == null || !areInputsValid(inputs)) {
			logger.error("Invalid processing input for recipe " + this.toString());
			return;
		}

		// Create the outputs.
		for (final RecipeComponent output : this.outputs) {
			if (container.getStackInSlot(output.slot) == null) {
				container.setStackInSlot(output.slot, output.itemStack.copy());
			} else {
				container.getStackInSlot(output.slot).stackSize += output.itemStack.stackSize;
			}
		}

		// Remove from the inputs.
		Set<RecipeInput> usedInputs = Sets.newHashSet();
		for (final RecipeComponent input : ImmutableList.copyOf(inputs)) {
			ItemStack itemStack = getItemstackForInput(input, usedInputs);
			if (itemStack != null) {
				container.getStackInSlot(input.slot).stackSize -= itemStack.stackSize;
				if (container.getStackInSlot(input.slot).stackSize <= 0) {
					container.clearSlotContents(input.slot);
				}
			} else {
				logger.error("Missing item stack for input " + input);
			}
		}
	}

	/**
	 * Process a generic minecraft crafting recipe which allows recipes to require itemstacks with any stackSize. The generic crafting recipes only remove a single item for each recipe item, so the rest may need to be removed.
	 */
	public void processGenericCrafting(final Set<RecipeComponent> inputs, IInventory container) {
		if (inputs == null || !areInputsValid(inputs)) {
			logger.error("Invalid processing input for recipe " + this.toString());
			return;
		}

		Set<RecipeInput> usedInputs = Sets.newHashSet();
		for (final RecipeComponent input : ImmutableList.copyOf(inputs)) {
			ItemStack itemStack = getItemstackForInput(input, usedInputs);
			if (itemStack != null) {
				// Remove all but one; the regular minecraft engine will remove the rest.
				container.decrStackSize(input.slot.getSlotIndex(), itemStack.stackSize - 1);
			} else {
				logger.error("Missing item stack for input " + input);
			}
		}
	}

	@Override
	public String toString() {
		return "PolycraftRecipe [shapelessInputs=" + shapelessInputs
				+ ", shapedInputs=" + shapedInputs + ", outputs=" + outputs
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((containerType == null) ? 0 : containerType.hashCode());
		result = prime * result
				+ ((shapedInputs == null) ? 0 : shapedInputs.hashCode());
		result = prime * result
				+ ((shapelessInputs == null) ? 0 : shapelessInputs.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolycraftRecipe other = (PolycraftRecipe) obj;
		if (containerType != other.containerType)
			return false;
		if (shapedInputs == null) {
			if (other.shapedInputs != null)
				return false;
		} else if (!shapedInputs.equals(other.shapedInputs))
			return false;
		if (shapelessInputs == null) {
			if (other.shapelessInputs != null)
				return false;
		} else if (!shapelessInputs.equals(other.shapelessInputs))
			return false;
		return true;
	}

}
