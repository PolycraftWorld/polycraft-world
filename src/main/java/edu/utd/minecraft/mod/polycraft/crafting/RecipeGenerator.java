package edu.utd.minecraft.mod.polycraft.crafting;

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

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.ElementVessel;
import edu.utd.minecraft.mod.polycraft.config.GrippedTool;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.Mold;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;
import edu.utd.minecraft.mod.polycraft.config.PogoStick;
import edu.utd.minecraft.mod.polycraft.config.PolymerBlock;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;
import edu.utd.minecraft.mod.polycraft.config.PolymerStairs;
import edu.utd.minecraft.mod.polycraft.config.PolymerWall;

public class RecipeGenerator {
	private static final Logger logger = LogManager.getLogger();

	public static void generateRecipes() {
		generateAutoRecipes();
		generateFileRecipes("recipes");
		if (System.getProperty("cheatRecipes") != null)
			generateCheatRecipes();
	}

	private static void generateAutoRecipes() {
		ColoringPolycraftRecipeFactory coloringFactory = new ColoringPolycraftRecipeFactory();

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
			PolycraftMod.recipeManager.addShapelessRecipe(
					PolycraftContainerType.FURNACE,
					polymerBlock.getItemStack(),
					ImmutableList.of(polymerBlock.source.getItemStack(PolycraftMod.recipePolymerPelletsPerBlock)));
			PolycraftMod.recipeManager.addShapelessRecipe(
					PolycraftContainerType.CRAFTING_TABLE,
					polymerBlock.source.getItemStack(PolycraftMod.recipePolymerPelletsPerBlock),
					ImmutableList.of(polymerBlock.getItemStack()));
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(polymerBlock.getItemStack(8)),
					new String[] { "xxx", "xyx", "xxx" },
					ImmutableMap.of(
							'x', polymerBlock.getItemStack(),
							'y', new ItemStack(Items.dye)), 0);
		}

		for (final PolymerSlab polymerSlab : PolymerSlab.registry.values()) {
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					polymerSlab.getItemStack(6),
					new String[] { "xxx", "   ", "   " },
					ImmutableMap.of('x', polymerSlab.source.getItemStack()));
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(polymerSlab.getItemStack(8)),
					new String[] { "xxx", "xyx", "xxx" },
					ImmutableMap.of(
							'x', polymerSlab.getItemStack(),
							'y', new ItemStack(Items.dye)), 0);
		}

		for (final PolymerStairs polymerStairs : PolymerStairs.registry.values()) {
			PolycraftMod.recipeManager.addShapedRecipe(
					PolycraftContainerType.CRAFTING_TABLE,
					polymerStairs.getItemStack(6),
					new String[] { "x ", "xx ", "xxx" },
					ImmutableMap.of('x', polymerStairs.source.getItemStack()));
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(polymerStairs.getItemStack(8)),
					new String[] { "xxx", "xyx", "xxx" },
					ImmutableMap.of(
							'x', polymerStairs.getItemStack(),
							'y', new ItemStack(Items.dye)), 0);
		}

		for (final PolymerWall polymerWall : PolymerWall.registry.values()) {
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					polymerWall.getItemStack(6),
					new String[] { "xxx", "xxx", "   " },
					ImmutableMap.of('x', polymerWall.source.getItemStack()));
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(polymerWall.getItemStack(8)),
					new String[] { "xxx", "xyx", "xxx" },
					ImmutableMap.of(
							'x', polymerWall.getItemStack(),
							'y', new ItemStack(Items.dye)), 0);
		}

		for (final ElementVessel largerElementVessel : ElementVessel.registry.values()) {
			if (largerElementVessel.vesselType.smallerType != null) {
				final ElementVessel smallerElementVessel = ElementVessel.registry.find(largerElementVessel.source, largerElementVessel.vesselType.smallerType);
				if (smallerElementVessel != null) {
					PolycraftMod.recipeManager.addShapelessRecipe(
							PolycraftContainerType.CRAFTING_TABLE,
							smallerElementVessel.getItemStack(largerElementVessel.vesselType.quantityOfSmallerType),
							ImmutableList.of(largerElementVessel.getItemStack()));
					PolycraftMod.recipeManager.addShapelessRecipe(
							PolycraftContainerType.CRAFTING_TABLE,
							largerElementVessel.getItemStack(),
							ImmutableList.of(smallerElementVessel.getItemStack(largerElementVessel.vesselType.quantityOfSmallerType)));
				}
			}
		}

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
					new String[] { "xy" },
					ImmutableMap.of(
							'x', moldedItem.polymerPellets.getItemStack(moldedItem.craftingPellets),
							'y', moldedItem.source.getItemStack()));

		for (final GrippedTool grippedTool : GrippedTool.registry.values())
			PolycraftMod.recipeManager.addShapelessRecipe(
					PolycraftContainerType.CRAFTING_TABLE,
					grippedTool.getItemStack(),
					ImmutableList.of(grippedTool.source.getItemStack(), grippedTool.grip.getItemStack(PolycraftMod.recipeGripsPerTool)));

		for (final PogoStick pogoStick : PogoStick.registry.values())
			if (pogoStick.source != null && pogoStick.grip != null)
				PolycraftMod.recipeManager.addShapelessRecipe(
						PolycraftContainerType.CRAFTING_TABLE,
						pogoStick.getItemStack(),
						ImmutableList.of(pogoStick.source.getItemStack(), pogoStick.grip.getItemStack(PolycraftMod.recipeGripsPerPogoStick)));
	}

	private static void generateFileRecipes(final String directory) {
		generateFileRecipesCraft(directory);
		generateFileRecipesSmelt(directory);
		generateFileRecipesMill(directory);
		generateFileRecipesDistill(directory);
		generateFileRecipesCrack(directory);
		generateFileRecipesTreat(directory);
		generateFileRecipesProcess(directory);
	}

	private static void generateFileRecipesCraft(final String directory) {
		final char[] shapedIdentifiers = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i' };
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, "craft")) {
			if (line.length > 4) {
				if (PolycraftMod.isVersionCompatible(PolycraftMod.getVersionNumeric(line[0]))) {
					final boolean shapeless = Boolean.parseBoolean(line[2]);
					final String outputItemName = line[4];
					final ItemStack outputItemStack = PolycraftRegistry.getItemStack(outputItemName, Integer.parseInt(line[5]));
					if (outputItemStack == null) {
						logger.warn("Unable to find output item for crafting recipe: {}", outputItemName);
						continue;
					}
					else {
						List<ItemStack> inputItems = Lists.newArrayList();
						for (int i = 6; i < 24; i += 2) {
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
	}

	private static void generateFileRecipesSmelt(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, "smelt")) {
			if (line.length > 4) {
				if (PolycraftMod.isVersionCompatible(PolycraftMod.getVersionNumeric(line[0]))) {
					final String outputItemName = line[2];
					final ItemStack outputItemStack = PolycraftRegistry.getItemStack(outputItemName, 1);
					if (outputItemStack == null) {
						logger.warn("Unable to find output item for smelting recipe: {}", outputItemName);
						continue;
					}
					final String inputItemName = line[4];
					final ItemStack inputItemStack = PolycraftRegistry.getItemStack(inputItemName, 1);
					if (inputItemStack == null) {
						logger.warn("Unable to find input item for smelting recipe ({}): {}", outputItemName, inputItemName);
						continue;
					}
					PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.FURNACE, outputItemStack, ImmutableList.of(inputItemStack));
				}
			}
		}
	}

	private static void generateFileRecipesMill(final String directory) {
		Mold currentMold = null;
		char currentMoldShapeChar = 'x';
		String[] currentMoldShape = null;
		int currentRow = 0;
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, "mill")) {
			if (currentMold == null || currentRow == 5 && line.length > 2) {
				if (PolycraftMod.isVersionCompatible(PolycraftMod.getVersionNumeric(line[0]))) {
					currentMold = Mold.registry.get(line[2]);
					currentMoldShape = new String[5];
					currentRow = 0;
				}
			}
			if (currentMold != null) {
				final StringBuffer currentMoldShapeRow = new StringBuffer();
				for (int i = 0; i < 5; i++) {
					final int index = 3 + i;
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
					for (final Ingot ingot : Ingot.registry.values()) {
						if (ingot.moldDamagePerUse > 0) {
							PolycraftMod.recipeManager.addShapedRecipe(
									PolycraftContainerType.MACHINING_MILL,
									currentMold.getItemStack(ingot),
									currentMoldShape,
									ImmutableMap.of(currentMoldShapeChar, ingot.getItemStack()));
						}
					}
				}
			}
		}
	}

	private static void generateFileRecipesDistill(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, "distill")) {
			if (line.length > 7) {
				if (PolycraftMod.isVersionCompatible(PolycraftMod.getVersionNumeric(line[0]))) {
					final String inputItemName = line[5];
					final ItemStack inputItemStack = PolycraftRegistry.getItemStack(inputItemName, Integer.parseInt(line[6]));
					if (inputItemStack == null) {
						logger.warn("Unable to find input item for distillation recipe: {}", inputItemName);
						continue;
					}

					List<ItemStack> outputItems = Lists.newArrayList();
					for (int i = 7; i < line.length; i += 2) {
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

					PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.DISTILLATION_COLUMN, ImmutableList.of(inputItemStack), outputItems);
				}
			}
		}
	}

	private static void generateFileRecipesCrack(final String directory) {
		final char[] shapedIdentifiers = new char[] { 'x', 'y' };
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, "crack")) {
			if (line.length > 8) {
				if (PolycraftMod.isVersionCompatible(PolycraftMod.getVersionNumeric(line[0]))) {
					Map<Character, ItemStack> shapedInputs = Maps.newHashMap();
					final StringBuilder inputShape = new StringBuilder();
					for (int i = 0; i < shapedIdentifiers.length; i++) {
						final String inputItemName = line[4 + (i * 2)];
						final ItemStack inputItemStack = PolycraftRegistry.getItemStack(inputItemName, Integer.parseInt(line[5 + (i * 2)]));
						if (inputItemStack == null) {
							logger.warn("Unable to find input item for cracking recipe: {}", inputItemName);
							shapedInputs = null;
							break;
						}
						shapedInputs.put(shapedIdentifiers[i], inputItemStack);
						inputShape.append(shapedIdentifiers[i]);
					}
					if (shapedInputs == null)
						continue;

					List<ItemStack> outputItems = Lists.newArrayList();
					for (int i = 8; i < line.length - 1; i += 2) {
						final String outputItemName = line[i];
						final ItemStack outputItemStack = PolycraftRegistry.getItemStack(outputItemName, Integer.parseInt(line[i + 1]));
						if (outputItemStack == null) {
							logger.warn("Unable to find output item for cracking recipe ({}): {}", shapedInputs.values().toArray()[0], outputItemName);
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
							new String[] { inputShape.toString() },
							shapedInputs);
				}
			}
		}
	}

	private static void generateFileRecipesTreat(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, "treat")) {
			if (line.length > 8) {
				//TODO
			}
		}
	}

	private static void generateFileRecipesProcess(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, "process")) {
			if (line.length > 8) {
				//TODO
			}
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
				MoldedItem.registry.get("Scuba Fins (PolyIsoPrene)").getItemStack(),
				MoldedItem.registry.get("Scuba Mask (PolyIsoPrene)").getItemStack(),
				CustomObject.registry.get("Scuba Tank").getItemStack(),
				CustomObject.registry.get("Parachute").getItemStack(),
				CustomObject.registry.get("Kevlar Vest").getItemStack(),
				Inventory.registry.get("Injection Molder").getItemStack(),
				PolymerPellets.registry.get("Bag (PolyIsoPrene Pellets)").getItemStack(),
				Mold.registry.get("Mold (Grip)").getItemStack(),
				new ItemStack(Items.water_bucket),
				new ItemStack(Items.coal, 64),
				new ItemStack(Blocks.furnace),
				new ItemStack(Items.diamond_pickaxe),
				new ItemStack(Blocks.torch, 64)
		};

		int intputs = 1;
		for (final ItemStack output : recipeCheatDirtOutputs)
			PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.CRAFTING_TABLE, output, ImmutableList.of(new ItemStack(Blocks.dirt, intputs++)));
	}
}
