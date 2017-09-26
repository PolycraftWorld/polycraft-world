package edu.utd.minecraft.mod.polycraft.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import codechicken.nei.ItemList;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * The Distillation Column takes 3x as much time to process a single item as a
 * normal Furnace... ugh.
 * 
 * @author Cappycot
 *
 */
public class DistillationColumnRecipeHandler extends TemplateRecipeHandler {

	public class DistColRecipe extends CachedRecipe {

		PositionedStack ingred;
		ArrayList<PositionedStack> results = new ArrayList<PositionedStack>();
		ArrayList<PositionedStack> buckets = new ArrayList<PositionedStack>();

		public DistColRecipe(PolycraftRecipe recipe) {
			for (RecipeInput input : recipe.getInputs()) {
				ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				for (ItemStack i : input.inputs) {
					if (i.getHasSubtypes()) {
						i.getItem().getSubItems(i.getItem(), null, items);
					} else {
						items.add(i);
					}
				}
				ingred = new PositionedStack(items, 21, 7);
				break;
			}
			for (RecipeComponent comp : recipe.getOutputs(null)) {
				int x = 111 + comp.slot.getRelativeX() * 18;
				int y = 7 + comp.slot.getRelativeY() * 18;
				results.add(new PositionedStack(comp.itemStack, x, y));
			}
			buckets.add(new PositionedStack(new ItemStack(Items.water_bucket), 57, 7));
			buckets.add(new PositionedStack(new ItemStack(Items.water_bucket), 57, 43));
		}

		@Override
		public PositionedStack getIngredient() {
			return ingred;
		}

		@Override
		public PositionedStack getResult() {
			return results.get(0);
		}

		@Override
		public List<PositionedStack> getOtherStacks() {
			ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
			for (int i = 1; i < results.size(); i++)
				stacks.add(results.get(i));
			stacks.add(afuels.get((cycleticks / 48) % afuels.size()).stack);
			stacks.addAll(buckets);
			return stacks;
		}
	}

	public static class FuelPair {
		public FuelPair(ItemStack ingred, int burnTime) {
			this.stack = new PositionedStack(ingred, 21, 43, false);
			this.burnTime = burnTime;
		}

		public PositionedStack stack;
		public int burnTime;
	}

	public static ArrayList<FuelPair> afuels;

	public static boolean checkInput(PolycraftRecipe recipe, ItemStack ingred) {
		for (RecipeInput inputs : recipe.getInputs())
			for (ItemStack input : inputs.inputs)
				if (ingred.getUnlocalizedName().equals(input.getUnlocalizedName()))
					return true;
		return false;
	}

	public static boolean checkOutput(PolycraftRecipe recipe, ItemStack result) {
		if (result == null)
			return true;
		for (RecipeComponent comp : recipe.getOutputs(null))
			if (result.getUnlocalizedName().equals(comp.itemStack.getUnlocalizedName()))
				return true;
		return false;
	}

	@Override
	public void drawExtras(int recipe) {
		// drawProgressBar(51, 25, 176, 0, 14, 14, 48, 7);
		// drawProgressBar(74, 23, 176, 14, 24, 16, 48, 0);
		drawProgressBar(21, 25, 176, 0, 14, 14, 48, 7); // Fire thingy
		drawProgressBar(84, 24, 176, 14, 24, 16, 48, 0); // Arrow thingy
	}

	@Override
	public void loadTransferRects() {
		// TODO: Make according fuel handler...
		transferRects.add(new RecipeTransferRect(new Rectangle(21, 25, 18, 18), "polycraftdcfuel"));
		transferRects.add(new RecipeTransferRect(new Rectangle(84, 24, 24, 18), "distillationcolumn"));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("item")) {
			for (Object res : results)
				if (res instanceof ItemStack)
					loadCraftingRecipes((ItemStack) res);
		} else if (outputId.equals("distillationcolumn")) {
			loadCraftingRecipes(null);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		Collection<PolycraftRecipe> recipes = PolycraftMod.recipeManagerRuntime
				.getRecipesByContainerType(PolycraftContainerType.DISTILLATION_COLUMN);
		for (PolycraftRecipe recipe : recipes) {
			if (checkOutput(recipe, result))
				arecipes.add(new DistColRecipe(recipe));
			// addPCraftingRecipe(recipe, res);
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		Collection<PolycraftRecipe> recipes = PolycraftMod.recipeManagerRuntime
				.getRecipesByContainerType(PolycraftContainerType.DISTILLATION_COLUMN);
		for (PolycraftRecipe recipe : recipes) {
			for (RecipeInput input : recipe.getInputs())
				if (checkInput(recipe, ingredient))
					arecipes.add(new DistColRecipe(recipe));
		}
	}

	@Override
	public String getGuiTexture() {
		// new
		// ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s.png",
		// PolycraftMod.getFileSafeName(config.name))));
		return PolycraftMod.getAssetName("textures/gui/container/distillation_column.png");
	}

	@Override
	public String getOverlayIdentifier() {
		return "distillationcolumn";
	}

	@Override
	public String getRecipeName() {
		// TODO Auto-generated method stub
		return "Distillation Column";
	}

	@Override
	public TemplateRecipeHandler newInstance() {
		if (afuels == null || afuels.isEmpty())
			findFuels();
		return super.newInstance();
	}

	private static void findFuels() {
		afuels = new ArrayList<FuelPair>();
		for (ItemStack item : ItemList.items) {
			// 20 ticks = 1 second, NEI burnTime is measured in ticks.
			if (Fuel.getHeatIntensity(item.getItem()) > 0) {
				int burnTime = (int) Fuel.getHeatDurationSeconds(item.getItem()) * 20;
				afuels.add(new FuelPair(item.copy(), burnTime));
			}
		}
	}
}