package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

// Implementation of a Polycraft recipe.  Recipes can consist of shaped or
// shapeless inputs, with any combination of both.
public class PolycraftRecipe<T extends PolycraftCraftingContainer> {
	// Collection of collections that are shapeless.  Recipe creation requires at
	// least one item from every RecipeInput, and each of those items can be any
	// one of the items specified within the RecipeInput's ItemStack values.
	private final Collection<RecipeInput> shapelessInputs = Lists.newArrayList();
	
	// Map of fixed recipe inputs.
	private final Map<ContainerSlot, RecipeInput> shapedInputs = Maps.newHashMap();
	
	// Collection of item outputs and their quantities.
	private final Collection<ItemStack> outputs;
	
	// Create a new recipe with the specified inputs and outputs.
	public PolycraftRecipe(final Iterable<RecipeInput> inputs, final Iterable<ItemStack> outputs) {
		Preconditions.checkNotNull(inputs);
		Preconditions.checkNotNull(outputs);		
		this.outputs = ImmutableList.copyOf(outputs);
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
	}
	
	// Creates a set of every possible combination of shaped recipes.
	public Collection<Set<SingleRecipeInput>> getShapedCombinations() {
		final List<Set<SingleRecipeInput>> shapedCombos = Lists.newArrayList();
		
		// Add combinations of the "one of any" in fixed slot positions
		for (final ContainerSlot slot : shapedInputs.keySet()) {
			if (shapedCombos.size() == 0) {
				// Initial set entry
				for (final ItemStack stack : shapedInputs.get(slot).inputs) {
					shapedCombos.add(ImmutableSet.of(new SingleRecipeInput(slot, stack)));
				}
			} else {
				List<Set<SingleRecipeInput>> newList = Lists.newArrayList();
				for (final ItemStack stack : shapedInputs.get(slot).inputs) {
					String key = "_" + Integer.toString(slot.getSlotIndex()) + "-" + stack.getItem().getUnlocalizedName();
					for (Set<SingleRecipeInput> existing : shapedCombos) {
						Set<SingleRecipeInput> newSet = Sets.newHashSet(existing);
						newSet.add(new SingleRecipeInput(slot, stack));
						newList.add(newSet);
					}
				}
				shapedCombos.clear();
				shapedCombos.addAll(newList);
			}
		}
		
		return shapedCombos;
	}
	
	// Creates a set of every combination of shapeless recipes.
	public Collection<Set<SingleRecipeInput>> getShapelessCombinations() {
		List<Set<SingleRecipeInput>> shapelessCombos = Lists.newArrayList();
		
		// Add combinations for the shapeless inputs
		for (final RecipeInput input : shapelessInputs) {
			if (shapelessCombos.size() == 0) {
				// For the first element, create the initial array
				for (final ItemStack stack : input.inputs) {				
					shapelessCombos.add(ImmutableSet.of(new SingleRecipeInput(input.slot, stack)));
				}				
			} else {
				final List<Set<SingleRecipeInput>> newListList = Lists.newArrayList();
				for (final ItemStack stack : input.inputs) {
					for (final Set<SingleRecipeInput> oldList : shapelessCombos) {
						Set<SingleRecipeInput> newList = Sets.newHashSet(oldList);
						newList.add(new SingleRecipeInput(input.slot, stack));
						newListList.add(newList);
					}
				}
				shapelessCombos.clear();
				shapelessCombos = newListList;
			}
		}
		return shapelessCombos;
	}
	
	// Returns true if the inputs are valid for this recipe.  Stack sizes are not checked,
	// only input item positioning.
	public boolean areInputsValid(final Set<SingleRecipeInput> inputs) {
		if (inputs.size() != (shapelessInputs.size() + shapedInputs.size())) {
			return false;
		}
				
		for (final SingleRecipeInput input : inputs) {
			// Iterate over the shaped inputs looking for a match
			boolean isValid = false;
			for (final RecipeInput recipeInput : shapedInputs.values()) {
				if (recipeInput.contains(input)) {
					isValid = true;
					break;
				}
			}
			if (isValid == false) {
				// If no shaped inputs match, iterate over the shapeless ones
				for (final RecipeInput recipeInput : shapelessInputs) {
					if (recipeInput.contains(input)) {
						isValid = true;
						break;
					}
				}
				if (isValid == false) {
					return false;
				}
			}
		}
		return true;
	}
}
