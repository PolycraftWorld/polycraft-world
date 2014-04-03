package edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.item.ItemFluidContainer;

// TODO: Delete?
public class ChemicalProcessorRecipe extends PolycraftRecipe {
	// Creates a chemical recipe that adjusts the number of output containers to equal the number of containers
	// used on the input stack.
	/*
	public static ChemicalProcessorRecipe createWithCorrectContainerOutput(
			final ItemStack[] materials, final ItemStack[] results) {		
		// Translate the item stacks into recipe inputs
		List<RecipeInput> inputs = Lists.newArrayList();
		for (ItemStack stack : materials) {
			inputs.add(RecipeInput.shapelessInput(stack));
		}
		
		List<SingleRecipeInput> outputs = Lists.newArrayList();
		List<ChemicalProcessorSlot> outputSlots = (List<ChemicalProcessorSlot>) SlotType.OUTPUT.getAll(ChemicalProcessorSlot.class);
		Collections.sort(outputSlots, new Comparator<ChemicalProcessorSlot>() {
			@Override
			public int compare(ChemicalProcessorSlot o1, ChemicalProcessorSlot o2) {
				return Integer.compare(o1.getSlotIndex(), o2.getSlotIndex());
			}
		});
		
		int outputIndex = 0;
		for (ItemStack result : results) {
			outputs.add(new SingleRecipeInput(
					outputSlots.get(outputIndex).getSlotIndex(),
					result));
			outputIndex++;
		}
		
		return new ChemicalProcessorRecipe(inputs, outputs);
	}
	*/
	public ChemicalProcessorRecipe(Iterable<RecipeInput> inputs, Iterable<RecipeComponent> outputs) {
		super(PolycraftContainerType.CHEMICAL_PROCESSOR, inputs, outputs);
		PolycraftMod.recipeManager.addRecipe(this);
	}	
}