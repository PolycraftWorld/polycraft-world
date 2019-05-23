package edu.utd.minecraft.mod.polycraft.nei;

//import static codechicken.lib.gui.GuiDraw.changeTexture;
//import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraftforge.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.Vessel;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TreeTapRecipeHandler extends TemplateRecipeHandler {

	private static final String RUBBER = "Al"; // Item ID for Bag (PolyIsoPrene
												// Pellets)

	public class TreeTapRecipe extends CachedRecipe {

		PositionedStack generated = null;

		public TreeTapRecipe() {
			Item bag = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(RUBBER));
			generated = new PositionedStack(new ItemStack(bag), 39, 9);
		}

		@Override
		public PositionedStack getResult() {
			return generated;
		}
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("item")) {
			for (Object res : results)
				if (res instanceof ItemStack)
					loadCraftingRecipes((ItemStack) res);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		if (result.getItem() instanceof ItemVessel) {
			ItemVessel vessel = (ItemVessel) result.getItem();
			if (vessel.config.gameID.equals(RUBBER))
				arecipes.add(new TreeTapRecipe());
		}
	}

	@Override
	public String getGuiTexture() {
		return PolycraftMod.getAssetNameString("textures/gui/container/tree_tap.png");
	}

	@Override
	public String getOverlayIdentifier() {
		return "treetap";
	}

	@Override
	public String getRecipeName() {
		return "Tree Tap";
	}

	@Override
	public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe) {
		TreeTapRecipe trecipe = (TreeTapRecipe) arecipes.get(0);
		if (trecipe != null)
			if (gui.isMouseOver(trecipe.generated, recipe)) {
				currenttip.add("Generates 1 every 2 minutes");
				currenttip.add("(1 every minute on Jungle trees)");
			}
		return currenttip;
	}

//	@Override
//	public void drawBackground(int recipe) {
//		GL11.glColor4f(1, 1, 1, 1);
//		changeTexture(getGuiTexture());
//		drawTexturedModalRect(0, 0, 5, 11, 166, 32);
//	}
}
