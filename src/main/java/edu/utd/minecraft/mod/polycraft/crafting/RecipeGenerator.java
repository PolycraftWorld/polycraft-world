package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeGenerator {
	private static final Logger logger = LogManager.getLogger();

	/**
	 * TODO: Actually generate recipes.
	 */
	public static void generateRecipes() {
		//TODO auto-generate ingot to compressed block and polymer pellet to block recipes
		if (System.getProperty("cheatRecipes") != null)
			RecipeGenerator.generateCheatRecipes();
	}

	private static void generateCheatRecipes() {
		final ItemStack dirtStack = new ItemStack(Blocks.dirt);
		final Collection<ItemStack> dirtStacks = new ArrayList<ItemStack>();

		final ItemStack cobblestoneStack = new ItemStack(Blocks.cobblestone);
		final Collection<ItemStack> cobblestoneStacks = new ArrayList<ItemStack>();

		dirtStacks.add(dirtStack);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.dirt, 64), dirtStacks.toArray());

		dirtStacks.add(dirtStack);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.cobblestone, 64), dirtStacks.toArray());

		dirtStacks.add(dirtStack);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.crafting_table), dirtStacks.toArray());

		cobblestoneStacks.add(dirtStack);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.furnace), cobblestoneStacks.toArray());

		cobblestoneStacks.add(cobblestoneStack);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.diamond_pickaxe, 64), cobblestoneStacks.toArray());

		cobblestoneStacks.add(cobblestoneStack);
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.torch, 64), cobblestoneStacks.toArray());

		cobblestoneStacks.add(cobblestoneStack);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.coal, 64), cobblestoneStacks.toArray());
	}
}
