package edu.utd.minecraft.mod.polycraft.nei;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler.RecipeTransferRect;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.item.ItemMold;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MachiningMillRecipeHandler extends TemplateRecipeHandler {

	public class MachMillRecipe extends CachedRecipe {

		ArrayList<PositionedStack> ingreds = new ArrayList<PositionedStack>();
		PositionedStack result;
		PositionedStack bucket = new PositionedStack(new ItemStack(Items.water_bucket), 111, 79);
		int uses;

		public MachMillRecipe(PolycraftRecipe recipe) {
			for (RecipeInput input : recipe.getInputs()) {
				int x = 3 + input.slot.getRelativeX() * 18;
				int y = 7 + input.slot.getRelativeY() * 18;
				ingreds.add(new PositionedStack(input.inputs, x, y));
			}
			for (RecipeComponent comp : recipe.getOutputs(null)) {
				result = new PositionedStack(comp.itemStack, 147, 43);
				uses = comp.itemStack.getMaxDamage() / ItemMold.getDamagePerUse(comp.itemStack);
				break;
			}
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return ingreds;
		}

		@Override
		public PositionedStack getResult() {
			return result;
		}

		@Override
		public PositionedStack getOtherStack() {
			return bucket;
		}
	}

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
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("item")) {
			for (Object res : results)
				if (res instanceof ItemStack)
					loadCraftingRecipes((ItemStack) res);
		} else if (outputId.equals("machiningmill")) {
			loadCraftingRecipes(null);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		Collection<PolycraftRecipe> recipes = PolycraftMod.recipeManagerRuntime
				.getRecipesByContainerType(PolycraftContainerType.MACHINING_MILL);
		for (PolycraftRecipe recipe : recipes) {
			if (checkOutput(recipe, result))
				arecipes.add(new MachMillRecipe(recipe));
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		Collection<PolycraftRecipe> recipes = PolycraftMod.recipeManagerRuntime
				.getRecipesByContainerType(PolycraftContainerType.MACHINING_MILL);
		for (PolycraftRecipe recipe : recipes) {
			if (checkInput(recipe, ingredient))
				arecipes.add(new MachMillRecipe(recipe));
		}
	}
	
	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(97, 5, 46, 72), "machiningmill"));
	}

	@Override
	public String getGuiTexture() {
		// new
		// ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s.png",
		// PolycraftMod.getFileSafeName(config.name))));
		return PolycraftMod.getAssetName("textures/gui/container/machining_mill.png");
	}

	@Override
	public String getOverlayIdentifier() {
		return "machiningmill";
	}

	@Override
	public String getRecipeName() {
		return "Machining Mill";
	}

	/**
	 * Display the amount of mold/die usages that will be yielded for a
	 * particular type of material.
	 */
	@Override
	public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe) {
		MachMillRecipe mrecipe = (MachMillRecipe) arecipes.get(recipe);
		if (gui.isMouseOver(mrecipe.result, recipe))
			currenttip.add(mrecipe.uses + " uses");
		return currenttip;
	}

	/**
	 * Holy moly the 5x5 recipes are large...
	 */
	@Override
	public int recipiesPerPage() {
		return 1;
	}

	@Override
	public void drawBackground(int recipe) {
		GL11.glColor4f(1, 1, 1, 1);
		changeTexture(getGuiTexture());
		drawTexturedModalRect(0, 0, 5, 11, 166, 100);
	}
}