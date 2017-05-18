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

public class SolarArrayRecipeHandler extends TemplateRecipeHandler {

	public class SolArrayRecipe extends CachedRecipe {

		ArrayList<PositionedStack> ingreds = new ArrayList<PositionedStack>(3);
		PositionedStack oxygen;
		PositionedStack hydrogen;

		public SolArrayRecipe() {
			for (CompoundVessel vessel : CompoundVessel.registry.values()) {
				if (vessel.name.endsWith("(Deionized Water)")) {
					Item water = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(vessel.gameID));
					switch (vessel.vesselType) {
					case Vial:
						ingreds.add(0, new PositionedStack(new ItemStack(water), 12, 7));
						break;
					case Beaker:
						ingreds.add(1, new PositionedStack(new ItemStack(water), 30, 7));
						break;
					default:
						ingreds.add(2, new PositionedStack(new ItemStack(water), 48, 7));
						break;
					}
				}
			}
			// ingred = new PositionedStack(waters, 12, 7);
			for (ElementVessel vessel : ElementVessel.registry.values()) {
				if (vessel.vesselType == Vessel.Type.Flask) {
					if (vessel.name.equals("Flask (Oxygen)")) {
						Item flask = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(vessel.gameID));
						oxygen = new PositionedStack(new ItemStack(flask), 111, 7);
					} else if (vessel.name.equals("Flask (Hydrogen)")) {
						Item flask = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(vessel.gameID));
						hydrogen = new PositionedStack(new ItemStack(flask, 2), 111, 25);
					}
				}
			}

		}

		@Override
		public List<PositionedStack> getIngredients() {
			return ingreds;
		}

		@Override
		public PositionedStack getResult() {
			return oxygen;
		}

		@Override
		public PositionedStack getOtherStack() {
			return hydrogen;
		}
	}

	private static SolArrayRecipe solarArray = null;

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("item"))
			for (Object res : results)
				if (res instanceof ItemStack)
					loadCraftingRecipes((ItemStack) res);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		if (result.getItem() instanceof ItemVessel) {
			ItemVessel vessel = (ItemVessel) result.getItem();
			if (vessel.config.vesselType == Vessel.Type.Flask
					&& (vessel.config.name.equals("Flask (Hydrogen)") || vessel.config.name.equals("Flask (Oxygen)"))) {
				if (solarArray == null)
					solarArray = new SolArrayRecipe();
				arecipes.add(solarArray);
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (ingredient.getItem() instanceof ItemVessel) {
			ItemVessel vessel = (ItemVessel) ingredient.getItem();
			if (vessel.config.name.endsWith("(Deionized Water)")) {
				if (solarArray == null)
					solarArray = new SolArrayRecipe();
				arecipes.add(solarArray);
			}
		}
	}

	@Override
	public String getRecipeName() {
		return "Solar Array";
	}

	@Override
	public String getGuiTexture() {
		// new
		// ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s.png",
		// PolycraftMod.getFileSafeName(config.name))));
		return PolycraftMod.getAssetName("textures/gui/container/solar_array.png");
	}

	@Override
	public String getOverlayIdentifier() {
		return "solararray";
	}
	
	private static String[] tips = {"1 processed every 10 seconds", "Scaled down to vials", "Scaled down to beakers"};

	@Override
	public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe) {
		SolArrayRecipe crecipe = (SolArrayRecipe) arecipes.get(recipe);
		if (crecipe != null) {
			if (gui.isMouseOver(crecipe.hydrogen, recipe)) {
				currenttip.add("2 produced every 10 seconds");
				currenttip.add("Requires daylight");
			} else if (gui.isMouseOver(crecipe.oxygen, recipe)) {
				currenttip.add("1 produced every 10 seconds");
				currenttip.add("Requires daylight");
			} else {
				for (int i = 0; i < 3; i++) {
					if (gui.isMouseOver(crecipe.ingreds.get(i), recipe)) {
						currenttip.add(tips[i]);
						currenttip.add("Requires daylight");
						break;
					}
				}
			}
		}
		return currenttip;
	}

}
