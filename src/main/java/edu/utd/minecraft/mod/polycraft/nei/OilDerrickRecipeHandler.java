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
import edu.utd.minecraft.mod.polycraft.config.Vessel;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class OilDerrickRecipeHandler extends TemplateRecipeHandler {

	public class OilDerrickRecipe extends CachedRecipe {

		PositionedStack generated = null;

		public OilDerrickRecipe() {
			for (CompoundVessel vessel : CompoundVessel.registry.values()) {
				if (vessel.vesselType == Vessel.Type.Drum && vessel.name.equals("Drum (Crude Oil)")) {
					Item drum = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(vessel.gameID));
					generated = new PositionedStack(new ItemStack(drum), 39, 9);
					break;
				}
			}
		}

		@Override
		public PositionedStack getResult() {
			return generated;
		}
	}

	private static OilDerrickRecipe oilDerrick = null;

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
			if (vessel.config.vesselType == Vessel.Type.Drum && vessel.config.name.equals("Drum (Crude Oil)")) {
				if (oilDerrick == null)
					oilDerrick = new OilDerrickRecipe();
				arecipes.add(oilDerrick);
			}
		}
	}

	@Override
	public String getGuiTexture() {
		// new
		// ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s.png",
		// PolycraftMod.getFileSafeName(config.name))));
		return PolycraftMod.getAssetName("textures/gui/container/oil_derrick.png");
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
		if (oilDerrick != null)
			if (gui.isMouseOver(oilDerrick.generated, recipe)) {
				currenttip.add("Generates 1 every 30 seconds");
				currenttip.add("(placed on an OilField block)");
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
