package edu.utd.minecraft.mod.polycraft.nei;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.Vessel;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TreeTapRecipeHandler extends TemplateRecipeHandler {

	public class TreeTapRecipe extends CachedRecipe {

		PositionedStack generated = null;

		public TreeTapRecipe() {
			// F*ckin' hell why isn't it named PolymerVessel or something like
			// that like the other children?
			for (PolymerPellets vessel : PolymerPellets.registry.values()) {
				if (vessel.vesselType == Vessel.Type.Bag && vessel.name.equals("Bag (PolyIsoPrene Pellets)")) {
					Item bag = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(vessel.gameID));
					generated = new PositionedStack(new ItemStack(bag), 39, 9);
					break;
				}
			}
		}

		@Override
		public PositionedStack getResult() {
			return generated;
		}
	}

	private static TreeTapRecipe treeTap = null;

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
			if (vessel.config.vesselType == Vessel.Type.Bag
					&& vessel.config.name.equals("Bag (PolyIsoPrene Pellets)")) {
				if (treeTap == null)
					treeTap = new TreeTapRecipe();
				arecipes.add(treeTap);
			}
		}
	}

	@Override
	public String getGuiTexture() {
		// new
		// ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s.png",
		// PolycraftMod.getFileSafeName(config.name))));
		return PolycraftMod.getAssetName("textures/gui/container/tree_tap.png");
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
		if (treeTap != null)
			if (gui.isMouseOver(treeTap.generated, recipe)) {
				currenttip.add("Generates 1 every 2 minutes");
				currenttip.add("(1 every minute on Jungle trees)");
			}
		return currenttip;
	}

	@Override
	public void drawBackground(int recipe) {
		GL11.glColor4f(1, 1, 1, 1);
		changeTexture(getGuiTexture());
		drawTexturedModalRect(0, 0, 5, 11, 166, 32);
	}
}
