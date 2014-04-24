package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.PolymerBlock;
import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;
import edu.utd.minecraft.mod.polycraft.config.Vessel;

public class RecipeGenerator {
	private static final Logger logger = LogManager.getLogger();

	/**
	 * TODO: Actually generate recipes.
	 */
	public static void generateRecipes() {
		generateAutoRecipes();
		if (System.getProperty("cheatRecipes") != null)
			generateCheatRecipes();
	}

	private static void generateAutoRecipes() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values()) {
			PolycraftMod.recipeManager.addShapedRecipe(
					PolycraftContainerType.CRAFTING_TABLE,
					compressedBlock.getItemStack(),
					new String[] { "xxx", "xxx", "xxx" },
					ImmutableMap.of('x', compressedBlock.source.getItemStack()));
			PolycraftMod.recipeManager.addShapelessRecipe(
					PolycraftContainerType.CRAFTING_TABLE,
					compressedBlock.source.getItemStack(9),
					ImmutableList.of(compressedBlock.getItemStack()));
		}

		for (final PolymerBlock polymerBlock : PolymerBlock.registry.values()) {
			//TODO furnace only uses one of the stack?
			PolycraftMod.recipeManager.addShapelessRecipe(
					PolycraftContainerType.FURNANCE,
					polymerBlock.getItemStack(),
					ImmutableList.of(polymerBlock.source.getItemStack(4)));
			PolycraftMod.recipeManager.addShapelessRecipe(
					PolycraftContainerType.CRAFTING_TABLE,
					polymerBlock.source.getItemStack(4),
					ImmutableList.of(polymerBlock.getItemStack()));
		}

		final String[][] slabShapes = new String[][] {
				new String[] { "xxx", "   ", "   " } //TODO fix issue with already added recipe,
				//new String[] { "   ", "xxx", "   " },
				//new String[] { "   ", "   ", "xxx" }
		};
		for (final PolymerSlab polymerSlab : PolymerSlab.registry.values()) {
			for (final String[] slabShape : slabShapes)
				PolycraftMod.recipeManager.addShapedRecipe(
						PolycraftContainerType.CRAFTING_TABLE,
						polymerSlab.getItemStack(),
						slabShape,
						ImmutableMap.of('x', polymerSlab.polymerBlock.getItemStack()));
		}

		for (final Vessel largerVessel : Vessel.registry.values()) {
			if (largerVessel.type.smallerType != null) {
				Vessel smallerVessel = Vessel.findVessel(largerVessel.source, largerVessel.type.smallerType);
				if (smallerVessel != null) {
					PolycraftMod.recipeManager.addShapelessRecipe(
							PolycraftContainerType.CRAFTING_TABLE,
							smallerVessel.getItemStack(64),
							ImmutableList.of(largerVessel.getItemStack()));
					PolycraftMod.recipeManager.addShapelessRecipe(
							PolycraftContainerType.CRAFTING_TABLE,
							largerVessel.getItemStack(),
							ImmutableList.of(smallerVessel.getItemStack(64)));
				}
			}
		}
	}

	private static void generateCheatRecipes() {
		final ItemStack[] dirtOutputs = new ItemStack[] {
				new ItemStack(Blocks.dirt, 64),
				new ItemStack(Blocks.crafting_table),
				Inventory.registry.get("Tree Tap").getItemStack(),
				new ItemStack(Blocks.furnace),
				new ItemStack(Items.diamond_pickaxe),
				new ItemStack(Blocks.torch, 64),
				new ItemStack(Items.coal, 64)
		};

		final Collection<ItemStack> inputs = new LinkedList<ItemStack>();
		for (final ItemStack output : dirtOutputs) {
			inputs.add(new ItemStack(Blocks.dirt));
			//TODO call PolycraftMod.recipeManager.addShapelessRecipe when it supports player inventory crafting
			GameRegistry.addShapelessRecipe(output, inputs.toArray());
		}
	}
}
