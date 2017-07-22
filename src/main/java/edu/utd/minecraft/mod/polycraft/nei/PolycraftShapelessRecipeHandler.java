package edu.utd.minecraft.mod.polycraft.nei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import codechicken.nei.PositionedStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.ColoringPolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import net.minecraft.item.ItemStack;

public class PolycraftShapelessRecipeHandler extends PolycraftShapedRecipeHandler {

	public int[][] stackorder = new int[][] { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 }, { 0, 2 }, { 1, 2 }, { 2, 0 },
			{ 2, 1 }, { 2, 2 } };

	public class CachedShapelessRecipe extends CachedRecipe {
		public CachedShapelessRecipe() {
			ingredients = new ArrayList<PositionedStack>();
		}

		public CachedShapelessRecipe(ItemStack output) {
			this();
			setResult(output);
		}

		public CachedShapelessRecipe(Object[] input, ItemStack output) {
			this(Arrays.asList(input), output);
		}

		public CachedShapelessRecipe(List<?> input, ItemStack output) {
			this(output);
			setIngredients(input);
		}

		public void setIngredients(List<?> items) {
			ingredients.clear();
			for (int ingred = 0; ingred < items.size(); ingred++) {
				PositionedStack stack = new PositionedStack(items.get(ingred), 25 + stackorder[ingred][0] * 18,
						6 + stackorder[ingred][1] * 18);
				// stack.setMaxSize(1);
				ingredients.add(stack);
			}
		}

		public void setResult(ItemStack output) {
			result = new PositionedStack(output, 119, 24);
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return getCycledIngredients(cycleticks / 20, ingredients);
		}

		@Override
		public PositionedStack getResult() {
			return result;
		}

		public ArrayList<PositionedStack> ingredients;
		public PositionedStack result;
	}

	public String getRecipeName() {
		return "Polycraft Conversions"; // +
										// NEIClientUtils.translate("recipe.shapeless");
	}

	/**
	 * Converts a PolycraftRecipe (shapeless crafting) into a CachedShapedRecipe
	 * to add to info results.
	 * 
	 * @param recipe
	 * @param res
	 */
	private void addPCraftingRecipe(PolycraftRecipe recipe, ItemStack res) {
		if (res == null)
			for (RecipeComponent comp : recipe.getOutputs(null)) {
				res = comp.itemStack;
				break;
			}
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (RecipeInput input : recipe.getInputs()) {
			// ins[input.slot.getSlotIndex()] = input.inputs.toArray();
			// would cause some ClassCastException that is beyond my
			// comprehension.
			if (recipe instanceof ColoringPolycraftRecipe)
				System.out.println("wtf");
			if (input.inputs.size() == 1) {
				items.addAll(input.inputs);
			}
		}
		CachedShapelessRecipe toadd = new CachedShapelessRecipe(items, res);
		arecipes.add(toadd);
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
			if (recipe.isShapedOnly())
				continue;
			ItemStack res = null;
			for (RecipeComponent comp : recipe.getOutputs(null)) {
				res = comp.itemStack;
			}
			if (result != null && !res.getUnlocalizedName().equals(result.getUnlocalizedName()))
				continue;
			addPCraftingRecipe(recipe, res);
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		Collection<PolycraftRecipe> recipes = PolycraftMod.recipeManagerRuntime
				.getRecipesByContainerType(PolycraftContainerType.CRAFTING_TABLE);
		for (PolycraftRecipe recipe : recipes) {
			if (recipe.isShapedOnly())
				continue;
			boolean found = false;
			for (RecipeInput input : recipe.getInputs())
				if (input.contains(ingredient)) {
					found = true;
					break;
				}
			if (!found)
				continue;

			addPCraftingRecipe(recipe, null);
		}
	}

	@Override
	public boolean isRecipe2x2(int recipe) {
		return getIngredientStacks(recipe).size() <= 4;
	}

}
