package edu.utd.minecraft.mod.polycraft.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.ColoringPolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

/**
 * Handles all rigid/shaped Polycraft recipes and places them into NEI based on
 * Polycraft's recipe manager.
 * 
 * @author Cappycot
 *
 */
public class PolycraftShapedRecipeHandler extends TemplateRecipeHandler {

	public class CachedShapedRecipe extends CachedRecipe {
		public ArrayList<PositionedStack> ingredients;
		public PositionedStack result;

		public CachedShapedRecipe(int width, int height, Object[] items, ItemStack out) {
			result = new PositionedStack(out, 119, 24);
			ingredients = new ArrayList<PositionedStack>();
			setIngredients(width, height, items);
		}

		public CachedShapedRecipe(ShapedRecipes recipe) {
			this(recipe.recipeWidth, recipe.recipeHeight, recipe.recipeItems, recipe.getRecipeOutput());
		}

		/**
		 * @param width
		 * @param height
		 * @param items
		 *            an ItemStack[] or ItemStack[][]
		 */
		public void setIngredients(int width, int height, Object[] items) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (items[y * width + x] == null)
						continue;

					PositionedStack stack = new PositionedStack(items[y * width + x], 25 + x * 18, 6 + y * 18, false);
					// stack.setMaxSize(1);
					ingredients.add(stack);
				}
			}
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return getCycledIngredients(cycleticks / 20, ingredients);
		}

		public PositionedStack getResult() {
			return result;
		}

		public void computeVisuals() {
			for (PositionedStack p : ingredients)
				p.generatePermutations();
		}
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(84, 23, 24, 18), "polycraftcrafting"));
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiCrafting.class;
	}

	@Override
	public String getRecipeName() {
		return "Polycraft Crafting"; // +
										// NEIClientUtils.translate("recipe.shaped");
	}

	protected boolean dyeInIngreds(PolycraftRecipe recipe) {
		for (RecipeInput input : recipe.getInputs())
			for (ItemStack i : input.inputs)
				if (Item.getIdFromItem(Items.dye) == Item.getIdFromItem(i.getItem()))
					return true;
		return false;
	}

	/**
	 * Converts a PolycraftRecipe (shaped crafting) into a CachedShapedRecipe to
	 * add to info results.
	 * 
	 * @param recipe
	 * @param res
	 */
	private void addPCraftingRecipe(PolycraftRecipe recipe, ItemStack res) {
		if (recipe instanceof ColoringPolycraftRecipe && dyeInIngreds(recipe))
			return;
		if (res == null)
			for (RecipeComponent comp : recipe.getOutputs(null)) {
				res = comp.itemStack;
				break;
			}
		Object[] ins = new Object[9]; // Allows for arbitrary sizes of
										// ItemStack[] to be stored.
		for (RecipeInput input : recipe.getInputs()) {
			// ins[input.slot.getSlotIndex()] = input.inputs.toArray();
			// would cause some ClassCastException that is beyond my
			// comprehension.
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			for (ItemStack i : input.inputs) {
				if (i.getHasSubtypes()) {
					i.getItem().getSubItems(i.getItem(), null, items);
				} else {
					items.add(i);
				}
			}
			ins[input.slot.getSlotIndex()] = items.toArray(new ItemStack[items.size()]);
		}
		CachedShapedRecipe toadd = new CachedShapedRecipe(3, 3, ins, res);
		toadd.computeVisuals();
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
			if (!recipe.isShapedOnly())
				continue;
			ItemStack res = null;
			for (RecipeComponent comp : recipe.getOutputs(null)) {
				res = comp.itemStack;
			}
			if (result != null && !res.getUnlocalizedName().equals(result.getUnlocalizedName()))
				continue;
			else if (result != null && result.getHasSubtypes() && result.getItemDamage() != res.getItemDamage())
				continue;
			addPCraftingRecipe(recipe, res);
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		Collection<PolycraftRecipe> recipes = PolycraftMod.recipeManagerRuntime
				.getRecipesByContainerType(PolycraftContainerType.CRAFTING_TABLE);
		for (PolycraftRecipe recipe : recipes) {
			if (!recipe.isShapedOnly())
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
	public String getGuiTexture() {
		return "textures/gui/container/crafting_table.png";
	}

	@Override
	public String getOverlayIdentifier() {
		return "polycraftcrafting";
	}

	public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
		return super.hasOverlay(gui, container, recipe)
				|| isRecipe2x2(recipe) && RecipeInfo.hasDefaultOverlay(gui, "crafting2x2");
	}

	@Override
	public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer gui, int recipe) {
		IRecipeOverlayRenderer renderer = super.getOverlayRenderer(gui, recipe);
		if (renderer != null)
			return renderer;

		IStackPositioner positioner = RecipeInfo.getStackPositioner(gui, "crafting2x2");
		if (positioner == null)
			return null;
		return new DefaultOverlayRenderer(getIngredientStacks(recipe), positioner);
	}

	@Override
	public IOverlayHandler getOverlayHandler(GuiContainer gui, int recipe) {
		IOverlayHandler handler = super.getOverlayHandler(gui, recipe);
		if (handler != null)
			return handler;

		return RecipeInfo.getOverlayHandler(gui, "crafting2x2");
	}

	public boolean isRecipe2x2(int recipe) {
		for (PositionedStack stack : getIngredientStacks(recipe))
			if (stack.relx > 43 || stack.rely > 24)
				return false;

		return true;
	}

}
