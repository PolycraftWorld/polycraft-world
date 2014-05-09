package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.GrippedTool;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.Mold;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;
import edu.utd.minecraft.mod.polycraft.config.PogoStick;
import edu.utd.minecraft.mod.polycraft.config.PolymerBlock;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;

public class RecipeGenerator {
	private static final Logger logger = LogManager.getLogger();

	public static void generateRecipes() {
		generateAutoRecipes();
		generateFileRecipes("recipes");
		if (System.getProperty("cheatRecipes") != null)
			generateCheatRecipes();
	}

	private static void generateAutoRecipes() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values()) {
			PolycraftMod.recipeManager.addShapedRecipe(
					PolycraftContainerType.CRAFTING_TABLE,
					compressedBlock.getItemStack(),
					PolycraftMod.recipeCompressedBlockFromItems,
					ImmutableMap.of('x', compressedBlock.source.getItemStack()));
			PolycraftMod.recipeManager.addShapelessRecipe(
					PolycraftContainerType.CRAFTING_TABLE,
					compressedBlock.source.getItemStack(PolycraftMod.recipeItemsPerCompressedBlock),
					ImmutableList.of(compressedBlock.getItemStack()));
		}

		for (final PolymerBlock polymerBlock : PolymerBlock.registry.values()) {
			// TODO furnace only uses one of the stack?
			PolycraftMod.recipeManager.addShapelessRecipe(
					PolycraftContainerType.FURNANCE,
					polymerBlock.getItemStack(),
					ImmutableList.of(polymerBlock.source.getItemStack(PolycraftMod.recipePolymerPelletsPerBlock)));
			PolycraftMod.recipeManager.addShapelessRecipe(
					PolycraftContainerType.CRAFTING_TABLE,
					polymerBlock.source.getItemStack(PolycraftMod.recipePolymerPelletsPerBlock),
					ImmutableList.of(polymerBlock.getItemStack()));
		}

		for (final PolymerSlab polymerSlab : PolymerSlab.registry.values())
			PolycraftMod.recipeManager.addShapedRecipe(
					PolycraftContainerType.CRAFTING_TABLE,
					polymerSlab.getItemStack(6),
					new String[] { "xxx", "   ", "   " },
					ImmutableMap.of('x', polymerSlab.source.getItemStack()));

		for (final CompoundVessel largerCompoundVessel : CompoundVessel.registry.values()) {
			if (largerCompoundVessel.vesselType.smallerType != null) {
				final CompoundVessel smallerCompoundVessel = CompoundVessel.registry.find(largerCompoundVessel.source, largerCompoundVessel.vesselType.smallerType);
				if (smallerCompoundVessel != null) {
					PolycraftMod.recipeManager.addShapelessRecipe(
							PolycraftContainerType.CRAFTING_TABLE,
							smallerCompoundVessel.getItemStack(largerCompoundVessel.vesselType.quantityOfSmallerType),
							ImmutableList.of(largerCompoundVessel.getItemStack()));
					PolycraftMod.recipeManager.addShapelessRecipe(
							PolycraftContainerType.CRAFTING_TABLE,
							largerCompoundVessel.getItemStack(),
							ImmutableList.of(smallerCompoundVessel.getItemStack(largerCompoundVessel.vesselType.quantityOfSmallerType)));
				}
			}
		}

		for (final PolymerPellets largerPolymerPelletsVessel : PolymerPellets.registry.values()) {
			if (largerPolymerPelletsVessel.vesselType.smallerType != null) {
				final PolymerPellets smallerPolymerPelletsVessel = PolymerPellets.registry.find(largerPolymerPelletsVessel.source, largerPolymerPelletsVessel.vesselType.smallerType);
				if (smallerPolymerPelletsVessel != null) {
					PolycraftMod.recipeManager.addShapelessRecipe(
							PolycraftContainerType.CRAFTING_TABLE,
							smallerPolymerPelletsVessel.getItemStack(largerPolymerPelletsVessel.vesselType.quantityOfSmallerType),
							ImmutableList.of(largerPolymerPelletsVessel.getItemStack()));
					PolycraftMod.recipeManager.addShapelessRecipe(
							PolycraftContainerType.CRAFTING_TABLE,
							largerPolymerPelletsVessel.getItemStack(),
							ImmutableList.of(smallerPolymerPelletsVessel.getItemStack(largerPolymerPelletsVessel.vesselType.quantityOfSmallerType)));
				}
			}
		}

		for (final MoldedItem moldedItem : MoldedItem.registry.values())
			PolycraftMod.recipeManager.addShapedRecipe(
					moldedItem.source.moldType == Mold.Type.Mold ? PolycraftContainerType.INJECTION_MOLDER : PolycraftContainerType.EXTRUDER,
					moldedItem.getItemStack(),
					new String[] { "xyz" },
					ImmutableMap.of(
							'x', moldedItem.polymerPellets.getItemStack(moldedItem.craftingPellets),
							'y', moldedItem.source.getItemStack(),
							'z', new ItemStack(Items.water_bucket)));

		for (final GrippedTool grippedTool : GrippedTool.registry.values())
			PolycraftMod.recipeManager.addShapelessRecipe(
					PolycraftContainerType.CRAFTING_TABLE,
					grippedTool.getItemStack(),
					ImmutableList.of(new ItemStack(PolycraftRegistry.getItem(grippedTool.source.name)), grippedTool.grip.getItemStack(PolycraftMod.recipeGripsPerTool)));

		for (final PogoStick pogoStick : PogoStick.registry.values())
			if (pogoStick.source != null && pogoStick.grip != null)
				PolycraftMod.recipeManager.addShapelessRecipe(
						PolycraftContainerType.CRAFTING_TABLE,
						pogoStick.getItemStack(),
						ImmutableList.of(new ItemStack(PolycraftRegistry.getItem(pogoStick.source.name)), pogoStick.grip.getItemStack(PolycraftMod.recipeGripsPerPogoStick)));
	}

	private static void generateFileRecipes(final String directory) {
		generateFileRecipesCraft(directory);
		generateFileRecipesSmelt(directory);
		generateFileRecipesMill(directory);
		generateFileRecipesDistill(directory);
		generateFileRecipesCrack(directory);
	}

	private static void generateFileRecipesCraft(final String directory) {
		final char[] shapedIdentifiers = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i' };
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, "craft")) {
			if (line.length > 3) {
				final boolean shapeless = Boolean.parseBoolean(line[1]);
				final String outputItemName = line[3];
				final ItemStack outputItemStack = PolycraftRegistry.getItemStack(outputItemName, Integer.parseInt(line[4]));
				if (outputItemStack == null) {
					logger.warn("Unable to find output item for crafting recipe: {}", outputItemName);
					continue;
				}
				else {
					List<ItemStack> inputItems = Lists.newArrayList();
					for (int i = 5; i < 23; i += 2) {
						final String inputItemName = line.length > i ? line[i] : "";
						ItemStack inputItemStack = null;
						if (!inputItemName.isEmpty()) {
							inputItemStack = PolycraftRegistry.getItemStack(inputItemName, Integer.parseInt(line[i + 1]));
							if (inputItemStack == null) {
								logger.warn("Unable to find input item for crafting recipe ({}): {}", outputItemName, inputItemName);
								inputItems = null;
								break;
							}
						}
						if (inputItemStack != null || !shapeless)
							inputItems.add(inputItemStack);
					}

					if (inputItems == null)
						continue;

					if (shapeless)
						PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE, outputItemStack, inputItems);
					else {
						final Map<Character, ItemStack> inputItemStacks = Maps.newHashMap();
						final String[] shape = new String[3];
						for (int r = 0; r < 3; r++) {
							final StringBuffer shapeRow = new StringBuffer();
							for (int c = 0; c < 3; c++) {
								final int index = (r * 3) + c;
								final ItemStack itemStack = inputItems.get(index);
								if (itemStack == null)
									shapeRow.append(" ");
								else {
									shapeRow.append(shapedIdentifiers[index]);
									inputItemStacks.put(shapedIdentifiers[index], itemStack);
								}
							}
							shape[r] = shapeRow.toString();
						}
						PolycraftMod.recipeManager.addShapedRecipe(PolycraftContainerType.CRAFTING_TABLE, outputItemStack, shape, inputItemStacks);
					}
				}
			}
		}
	}

	private static void generateFileRecipesSmelt(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, "smelt")) {
			if (line.length > 3) {
				final String outputItemName = line[1];
				final ItemStack outputItemStack = PolycraftRegistry.getItemStack(outputItemName, 1);
				if (outputItemStack == null) {
					logger.warn("Unable to find output item for smelting recipe: {}", outputItemName);
					continue;
				}
				final String inputItemName = line[3];
				final ItemStack inputItemStack = PolycraftRegistry.getItemStack(inputItemName, 1);
				if (inputItemStack == null) {
					logger.warn("Unable to find input item for smelting recipe ({}): {}", outputItemName, inputItemName);
					continue;
				}
				PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.FURNANCE, outputItemStack, ImmutableList.of(inputItemStack));
			}
		}
	}

	private static void generateFileRecipesMill(final String directory) {
		Mold currentMold = null;
		char currentMoldShapeChar = 'x';
		String[] currentMoldShape = null;
		int currentRow = 0;
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, "mill")) {
			if (line.length > 0) {
				if (currentMold == null || currentRow == 5 && line.length > 1) {
					currentMold = Mold.registry.get(line[1]);
					currentMoldShape = new String[6];
					currentRow = 0;
				}
				if (currentMold != null) {
					final StringBuffer currentMoldShapeRow = new StringBuffer();
					for (int i = 0; i < 5; i++) {
						final int index = 2 + i;
						char moldSlot = ' ';
						if (line.length > index && !line[index].trim().isEmpty())
							moldSlot = line[index].trim().charAt(0);
						currentMoldShapeRow.append(moldSlot);
						if (moldSlot != ' ')
							currentMoldShapeChar = moldSlot;
					}
					currentMoldShape[currentRow] = currentMoldShapeRow.toString();
					currentRow++;

					if (currentRow == 5) {
						char nextMoldShapeChar = (char) (currentMoldShapeChar + 1);
						currentMoldShape[currentMoldShape.length - 1] = String.valueOf(nextMoldShapeChar);

						for (final Ingot ingot : Ingot.registry.values()) {
							if (ingot.moldDamagePerUse > 0) {
								PolycraftMod.recipeManager.addShapedRecipe(
										PolycraftContainerType.MACHINING_MILL,
										currentMold.getItemStack(ingot),
										currentMoldShape,
										ImmutableMap.of(
												currentMoldShapeChar, ingot.getItemStack(),
												nextMoldShapeChar, new ItemStack(Items.water_bucket)));
							}
						}
					}
				}
			}
		}
	}

	private static void generateFileRecipesDistill(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, "distill")) {
			final String inputItemName = line[4];
			final ItemStack inputItemStack = PolycraftRegistry.getItemStack(inputItemName, Integer.parseInt(line[5]));
			if (inputItemStack == null) {
				logger.warn("Unable to find input item for distillation recipe: {}", inputItemName);
				continue;
			}

			List<ItemStack> outputItems = Lists.newArrayList();
			for (int i = 6; i < line.length; i += 2) {
				final String outputItemName = line[i];
				final ItemStack outputItemStack = PolycraftRegistry.getItemStack(outputItemName, Integer.parseInt(line[i + 1]));
				if (outputItemStack == null) {
					logger.warn("Unable to find output item for distillation recipe ({}): {}", inputItemName, outputItemName);
					outputItems = null;
					break;
				}
				else
					outputItems.add(outputItemStack);
			}

			if (outputItems == null)
				continue;

			PolycraftMod.recipeManager.addShapedRecipe(
					PolycraftContainerType.DISTILLATION_COLUMN,
					outputItems,
					new String[] { "xyz" },
					ImmutableMap.of(
							'x', inputItemStack,
							'y', new ItemStack(Items.water_bucket),
							'z', new ItemStack(Items.water_bucket)));
		}
	}

	private static void generateFileRecipesCrack(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, "crack")) {
			final String inputItemName1 = line[3];
			final ItemStack inputItemStack1 = PolycraftRegistry.getItemStack(inputItemName1, Integer.parseInt(line[4]));
			if (inputItemStack1 == null) {
				logger.warn("Unable to find input item for cracking recipe: {}", inputItemName1);
				continue;
			}

			final String inputItemName2 = line[5];
			final ItemStack inputItemStack2 = PolycraftRegistry.getItemStack(inputItemName2, Integer.parseInt(line[6]));
			if (inputItemStack2 == null) {
				logger.warn("Unable to find input item for cracking recipe: {}", inputItemName2);
				continue;
			}

			List<ItemStack> outputItems = Lists.newArrayList();
			for (int i = 7; i < line.length - 1; i += 2) {
				final String outputItemName = line[i];
				final ItemStack outputItemStack = PolycraftRegistry.getItemStack(outputItemName, Integer.parseInt(line[i + 1]));
				if (outputItemStack == null) {
					logger.warn("Unable to find output item for cracking recipe ({}): {}", inputItemName1, outputItemName);
					outputItems = null;
					break;
				}
				else
					outputItems.add(outputItemStack);
			}

			if (outputItems == null)
				continue;

			PolycraftMod.recipeManager.addShapedRecipe(
					PolycraftContainerType.STEAM_CRACKER,
					outputItems,
					new String[] { "wxyz" },
					ImmutableMap.of(
							'w', inputItemStack1,
							'x', inputItemStack2,
							'y', new ItemStack(Items.water_bucket),
							'z', new ItemStack(Items.water_bucket)));

		}
	}

	private static void generateCheatRecipes() {
		final ItemStack[] recipeCheatDirtOutputs = new ItemStack[] {
				new ItemStack(Blocks.dirt, 64),
				new ItemStack(Blocks.crafting_table),
				CustomObject.registry.get("Flashlight").getItemStack(),
				CustomObject.registry.get("Flame Thrower").getItemStack(),
				CustomObject.registry.get("Jet Pack").getItemStack(),
				PolymerSlab.registry.get("Slab (PolyIsoPrene)").getItemStack(),
				PolymerBlock.registry.get("Block (PolyIsoPrene)").getItemStack(),
				CustomObject.registry.get("Phase Shifter").getItemStack(),
				MoldedItem.registry.get("Life Preserver (PolyIsoPrene)").getItemStack(),
				MoldedItem.registry.get("Running Shoes (PolyIsoPrene)").getItemStack(),
				MoldedItem.registry.get("Running Shoes (Low Density PolyEthylene)").getItemStack(),
				MoldedItem.registry.get("Scuba Fins (PolyIsoPrene)").getItemStack(),
				MoldedItem.registry.get("Scuba Mask (PolyIsoPrene)").getItemStack(),
				CustomObject.registry.get("Scuba Tank").getItemStack(),
				CustomObject.registry.get("Parachute").getItemStack(),
				CustomObject.registry.get("Kevlar Vest").getItemStack(),
				Inventory.registry.get("Injection Molder").getItemStack(),
				PolymerPellets.registry.get("Vial (PolyIsoPrene Pellets)").getItemStack(),
				Mold.registry.get("Mold (Grip)").getItemStack(),
				new ItemStack(Items.water_bucket),
				new ItemStack(Items.coal, 64),
				// PolymerSlab.registry.get("Slab (PolyIsoPrene)").getItemStack(),
				// PolymerBlock.registry.get("Block (PolyIsoPrene)").getItemStack(),
				// new ItemStack(PolycraftMod.getItem("Gripped Diamond Pogo Stick")),
				// Inventory.registry.get("Tree Tap").getItemStack(),
				new ItemStack(Blocks.furnace),
				new ItemStack(Items.diamond_pickaxe),
				new ItemStack(Blocks.torch, 64)
		};

		final Collection<ItemStack> inputs = new LinkedList<ItemStack>();
		for (final ItemStack output : recipeCheatDirtOutputs) {
			inputs.add(new ItemStack(Blocks.dirt));
			GameRegistry.addShapelessRecipe(output, inputs.toArray());
		}
	}
}
