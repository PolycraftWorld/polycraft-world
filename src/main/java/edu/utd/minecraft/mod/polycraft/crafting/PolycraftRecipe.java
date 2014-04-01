package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.ContainerChemicalProcessor;

// Implementation of a Polycraft recipe.  Recipes can consist of shaped or
// shapeless inputs, with any combination of both.
public class PolycraftRecipe<T extends PolycraftCraftingContainer> {
	private static Logger logger = LogManager.getLogger();
	
	// Collection of collections that are shapeless.  Recipe creation requires at
	// least one item from every RecipeInput, and each of those items can be any
	// one of the items specified within the RecipeInput's ItemStack values.
	private final Collection<RecipeInput> shapelessInputs = Lists.newArrayList();
	
	// Map of fixed recipe inputs.
	private final Map<ContainerSlot, RecipeInput> shapedInputs = Maps.newHashMap();
	
	// Collection of item outputs and their quantities.
	private final Set<SingleRecipeInput> outputs;
	
	// Create a new recipe with the specified inputs and outputs.
	public PolycraftRecipe(final Iterable<RecipeInput> inputs, final Iterable<SingleRecipeInput> outputs) {
		Preconditions.checkNotNull(inputs);
		Preconditions.checkNotNull(outputs);
		
		// Validate the outputs
		Set<ContainerSlot> slotMap = Sets.newHashSet();
		for (SingleRecipeInput output : outputs) {
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
				for (RecipeInput existingInput : shapelessInputs) {
					// Can't have a similar input to any other shapeless item
					for (ItemStack inputStack : input.inputs) {
						Preconditions.checkArgument(!existingInput.contains(inputStack));
					}
				}
				for (RecipeInput existingInput : shapedInputs.values()) {
					// Can't have a similar input to any other shad item
					for (ItemStack inputStack : input.inputs) {
						Preconditions.checkArgument(!existingInput.contains(inputStack));
					}
				}
				shapelessInputs.add(RecipeInput.shapelessAnyOneOf(input.inputs));
			} else {
				// Fixed input position
				if (shapedInputs.containsKey(input.slot)) {
					throw new IllegalArgumentException("A recipe item already exists at container slot " + input.slot);
				}
				
				// Can't be the same as any existing shaped input.
				for (RecipeInput existingInput : shapelessInputs) {
					// Can't have a similar input to any other shapeless item
					for (ItemStack inputStack : input.inputs) {
						Preconditions.checkArgument(!existingInput.contains(inputStack));
					}
				}
				shapedInputs.put(input.slot, input);
			}
		}
		
		// Must have at least 1 fixed input or 1 shapeless inputs
		Preconditions.checkArgument(this.shapedInputs.size() + this.shapelessInputs.size() != 0);
	}
	
	// Gets the list of outputs
	public Collection<SingleRecipeInput> getOutputs() {
		return this.outputs;
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
	
	private ItemStack getItemstackForInput(SingleRecipeInput input) {
		for (final RecipeInput recipeInput : shapedInputs.values()) {
			ItemStack recipeStack = recipeInput.get(input);
			if (recipeStack != null && input.itemStack.stackSize >= recipeStack.stackSize) {
				return recipeStack;
			}
		}
		for (final RecipeInput recipeInput : shapelessInputs) {
			ItemStack recipeStack = recipeInput.get(input);
			if (recipeStack != null && input.itemStack.stackSize >= recipeStack.stackSize) {
				return recipeStack;
			}
		}
		return null;
	}
	
	// Returns true if the inputs and stack sizes are valid for this recipe.
	public boolean areInputsValid(final Set<SingleRecipeInput> inputs) {
		if (inputs.size() != (shapelessInputs.size() + shapedInputs.size())) {
			return false;
		}
				
		for (final SingleRecipeInput input : inputs) {
			// Iterate over the shaped inputs looking for a match
			if (getItemstackForInput(input) == null) {
				return false;
			}
		}
		return true;
	}
	
	// Processes the inputs against the recipe, adding the results
	// to the container's output and subtracting from the inputs.
	public void process(Set<SingleRecipeInput> inputs, ItemStack[] containerStacks) {
		if (inputs == null) {
			logger.warn("Invalid processing input for recipe " + this.toString());
			return;
		}
		
		if (!areInputsValid(inputs)) {
			return;
		}
		
		// Create the outputs.
		for (SingleRecipeInput output : this.outputs) {
			int slot = output.slot.getSlotIndex();
			if (containerStacks[slot] == null) {
				containerStacks[slot] = output.itemStack.copy();
			} else {
				containerStacks[slot].stackSize += output.itemStack.stackSize; 
			}				
		}
		
		// Remove from the inputs.
		for (SingleRecipeInput input : inputs) {
			int slot = input.slot.getSlotIndex();
			ItemStack itemStack = getItemstackForInput(input);
			
			containerStacks[slot].stackSize -= input.itemStack.stackSize;
			if (containerStacks[slot].stackSize <= 0) {
				containerStacks[slot] = null;
			}
		}
	}
}
