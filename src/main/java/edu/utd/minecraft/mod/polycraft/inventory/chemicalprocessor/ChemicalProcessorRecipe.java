package edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.item.ItemFluidContainer;

public class ChemicalProcessorRecipe extends PolycraftRecipe {
	public static final int MAX_INPUTS = 5;
	public static final int MAX_OUTPUTS = 9;

	// Counts the number of fluid containers used in the stack.
	private static int countFluidContainerStack(ItemStack[] itemstack) {
		int fluidContainersRequired = 0;
		for (final ItemStack item : itemstack) {
			if (item.getItem() instanceof ItemFluidContainer) {
				fluidContainersRequired += item.stackSize;
			}
		}
		return fluidContainersRequired;
	}
	
	// Creates a chemical recipe that adjusts the number of output containers to equal the number of containers
	// used on the input stack.
	public static ChemicalProcessorRecipe createWithCorrectContainerOutput(final ItemStack[] materials, final ItemStack[] results) {
		int fluidContainersInput = countFluidContainerStack(materials);
		int fluidContainersOutput = countFluidContainerStack(results);
		
		ItemStack[] resultStack = results;
		if (fluidContainersOutput > 0) {
			resultStack = Arrays.copyOf(results, results.length + 1);
			resultStack[results.length] = new ItemStack(
					PolycraftMod.itemFluidContainer, fluidContainersInput);
		}
		return new ChemicalProcessorRecipe(materials, results, fluidContainersOutput);
	}
	
	public final int fluidContainersOutput;
	
	public ChemicalProcessorRecipe(final ItemStack[] materials, final ItemStack[] results, int fluidContainersOutput) {
		super(materials, results);
		Preconditions.checkArgument(materials.length < MAX_INPUTS);
		Preconditions.checkArgument(results.length < MAX_OUTPUTS);
		
		this.fluidContainersOutput = fluidContainersOutput;
	}
}