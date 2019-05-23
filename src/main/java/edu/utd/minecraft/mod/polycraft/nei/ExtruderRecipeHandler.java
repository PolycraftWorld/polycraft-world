package edu.utd.minecraft.mod.polycraft.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.nei.ItemList;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler.RecipeTransferRect;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.item.ItemMold;
import edu.utd.minecraft.mod.polycraft.nei.InjectionMolderRecipeHandler.InjectMoldRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ExtruderRecipeHandler extends TemplateRecipeHandler {

	public class ExtruderRecipe extends CachedRecipe {

		PositionedStack ingred;
		PositionedStack mold;
		PositionedStack result;
		PositionedStack bucket = new PositionedStack(new ItemStack(Items.water_bucket), 105, 44);

		public ExtruderRecipe(PolycraftRecipe recipe) {
			for (RecipeInput input : recipe.getInputs()) {
				for (ItemStack item : input.inputs) {
					if (item.getItem() instanceof ItemMold)
						mold = new PositionedStack(item, 85, 44);
					else
						ingred = new PositionedStack(item, 3, 7);
				}
			}
			for (RecipeComponent res : recipe.getOutputs(null)) {
				result = new PositionedStack(res.itemStack, 147, 44);
				break;
			}
		}

		@Override
		public PositionedStack getIngredient() {
			return ingred;
		}

		@Override
		public PositionedStack getResult() {
			return result;
		}

		@Override
		public List<PositionedStack> getOtherStacks() {
			ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>(2);
			stacks.add(afuels.get((cycleticks / 48) % afuels.size()).stack);
			stacks.add(mold);
			stacks.add(bucket);
			return stacks;
		}
	}

	public static class FuelPair {
		public FuelPair(ItemStack ingred, int burnTime) {
			this.stack = new PositionedStack(ingred, 39, 80, false);
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
		drawProgressBar(40, 61, 176, 0, 14, 14, 48, 7); // Fire thingy
		drawProgressBar(126, 44, 176, 14, 24, 16, 48, 0); // Arrow thingy
	}
	
	@Override
	public void loadTransferRects() {
		// TODO: Change "polycraftiofuel" to "polycraftcmpfuel" and make
		// according fuel handler...
		transferRects.add(new RecipeTransferRect(new Rectangle(40, 61, 18, 18), "polycraftimfuel"));
		transferRects.add(new RecipeTransferRect(new Rectangle(126, 44, 20, 18), "extruder"));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("item")) {
			for (Object res : results)
				if (res instanceof ItemStack)
					loadCraftingRecipes((ItemStack) res);
		} else if (outputId.equals("extruder")) {
			loadCraftingRecipes(null);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		Collection<PolycraftRecipe> recipes = PolycraftMod.recipeManagerRuntime
				.getRecipesByContainerType(PolycraftContainerType.EXTRUDER);
		for (PolycraftRecipe recipe : recipes) {
			if (checkOutput(recipe, result))
				arecipes.add(new ExtruderRecipe(recipe));
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		Collection<PolycraftRecipe> recipes = PolycraftMod.recipeManagerRuntime
				.getRecipesByContainerType(PolycraftContainerType.EXTRUDER);
		for (PolycraftRecipe recipe : recipes) {
			if (checkInput(recipe, ingredient))
				arecipes.add(new ExtruderRecipe(recipe));
		}
	}

	@Override
	public String getGuiTexture() {
		// new
		// ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s.png",
		// PolycraftMod.getFileSafeName(config.name))));
		return PolycraftMod.getAssetNameString("textures/gui/container/extruder.png");
	}

	@Override
	public String getOverlayIdentifier() {
		return "extruder";
	}

	@Override
	public String getRecipeName() {
		return "Extruder";
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

	/**
	 * Also large page like the machining mill...
	 */
	@Override
	public int recipiesPerPage() {
		return 1;
	}

//	@Override
//	public void drawBackground(int recipe) {
//		GL11.glColor4f(1, 1, 1, 1);
//		changeTexture(getGuiTexture());
//		drawTexturedModalRect(0, 0, 5, 11, 166, 100);
//	}
}
