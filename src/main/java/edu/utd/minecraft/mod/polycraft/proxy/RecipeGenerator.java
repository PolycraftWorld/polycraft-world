package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;
import edu.utd.minecraft.mod.polycraft.config.Compound;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.Element;
import edu.utd.minecraft.mod.polycraft.config.Entity;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.Polymer;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.item.ItemFluidContainer;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;

public class RecipeGenerator {
	private static final Logger logger = LogManager.getLogger();
	
	/**
	 * TODO: Actually generate recipes.
	 */
	public void generateRecipes() {
		createCustomItemRecipes();
		createOreRecipes();
		createCompressedBlockRecipes();
		createPolymerRecipes();
		createChemicalProcessorRecipes();
		createCheatRecipes();		
	}

	private ItemStack createItemStack(final Entity entity) {
		return createItemStack(entity, 1);
	}

	private ItemStack createItemStack(final Entity entity, int size) {
		Item item = null;
		if ((entity instanceof Element && ((Element) entity).fluid) ||
				(entity instanceof Compound && ((Compound) entity).fluid)) {
			item = PolycraftMod.items.get(ItemFluidContainer.getGameName(entity));
			if (item == null) {
				throw new IllegalArgumentException("No fluid container item for " + entity.name);
			}
			return new ItemStack(item, size);
		} else if (entity instanceof Ingot || entity instanceof Catalyst) {
			item = PolycraftMod.items.get(entity.gameName);
			if (item == null) {
				throw new IllegalArgumentException("No ingot/catalyst item for " + entity.name);
			}
			return new ItemStack(item, size);
		}
		
		Block block = PolycraftMod.blocks.get(entity.gameName);
		if (block == null) {
			throw new IllegalArgumentException("Can't create an item stack: No block exists for " + entity.name);
		}
		return new ItemStack(block, size);
	}

	private void createCustomItemRecipes() {
		/*
		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemKevlarVest),
				new String [] { "xxx" },
				ImmutableMap.of('x', new ItemStack(PolycraftMod.itemScubaMask, 1)));
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemKevlarVest),
				ImmutableList.of(new ItemStack(PolycraftMod.itemScubaMask, 1), new ItemStack(PolycraftMod.itemScubaMask, 1)));
*/

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemFluidContainerNozzle),
				new String [] { "xxx", " x ", " x " },
				ImmutableMap.of('x', createItemStack(Ingot.copper)));
		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemFluidContainer),
				new String [] { " y ", "x x", " x " },
				ImmutableMap.of(
						'x', createItemStack(Ingot.steel),
						'y', new ItemStack(PolycraftMod.itemFluidContainerNozzle)));

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemKevlarVest),
				new String [] { "x x", "xxx", "xxx" },
				ImmutableMap.of('x', new ItemStack(PolycraftMod.items.get(Polymer.kevlar.itemNameFiber), 4)));

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemRunningShoes),
				new String [] { "   ", "x x", "x x" },
				ImmutableMap.of('x', new ItemStack(PolycraftMod.items.get(Polymer.LDPE.itemNameFiber), 2)));

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemJetPack),
				new String [] { "xzx", "yxy", "xzx", },
				ImmutableMap.of(
						'x', new ItemStack(PolycraftMod.items.get(Polymer.HDPE.itemNameFiber), 8),
						'y', createItemStack(Element.hydrogen, 2),
						'z', createItemStack(Ingot.aluminium, 8)));
		
		// allow refilling tanks
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(new ItemStack(PolycraftMod.itemJetPack), createItemStack(Element.hydrogen, 4)),
				ImmutableList.of(new ItemStack(PolycraftMod.itemJetPack)));

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemParachute),
				new String [] { "xxx", "x x", " x " },
				ImmutableMap.of('x', new ItemStack(PolycraftMod.items.get(Polymer.nylon11.itemNameFiber), 8)));

 		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemParachute),
				new String [] {  "xxx", "x x", " x " },
				ImmutableMap.of(
						'x', new ItemStack(PolycraftMod.items.get(Polymer.nylon11.itemNameFiber), 8)));
/* TODO(jim-mcandrew): Fix ?
 		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemParachute),
				new String [] { "xyx", "xzx", "xxx" },
				ImmutableMap.of(
						'x', new ItemStack(PolycraftMod.items.get(Polymer.nylon11.itemNameFiber), 8)));
*/
 		
 /* TODO: Fix this recipe
		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,new ItemStack(PolycraftMod.itemScubaTank),
				new String [] { "xzx", "yxy", "x x" },
				ImmutableMap.of(
					'x', new ItemStack(PolycraftMod.items.get(Polymer.HDPE.itemNameFiber), 8),
					'y', createItemStack(Element.oxygen)));
		
*/

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,new ItemStack(PolycraftMod.itemFlashlight),
				new String [] { "xxx", "xyx", "xxx" },
				ImmutableMap.of(
					'x', new ItemStack(PolycraftMod.items.get(Polymer.LDPE.itemNamePellet)),
					'y', new ItemStack(Blocks.glass_pane),
					'z', new ItemStack(Blocks.redstone_lamp)));

		// allow refilling tanks
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(new ItemStack(PolycraftMod.itemScubaTank), createItemStack(Element.oxygen, 2)),
				ImmutableList.of(new ItemStack(PolycraftMod.itemScubaTank)));

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemScubaFins),
				new String [] { "x x", "x x", "x x" },
				ImmutableMap.of('x', new ItemStack(PolycraftMod.items.get(Polymer.LDPE.itemNameFiber))));

		for (final Polymer polymer : ItemGripped.allowedPolymers) {
			final Item polymerGrip = PolycraftMod.items.get(polymer.gameName + "_grip");
			PolycraftMod.recipeManager.addShapedRecipe(
					PolycraftContainerType.CRAFTING_TABLE,
					new ItemStack(polymerGrip),
					new String [] { "x x", "x x", "xxx" },
					ImmutableMap.of('x', new ItemStack(PolycraftMod.items.get(polymer.itemNamePellet))));
			
			for (final Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet()) {
				final String materialName = materialEntry.getKey();
				final ToolMaterial material = materialEntry.getValue();
				for (final String type : ItemGripped.allowedTypes.keySet()) {
					Item grippedItem = (Item)Item.itemRegistry.getObject(ItemGripped.getNameBase(materialName, type));
					PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
							new ItemStack(PolycraftMod.items.get(ItemGripped.getName(polymer, materialName, type))),
							ImmutableList.of(
									new ItemStack(grippedItem),
									new ItemStack(polymerGrip)));
				}
			}
		}
	}

	private void createOreRecipes() {
		for (final Ore ore : Ore.registry.values()) {
			if (ore.smeltingEntity != null) {
				try {
					PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.FURNANCE,
							new ItemStack(Item.getItemFromBlock(PolycraftMod.blocks.get(ore.gameName))),
							ImmutableList.of(createItemStack(ore.smeltingEntity, ore.smeltingEntitiesPerBlock)),
							(double)ore.smeltingExperience);
				} catch(IllegalArgumentException illegalEx) {
					// TODO: Don't catch this error; it should blow up once all the recipes are in.
					logger.error("Couldn't create smelting recipe: " + illegalEx.getMessage());
				}
			}
		}
	}

	private void createCompressedBlockRecipes() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values()) {
			final ItemStack blockCompressed = createItemStack(compressedBlock);
			final Item compressedItem = PolycraftMod.items.get(compressedBlock.type.gameName);
			
			final ItemStack [] compressedItems = new ItemStack[compressedBlock.itemsPerBlock];
			for (int i = 0; i < compressedItems.length; i++) {
				compressedItems[i] = new ItemStack(compressedItem);
			}
			
			PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
					blockCompressed, ImmutableList.copyOf(compressedItems));
			PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
					new ItemStack(compressedItem, compressedBlock.itemsPerBlock),
					ImmutableList.of(blockCompressed));
		}
	}

	private void createPolymerRecipes() {
		for (final Polymer polymer : Polymer.registry.values()) {
			final Block polymerBlock = PolycraftMod.blocks.get(polymer.gameName);
			final Item polymerPellet = PolycraftMod.items.get(polymer.itemNamePellet);
			final Item polymerFiber = PolycraftMod.items.get(polymer.itemNameFiber);
			try {
				// convert between blocks and pellets
				PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
						new ItemStack(polymerPellet, polymer.craftingPelletsPerBlock),
						ImmutableList.of(new ItemStack(polymerBlock)));
				PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
						new ItemStack(polymerBlock),
						ImmutableList.of(new ItemStack(polymerPellet, polymer.craftingPelletsPerBlock)));
				
				// convert between pellets and fibers
				PolycraftMod.recipeManager.addShapedRecipe(PolycraftContainerType.CRAFTING_TABLE,
						new ItemStack(polymerFiber),
						new String [] {  "x  ", " x ", "  x" },
						ImmutableMap.of('x', new ItemStack(polymerPellet)));
				PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
						new ItemStack(polymerPellet, 3),
						ImmutableList.of(new ItemStack(polymerFiber)));
			} catch(Exception ex) {
				// TODO: Fix these recipes - should not ignore this exception!  Most of these
				// polymer blocks are null and cause NullPointerException
				logger.error("Error creating polymer " + polymer + ": ", ex.getMessage());
			}
		}
	}

	private void createChemicalProcessorRecipes() {
		PolycraftMod.recipeManager.addShapedRecipe(PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.blockChemicalProcessor),
				new String [] { "xxx", "xzx", "xyx" },
				ImmutableMap.of(
						'x', createItemStack(Ingot.steel),
						'y', new ItemStack(Blocks.furnace),
						'z', new ItemStack(Blocks.glass_pane)));
		
		createAlloyRecipes();
		createCatalystRecipes();
		createMineralRecipes();
		createElementRecipes();
		createCompoundRecipes();
	}

	private void createAlloyRecipes() {
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(new ItemStack(Blocks.iron_block, 9), createItemStack(Ingot.carbon)),
				ImmutableList.of(createItemStack(CompressedBlock.steel, 9)));
	}

	private void createCatalystRecipes() {
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Ingot.platinum)),
				ImmutableList.of(createItemStack(Catalyst.platinum, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Ingot.titanium)),
				ImmutableList.of(createItemStack(Catalyst.titanium, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Ingot.palladium)),
				ImmutableList.of(createItemStack(Catalyst.palladium, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Ingot.cobalt)),
				ImmutableList.of(createItemStack(Catalyst.cobalt, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Ingot.manganese)),
				ImmutableList.of(createItemStack(Catalyst.manganese, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Ingot.magnesium)),
				ImmutableList.of(createItemStack(Catalyst.magnesiumOxide, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Ingot.antimony)),
				ImmutableList.of(createItemStack(Catalyst.antimonyTrioxide, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Element.chlorine), createItemStack(Ingot.copper)),
				ImmutableList.of(createItemStack(Catalyst.copperIIChloride, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Element.chlorine), new ItemStack(Items.iron_ingot)),
				ImmutableList.of(createItemStack(Catalyst.ironIIIChloride, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(new ItemStack(Items.iron_ingot)),
				ImmutableList.of(createItemStack(Catalyst.ironIIIOxide, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Catalyst.titanium), createItemStack(Ingot.aluminium), createItemStack(Compound.olefins)),
				ImmutableList.of(createItemStack(Catalyst.zieglerNatta, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Catalyst.cobalt), createItemStack(Catalyst.manganese), createItemStack(Element.bromine)),
				ImmutableList.of(createItemStack(Catalyst.cobaltManganeseBromide, 16)));
	}

	private void createMineralRecipes() {
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Ore.shale)),
				ImmutableList.of(createItemStack(Compound.naturalGas)));
	}

	private void createElementRecipes() {
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(new ItemStack(Items.water_bucket)),
				ImmutableList.of(createItemStack(Element.hydrogen, 2), createItemStack(Element.oxygen), new ItemStack(Items.bucket)));
	}

	private void createCompoundRecipes() {
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Element.hydrogen, 2), createItemStack(Element.oxygen)),
				ImmutableList.of(createItemStack(Compound.water)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.water)),
				ImmutableList.of(createItemStack(Element.chlorine, 10), createItemStack(Element.bromine), new ItemStack(Items.bucket)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.naturalGas)),
				ImmutableList.of(createItemStack(Compound.ethane), createItemStack(Compound.propane), createItemStack(Compound.butane), createItemStack(Compound.methane)));

		/*
		 * TODO: Fix Bitumen
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(new ItemStack(PolycraftMod.itemBucketOil)),
				ImmutableList.of(createItemStack(Compound.naphtha), createItemStack(Compound.gasOil), createItemStack(Compound.btx), createItemStack(Compound.bitumen), new ItemStack(Items.bucket)));
		 */
		
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.ethane), createItemStack(Catalyst.platinum)),
				ImmutableList.of(createItemStack(Compound.ethylene, 2)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.propane), createItemStack(Catalyst.platinum)),
				ImmutableList.of(createItemStack(Compound.propylene, 2)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.butane), createItemStack(Catalyst.platinum)),
				ImmutableList.of(createItemStack(Compound.butadiene, 2)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.ethylene), createItemStack(Catalyst.zieglerNatta)),
				ImmutableList.of(createItemStack(Polymer.HDPE, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.ethylene)),
				ImmutableList.of(createItemStack(Polymer.LDPE, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.ethylene), createItemStack(Catalyst.palladium)),
				ImmutableList.of(createItemStack(Compound.ethyleneOxide)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.ethyleneOxide), createItemStack(Compound.water)),
				ImmutableList.of(createItemStack(Compound.ethyleneGlycol)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.naphtha), createItemStack(Catalyst.platinum)),
				ImmutableList.of(createItemStack(Compound.olefins), createItemStack(Compound.paraffin)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.gasOil), createItemStack(Catalyst.platinum)),
				ImmutableList.of(createItemStack(Compound.diesel), createItemStack(Compound.kerosene)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.btx), createItemStack(Catalyst.cobaltManganeseBromide)),
				ImmutableList.of(createItemStack(Compound.terephthalicAcid)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.terephthalicAcid), createItemStack(Compound.methanol)),
				ImmutableList.of(createItemStack(Compound.dimethylTerephthalate, 2)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.dimethylTerephthalate), createItemStack(Compound.ethyleneGlycol, 2), createItemStack(Catalyst.magnesiumOxide)),
				ImmutableList.of(createItemStack(Polymer.LDPE, 16), createItemStack(Compound.methanol, 2), createItemStack(Compound.ethyleneGlycol)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.dimethylTerephthalate), createItemStack(Compound.ethyleneGlycol, 2), createItemStack(Catalyst.antimonyTrioxide)),
				ImmutableList.of(createItemStack(Polymer.LDPE, 16), createItemStack(Compound.methanol, 2), createItemStack(Compound.ethyleneGlycol)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Element.chlorine), createItemStack(Compound.ethylene, 2), createItemStack(Catalyst.copperIIChloride)),
				ImmutableList.of(createItemStack(Compound.edc, 2)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Element.chlorine), createItemStack(Compound.ethylene, 2), createItemStack(Catalyst.ironIIIChloride)),
				ImmutableList.of(createItemStack(Compound.edc, 2)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.edc)),
				ImmutableList.of(createItemStack(Compound.vinylChloride), createItemStack(Compound.hcl)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.Acetylene), createItemStack(Compound.hcl)),
				ImmutableList.of(createItemStack(Compound.vinylChloride)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.methane)),
				ImmutableList.of(createItemStack(Compound.Acetylene)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Element.chlorine, 16)),
				ImmutableList.of(createItemStack(Compound.cl2)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.h2), createItemStack(Compound.cl2)),
				ImmutableList.of(createItemStack(Compound.hcl, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.vinylChloride), createItemStack(Compound.water, 3)),
				ImmutableList.of(createItemStack(Polymer.PVC, 16), createItemStack(Compound.water, 3)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(new ItemStack(Items.iron_ingot), new ItemStack(Items.lava_bucket)),
				ImmutableList.of(createItemStack(Compound.sulfuricAcid, 32), new ItemStack(Items.bucket)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.ethylene), createItemStack(Compound.benzene), createItemStack(Compound.sulfuricAcid)),
				ImmutableList.of(createItemStack(Compound.ethylbenzene, 2)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.ethylbenzene), createItemStack(Compound.water, 10), new ItemStack(Items.coal), createItemStack(Catalyst.ironIIIOxide)),
				ImmutableList.of(createItemStack(Compound.styrene)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.styrene)),
				ImmutableList.of(createItemStack(Polymer.PS, 16)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.btx)),
				ImmutableList.of(createItemStack(Compound.benzene), createItemStack(Compound.toluene), createItemStack(Compound.xylene), createItemStack(Compound.methane)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.xylene)),
				ImmutableList.of(createItemStack(Compound.metaXylene), createItemStack(Compound.paraXylene), createItemStack(Compound.orthoXylene)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.xylene)),
				ImmutableList.of(createItemStack(Compound.metaXylene), createItemStack(Compound.paraXylene), createItemStack(Compound.orthoXylene)));

		// TODO: fix this so that if the inputs are containers and block, the correct amount of empty containers are on the right side.
		
		/*
		 * TODO: Fix coke
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(new ItemStack(Items.coal)),
				ImmutableList.of(createItemStack(Compound.coke), createItemStack(Compound.naphtha), createItemStack(Compound.bitumen)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.calcium_oxide), createItemStack(Compound.coke, 3)),
				ImmutableList.of(createItemStack(Compound.calcium_carbide), createItemStack(Compound.carbon_monoxide)));

		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(createItemStack(Compound.calcium_carbide), createItemStack(Compound.water)),
				ImmutableList.of(createItemStack(Compound.Acetylene), createItemStack(Compound.calcium_hydroxide)));
		 */
	}

	private void createCheatRecipes() {
		if (PolycraftMod.cheatRecipesEnabled) {
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.torch, 64), new ItemStack(Blocks.cobblestone));
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.furnace), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone));
			GameRegistry.addShapelessRecipe(new ItemStack(Items.coal, 64), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone));

			final ItemStack dirtStack = new ItemStack(Blocks.dirt);
			final Collection<ItemStack> dirtStacks = new ArrayList<ItemStack>();

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.dirt, 64), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.crafting_table), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemFlashlight), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemRunningShoes), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemScubaFins), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemScubaTank), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemScubaMask), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemJetPack), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemParachute), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemKevlarVest), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			final ItemStack oilBucketStack = new ItemStack(PolycraftMod.itemBucketOil);
			GameRegistry.addShapelessRecipe(oilBucketStack, dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.blockChemicalProcessor), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Items.coal, 64), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemFluidContainer, 64), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Items.diamond_pickaxe), dirtStacks.toArray());
		}
	}
}
