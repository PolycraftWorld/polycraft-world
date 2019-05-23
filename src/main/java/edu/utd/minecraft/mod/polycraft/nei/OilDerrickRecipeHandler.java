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
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.Vessel;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class OilDerrickRecipeHandler extends TemplateRecipeHandler {

	private static final String OIL = "qg"; // Item ID for Drum (Crude Oil)

	public class OilDerrickRecipe extends CachedRecipe {

		PositionedStack generated = null;

		public OilDerrickRecipe() {
			Item drum = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(OIL));
			generated = new PositionedStack(new ItemStack(drum), 39, 9);
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
			if (vessel.config.gameID.equals(OIL))
				arecipes.add(new OilDerrickRecipe());
		}
	}

	@Override
	public String getGuiTexture() {
		return PolycraftMod.getAssetNameString("textures/gui/container/oil_derrick.png");
	}

	@Override
	public String getOverlayIdentifier() {
		return "oilderrick";
	}

	@Override
	public String getRecipeName() {
		return "Oil Derrick";
	}

	@Override
	public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe) {
		OilDerrickRecipe orecipe = (OilDerrickRecipe) arecipes.get(recipe);
		if (orecipe != null)
			if (gui.isMouseOver(orecipe.generated, recipe)) {
				currenttip.add("Generates 1 every 30 seconds");
				currenttip.add("(placed on an OilField block)");
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
