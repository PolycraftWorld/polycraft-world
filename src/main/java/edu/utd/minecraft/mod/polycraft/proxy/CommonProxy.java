package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockCompound;
import edu.utd.minecraft.mod.polycraft.block.BlockCompressed;
import edu.utd.minecraft.mod.polycraft.block.BlockFluid;
import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.block.BlockPlastic;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;
import edu.utd.minecraft.mod.polycraft.config.Compound;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.Element;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.Plastic;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.handler.GuiHandler;
import edu.utd.minecraft.mod.polycraft.handler.PolycraftEventHandler;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.BlockChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.ChemicalProcessorRecipe;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.RenderChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.TileEntityChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.item.ItemFluidContainer;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemKevlarVest;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemWorn;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeInitializer;
import edu.utd.minecraft.mod.polycraft.worldgen.OilPopulate;
import edu.utd.minecraft.mod.polycraft.worldgen.OreWorldGenerator;

public class CommonProxy {

	public void preInit() {
		createBiomes();
		createFluids();
		createBlocks();
		createOres();
		createIngots();
		createCompressedBlocks();
		createCatalysts();
		createPlastics();
		createTools();
		createInventories();
		createClothes(); // TODO fix the Clothes Class
	}

	public void init() {
		createOreRecipes();
		createCompressedBlockRecipes();
		createPlasticRecipes();
		createFluidRecipes();
		createChemicalProcessorRecipes();
		createCheatRecipes();

		GameRegistry.registerWorldGenerator(new OreWorldGenerator(), PolycraftMod.oreWorldGeneratorWeight);
		RenderingRegistry.registerBlockHandler(PolycraftMod.renderChemicalProcessorID, RenderChemicalProcessor.INSTANCE);
		NetworkRegistry.INSTANCE.registerGuiHandler(PolycraftMod.instance, new GuiHandler());
	}

	public void postInit() {
		MinecraftForge.EVENT_BUS.register(new PolycraftEventHandler());
		MinecraftForge.EVENT_BUS.register(OilPopulate.INSTANCE);
		MinecraftForge.TERRAIN_GEN_BUS.register(new BiomeInitializer());
	}

	private void createBiomes() {
		class BiomeIdException extends RuntimeException {
			public BiomeIdException(String biome, int id) {
				super(String.format("You have a Biome Id conflict at %d for %s", id, biome));
			}
		}

		if (PolycraftMod.oilDesertBiomeId > 0) {
			if (BiomeGenBase.getBiomeGenArray()[PolycraftMod.oilDesertBiomeId] != null) {
				throw new BiomeIdException("oilDesert", PolycraftMod.oilDesertBiomeId);
			}
			PolycraftMod.biomeOilDesert = BiomeGenOilDesert.makeBiome(PolycraftMod.oilDesertBiomeId);
		}

		if (PolycraftMod.oilOceanBiomeId > 0) {
			if (BiomeGenBase.getBiomeGenArray()[PolycraftMod.oilOceanBiomeId] != null) {
				throw new BiomeIdException("oilOcean", PolycraftMod.oilOceanBiomeId);
			}
			PolycraftMod.biomeOilOcean = BiomeGenOilOcean.makeBiome(PolycraftMod.oilOceanBiomeId);
		}
	}

	private void createFluids() {
		final Fluid fluidOil = new Fluid("oil").setDensity(PolycraftMod.oilFluidDensity).setViscosity(PolycraftMod.oilFluidViscosity);
		FluidRegistry.registerFluid(fluidOil);

		PolycraftMod.blockOil = PolycraftMod.registerBlock("oil", new BlockFluid(fluidOil, Material.water).setFlammable(true).setFlammability(PolycraftMod.oilBlockFlammability).setParticleColor(0.7F, 0.7F, 0.0F));
		fluidOil.setBlock(PolycraftMod.blockOil);

		PolycraftMod.itemBucketOil = PolycraftMod.registerItem("bucket_oil", new ItemBucket(PolycraftMod.blockOil).setContainerItem(Items.bucket).setTextureName(PolycraftMod.getTextureName("bucket_oil")));
		FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("oil", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(PolycraftMod.itemBucketOil), new ItemStack(Items.bucket));
		BucketHandler.INSTANCE.buckets.put(PolycraftMod.blockOil, PolycraftMod.itemBucketOil);
		MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

		PolycraftMod.itemFluidContainer = PolycraftMod.registerItem(PolycraftMod.itemFluidContainerName, new ItemFluidContainer());
		PolycraftMod.registerItem(PolycraftMod.itemFluidContainerName + "_nozzle", new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(PolycraftMod.itemFluidContainerName + "_nozzle")));

		for (final Element element : Element.registry.values())
			if (element.fluid)
				PolycraftMod.registerItem(ItemFluidContainer.getGameName(element), new ItemFluidContainer());
		for (final Compound compound : Compound.registry.values())
			if (compound.fluid)
				PolycraftMod.registerItem(ItemFluidContainer.getGameName(compound), new ItemFluidContainer());
	}

	private void createBlocks() {
		for (final Compound compound : Compound.registry.values())
			if (!compound.fluid)
				PolycraftMod.registerBlock(compound.gameName, new BlockCompound(compound));
	}

	private void createOres() {
		for (final Ore ore : Ore.registry.values())
			PolycraftMod.registerBlock(ore.gameName, new BlockOre(ore));
	}

	private void createIngots() {
		for (final Ingot ingot : Ingot.registry.values())
			PolycraftMod.registerItem(ingot.gameName, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(ingot.gameName)));
	}

	private void createCompressedBlocks() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			PolycraftMod.registerBlock(compressedBlock.gameName, new BlockCompressed(compressedBlock));
	}

	private void createCatalysts() {
		for (final Catalyst catalyst : Catalyst.registry.values())
			PolycraftMod.registerItem(catalyst.gameName, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(catalyst.gameName)));
	}

	private void createPlastics() {
		for (final Plastic plastic : Plastic.registry.values()) {
			PolycraftMod.registerBlock(plastic.gameName, new BlockPlastic(plastic));
			PolycraftMod.registerItem(plastic.itemNamePellet, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(plastic.itemNamePellet.replaceAll("_[0-9]", ""))));
			PolycraftMod.registerItem(plastic.itemNameFiber, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(plastic.itemNameFiber.replaceAll("_[0-9]", ""))));

			// TODO need to create textures for all colors
			PolycraftMod.registerItem(plastic.itemNameKevlarVest, new ItemKevlarVest(plastic).setTextureName(PolycraftMod.getTextureName("kevlar_vest")));
			PolycraftMod.registerItem(plastic.itemNameRunningShoes, new ItemRunningShoes(plastic).setTextureName(PolycraftMod.getTextureName("running_shoes")));
			PolycraftMod.registerItem(plastic.itemNameJetPack, new ItemJetPack(plastic, 10).setTextureName(PolycraftMod.getTextureName("jet_pack")));

			// this makes only one grip object for each type of plastic color
			if (plastic.isDefaultColor())
				PolycraftMod.registerItem(plastic.itemNameGrip, new Item().setCreativeTab(CreativeTabs.tabTools).setTextureName(PolycraftMod.getTextureName("plastic_grip")));
		}
	}

	private void createTools() {
		for (final Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet()) {
			final String materialName = materialEntry.getKey();
			final ToolMaterial material = materialEntry.getValue();
			for (final Plastic plastic : Plastic.registry.values())
				for (final String type : ItemGripped.allowedTypes.keySet()) {
					if (plastic.isDefaultColor())
						PolycraftMod.registerItem(ItemGripped.getName(plastic, materialName, type), ItemGripped.create(type, materialName, material, plastic.gripDurabilityBuff));
				}
		}
	}

	private void createClothes() {
		for (final Entry<String, ArmorMaterial> materialEntry : ItemWorn.allowedMaterials.entrySet()) {
			final String materialName = materialEntry.getKey();
			final ArmorMaterial material = materialEntry.getValue();
			for (final Plastic plastic : Plastic.registry.values())
				for (final String type : ItemWorn.allowedTypes.keySet()) {

					for (int bodyLocation = 0; bodyLocation < 3; bodyLocation++) {
						PolycraftMod.registerItem(ItemWorn.getName(plastic, materialName, type, bodyLocation), ItemWorn.create(type, materialName, material, plastic.gripDurabilityBuff, bodyLocation));
					}

				}
		}
	}

	private void createInventories() {
		PolycraftMod.blockChemicalProcessor = PolycraftMod.registerBlock("chemical_processor", new BlockChemicalProcessor(false));
		PolycraftMod.blockChemicalProcessorActive = PolycraftMod.registerBlock("chemical_processor_active", new BlockChemicalProcessor(true));
		GameRegistry.registerTileEntity(TileEntityChemicalProcessor.class, "tile_entity_chemical_processor");
	}

	private void createOreRecipes() {
		for (final Ore ore : Ore.registry.values())
			if (ore.smeltingEntity != null)
				GameRegistry.addSmelting(PolycraftMod.blocks.get(ore.gameName),
						ore.smeltingEntityIsItem ? new ItemStack(PolycraftMod.items.get(ore.smeltingEntity.gameName), ore.smeltingEntitiesPerBlock) : new ItemStack(PolycraftMod.blocks.get(ore.smeltingEntity.gameName),
								ore.smeltingEntitiesPerBlock), ore.smeltingExperience);
	}

	private void createCompressedBlockRecipes() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values()) {
			final ItemStack blockCompressed = new ItemStack(PolycraftMod.blocks.get(compressedBlock.gameName));
			final Item compressedItem = PolycraftMod.items.get(compressedBlock.type.gameName);
			final Object[] compressedItems = new ItemStack[compressedBlock.itemsPerBlock];
			for (int i = 0; i < compressedItems.length; i++)
				compressedItems[i] = new ItemStack(compressedItem);
			GameRegistry.addShapelessRecipe(blockCompressed, compressedItems);
			GameRegistry.addShapelessRecipe(new ItemStack(compressedItem, compressedBlock.itemsPerBlock), blockCompressed);
		}
	}

	private void createPlasticRecipes() {
		// anything you put in here will be able to be crafted with all plastics of all colors
		for (final Plastic plastic : Plastic.registry.values()) {

			// there is plastic block corresponding to each type and each color of plastic
			final Block plasticBlock = PolycraftMod.blocks.get(plastic.gameName);

			// there are plastic pellets and fibers corresponding to each type and each color of plastic
			final Item plasticPellet = PolycraftMod.items.get(plastic.itemNamePellet);
			final Item plasticFiber = PolycraftMod.items.get(plastic.itemNameFiber);

			final Item plasticGrip = PolycraftMod.items.get(plastic.itemNameGrip);
			final Item kevlarVest = PolycraftMod.items.get(plastic.itemNameKevlarVest);
			final Item runningShoes = PolycraftMod.items.get(plastic.itemNameRunningShoes);
			final Item jetPack = PolycraftMod.items.get(plastic.itemNameJetPack);

			// three diagonal pellets give you a fiber
			GameRegistry.addRecipe(new ItemStack(plasticFiber, plastic.craftingFibersPerPellet), "x  ", " x ", "  x", 'x', new ItemStack(plasticPellet));

			// a fiber can be back converted into pellets
			GameRegistry.addShapelessRecipe(new ItemStack(plasticPellet), new ItemStack(plasticFiber));

			GameRegistry.addShapelessRecipe(new ItemStack(plasticPellet, plastic.craftingPelletsPerBlock), new ItemStack(plasticBlock));
			GameRegistry.addRecipe(new ItemStack(plasticGrip), "x x", "x x", "xxx", 'x', new ItemStack(plasticPellet));
			GameRegistry.addRecipe(new ItemStack(kevlarVest), "x x", "xxx", "xxx", 'x', new ItemStack(plasticFiber, 4));
			GameRegistry.addRecipe(new ItemStack(runningShoes), "   ", "x x", "x x", 'x', new ItemStack(plasticFiber, 2));
			// TODO need to allow refilling tanks (like repairing an item on an anvil?)
			GameRegistry.addRecipe(new ItemStack(jetPack),
					"xzx", "yxy", "xzx",
					'x', new ItemStack(plasticFiber, 8),
					'y', new ItemStack(PolycraftMod.getItemFluidContainer(Compound.kerosene), 2),
					'z', new ItemStack(PolycraftMod.items.get(Ingot.aluminum.gameName), 8));

			// this only builds new tools for each type of plastic, not each color...
			if (plastic.isDefaultColor())
				for (final String materialName : ItemGripped.allowedMaterials.keySet())
					for (final String type : ItemGripped.allowedTypes.keySet())
						GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.items.get(ItemGripped.getName(plastic, materialName, type))),
								new ItemStack((Item) Item.itemRegistry.getObject(ItemGripped.getNameBase(materialName, type))), new ItemStack(plasticGrip));
		}
	}

	private void createFluidRecipes() {
		final Item fluidContainerNozzle = PolycraftMod.items.get(PolycraftMod.itemFluidContainerName + "_nozzle");
		GameRegistry.addRecipe(new ItemStack(fluidContainerNozzle),
				"xxx", " x ", " x ",
				'x', new ItemStack(PolycraftMod.items.get(Ingot.copper.gameName)));

		GameRegistry.addRecipe(new ItemStack(PolycraftMod.itemFluidContainer),
				" y ", "x x", " x ",
				'x', new ItemStack(PolycraftMod.items.get(Ingot.steel.gameName)),
				'y', new ItemStack(fluidContainerNozzle));
	}

	private void createChemicalProcessorRecipes() {
		GameRegistry.addRecipe(new ItemStack(PolycraftMod.blockChemicalProcessor),
				"xxx", "xzx", "xyx",
				'x', new ItemStack(PolycraftMod.items.get(Ingot.steel.gameName)),
				'y', new ItemStack(Blocks.furnace),
				'z', new ItemStack(Blocks.glass_pane));

		createAlloyRecipes();
		createCatalystRecipes();
		createWaterRecipes();
		createMineralRecipes();
		createCompoundRecipes();
	}

	private void createAlloyRecipes() {
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(Blocks.iron_block, 9),
						new ItemStack(PolycraftMod.items.get(Ingot.carbon.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.blocks.get(CompressedBlock.steel.gameName), 9)
				}));
	}

	private void createCatalystRecipes() {
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Ingot.platinum.gameName)) },
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Catalyst.platinum.gameName), 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Ingot.titanium.gameName)) },
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Catalyst.titanium.gameName), 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Ingot.palladium.gameName)) },
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Catalyst.palladium.gameName), 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Ingot.cobalt.gameName)) },
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Catalyst.cobalt.gameName), 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Ingot.manganese.gameName)) },
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Catalyst.manganese.gameName), 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Ingot.magnesium.gameName)) },
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Catalyst.magnesiumOxide.gameName), 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Ingot.antimony.gameName)) },
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Catalyst.antimonyTrioxide.gameName), 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Element.chlorine)),
						new ItemStack(PolycraftMod.items.get(Ingot.copper.gameName))
				},
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Catalyst.copperIIChloride.gameName), 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Element.chlorine)),
						new ItemStack(Items.iron_ingot)
				},
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Catalyst.ironIIIChloride.gameName), 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(Items.iron_ingot) },
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Catalyst.ironIIIOxide.gameName), 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.items.get(Catalyst.titanium.gameName)),
						new ItemStack(PolycraftMod.items.get(Ingot.aluminum.gameName)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.olefins))
				},
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Catalyst.zieglerNatta.gameName), 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.items.get(Catalyst.cobalt.gameName)),
						new ItemStack(PolycraftMod.items.get(Catalyst.manganese.gameName)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Element.bromine))
				},
				new ItemStack[] { new ItemStack(PolycraftMod.items.get(Catalyst.cobaltManganeseBromide.gameName), 16) }));
	}

	private void createWaterRecipes() {
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(Items.water_bucket)
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.h2o), 4),
						new ItemStack(Items.bucket)
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.h2o))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Element.chlorine), 10),
						new ItemStack(PolycraftMod.getItemFluidContainer(Element.bromine)),
						new ItemStack(Items.bucket)
				}));
	}

	private void createMineralRecipes() {
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(PolycraftMod.blocks.get(Ore.shale.gameName)) },
				new ItemStack[] { new ItemStack(PolycraftMod.getItemFluidContainer(Compound.naturalGas)) }));
	}

	private void createCompoundRecipes() {
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.naturalGas))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethane)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.propane)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.butane)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.methane))
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.itemBucketOil) },
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.naphtha)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.gasOil)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.btx)),
						new ItemStack(PolycraftMod.blocks.get(Compound.bitumen.gameName)),
						new ItemStack(Items.bucket)
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethane)),
						new ItemStack(PolycraftMod.items.get(Catalyst.platinum.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethylene), 2)
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.propane)),
						new ItemStack(PolycraftMod.items.get(Catalyst.platinum.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.propylene), 2)
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.butane)),
						new ItemStack(PolycraftMod.items.get(Catalyst.platinum.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.butadiene), 2)
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethylene)),
						new ItemStack(PolycraftMod.items.get(Catalyst.zieglerNatta.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.blocks.get(Plastic.getDefaultForType(2).gameName), 16)
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethylene))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.blocks.get(Plastic.getDefaultForType(4).gameName), 16)
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethylene)),
						new ItemStack(PolycraftMod.items.get(Catalyst.palladium.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethyleneOxide))
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethyleneOxide)),
						new ItemStack(Items.water_bucket)
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethyleneGlycol))
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.naphtha)),
						new ItemStack(PolycraftMod.items.get(Catalyst.platinum.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.olefins)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.paraffin))
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.gasOil)),
						new ItemStack(PolycraftMod.items.get(Catalyst.platinum.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.diesel)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.kerosene))
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.btx)),
						new ItemStack(PolycraftMod.items.get(Catalyst.cobaltManganeseBromide.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.terephthalicAcid))
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.terephthalicAcid)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.methanol))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.dimethylTerephthalate), 2)
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.dimethylTerephthalate)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethyleneGlycol), 2),
						new ItemStack(PolycraftMod.items.get(Catalyst.magnesiumOxide.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.blocks.get(Plastic.getDefaultForType(4).gameName), 16),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.methanol), 2),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethyleneGlycol))

				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.dimethylTerephthalate)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethyleneGlycol), 2),
						new ItemStack(PolycraftMod.items.get(Catalyst.antimonyTrioxide.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.blocks.get(Plastic.getDefaultForType(4).gameName), 16),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.methanol), 2),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethyleneGlycol))

				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Element.chlorine)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethylene), 2),
						new ItemStack(PolycraftMod.items.get(Catalyst.copperIIChloride.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.edc), 2)

				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Element.chlorine)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethylene), 2),
						new ItemStack(PolycraftMod.items.get(Catalyst.ironIIIChloride.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.edc), 2)

				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.edc))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.vinylChloride)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.hcl))

				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.acetylene)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.hcl))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.vinylChloride))

				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.methane))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.acetylene))

				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Element.chlorine), 16)
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.cl2))

				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.h2)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.cl2))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.hcl), 16)

				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.vinylChloride)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.h2o), 3)
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.blocks.get(Plastic.getDefaultForType(3).gameName), 16),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.h2o), 3)

				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(Items.iron_ingot),
						new ItemStack(Items.lava_bucket)
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.sulfuricAcid), 32),
						new ItemStack(Items.bucket)
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethylene)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.benzene)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.sulfuricAcid))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethylbenzene), 2)
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.ethylbenzene)),
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.h2o), 10),
						new ItemStack(Items.coal),
						new ItemStack(PolycraftMod.items.get(Catalyst.ironIIIOxide.gameName))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.styrene))
				}));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] {
						new ItemStack(PolycraftMod.getItemFluidContainer(Compound.styrene))
				},
				new ItemStack[] {
						new ItemStack(PolycraftMod.blocks.get(Plastic.getDefaultForType(6).gameName), 16)
				}));
	}

	private void createCheatRecipes() {
		if (PolycraftMod.cheatRecipesEnabled) {
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.torch, 64), new ItemStack(Blocks.cobblestone));
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.furnace), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone));
			GameRegistry.addShapelessRecipe(new ItemStack(Items.coal, 64), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.cobblestone));

			final ItemStack dirtStack = new ItemStack(Blocks.dirt);
			final Collection<ItemStack> dirtStacks = new ArrayList<ItemStack>();

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.blockChemicalProcessor), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.getItemFluidContainer(Compound.kerosene), 64), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Items.coal, 64), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.items.get("plastic_7_white_jet_pack")), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.crafting_table), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.items.get("plastic_7_white_kevlar_vest")), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.items.get("plastic_7_white_running_shoes")), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			final ItemStack oilBucketStack = new ItemStack(PolycraftMod.itemBucketOil);
			GameRegistry.addShapelessRecipe(oilBucketStack, dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(Items.diamond_pickaxe), dirtStacks.toArray());

			final Collection<ItemStack> oilBuckets = new ArrayList<ItemStack>();
			for (final Plastic plastic : Plastic.registry.values()) {
				oilBuckets.add(oilBucketStack);
				GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.blocks.get(plastic.gameName)), oilBuckets.toArray());
			}
		}
	}
}
