package edu.utd.minecraft.mod.polycraft.nei;

import static codechicken.nei.NEIClientUtils.translate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry; // lol where tf does this one come from?

import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class PolycraftIOFuelRecipeHandler extends IndustrialOvenRecipeHandler {

	public class CachedFuelRecipe extends CachedRecipe {
		public FuelPair fuel;

		public CachedFuelRecipe(FuelPair fuel) {
			this.fuel = fuel;
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return mfurnace.get(cycleticks / 48 % mfurnace.size()).getIngredients();
		}

		@Override
		public PositionedStack getResult() {
			return mfurnace.get(cycleticks / 48 % mfurnace.size()).result;
		}

		@Override
		public List<PositionedStack> getOtherStacks() {
			ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
			stacks.add(fuel.stack);
			stacks.add(mfurnace.get(cycleticks / 48 % mfurnace.size()).bucket);
			stacks.addAll(mfurnace.get(cycleticks / 48 % mfurnace.size()).others);
			return stacks;
		}
		
		public PositionedStack getBucket() {
			return mfurnace.get(cycleticks / 48 % mfurnace.size()).bucket;
		}

		public List<PositionedStack> getExternalStacks() {
			ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
			stacks.addAll(mfurnace.get(cycleticks / 48 % mfurnace.size()).others);
			return stacks;
		}
	}

	private ArrayList<SmeltingPair> mfurnace = new ArrayList<IndustrialOvenRecipeHandler.SmeltingPair>();

	public PolycraftIOFuelRecipeHandler() {
		super();
		loadAllSmelting();
	}

	public String getRecipeName() {
		return "Industrial Oven " + NEIClientUtils.translate("recipe.fuel");
	}

	private void loadAllSmelting() {
		Map<ItemStack, ItemStack> recipes = (Map<ItemStack, ItemStack>) FurnaceRecipes.smelting().getSmeltingList();
		for (Entry<ItemStack, ItemStack> recipe : recipes.entrySet())
			mfurnace.add(new SmeltingPair(recipe.getKey(), recipe.getValue()));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("polycraftiofuel") && getClass() == PolycraftIOFuelRecipeHandler.class)
			for (FuelPair fuel : afuels)
				arecipes.add(new CachedFuelRecipe(fuel));
	}

	public void loadUsageRecipes(ItemStack ingredient) {
		for (FuelPair fuel : afuels)
			if (fuel.stack.contains(ingredient)) {
				arecipes.add(new CachedFuelRecipe(fuel));
			}
	}

	public String getOverlayIdentifier() {
		return "fuel";
	}

	@Override
	public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe) {
		CachedFuelRecipe crecipe = (CachedFuelRecipe) arecipes.get(recipe);
		FuelPair fuel = crecipe.fuel;
		float burnTime = fuel.burnTime / 300F; // changed from 200F because
												// Industrial Oven takes 1.5x
												// time

		if (gui.isMouseOver(fuel.stack, recipe) && burnTime < 1) {
			burnTime = 1F / burnTime;
			String s_time = Float.toString(burnTime);
			if (burnTime == Math.round(burnTime))
				s_time = Integer.toString((int) burnTime);
			currenttip.add(translate("recipe.fuel.required", s_time));
		} else if (burnTime > 1) {
			boolean overprocess = false;
			boolean overproduce = false;
			ArrayList<PositionedStack> produced = new ArrayList<PositionedStack>();
			produced.add(crecipe.getResult());
			produced.addAll(crecipe.getExternalStacks());

			for (PositionedStack ps : crecipe.getIngredients()) {
				overprocess = gui.isMouseOver(ps, recipe);
				if (overprocess)
					break;
			}
			if (!overprocess)
				for (PositionedStack ps : produced) {
					overproduce = gui.isMouseOver(ps, recipe);
					if (overproduce)
						break;
				}

			if (overprocess || overproduce) {
				if (overproduce)
					burnTime *= crecipe.getResult().item.stackSize;
				String s_time = Float.toString(burnTime);
				if (burnTime == Math.round(burnTime))
					s_time = Integer.toString((int) burnTime);

				currenttip.add(translate("recipe.fuel." + (overproduce ? "produced" : "processed"), s_time));
			}
		} else if (gui.isMouseOver(crecipe.getBucket(), recipe)) {
			currenttip.add("Required catalyst");
		}

		return currenttip;
	}

}
