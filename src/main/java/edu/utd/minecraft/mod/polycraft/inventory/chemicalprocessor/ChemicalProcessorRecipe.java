package edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.crafting.SingleRecipeInput;
import edu.utd.minecraft.mod.polycraft.item.ItemFluidContainer;

public class ChemicalProcessorRecipe extends PolycraftRecipe {
	public static final int MAX_INPUTS = 5;
	public static final int MAX_OUTPUTS = 9;
	
	private static final Map<String, Integer> FUEL_BURN_TIME = Maps.newHashMap();
	private static final Set<ItemStack> FUEL_SET = Sets.newHashSet();
	
	private static int getItemBurnTime(Item item) {
		if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
			Block block = Block.getBlockFromItem(item);

			if (block == Blocks.wooden_slab) {
				return 150;
			}

			if (block.getMaterial() == Material.wood) {
				return 300;
			}

			if (block == Blocks.coal_block) {
				return 16000;
			}
		}

		if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD")) {
			return 200;
		}
		if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD")) {
			return 200;
		}
		if (item instanceof ItemHoe && ((ItemHoe) item).getToolMaterialName().equals("WOOD")) {
			return 200;
		}
		if (item == Items.stick) {
			return 100;
		}
		if (item == Items.coal) {
			return 1600;
		}
		if (item == Items.lava_bucket) {
			return 20000;
		}
		if (item == Item.getItemFromBlock(Blocks.sapling)) {
			return 100;
		}
		if (item == Items.blaze_rod) {
			return 2400;
		}
		return GameRegistry.getFuelValue(new ItemStack(item, 1));		
	}
	
	// Initialize the fuel burn time
	public static void InitializeChemicalProcessorRecipe() {
		for (Object obj : GameData.itemRegistry) {
			if (!(obj instanceof Item)) {
				continue;
			}
			Item item = (Item)obj;
			int burnTime = getItemBurnTime(item);
			if (burnTime > 0) {
				FUEL_BURN_TIME.put(item.getUnlocalizedName(), burnTime);
				FUEL_SET.add(new ItemStack(item, 1));
			}
		}
	}
	
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
	public static ChemicalProcessorRecipe createWithCorrectContainerOutput(
			final ItemStack[] materials, final ItemStack[] results) {
		int fluidContainersInput = countFluidContainerStack(materials);
		int fluidContainersOutput = countFluidContainerStack(results);
		
		ItemStack[] resultStack = results;
		if (fluidContainersOutput > 0) {
			resultStack = Arrays.copyOf(results, results.length + 1);
			resultStack[results.length] = new ItemStack(
					PolycraftMod.itemFluidContainer, fluidContainersInput);
		}
		
		// Translate the item stacks into recipe inputs
		List<RecipeInput> inputs = Lists.newArrayList();
		for (ItemStack stack : materials) {
			inputs.add(RecipeInput.shapelessInput(stack));
		}
		
		// All chemical processor recipes use some fuel source
		inputs.add(RecipeInput.shapedAnyOneOf(ContainerChemicalProcessor.SLOT_FUEL, FUEL_SET));
		
		List<SingleRecipeInput> outputs = Lists.newArrayList();
		int outputIndex = 0;
		for (ItemStack result : results) {
			outputs.add(new SingleRecipeInput(
					ContainerChemicalProcessor.SLOT_INDEX_FIRST_RESULT + outputIndex,
					result));
			outputIndex++;
		}
		return new ChemicalProcessorRecipe(inputs, outputs, fluidContainersOutput);
	}
	
	public final int fluidContainersOutput;
	
	public ChemicalProcessorRecipe(Iterable<RecipeInput> inputs, Iterable<SingleRecipeInput> outputs, int fluidContainersOutput) {
		super(inputs, outputs);
		this.fluidContainersOutput = fluidContainersOutput;
	}	
}