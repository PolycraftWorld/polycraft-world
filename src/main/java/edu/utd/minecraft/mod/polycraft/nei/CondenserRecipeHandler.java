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
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.ElementVessel;
import edu.utd.minecraft.mod.polycraft.config.Vessel;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CondenserRecipeHandler extends TemplateRecipeHandler {

	private static final String NITROGEN = "6d"; // Item ID for Flask (Nitrogen)
	private static final String SALT_WATER = "11P"; // Item ID for Vial (Salt
													// Water)

	public class CondenserRecipe extends CachedRecipe {
		String over = "Generates 1 every 10 seconds";
		String over2;
		PositionedStack generated;

		public CondenserRecipe(boolean water) {
			if (!water) {
				over2 = "if condenser is not next to water";
				Item flask = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(NITROGEN));
				generated = new PositionedStack(new ItemStack(flask), 3, 9);
			} else {
				over2 = "if condenser is next to water";
				Item vial = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(SALT_WATER));
				generated = new PositionedStack(new ItemStack(vial), 3, 9);
			}
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
			if (vessel.config.gameID.equals(NITROGEN) || vessel.config.gameID.equals(SALT_WATER)) {
				arecipes.add(new CondenserRecipe(false));
				arecipes.add(new CondenserRecipe(true));
			}
		}
	}

	@Override
	public String getGuiTexture() {
		return PolycraftMod.getAssetName("textures/gui/container/condenser.png");
	}

	@Override
	public String getOverlayIdentifier() {
		return "condenser";
	}

	@Override
	public String getRecipeName() {
		return "Condenser";
	}

	@Override
	public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe) {
		CondenserRecipe crecipe = (CondenserRecipe) arecipes.get(recipe);
		if (crecipe != null)
			if (gui.isMouseOver(crecipe.generated, recipe)) {
				currenttip.add(crecipe.over);
				currenttip.add(crecipe.over2);
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
