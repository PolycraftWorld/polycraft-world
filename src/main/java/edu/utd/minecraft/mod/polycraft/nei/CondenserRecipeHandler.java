package edu.utd.minecraft.mod.polycraft.nei;

import java.util.ArrayList;
import java.util.List;

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

	public class CondenserRecipe extends CachedRecipe {

		ArrayList<PositionedStack> generated = new ArrayList<PositionedStack>(2);

		public CondenserRecipe() {
			for (ElementVessel vessel : ElementVessel.registry.values()) {
				if (vessel.vesselType == Vessel.Type.Flask && vessel.name.equals("Flask (Nitrogen)")) {
					Item flask = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(vessel.gameID));
					generated.add(new PositionedStack(new ItemStack(flask), 3, 9));
					break;
				}
			}
			for (CompoundVessel vessel : CompoundVessel.registry.values()) {
				if (vessel.vesselType == Vessel.Type.Vial && vessel.name.equals("Vial (Salt Water)")) {
					Item vial = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(vessel.gameID));
					generated.add(new PositionedStack(new ItemStack(vial), 21, 9));
					break;
				}
			}
		}

		@Override
		public PositionedStack getResult() {
			return null;
		}

		@Override
		public List<PositionedStack> getOtherStacks() {
			return generated;
		}
	}

	private static CondenserRecipe condenser = null;

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
			if (vessel.config.vesselType == Vessel.Type.Vial && vessel.config.name.equals("Vial (Salt Water)")
					|| vessel.config.vesselType == Vessel.Type.Flask && vessel.config.name.equals("Flask (Nitrogen)")) {
				if (condenser == null)
					condenser = new CondenserRecipe();
				arecipes.add(condenser);
			}
		}
	}

	@Override
	public String getGuiTexture() {
		// new
		// ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s.png",
		// PolycraftMod.getFileSafeName(config.name))));
		return PolycraftMod.getAssetName("textures/gui/container/condenser_nei.png");
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
		if (condenser != null) {
			currenttip.add("Generates 1 every 10 seconds");
			if (gui.isMouseOver(condenser.generated.get(0), recipe))
				currenttip.add("if condenser is not next to water");
			else if (gui.isMouseOver(condenser.generated.get(1), recipe))
				currenttip.add("if condenser is next to water");
		}
		return currenttip;
	}

}
