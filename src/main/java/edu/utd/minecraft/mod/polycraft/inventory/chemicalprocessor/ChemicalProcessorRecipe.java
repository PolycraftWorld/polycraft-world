package edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.item.ItemFluidContainer;

public class ChemicalProcessorRecipe {
	public static final int MAX_INPUTS = 5;
	public static final int MAX_OUTPUTS = 9;

	public static final Collection<ChemicalProcessorRecipe> recipes = Lists.newLinkedList();

	public static void addRecipe(final ChemicalProcessorRecipe recipe) {
		Preconditions.checkNotNull(recipe);
		recipes.add(recipe);
	}

	public static ChemicalProcessorRecipe findRecipe(final ItemStack... materials) {
		if (materials.length > 0) {
			for (final ChemicalProcessorRecipe recipe : recipes) {
				if (recipe.materials.length == materials.length) {
					for (int m = 0; m < materials.length; m++) {
						if (recipe.materials[m].isItemEqual(materials[m])) {
							if (m == materials.length - 1)
								return recipe;
						} else {
							break;
						}
					}
				}
			}
		}
		return null;
	}

	public final ItemStack[] materials;
	public final ItemStack[] results;
	public final int fluidContainersRequired;

	public ChemicalProcessorRecipe(final ItemStack[] materials, final ItemStack[] results) {
		Preconditions.checkNotNull(materials);
		Preconditions.checkNotNull(results);
		Preconditions.checkArgument(materials.length != 0 & materials.length < MAX_INPUTS);
		Preconditions.checkArgument(results.length != 0 && results.length < MAX_OUTPUTS);
		this.materials = materials;
		int fluidContainersRequired = 0;
		for (final ItemStack result : results) {
			if (result.getItem() instanceof ItemFluidContainer) {
				fluidContainersRequired += result.stackSize;
			}
		}
		
		this.fluidContainersRequired = fluidContainersRequired;
		int fluidContainersOutput = 0;
		for (final ItemStack material : materials) {
			if (material.getItem() instanceof ItemFluidContainer) {
				fluidContainersOutput += material.stackSize;
			}
		}
		
		if (fluidContainersOutput > 0) {
			this.results = Arrays.copyOf(results, results.length + 1);
			this.results[results.length] = new ItemStack(PolycraftMod.itemFluidContainer, fluidContainersOutput);
		} else {
			this.results = results;
		}
	}
}