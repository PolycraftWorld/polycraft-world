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
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.PolycraftItemHelper;

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
	}

	private static ItemStack createItemStack(final Entity entity) {
		return createItemStack(entity, 1);
	}

	private static ItemStack createItemStack(final Entity entity, int size) {
		if ((entity instanceof Element && ((Element) entity).fluid)) {
			Item item = PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Element, ItemFluidContainer.getItemName(entity));
			if (item == null) {
				throw new IllegalArgumentException("No fluid container item for " + entity.name);
			}
			return new ItemStack(item, size);
		}
		if (entity instanceof Ingot) {
			Item item = PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Ingot, entity);
			if (item == null) {
				throw new IllegalArgumentException("No ingot item for " + entity.name);
			}
			return new ItemStack(item, size);
		}
		if (entity instanceof Catalyst) {
			Item item = PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Catalyst, entity);
			if (item == null) {
				throw new IllegalArgumentException("No catalyst item for " + entity.name);
			}
			return new ItemStack(item, size);
		}
		if (entity instanceof Polymer) {
			Block block = PolycraftMod.getBlock(PolycraftMod.RegistryNamespace.Polymer, entity);
			if (block == null) {
				throw new IllegalArgumentException("No polymer block for " + entity.name);
			}
			return new ItemStack(block, size);
		}
		if (entity instanceof Ore) {
			Block block = PolycraftMod.getBlock(PolycraftMod.RegistryNamespace.Ore, entity);
			if (block == null) {
				throw new IllegalArgumentException("No ore block for " + entity.name);
			}
			return new ItemStack(block, size);
		}
		if (entity instanceof CompressedBlock) {
			Block block = PolycraftMod.getBlock(PolycraftMod.RegistryNamespace.CompressedBlock, entity);
			if (block == null) {
				throw new IllegalArgumentException("No compressed block for " + entity.name);
			}
			return new ItemStack(block, size);
		}
		if (entity instanceof Compound) {
			if (((Compound) entity).fluid) {
				Item item = PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Compound, ItemFluidContainer.getItemName(entity));
				if (item == null) {
					throw new IllegalArgumentException("No fluid container item for " + entity.name);
				}
				return new ItemStack(item, size);
			}
			else {
				Block block = PolycraftMod.getBlock(PolycraftMod.RegistryNamespace.Compound, entity);
				if (block == null) {
					throw new IllegalArgumentException("No compound block for " + entity.name);
				}
				return new ItemStack(block, size);
			}
		}

		throw new IllegalArgumentException("No block or item for entity type " + entity.getClass().getSimpleName());
	}

	private void createCustomItemRecipes() {
		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.blockTreeTap),
				new String[] { "x x", "xyx", " x " },
				ImmutableMap.of(
						'x', new ItemStack(Blocks.planks),
						'y', new ItemStack(Blocks.chest)));
		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemFluidContainerNozzle),
				new String[] { "xxx", " x ", " x " },
				ImmutableMap.of('x', createItemStack(Ingot.registry.get("Copper"))));
		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemFluidContainer),
				new String[] { " y ", "x x", " x " },
				ImmutableMap.of(
						'x', createItemStack(Ingot.registry.get("Steel")),
						'y', new ItemStack(PolycraftMod.itemFluidContainerNozzle)));

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemKevlarVest),
				new String[] { "x x", "xxx", "xxx" },
				ImmutableMap.of('x', new ItemStack(PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, Polymer.registry.get("Kevlar-Epoxy Matrix Composite").itemNameFiber), 4)));

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemRunningShoes),
				new String[] { "   ", "x x", "x x" },
				ImmutableMap.of('x', new ItemStack(PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, Polymer.registry.get("Low-Density PolyEthylene").itemNameFiber), 2)));

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemJetPack),
				new String[] { "xzx", "yxy", "xzx", },
				ImmutableMap.of(
						'x', new ItemStack(PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, Polymer.registry.get("High-Density PolyEthylene").itemNameFiber), 8),
						'y', createItemStack(Compound.registry.get("Hydrogen Gas"), 2),
						'z', createItemStack(Ingot.registry.get("Aluminum"), 8)));

		//TODO add a recipe for PolycraftMod.itemFlameThrower

		// allow refilling tanks
		ItemStack jetPackFilled = new ItemStack(PolycraftMod.itemJetPack);
		PolycraftItemHelper.createTagCompound(jetPackFilled);
		ItemJetPack.setFuelUnitsRemaining(jetPackFilled, PolycraftMod.itemJetPackFuelUnitsFull);
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(new ItemStack(PolycraftMod.itemJetPack), createItemStack(Compound.registry.get("Hydrogen Gas"), 4)),
				ImmutableList.of(jetPackFilled));

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemParachute),
				new String[] { "xxx", "x x", " x " },
				ImmutableMap.of('x', new ItemStack(PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, Polymer.registry.get("Nylon").itemNameFiber), 8)));

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemPogoStick),
				new String[] { "yxy", " x ", "yxy" },
				ImmutableMap.of(
						'x', new ItemStack(PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, Polymer.registry.get("Natural Rubber").itemNamePellet)),
						'y', new ItemStack(Items.iron_ingot)));

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
				PolycraftContainerType.CRAFTING_TABLE, new ItemStack(PolycraftMod.itemFlashlight),
				new String[] { "xxx", "xyx", "xxx" },
				ImmutableMap.of(
						'x', new ItemStack(PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, Polymer.registry.get("Low-Density PolyEthylene").itemNameFiber)),
						'y', new ItemStack(Blocks.glass_pane),
						'z', new ItemStack(Blocks.redstone_lamp)));

		// allow refilling tanks
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(new ItemStack(PolycraftMod.itemScubaTank), createItemStack(Compound.registry.get("Oxygen Gas"), 2)),
				ImmutableList.of(new ItemStack(PolycraftMod.itemScubaTank)));

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemScubaFins),
				new String[] { "x x", "x x", "x x" },
				ImmutableMap.of('x', new ItemStack(PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, Polymer.registry.get("Low-Density PolyEthylene").itemNameFiber))));

		PolycraftMod.recipeManager.addShapedRecipe(
				PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.itemGrip),
				new String[] { "x x", "x x", "xxx" },
				ImmutableMap.of('x', new ItemStack(PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, Polymer.registry.get("PolyOxymethylene").itemNamePellet))));

		for (final Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet()) {
			final String materialName = materialEntry.getKey();
			for (final String type : ItemGripped.allowedTypes.keySet()) {
				Item itemToGrip = (Item) Item.itemRegistry.getObject(ItemGripped.getNameBase(materialName, type));
				PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
						new ItemStack(PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Tool, ItemGripped.getName(Polymer.registry.get("PolyOxymethylene"), materialName, type))),
						ImmutableList.of(
								new ItemStack(itemToGrip),
								new ItemStack(PolycraftMod.itemGrip)));
			}
		}
	}

	private void createOreRecipes() {
		for (final Ore ore : Ore.registry.values()) {
			if (ore.smeltingEntity != null) {
				try {
					PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.FURNANCE,
							new ItemStack(Item.getItemFromBlock(PolycraftMod.getBlock(PolycraftMod.RegistryNamespace.Ore, ore))),
							ImmutableList.of(createItemStack(ore.smeltingEntity, ore.smeltingEntitiesPerBlock)),
							ore.smeltingExperience);
				} catch (IllegalArgumentException illegalEx) {
					// TODO: Don't catch this error; it should blow up once all the recipes are in.
					logger.error("Couldn't create smelting recipe: " + illegalEx.getMessage());
				}
			}
		}
	}

	private void createCompressedBlockRecipes() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values()) {
			final ItemStack compressedBlockItemStack = createItemStack(compressedBlock);
			final Block compressedBlockBlock = PolycraftMod.getBlock(PolycraftMod.RegistryNamespace.CompressedBlock, compressedBlock);

			final ItemStack[] compressedItems = new ItemStack[compressedBlock.itemsPerBlock];
			for (int i = 0; i < compressedItems.length; i++) {
				compressedItems[i] = new ItemStack(compressedBlockBlock);
			}

			PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
					compressedBlockItemStack, ImmutableList.copyOf(compressedItems));
			PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
					new ItemStack(compressedBlockBlock, compressedBlock.itemsPerBlock),
					ImmutableList.of(compressedBlockItemStack));
		}
	}

	private void createPolymerRecipes() {
		for (final Polymer polymer : Polymer.registry.values()) {
			final Block polymerBlock = PolycraftMod.getBlock(PolycraftMod.RegistryNamespace.Polymer, polymer);
			final Item polymerPellet = PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, polymer.itemNamePellet);
			final Item polymerFiber = PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, polymer.itemNameFiber);
			try {
				// convert between blocks and pellets
				final ItemStack pelletItem = new ItemStack(polymerPellet);
				final ItemStack[] pelletItems = new ItemStack[polymer.craftingPelletsPerBlock];
				for (int i = 0; i < pelletItems.length; i++) {
					pelletItems[i] = pelletItem;
				}
				PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
						ImmutableList.copyOf(pelletItems),
						ImmutableList.of(new ItemStack(polymerBlock)));
				PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
						new ItemStack(polymerBlock),
						ImmutableList.of(new ItemStack(polymerPellet, polymer.craftingPelletsPerBlock)));

				// convert between pellets and fibers
				PolycraftMod.recipeManager.addShapedRecipe(PolycraftContainerType.CRAFTING_TABLE,
						new ItemStack(polymerFiber),
						new String[] { "x  ", " x ", "  x" },
						ImmutableMap.of('x', new ItemStack(polymerPellet)));
				PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
						new ItemStack(polymerPellet, 3),
						ImmutableList.of(new ItemStack(polymerFiber)));

				if (polymer.slabable) {
					//convert between blocks and slabs
					PolycraftMod.recipeManager.addShapedRecipe(PolycraftContainerType.CRAFTING_TABLE,
							new ItemStack(PolycraftMod.getBlock(PolycraftMod.RegistryNamespace.Polymer, polymer.blockNameSlab)),
							new String[] { "   ", "xxx", "   " },
							ImmutableMap.of('x', new ItemStack(polymerBlock)));
					PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE,
							new ItemStack(polymerBlock, 3),
							ImmutableList.of(new ItemStack(PolycraftMod.getBlock(PolycraftMod.RegistryNamespace.Polymer, polymer.blockNameSlab))));
				}

			} catch (Exception ex) {
				// TODO: Fix these recipes - should not ignore this exception!  Most of these
				// polymer blocks are null and cause NullPointerException
				logger.error("Error creating polymer " + polymer + ": ", ex.getMessage());
			}
		}
	}

	private void createChemicalProcessorRecipes() {
		PolycraftMod.recipeManager.addShapedRecipe(PolycraftContainerType.CRAFTING_TABLE,
				new ItemStack(PolycraftMod.blockChemicalProcessor),
				new String[] { "xxx", "xzx", "xyx" },
				ImmutableMap.of(
						'x', createItemStack(Ingot.registry.get("Steel")),
						'y', new ItemStack(Blocks.furnace),
						'z', new ItemStack(Blocks.glass_pane)));
	}

	public void generateCheatRecipes() {
		if (PolycraftMod.cheatRecipesEnabled) {
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.torch, 64), new ItemStack(Blocks.cobblestone));
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.furnace), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone));
			GameRegistry.addShapelessRecipe(new ItemStack(Items.coal, 64), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone));

			final ItemStack dirtStack = new ItemStack(Blocks.dirt);
			final Collection<ItemStack> dirtStacks = new ArrayList<ItemStack>();

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.dirt, 64), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemPogoStick), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.crafting_table), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemFlameThrower), dirtStacks.toArray());

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
