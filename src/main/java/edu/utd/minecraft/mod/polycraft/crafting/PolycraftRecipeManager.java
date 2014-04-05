package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.util.SetMap;

/**
 * Utility class to manage recipes for each type of container and handle fast searching for
 * both shaped and shapeless recipes.
 */
public class PolycraftRecipeManager {
	private static final Logger logger = LogManager.getLogger();
	
	private Map<PolycraftContainerType, Set<PolycraftRecipe>> recipesByContainer = Maps.newHashMap();
	
	private Map<PolycraftContainerType, SetMap<RecipeComponent, PolycraftRecipe>> shapedRecipesByContainer = Maps.newHashMap();
	private Map<PolycraftContainerType, SetMap<String, PolycraftRecipe>> shapelessRecipesByContainer = Maps.newHashMap();
	
	/**
	 * @return All recipes known to the Recipe manager.
	 */
	public Collection<PolycraftRecipe> getAllRecipies() {
		List<PolycraftRecipe> recipes = Lists.newArrayList();		
		for (PolycraftContainerType container : recipesByContainer.keySet()) {
			recipes.addAll(recipesByContainer.get(container));
		}
		return recipes;
	}
	
	/**
	 * Add a recipe to the manager to be indexed for searching.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addRecipe(final PolycraftRecipe recipe) {
		PolycraftContainerType containerType = recipe.getContainerType();
		if (!recipesByContainer.containsKey(containerType)) {
			recipesByContainer.put(containerType, new HashSet<PolycraftRecipe>());
			shapedRecipesByContainer.put(containerType, new SetMap());
			shapelessRecipesByContainer.put(containerType, new SetMap());
		}
		
		recipesByContainer.get(containerType).add(recipe);
		
		// Add shapeless recipes to the SetMap.  If there are no shapeless inputs,
		// then the recipe is added as an empty set into the shapemap
		final Collection<Set<RecipeComponent>> shapelessCombinations = recipe.getShapelessCombinations();
		if (shapelessCombinations.size() != 0) {
			for (final Set<RecipeComponent> inputs : shapelessCombinations) {
				Set<String> itemSet = Sets.newHashSet();
				for (final RecipeComponent input : inputs) {
					itemSet.add(input.itemStack.getItem().toString());
				}
				shapelessRecipesByContainer.get(containerType).add(itemSet, recipe);
			}
		} else {
			shapelessRecipesByContainer.get(containerType).add(Collections.EMPTY_SET, recipe);
		}
		
		// Add shaped recipes to the SetMap.  If there are no shaped inputs,
		// then the recipe is added as an empty set into the shapemap
		final Collection<Set<RecipeComponent>> shapedCombinations = recipe.getShapedCombinations();
		if (shapedCombinations.size() != 0) {
			for (final Set<RecipeComponent> inputs : shapedCombinations) {
				shapedRecipesByContainer.get(containerType).add(inputs, recipe);
			}
		} else {
			shapedRecipesByContainer.get(containerType).add(Collections.EMPTY_SET, recipe);
		}
	}
	
	/**
	 * Searches the recipes available to the container type for the given set of recipe inputs.
	 */
	public PolycraftRecipe findRecipe(final PolycraftContainerType container, final Set<RecipeComponent> inputs) {
		if (!recipesByContainer.containsKey(container)) {
			return null;
		}

		// Look in shaped recipes by ingredient first.  This ensures that when a shaped
		// recipe and a shapeless recipe look similar, the shaped ones take precedence
		final Set<PolycraftRecipe> shapedSet = shapedRecipesByContainer.get(container).getAnySubset(inputs);		
		for (final PolycraftRecipe recipe : shapedSet) {
			if (recipe.areInputsValid(inputs)) {
				return recipe;
			}
		}
		
		Set<String> itemSet = Sets.newHashSet();
		for (final RecipeComponent input : inputs) {
			itemSet.add(input.itemStack.getItem().toString());
		}
		final Set<PolycraftRecipe> shapelessSet = shapelessRecipesByContainer.get(container).getAnySubset(itemSet);
		for (final PolycraftRecipe recipe : shapelessSet) {
			if (recipe.areInputsValid(inputs)) {
				return recipe;
			}
		}		
		return null;
	}

	/**
	 * Creates a generic shapeless recipe from the inputs and adds it
	 * to the recipe inputs for searching.
	 */
	public PolycraftRecipe addShapelessRecipe(final PolycraftContainerType containerType,
			final Iterable<ItemStack> inputs, final Iterable<ItemStack> outputs) {
		Preconditions.checkNotNull(containerType);
		final List<ContainerSlot> inputSlots = ImmutableList.copyOf(containerType.getSlots(SlotType.INPUT));
		final List<ContainerSlot> outputSlots = ImmutableList.copyOf(containerType.getSlots(SlotType.OUTPUT));

		List<RecipeInput> recipeInputs = Lists.newArrayList();
		int index = 0;
		for (final ItemStack stack : inputs) {
			if (index >= inputSlots.size()) {
				throw new IllegalArgumentException("Too many inputs for this container.");
			}
			recipeInputs.add(RecipeInput.shapelessInput(stack));
			index++;
		}
		
		final List<RecipeComponent> recipeOutputs = Lists.newArrayList();
		index = 0;
		for (final ItemStack stack : outputs) {
			if (index >= outputSlots.size()) {
				throw new IllegalArgumentException("Too many outputs for this container.");
			}
			recipeOutputs.add(new RecipeComponent(outputSlots.get(index), stack));
			index++;
		}
		PolycraftRecipe newRecipe = new PolycraftRecipe(containerType, recipeInputs, recipeOutputs);
		this.addRecipe(newRecipe);
		return newRecipe;
	}

	private static ContainerSlot [][] createInputGrid(final PolycraftContainerType containerType) {
		Collection<ContainerSlot> slots = containerType.getSlots(SlotType.INPUT);
		int maxX = 0;
		int maxY = 0;
		for (final ContainerSlot slot : slots) {
			maxX = Math.max(maxX, slot.getRelativeX());
			maxY = Math.max(maxY, slot.getRelativeY());
		}
		maxX++;
		maxY++;
		ContainerSlot[][] grid = new ContainerSlot[maxX][maxY];
		for (final ContainerSlot slot : slots) {
			grid[slot.getRelativeX()][slot.getRelativeY()] = slot;
		}
		return grid;
	}
	
	/**
	 * Generates arguments to call Forge's recipe APIs for shaped recipes.
	 */
	private static Object [] generateForgeShapedRecipeArgs(final String [] inputShape, final Map<Character, ItemStack> itemStackMap) {		
		List<Object> list = Lists.newArrayList();
		for (String input : inputShape) {
			list.add(input);
		}
		for (Character key : itemStackMap.keySet()) {
			list.add(key);
			list.add(itemStackMap.get(key));
		}
		return list.toArray();
	}

	/**
	 * Creates and adds a shapeless recipe.  If the crafting recipe type is a generic
	 * or smelting recipe, it is added to Forge via the GameRegistry API.
	 */
	public PolycraftRecipe addShapelessRecipe(final PolycraftContainerType containerType,
			final ItemStack resultItem, final Iterable<ItemStack> inputItems) {
		return addShapelessRecipe(containerType, resultItem, inputItems, 0);
	}

	/**
	 * Creates and adds a shapeless recipe.  If the crafting recipe type is a generic
	 * or smelting recipe, it is added to Forge via the GameRegistry API.
	 */
	public PolycraftRecipe addShapelessRecipe(final PolycraftContainerType containerType,
			final ItemStack resultItem, final Iterable<ItemStack> inputItems,
			final double experience) {
		Preconditions.checkNotNull(containerType);
		Preconditions.checkNotNull(resultItem);
		Preconditions.checkArgument(resultItem.stackSize > 0, "Result stack size must be > 0!");
		Preconditions.checkNotNull(inputItems);
		
		List<RecipeInput> recipeInputs = Lists.newArrayList();
		for (ItemStack inputItem : inputItems) {
			recipeInputs.add(RecipeInput.shapelessInput(inputItem));			
		}		
		Preconditions.checkArgument(recipeInputs.size() != 0, "No inputs were given!");
		
		ContainerSlot firstOutput = containerType.getSlots(SlotType.OUTPUT).iterator().next();
		PolycraftRecipe newRecipe = new PolycraftRecipe(containerType, recipeInputs, 
				ImmutableList.of(new RecipeComponent(firstOutput, resultItem)), experience);
		if (containerType.equals(PolycraftContainerType.CRAFTING_TABLE)) {
			List<Object> args = Lists.newArrayList();
			for (ItemStack item : inputItems) {
				args.add(item);
			}
			GameRegistry.addShapelessRecipe(resultItem, args.toArray());
		} else if(containerType.equals(PolycraftContainerType.FURNANCE)) {
			Preconditions.checkArgument(newRecipe.getInputCount() == 1, "Furnace recipes may only have one input!");
			ItemStack singleInput = inputItems.iterator().next();
			GameRegistry.addSmelting(singleInput, resultItem, (float)experience);
		}
		this.addRecipe(newRecipe);
		return newRecipe;
	}

	/**
	 * Adds a shaped recipe from the inputs, in a similar format to the Forge API.  If the crafting
	 * recipe container type is a generic or smelting recipe, it will be added to Forge
	 * via the GameRegistry APi.
	 * @param containerType The container the recipe is created in
	 * @param resultItem The item generated from the recipe
	 * @param inputShape The shape of the items, one string per row.  Any letter can be used to represent
	 * an item input; spaces represent no input.
	 * @param inputItems Map of character representation to item stack needed.
	 */
	public PolycraftRecipe addShapedRecipe(final PolycraftContainerType containerType,
			final ItemStack resultItem, final String [] inputShape,
			final Map<Character, ItemStack> itemStackMap) {
		return addShapedRecipe(containerType, resultItem, inputShape, itemStackMap, 0);
	}

	/**
	 * Adds a shaped recipe from the inputs, in a similar format to the Forge API.  If the crafting
	 * recipe container type is a generic or smelting recipe, it will be added to Forge
	 * via the GameRegistry APi.
	 * @param containerType The container the recipe is created in
	 * @param resultItem The item generated from the recipe
	 * @param inputShape The shape of the items, one string per row.  Any letter can be used to represent
	 * an item input; spaces represent no input.
	 * @param inputItems Map of character representation to item stack needed.
	 * @param experience The amount of experience yielded by crafting the recipe.
	 */
	public PolycraftRecipe addShapedRecipe(final PolycraftContainerType containerType,
			final ItemStack resultItem, final String [] inputShape,
			final Map<Character, ItemStack> itemStackMap, double experience) {
		Preconditions.checkNotNull(containerType);
		Preconditions.checkNotNull(resultItem);
		Preconditions.checkArgument(resultItem.stackSize > 0, "Result stack size must be > 0!");
		Preconditions.checkNotNull(inputShape);
		Preconditions.checkArgument(inputShape.length != 0, "No input shapes given!");
		Preconditions.checkNotNull(itemStackMap);
		Preconditions.checkArgument(itemStackMap.size() != 0, "No input items were specified");
		
		// Map letters to items and place them in a map
		Set<RecipeInput> recipeInputs = Sets.newHashSet();
		ContainerSlot[][] inputGrid = createInputGrid(containerType);
		Preconditions.checkArgument(inputGrid.length != 0, "Input Container type has not slots defined!");
		for (int y = 0; y < inputShape.length; ++y) {
			final String shapeRow = inputShape[y];
			for (int x = 0; x < shapeRow.length(); ++x) {
				Preconditions.checkArgument(x < inputGrid.length, "The item configuration specified is too big for the container!");
				Preconditions.checkArgument(y < inputGrid[x].length, "The item configuration specified is too big for the container!");
				Preconditions.checkNotNull(inputGrid[x][y], "Invalid container slot on row " + y + " column " + x);
				final char ch = shapeRow.charAt(x);
				if(ch != ' ') {
					Preconditions.checkNotNull(itemStackMap.get(ch), "No item specified for character '" + ch + "'");
					recipeInputs.add(RecipeInput.shapedInput(inputGrid[x][y], itemStackMap.get(ch)));
				}
			}
		}
		
		ContainerSlot firstOutput = containerType.getSlots(SlotType.OUTPUT).iterator().next();
		PolycraftRecipe newRecipe = new PolycraftRecipe(containerType, recipeInputs,
				ImmutableList.of(new RecipeComponent(firstOutput, resultItem)));
		this.addRecipe(newRecipe);
		
		// Add to Forge's GameRegistry if necessary
		if (containerType.equals(PolycraftContainerType.CRAFTING_TABLE)) {
			Object [] args = generateForgeShapedRecipeArgs(inputShape, itemStackMap);
			GameRegistry.addShapedRecipe(resultItem, args);
		} else if(containerType.equals(PolycraftContainerType.FURNANCE)) {
			Preconditions.checkArgument(newRecipe.getInputCount() == 1, "Furnace recipes may only have one input!");
			ItemStack singleInput = itemStackMap.values().iterator().next();
			GameRegistry.addSmelting(singleInput, resultItem, (float)experience);		
		}
		
		return newRecipe;
	}
}
