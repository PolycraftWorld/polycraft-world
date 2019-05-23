package edu.utd.minecraft.mod.polycraft.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import codechicken.nei.ItemList;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

/**
 * Basically all the smelting recipes, except multiplied by a factor of 9 due to
 * how the Industrial Oven works.
 * 
 * @author Cappycot
 *
 */
public class IndustrialOvenRecipeHandler extends TemplateRecipeHandler {

	public class SmeltingPair extends CachedRecipe {

		ArrayList<PositionedStack> ingreds = new ArrayList<PositionedStack>();
		PositionedStack result;
		ArrayList<PositionedStack> others = new ArrayList<PositionedStack>();
		PositionedStack bucket = new PositionedStack(new ItemStack(Items.water_bucket), 66, 7, false);

		public SmeltingPair(ItemStack ingred, ItemStack result) {
			ingred.stackSize = 1;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					int offx = i * 18;
					int offy = j * 18;
					ingreds.add(new PositionedStack(ingred, 3 + offx, 7 + offy));
					if (i != 0 || j != 0)
						others.add(new PositionedStack(result, 111 + offx, 7 + offy));
				}
			}
			this.result = new PositionedStack(result, 111, 7);
		}

		@Override
		public PositionedStack getIngredient() {
			return ingreds.get(cycleticks / 48 % ingreds.size());
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return getCycledIngredients(cycleticks / 48, ingreds);
		}

		@Override
		public PositionedStack getResult() {
			return result;
		}

		@Override
		public List<PositionedStack> getOtherStacks() {
			ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
			stacks.add(afuels.get((cycleticks / 48) % afuels.size()).stack);
			stacks.addAll(others);
			stacks.add(bucket);
			return stacks;
		}

	}

	public static class FuelPair {
		public FuelPair(ItemStack ingred, int burnTime) {
			this.stack = new PositionedStack(ingred, 66, 43, false);
			this.burnTime = burnTime;
		}

		public PositionedStack stack;
		public int burnTime;
	}

	public static ArrayList<FuelPair> afuels;

	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(66, 25, 18, 18), "polycraftiofuel"));
		transferRects.add(new RecipeTransferRect(new Rectangle(84, 24, 24, 18), "industrialoven"));
	}

	@Override
	public String getRecipeName() {
		return "Industrial Oven";
	}

	@Override
	public TemplateRecipeHandler newInstance() {
		if (afuels == null || afuels.isEmpty())
			findFuels();
		return super.newInstance();
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		System.out.println(outputId);
		if ((outputId.equals("smelting") || outputId.equals("industrialoven"))
				&& getClass() == IndustrialOvenRecipeHandler.class) {
			Map<ItemStack, ItemStack> recipes = (Map<ItemStack, ItemStack>) FurnaceRecipes.instance().getSmeltingList();
			for (Entry<ItemStack, ItemStack> recipe : recipes.entrySet())
				arecipes.add(new SmeltingPair(recipe.getKey(), recipe.getValue()));
		} else
			super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		System.out.println("Load recipes for " + result);
		Map<ItemStack, ItemStack> recipes = (Map<ItemStack, ItemStack>) FurnaceRecipes.instance().getSmeltingList();
		for (Entry<ItemStack, ItemStack> recipe : recipes.entrySet())
			if (recipe.getValue().getUnlocalizedName().equals(result.getUnlocalizedName()))
				// NEIServerUtils.areStacksSameType(recipe.getValue(), result))
				arecipes.add(new SmeltingPair(recipe.getKey(), recipe.getValue()));
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if (inputId.equals("fuel") && getClass() == IndustrialOvenRecipeHandler.class)
			loadCraftingRecipes("smelting");
		else
			super.loadUsageRecipes(inputId, ingredients);
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		Map<ItemStack, ItemStack> recipes = (Map<ItemStack, ItemStack>) FurnaceRecipes.instance().getSmeltingList();
		for (Entry<ItemStack, ItemStack> recipe : recipes.entrySet())
			if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getKey(), ingredient)) {
				SmeltingPair arecipe = new SmeltingPair(recipe.getKey(), recipe.getValue());
				arecipe.setIngredientPermutation(arecipe.ingreds, ingredient);
				arecipes.add(arecipe);
			}
	}

	@Override
	public String getGuiTexture() {
		// new
		// ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s.png",
		// PolycraftMod.getFileSafeName(config.name))));
		return PolycraftMod.getAssetNameString("textures/gui/container/industrial_oven.png");
	}

	@Override
	public void drawExtras(int recipe) {
		// drawProgressBar(51, 25, 176, 0, 14, 14, 48, 7);
		// drawProgressBar(74, 23, 176, 14, 24, 16, 48, 0);
		drawProgressBar(66, 25, 176, 0, 14, 14, 48, 7); // Fire thingy
		drawProgressBar(84, 24, 176, 14, 24, 16, 48, 0); // Arrow thingy
	}

	private static void findFuels() {
		afuels = new ArrayList<FuelPair>();
		for (ItemStack item : ItemList.items) {
			// Dunno how to calculate actual burnTime but it doesn't matter too
			// much here I think. Given that 1 tick is 1/20th of a second, we'll
			// just multiply by 20.
			if (Fuel.getHeatIntensity(item.getItem()) > 0) {
				int burnTime = (int) Fuel.getHeatDurationSeconds(item.getItem()) * 20;
				afuels.add(new FuelPair(item.copy(), burnTime));
			}
		}
	}

	@Override
	public String getOverlayIdentifier() {
		return "industrialoven";
	}

}
