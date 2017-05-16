package edu.utd.minecraft.mod.polycraft.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;

/**
 * Creates and registers handlers for Not Enough Items so that you don't have to
 * browse the gosh darn wiki every few minutes to make Polypropylene...
 * 
 * @author Cappycot
 */
public class NEIPolycraftConfig implements IConfigureNEI {

	@Override
	public String getName() {
		return "Cappycot's Polycraft Plugin";
	}

	@Override
	public String getVersion() {
		return PolycraftMod.VERSION;
	}

	/**
	 * Largest drawback we have on NEI integration is that we can't get a direct
	 * overlay on Polycraft Inventories because all the guis of the inventories
	 * uses the same class {@link PolycraftInventoryGui}
	 * 
	 * <br>
	 * Precedence List:
	 * <ol>
	 * <li>Levels 1-8 of Polycraft Machinery</li>
	 * <li>Crafting Table Recipes</li>
	 * <li>Industrial Oven/Smelting</li>
	 * <li>All fuel GUIs</li>
	 * </ol>
	 */
	@Override
	public void loadConfig() {
		API.registerRecipeHandler(new ChemicalProcessorRecipeHandler()); // "chemicalprocessor"
		API.registerUsageHandler(new ChemicalProcessorRecipeHandler());

		API.registerRecipeHandler(new PolycraftShapedRecipeHandler()); // "polycraftcrafting"
		API.registerUsageHandler(new PolycraftShapedRecipeHandler());

		API.registerRecipeHandler(new PolycraftShapelessRecipeHandler());
		API.registerUsageHandler(new PolycraftShapelessRecipeHandler());

		API.registerRecipeHandler(new PolycraftColoringRecipeHandler());
		API.registerUsageHandler(new PolycraftColoringRecipeHandler());

		API.registerRecipeHandler(new IndustrialOvenRecipeHandler()); // "industrialoven"
		API.registerUsageHandler(new IndustrialOvenRecipeHandler());

		API.registerRecipeHandler(new PolycraftIOFuelRecipeHandler()); // "fuel"
		API.registerUsageHandler(new PolycraftIOFuelRecipeHandler());
		// Named such because Industrial Oven is near-identical to smelting.
	}

}
