package edu.utd.minecraft.mod.polycraft.nei;

import java.util.ArrayList;
import java.util.Collection;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.ColoringPolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PolycraftColoringRecipeHandler extends PolycraftShapedRecipeHandler {

	/**
	 * Converts a ColoringPolycraftRecipe (shaped crafting) into a
	 * CachedShapedRecipe to add to info results.
	 * 
	 * @param recipe
	 * @param res
	 */
	private void addPColoringRecipe(ColoringPolycraftRecipe recipe, ItemStack res) {
		for (int m = 0; m < 16; m++)
			addPColoringRecipe(recipe, res, m);
	}

	private void addPColoringRecipe(ColoringPolycraftRecipe recipe, ItemStack res, int meta) {
		Object[] ins = new Object[9];
		int count = 0;
		for (RecipeInput input : recipe.getInputs()) {
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			for (ItemStack i : input.inputs) {
				if (Item.getIdFromItem(Items.dye) == Item.getIdFromItem(i.getItem())) {
					items.add(new ItemStack(Items.dye, 1, meta));
				} else if (i.getHasSubtypes()) {
					i.getItem().getSubItems(i.getItem(), null, items);
					count++;
				} else {
					items.add(i);
				}
			}
			ins[input.slot.getSlotIndex()] = items.toArray(new ItemStack[items.size()]);
		}
		CachedShapedRecipe toadd = new CachedShapedRecipe(3, 3, ins, new ItemStack(res.getItem(), res.stackSize, meta));
		toadd.computeVisuals();
		arecipes.add(toadd);
	}

	@Override
	public String getRecipeName() {
		return "Polycraft Coloring";
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("item")) {
			for (Object res : results)
				if (res instanceof ItemStack)
					loadCraftingRecipes((ItemStack) res);
		} else if (outputId.indexOf("crafting") != -1) {
			loadCraftingRecipes(null);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		Collection<PolycraftRecipe> recipes = PolycraftMod.recipeManagerRuntime
				.getRecipesByContainerType(PolycraftContainerType.CRAFTING_TABLE);
		for (PolycraftRecipe recipe : recipes) {
			if (!recipe.isShapedOnly() || !(recipe instanceof ColoringPolycraftRecipe) || !dyeInIngreds(recipe))
				continue;
			ItemStack res = null;
			for (RecipeComponent comp : recipe.getOutputs(null)) {
				res = comp.itemStack;
			}
			if (result != null && !res.getUnlocalizedName().equals(result.getUnlocalizedName()))
				continue;
			addPColoringRecipe((ColoringPolycraftRecipe) recipe, res);
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		Collection<PolycraftRecipe> recipes = PolycraftMod.recipeManagerRuntime
				.getRecipesByContainerType(PolycraftContainerType.CRAFTING_TABLE);
		for (PolycraftRecipe recipe : recipes) {
			if (!recipe.isShapedOnly() || !(recipe instanceof ColoringPolycraftRecipe) || !dyeInIngreds(recipe))
				continue;
			boolean found = Item.getIdFromItem(Items.dye) == Item.getIdFromItem(ingredient.getItem());
			ItemStack res = null;
			for (RecipeComponent comp : recipe.getOutputs(null)) {
				res = comp.itemStack;
			}
			if (found) {
				addPColoringRecipe((ColoringPolycraftRecipe) recipe, res, ingredient.getItemDamage());
			} else {
				for (RecipeInput input : recipe.getInputs())
					if (input.contains(ingredient)) {
						found = true;
						break;
					}
				if (!found)
					continue;
				addPColoringRecipe((ColoringPolycraftRecipe) recipe, res);
			}

		}
	}

}
