package edu.utd.minecraft.mod.polycraft;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

// Base recipe class for all polycraft recipes.
public abstract class PolycraftRecipe {
	private static final Map<Class<? extends PolycraftRecipe>, Collection<PolycraftRecipe>> recipesByType = Maps.newHashMap();
	private static Logger logger = LogManager.getLogger();
	
	public final ItemStack[] materials;
	public final ItemStack[] results;
	
	public static void addRecipe(final PolycraftRecipe recipe) {
		logger.info("Adding recipe: " + recipe.toString());
		Preconditions.checkNotNull(recipe);
		Class<? extends PolycraftRecipe> recipeType = recipe.getClass();
		logger.info("Recipe type: " + recipeType.getName());
		if (!recipesByType.containsKey(recipeType)) {
			recipesByType.put(recipeType, new LinkedList<PolycraftRecipe>());
		}
		logger.info("Adding recipe of type " + recipeType.getName());
		recipesByType.get(recipeType).add(recipe);
	}

	private static boolean materialsMatch(final ItemStack [] stack1, final ItemStack [] stack2) {
		if (stack1.length != stack2.length) {
			return false;
		}		
		for (int m = 0; m < stack1.length; m++) {
			if (!stack2[m].isItemEqual(stack1[m])) {
				return false;
			}
		}
		return true;
	}
	
	// Finds a recipe given the input materials and recipe type.
	// TODO: Should recipeType instead be the recipe container?
	public static <T extends PolycraftRecipe> T findRecipe(
			final Class<T> recipeType,
			final ItemStack ... materials) {
		if (materials == null || materials.length == 0) {
			logger.info("No materials.");
			return null;
		}
		logger.info("Looking for recipe type " + recipeType.getName());
		if (!recipesByType.containsKey(recipeType)) {
			logger.info("No recipes of that type.");
			return null;
		}
		
		for (ItemStack item : materials) {
			logger.info("  Item: " + item.getDisplayName() + " " + item.stackSize);
		}
		for (final PolycraftRecipe recipe : recipesByType.get(recipeType)) {
			logger.info("Checking recipe " + recipe.toString());
			if (materialsMatch(recipe.materials, materials)) {
				return (T)recipe;
			}
		}
		return null;
	}
	
	public PolycraftRecipe(final ItemStack[] materials, final ItemStack[] results) {
		Preconditions.checkNotNull(materials);
		Preconditions.checkNotNull(results);
		Preconditions.checkArgument(materials.length != 0);
		Preconditions.checkArgument(results.length != 0);

		this.materials = materials;
		this.results = results;
		
		PolycraftRecipe.addRecipe(this);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Inputs: [");
		for (ItemStack material : materials) {
			Item materialItem = material.getItem();
			String name = "";
			if (materialItem != null) {
				name = materialItem.getUnlocalizedName();
			} else {
				name = "???";
			}
			sb.append(name + " (" + material.stackSize + "),");
		}
		sb.append("], Outputs: [");
		for (ItemStack material : results) {
			Item materialItem = material.getItem();
			String name = "";
			if (materialItem != null) {
				name = materialItem.getUnlocalizedName();
			} else {
				name = "???";
			}
			sb.append(name + " (" + material.stackSize + "),");
		}
		sb.append("]");
		return sb.toString();
	}
}
