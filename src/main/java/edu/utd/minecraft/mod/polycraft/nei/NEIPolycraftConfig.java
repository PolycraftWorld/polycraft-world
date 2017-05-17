package edu.utd.minecraft.mod.polycraft.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;

/**
 * Creates and registers handlers for Not Enough Items so that you don't have to
 * browse the gosh darn wiki every few minutes to make polypropylene...
 * 
 * @author Cappycot
 */
public class NEIPolycraftConfig implements IConfigureNEI {

	/**
	 * lol
	 */
	@Override
	public String getName() {
		return "Cappycot's Polycraft Plugin";
	}

	/**
	 * lol
	 */
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
	 * <li>Production: Tree Tap, Condenser, Oil Derrick</li>
	 * <li>Industrial Oven</li>
	 * <li>Levels 1-8 of Polycraft Machinery</li>
	 * <li>Crafting Table Recipes</li>
	 * <li>Mask Writer and Contact Printer</li>
	 * <li>All fuel GUIs</li>
	 * </ol>
	 */
	@Override
	public void loadConfig() {
		API.registerRecipeHandler(new TreeTapRecipeHandler()); // "treetap"
		// API.registerUsageHandler(new TreeTapRecipeHandler());

		API.registerRecipeHandler(new CondenserRecipeHandler()); // "condenser"
		// API.registerUsageHandler(new CondenserRecipeHandler());

		API.registerRecipeHandler(new OilDerrickRecipeHandler()); // "oilderrick"
		// API.registerUsageHandler(new OilDerrickRecipeHandler());
		
		API.registerRecipeHandler(new MachiningMillRecipeHandler()); // "machiningmill"
		API.registerUsageHandler(new MachiningMillRecipeHandler());
		
		API.registerRecipeHandler(new InjectionMolderRecipeHandler()); // "injectionmolder"
		API.registerUsageHandler(new InjectionMolderRecipeHandler());

		API.registerRecipeHandler(new IndustrialOvenRecipeHandler()); // "industrialoven"
		API.registerUsageHandler(new IndustrialOvenRecipeHandler());

		API.registerRecipeHandler(new DistillationColumnRecipeHandler()); // "distillationcolumn"
		API.registerUsageHandler(new DistillationColumnRecipeHandler());

		API.registerRecipeHandler(new ChemicalProcessorRecipeHandler()); // "chemicalprocessor"
		API.registerUsageHandler(new ChemicalProcessorRecipeHandler());

		API.registerRecipeHandler(new PolycraftShapedRecipeHandler()); // "polycraftcrafting"
		API.registerUsageHandler(new PolycraftShapedRecipeHandler());

		API.registerRecipeHandler(new PolycraftShapelessRecipeHandler());
		API.registerUsageHandler(new PolycraftShapelessRecipeHandler());

		API.registerRecipeHandler(new PolycraftColoringRecipeHandler());
		API.registerUsageHandler(new PolycraftColoringRecipeHandler());

		API.registerRecipeHandler(new PolycraftIOFuelRecipeHandler()); // "fuel"
		API.registerUsageHandler(new PolycraftIOFuelRecipeHandler());
	}

}
