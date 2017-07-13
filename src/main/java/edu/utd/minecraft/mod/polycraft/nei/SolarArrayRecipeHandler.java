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

	private static final String HYDROGEN = "67"; // Item ID for Flask (Hydrogen)
	private static final String OXYGEN = "6e"; // Item ID for Flask (Oxygen)
	private static final String WATER_VIAL = "iP"; // Item ID for Vial
													// (Deionized Water)
	private static final String WATER_BEAKER = "od"; // Item ID for Beaker
														// (Deionized Water)
	private static final String WATER_DRUM = "tB"; // Item ID for Drum
													// (Deionized Water)

	public class SolArrayRecipe extends CachedRecipe {

		ArrayList<PositionedStack> ingreds = new ArrayList<PositionedStack>(3);
		PositionedStack oxygen;
		PositionedStack hydrogen;

		public SolArrayRecipe() {
			Item flask = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(HYDROGEN));
			hydrogen = new PositionedStack(new ItemStack(flask, 2), 111, 25);
			flask = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(OXYGEN));
			oxygen = new PositionedStack(new ItemStack(flask), 111, 7);
			Item water = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(WATER_VIAL));
			ingreds.add(new PositionedStack(new ItemStack(water), 12, 7));
			water = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(WATER_BEAKER));
			ingreds.add(new PositionedStack(new ItemStack(water), 30, 7));
			water = GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(WATER_DRUM));
			ingreds.add(new PositionedStack(new ItemStack(water), 48, 7));
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
					&& (vessel.config.gameID.equals(HYDROGEN) || vessel.config.gameID.equals(OXYGEN)))
				arecipes.add(new SolArrayRecipe());
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (ingredient.getItem() instanceof ItemVessel) {
			ItemVessel vessel = (ItemVessel) ingredient.getItem();
			if (vessel.config.name.endsWith("(Deionized Water)"))
				arecipes.add(new SolArrayRecipe());
		}
	}

	@Override
	public String getRecipeName() {
		return "Solar Array";
	}

	@Override
	public String getGuiTexture() {
		return PolycraftMod.getAssetName("textures/gui/container/solar_array.png");
	}

	@Override
	public String getOverlayIdentifier() {
		return "solararray";
	}

	private static String[] tips = { "1 processed every 10 seconds", "Scaled down to vials", "Scaled down to beakers" };

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
