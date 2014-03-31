package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.util.SetMap;

// Utility class to manage recipes for each type of container and handle fast searching for
// both shaped and shapeless recipes.
public class PolycraftRecipeManager {
	private static final Logger logger = LogManager.getLogger();
	
	private Map<PolycraftContainerType, Set<PolycraftRecipe>> recipesByContainer = Maps.newHashMap();
	
	private Map<PolycraftContainerType, SetMap<SingleRecipeInput, PolycraftRecipe>> shapedRecipesByContainer = Maps.newHashMap();
	private Map<PolycraftContainerType, SetMap<SingleRecipeInput, PolycraftRecipe>> shapelessRecipesByContainer = Maps.newHashMap();
	
	// Add a recipe to the manager to be indexed for searching.
	public void addRecipe(final PolycraftContainerType containerType, final PolycraftRecipe recipe) {
		if (!recipesByContainer.containsKey(containerType)) {
			recipesByContainer.put(containerType, new HashSet<PolycraftRecipe>());
			shapedRecipesByContainer.put(containerType, new SetMap());
			shapelessRecipesByContainer.put(containerType, new SetMap());
		}
		
		recipesByContainer.get(containerType).add(recipe);
		
		// Add shapeless recipes to the SetMap.  If there are no shapeless inputs,
		// then the recipe is added as an empty set into the shapemap
		final Collection<Set<SingleRecipeInput>> shapelessCombinations = recipe.getShapelessCombinations();
		if (shapelessCombinations.size() != 0) {
			for (final Set<SingleRecipeInput> inputs : shapelessCombinations) {
				shapelessRecipesByContainer.get(containerType).add(inputs, recipe);
			}
		} else {
			shapelessRecipesByContainer.get(containerType).add(Collections.EMPTY_SET, recipe);
		}
		
		// Add shaped recipes to the SetMap.  If there are no shaped inputs,
		// then the recipe is added as an empty set into the shapemap
		final Collection<Set<SingleRecipeInput>> shapedCombinations = recipe.getShapedCombinations();
		if (shapedCombinations.size() != 0) {
			for (final Set<SingleRecipeInput> inputs : shapedCombinations) {
				shapedRecipesByContainer.get(containerType).add(inputs, recipe);
			}
		} else {
			shapedRecipesByContainer.get(containerType).add(Collections.EMPTY_SET, recipe);
		}
	}
	
	// Searches the recipes available to the container type for the given set of recipe inputs.
	public PolycraftRecipe findRecipe(final PolycraftContainerType container, final Set<SingleRecipeInput> inputs) {
		if (!recipesByContainer.containsKey(container)) {
			return null;
		}
		
		// Get any subset of the fixed inputs (ie, ignore the shapeless inputs).
		final Set<PolycraftRecipe> shapedSet = shapedRecipesByContainer.get(container).getAnySubset(inputs);
		final Set<PolycraftRecipe> shapelessSet = shapedRecipesByContainer.get(container).getAnySubset(inputs);
		shapedSet.retainAll(shapelessSet);
		final Set<PolycraftRecipe> finalSet = shapedSet;
		
		if (finalSet.size() == 0) {
			// no recipes found
			return null;
		}
		
		// Found all subsets, now need to find exact sets.
		for (final PolycraftRecipe recipe : shapedSet) {
			if (recipe.areInputsValid(inputs)) {
				return recipe;
			}
		}		
		return null;
	}
}
