package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.List;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.Armor;
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
import edu.utd.minecraft.mod.polycraft.config.PolymerBrick;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;
import edu.utd.minecraft.mod.polycraft.config.PolymerStairs;
import edu.utd.minecraft.mod.polycraft.config.PolymerWall;
import edu.utd.minecraft.mod.polycraft.config.Tool;
import edu.utd.minecraft.mod.polycraft.item.ArmorSlot;

public class RecipeGenerator {
	private static final Logger logger = LogManager.getLogger();
	private static final String ItemPolymerBrick = null;

	public static void generateRecipes() {
		generateFileRecipes("recipes");
		generateAutoRecipes();
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
					new String[] { "   ", "xxx", "   " },
					ImmutableMap.of('x', polymerSlab.source.getItemStack()));
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					polymerSlab.getItemStack(6),
					new String[] { "   ", "   ", "xxx" },
					ImmutableMap.of('x', polymerSlab.source.getItemStack()));
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
					polymerWall.getItemStack(6),
					new String[] { "   ", "xxx", "xxx" },
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
		{
			PolycraftMod.recipeManager.addShapedRecipe(
					moldedItem.source.moldType == Mold.Type.Mold ? PolycraftContainerType.INJECTION_MOLDER : PolycraftContainerType.EXTRUDER,
					moldedItem.getItemStack(),
					new String[] { "xy" },
					ImmutableMap.of(
							'x', moldedItem.polymerPellets.getItemStack(moldedItem.craftingPellets),
							'y', moldedItem.source.getItemStack()));
			if ((moldedItem.params != null) && (moldedItem.params.names.length > 1))
			{
				int count = 0;
				for (final String name : moldedItem.params.names)
				{
					if (name.contains("Source"))
					{
						PolycraftMod.recipeManager.addShapedRecipe(
								moldedItem.source.moldType == Mold.Type.Mold ? PolycraftContainerType.INJECTION_MOLDER : PolycraftContainerType.EXTRUDER,
								moldedItem.getItemStack(),
								new String[] { "xy" },
								ImmutableMap.of(
										'x', PolymerPellets.registry.get(moldedItem.params.get(count)).getItemStack(moldedItem.craftingPellets),
										'y', moldedItem.source.getItemStack()));

					}
					count++;

				}

			}
		}

		for (final PolymerBrick brickItem : PolymerBrick.registry.values())
			PolycraftMod.recipeManager.addShapedRecipe(
					PolycraftContainerType.INJECTION_MOLDER,
					brickItem.getItemStack(),
					new String[] { "xy" },
					ImmutableMap.of(
							'x', brickItem.source.getItemStack(brickItem.craftingPellets),
							'y', brickItem.brickMold.getItemStack()));

		for (final PolymerBrick brickItem : PolymerBrick.registry.values()) {
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(brickItem.getItemStack(8)),
					new String[] { "xxx", "xyx", "xxx" },
					ImmutableMap.of(
							'x', brickItem.getItemStack(),
							'y', new ItemStack(Items.dye)), 0);
		}

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

		for (final Armor armor : Armor.registry.values()) {
			final ItemStack craftingItemStack = PolycraftRegistry.getItemStack(armor.craftingItemName, 1);
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(PolycraftRegistry.getItemStack(armor.getFullComponentName(ArmorSlot.HEAD), 1)),
					new String[] { "xxx", "x x", "   " },
					ImmutableMap.of('x', craftingItemStack), 0);
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(PolycraftRegistry.getItemStack(armor.getFullComponentName(ArmorSlot.CHEST), 1)),
					new String[] { "x x", "xxx", "xxx" },
					ImmutableMap.of('x', craftingItemStack), 0);
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(PolycraftRegistry.getItemStack(armor.getFullComponentName(ArmorSlot.LEGS), 1)),
					new String[] { "xxx", "x x", "x x" },
					ImmutableMap.of('x', craftingItemStack), 0);
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(PolycraftRegistry.getItemStack(armor.getFullComponentName(ArmorSlot.FEET), 1)),
					new String[] { "   ", "x x", "x x" },
					ImmutableMap.of('x', craftingItemStack), 0);
		}

		for (final Tool tool : Tool.registry.values()) {
			final ItemStack craftingHeadItemStack = PolycraftRegistry.getItemStack(tool.craftingHeadItemName, 1);
			final ItemStack craftingShaftItemStack = PolycraftRegistry.getItemStack(tool.craftingShaftItemName, 1);
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(PolycraftRegistry.getItemStack(tool.getFullTypeName(Tool.Type.HOE), 1)),
					new String[] { "xx ", " y ", " y " },
					ImmutableMap.of('x', craftingHeadItemStack, 'y', craftingShaftItemStack), 0);
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(PolycraftRegistry.getItemStack(tool.getFullTypeName(Tool.Type.HOE), 1)),
					new String[] { " xx", " y ", " y " },
					ImmutableMap.of('x', craftingHeadItemStack, 'y', craftingShaftItemStack), 0);
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(PolycraftRegistry.getItemStack(tool.getFullTypeName(Tool.Type.SWORD), 1)),
					new String[] { " x ", " x ", " y " },
					ImmutableMap.of('x', craftingHeadItemStack, 'y', craftingShaftItemStack), 0);
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(PolycraftRegistry.getItemStack(tool.getFullTypeName(Tool.Type.SHOVEL), 1)),
					new String[] { " x ", " y ", " y " },
					ImmutableMap.of('x', craftingHeadItemStack, 'y', craftingShaftItemStack), 0);
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(PolycraftRegistry.getItemStack(tool.getFullTypeName(Tool.Type.PICKAXE), 1)),
					new String[] { "xxx", " y ", " y " },
					ImmutableMap.of('x', craftingHeadItemStack, 'y', craftingShaftItemStack), 0);
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(PolycraftRegistry.getItemStack(tool.getFullTypeName(Tool.Type.AXE), 1)),
					new String[] { " xx", " yx", " y " },
					ImmutableMap.of('x', craftingHeadItemStack, 'y', craftingShaftItemStack), 0);
			PolycraftMod.recipeManager.addShapedRecipe(
					coloringFactory,
					PolycraftContainerType.CRAFTING_TABLE,
					ImmutableList.of(PolycraftRegistry.getItemStack(tool.getFullTypeName(Tool.Type.AXE), 1)),
					new String[] { "xx ", "xy ", " y " },
					ImmutableMap.of('x', craftingHeadItemStack, 'y', craftingShaftItemStack), 0);
		}

		//add all furnace recipes to the industrial oven
		for (final Object furnaceRecipeEntry : FurnaceRecipes.smelting().getSmeltingList().entrySet()) {
			final Map.Entry<ItemStack, ItemStack> furnaceRecipe = (Map.Entry<ItemStack, ItemStack>) furnaceRecipeEntry;
			try
			{
				PolycraftMod.recipeManager.addShapelessRecipe(
						PolycraftContainerType.INDUSTRIAL_OVEN,
						furnaceRecipe.getValue(),
						ImmutableList.of(furnaceRecipe.getKey()));
			} catch (final Exception e)
			{
				System.err.println("Unable to generate industrial oven recipe: " + furnaceRecipe.getKey().getDisplayName() + " => " + furnaceRecipe.getValue().getDisplayName());
				System.err.println(e.getMessage());
			}
		}
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
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolycraftMod.getFileSafeName(PolycraftContainerType.CRAFTING_TABLE.toString()))) {
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
								//System.out.println(inputItemName); //test output of each crafting recipes to catch mistakes
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
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolycraftMod.getFileSafeName(PolycraftContainerType.FURNACE.toString()))) {
			if (line.length > 4) {
				if (PolycraftMod.isVersionCompatible(PolycraftMod.getVersionNumeric(line[0]))) {
					final String outputItemName = line[2];
					final ItemStack outputItemStack = PolycraftRegistry.getItemStack(outputItemName, Integer.parseInt(line[3]));
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
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolycraftMod.getFileSafeName(PolycraftContainerType.MACHINING_MILL.toString()))) {
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
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolycraftMod.getFileSafeName(PolycraftContainerType.DISTILLATION_COLUMN.toString()))) {
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
						if (!StringUtils.isEmpty(outputItemName))
						{
							final ItemStack outputItemStack = PolycraftRegistry.getItemStack(outputItemName, Integer.parseInt(line[i + 1]));
							if (outputItemStack == null) {
								logger.warn("Unable to find output item for distillation recipe ({}): {}", inputItemName, outputItemName);
								outputItems = null;
								break;
							}
							else
								outputItems.add(outputItemStack);
						}
					}

					if (outputItems == null)
						continue;
					if (outputItems.size() == 0)
					{
						logger.warn("Distillation recipe missing outputs: {}", inputItemName);
						continue;
					}

					PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.DISTILLATION_COLUMN, ImmutableList.of(inputItemStack), outputItems);
				}
			}
		}
	}

	private static void generateFileRecipesCrack(final String directory) {
		final char[] shapedIdentifiers = new char[] { 'x', 'y' };
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolycraftMod.getFileSafeName(PolycraftContainerType.STEAM_CRACKER.toString()))) {
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
						if (!StringUtils.isEmpty(outputItemName))
						{
							final ItemStack outputItemStack = PolycraftRegistry.getItemStack(outputItemName, Integer.parseInt(line[i + 1]));
							if (outputItemStack == null) {
								logger.warn("Unable to find output item for cracking recipe ({}): {}", shapedInputs.values().toArray()[0], outputItemName);
								outputItems = null;
								break;
							}
							else
								outputItems.add(outputItemStack);
						}
					}

					if (outputItems == null)
						continue;
					if (outputItems.size() == 0)
					{
						logger.warn("Cracking recipe missing outputs: {}", shapedInputs.values().toArray()[0]);
						continue;
					}

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
		final char[] shapedIdentifiers = new char[] { 'x', 'y', 'z' };
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolycraftMod.getFileSafeName(PolycraftContainerType.MEROX_TREATMENT_UNIT.toString()))) {
			if (line.length > 10) {
				if (PolycraftMod.isVersionCompatible(PolycraftMod.getVersionNumeric(line[0]))) {
					Map<Character, ItemStack> shapedInputs = Maps.newHashMap();
					final StringBuilder inputShape = new StringBuilder();
					for (int i = 0; i < shapedIdentifiers.length; i++) {
						final String inputItemName = line[4 + (i * 2)];
						if (StringUtils.isEmpty(inputItemName))
						{
							if (i == 0)
							{
								logger.warn("Treating recipe missing first input!");
								shapedInputs = null;
								break;
							}
						}
						else
						{
							final ItemStack inputItemStack = PolycraftRegistry.getItemStack(inputItemName, Integer.parseInt(line[5 + (i * 2)]));
							if (inputItemStack == null) {
								logger.warn("Unable to find input item for treating recipe: {}", inputItemName);
								shapedInputs = null;
								break;
							}
							shapedInputs.put(shapedIdentifiers[i], inputItemStack);
							inputShape.append(shapedIdentifiers[i]);
						}
					}
					if (shapedInputs == null)
						continue;

					List<ItemStack> outputItems = Lists.newArrayList();
					for (int i = 10; i < line.length - 1; i += 2) {
						final String outputItemName = line[i];
						if (!StringUtils.isEmpty(outputItemName))
						{
							final ItemStack outputItemStack = PolycraftRegistry.getItemStack(outputItemName, Integer.parseInt(line[i + 1]));
							if (outputItemStack == null) {
								logger.warn("Unable to find output item for treating recipe ({}): {}", shapedInputs.values().toArray()[0], outputItemName);
								outputItems = null;
								break;
							}
							else
								outputItems.add(outputItemStack);
						}
					}

					if (outputItems == null)
						continue;
					if (outputItems.size() == 0)
					{
						logger.warn("Treating recipe missing outputs: {}", shapedInputs.values().toArray()[0]);
						continue;
					}

					PolycraftMod.recipeManager.addShapedRecipe(
							PolycraftContainerType.MEROX_TREATMENT_UNIT,
							outputItems,
							new String[] { inputShape.toString() },
							shapedInputs);
				}
			}
		}
	}

	private static void generateFileRecipesProcess(final String directory) {
		final char[] shapedIdentifiers = new char[] { 'v', 'w', 'x', 'y', 'z' };
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolycraftMod.getFileSafeName(PolycraftContainerType.CHEMICAL_PROCESSOR.toString()))) {
			if (line.length > 14) {
				if (PolycraftMod.isVersionCompatible(PolycraftMod.getVersionNumeric(line[0]))) {
					Map<Character, ItemStack> shapedInputs = Maps.newHashMap();
					final StringBuilder inputShape = new StringBuilder();
					for (int i = 0; i < shapedIdentifiers.length; i++) {
						final String inputItemName = line[4 + (i * 2)];
						if (StringUtils.isEmpty(inputItemName))
						{
							if (i == 0)
							{
								logger.warn("Processing recipe missing first input!");
								shapedInputs = null;
								break;
							}
						}
						else
						{
							final ItemStack inputItemStack = PolycraftRegistry.getItemStack(inputItemName, Integer.parseInt(line[5 + (i * 2)]));
							if (inputItemStack == null) {
								logger.warn("Unable to find input item for processing recipe: {}", inputItemName);
								shapedInputs = null;
								break;
							}
							shapedInputs.put(shapedIdentifiers[i], inputItemStack);
							inputShape.append(shapedIdentifiers[i]);
						}
					}
					if (shapedInputs == null)
						continue;

					List<ItemStack> outputItems = Lists.newArrayList();
					for (int i = 14; i < line.length - 1; i += 2) {
						final String outputItemName = line[i];
						if (!StringUtils.isEmpty(outputItemName))
						{
							final ItemStack outputItemStack = PolycraftRegistry.getItemStack(outputItemName, Integer.parseInt(line[i + 1]));
							if (outputItemStack == null) {
								logger.warn("Unable to find output item for processing recipe ({}): {}", shapedInputs.values().toArray()[0], outputItemName);
								outputItems = null;
								break;
							}
							else
								outputItems.add(outputItemStack);
						}
					}

					if (outputItems == null)
						continue;
					if (outputItems.size() == 0)
					{
						logger.warn("Processing recipe missing outputs: {}", shapedInputs.values().toArray()[0]);
						continue;
					}

					final String[] inputShapes = new String[inputShape.length() > 3 ? 2 : 1];
					if (inputShapes.length == 1)
						inputShapes[0] = inputShape.toString();
					else
					{
						inputShapes[0] = inputShape.substring(0, 3);
						inputShapes[1] = inputShape.substring(3);
					}

					final String inputShapeRow1 = inputShape.toString();
					final String inputShapeRow2 = inputShape.toString();

					PolycraftMod.recipeManager.addShapedRecipe(
							PolycraftContainerType.CHEMICAL_PROCESSOR,
							outputItems,
							inputShapes,
							shapedInputs);
				}
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
				PolymerSlab.registry.get("Slab (Natural Rubber)").getItemStack(),
				PolymerBlock.registry.get("Block (Natural Rubber)").getItemStack(),
				CustomObject.registry.get("Phase Shifter").getItemStack(),
				MoldedItem.registry.get("Life Preserver (Natural Rubber)").getItemStack(),
				MoldedItem.registry.get("Running Shoes (Walker)").getItemStack(),
				MoldedItem.registry.get("Scuba Fins (Beginner)").getItemStack(),
				MoldedItem.registry.get("Scuba Mask (Beginner)").getItemStack(),
				CustomObject.registry.get("Scuba Tank (Beginner)").getItemStack(),
				CustomObject.registry.get("Parachute").getItemStack(),
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
