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
import edu.utd.minecraft.mod.polycraft.block.BlockCompressed;
import edu.utd.minecraft.mod.polycraft.block.BlockFluid;
import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;
import edu.utd.minecraft.mod.polycraft.config.Compound;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.Element;
import edu.utd.minecraft.mod.polycraft.config.Entity;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.Polymer;
import edu.utd.minecraft.mod.polycraft.config.Polymer.ResinCode;
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
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaMask;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeInitializer;
import edu.utd.minecraft.mod.polycraft.worldgen.OilPopulate;
import edu.utd.minecraft.mod.polycraft.worldgen.OreWorldGenerator;

public class CommonProxy {

	public void preInit() {
		createBiomes();
		createFluids();
		createCustomItems();
		createElements();
		createCompounds();
		createPolymers();
		createOres();
		createIngots();
		createCompressedBlocks();
		createCatalysts();
		createInventories();
	}

	public void init() {
		createCustomItemRecipes();
		createOreRecipes();
		createCompressedBlockRecipes();
		createPolymerRecipes();
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

	private ItemStack createItemStack(final Entity entity) {
		return createItemStack(entity, 1);
	}

	private ItemStack createItemStack(final Entity entity, int size) {
		if ((entity instanceof Element && ((Element) entity).fluid) ||
				(entity instanceof Compound && ((Compound) entity).fluid))
			return new ItemStack(PolycraftMod.items.get(ItemFluidContainer.getGameName(entity)), size);
		if (entity instanceof Ingot || entity instanceof Catalyst)
			return new ItemStack(PolycraftMod.items.get(entity.gameName), size);
		return new ItemStack(PolycraftMod.blocks.get(entity.gameName), size);
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

	}

	private void createCustomItems() {
		PolycraftMod.itemFluidContainer = PolycraftMod.registerItem(PolycraftMod.itemNameFluidContainer, new ItemFluidContainer());
		PolycraftMod.itemFluidContainerNozzle = PolycraftMod.registerItem(PolycraftMod.itemNameFluidContainer + "_nozzle",
				new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(PolycraftMod.itemNameFluidContainer + "_nozzle")));

		PolycraftMod.itemRunningShoes = PolycraftMod.registerItem("running_shoes", new ItemRunningShoes(PolycraftMod.itemRunningShoesWalkSpeedBuff));
		PolycraftMod.itemKevlarVest = PolycraftMod.registerItem("kevlar_vest", new ItemKevlarVest());
		PolycraftMod.itemJetPack = PolycraftMod.registerItem("jet_pack", new ItemJetPack(PolycraftMod.itemJetPackFuelUnitsFull, PolycraftMod.itemJetPackFuelUnitsBurnPerTick, PolycraftMod.itemJetPackFlySpeedBuff));
		PolycraftMod.itemParachute = PolycraftMod.registerItem("parachute", new ItemParachute(PolycraftMod.itemParachuteDescendVelocity));
		PolycraftMod.itemScubaMask = PolycraftMod.registerItem("scuba_mask", new ItemScubaMask());
		PolycraftMod.itemScubaTank = PolycraftMod.registerItem("scuba_tank", new ItemScubaTank(PolycraftMod.itemScubaTankAirUnitsFull, PolycraftMod.itemScubaTankAirUnitsConsumePerTick));
		PolycraftMod.itemScubaFins = PolycraftMod.registerItem("scuba_fins", new ItemScubaFins(PolycraftMod.itemScubaFinsSwimSpeedBuff));

		for (final Polymer polymer : ItemGripped.allowedPolymers) {
			PolycraftMod.registerItem(polymer.gameName + "_grip", new Item().setCreativeTab(CreativeTabs.tabTools).setTextureName(PolycraftMod.getTextureName("polymer_grip")));
			for (final Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet()) {
				final String materialName = materialEntry.getKey();
				final ToolMaterial material = materialEntry.getValue();
				for (final String type : ItemGripped.allowedTypes.keySet())
					PolycraftMod.registerItem(ItemGripped.getName(polymer, materialName, type), ItemGripped.create(type, materialName, material, PolycraftMod.itemGrippedToolDurabilityBuff));
			}
		}
	}

	private void createElements() {
		for (final Element element : Element.registry.values())
			if (element.fluid)
				PolycraftMod.registerItem(ItemFluidContainer.getGameName(element), new ItemFluidContainer());
	}

	private void createCompounds() {
		for (final Compound compound : Compound.registry.values())
			if (compound.fluid)
				PolycraftMod.registerItem(ItemFluidContainer.getGameName(compound), new ItemFluidContainer());
	}

	private void createPolymers() {
		for (final Polymer polymer : Polymer.registry.values()) {
			//TODO remove this check when forge fixes their bug for EntityEnderMan.carriableBlocks array index out of bounds exception (cannot make blocks with ids bigger than 255!)
			if (polymer.resinCode != ResinCode.NONE)
				PolycraftMod.registerBlock(polymer, new BlockPolymer(polymer));
			PolycraftMod.registerItem(polymer.itemNamePellet, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName("polymer_pellet")));
			PolycraftMod.registerItem(polymer.itemNameFiber, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName("polymer_fiber")));
		}
	}

	private void createCatalysts() {
		for (final Catalyst catalyst : Catalyst.registry.values())
			PolycraftMod.registerItem(catalyst, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName("catalyst")));
	}

	private void createOres() {
		for (final Ore ore : Ore.registry.values())
			PolycraftMod.registerBlock(ore, new BlockOre(ore));
	}

	private void createIngots() {
		for (final Ingot ingot : Ingot.registry.values())
			PolycraftMod.registerItem(ingot, new Item().setCreativeTab(CreativeTabs.tabMaterials).setTextureName(PolycraftMod.getTextureName(ingot.gameName)));
	}

	private void createCompressedBlocks() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			PolycraftMod.registerBlock(compressedBlock, new BlockCompressed(compressedBlock));
	}

	private void createInventories() {
		PolycraftMod.blockChemicalProcessor = PolycraftMod.registerBlock("chemical_processor", new BlockChemicalProcessor(false));
		PolycraftMod.blockChemicalProcessorActive = PolycraftMod.registerBlock("chemical_processor_active", new BlockChemicalProcessor(true));
		GameRegistry.registerTileEntity(TileEntityChemicalProcessor.class, "tile_entity_chemical_processor");
	}

	private void createCustomItemRecipes() {
		GameRegistry.addRecipe(new ItemStack(PolycraftMod.itemFluidContainerNozzle), "xxx", " x ", " x ", 'x', createItemStack(Ingot.copper));
		GameRegistry.addRecipe(new ItemStack(PolycraftMod.itemFluidContainer), " y ", "x x", " x ",
				'x', createItemStack(Ingot.steel),
				'y', new ItemStack(PolycraftMod.itemFluidContainerNozzle));

		GameRegistry.addRecipe(new ItemStack(PolycraftMod.itemKevlarVest), "x x", "xxx", "xxx",
				'x', new ItemStack(PolycraftMod.items.get(Polymer.kevlar.itemNameFiber), 4));

		GameRegistry.addRecipe(new ItemStack(PolycraftMod.itemRunningShoes), "   ", "x x", "x x",
				'x', new ItemStack(PolycraftMod.items.get(Polymer.LDPE.itemNameFiber), 2));

		GameRegistry.addRecipe(new ItemStack(PolycraftMod.itemJetPack), "xzx", "yxy", "xzx",
				'x', new ItemStack(PolycraftMod.items.get(Polymer.HDPE.itemNameFiber), 8),
				'y', createItemStack(Element.hydrogen, 2),
				'z', createItemStack(Ingot.aluminium, 8));
		// allow refilling tanks
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(PolycraftMod.itemJetPack), createItemStack(Element.hydrogen, 4) },
				new ItemStack[] { new ItemStack(PolycraftMod.itemJetPack) }));

		GameRegistry.addRecipe(new ItemStack(PolycraftMod.itemParachute), "xxx", "x x", " x ",
				'x', new ItemStack(PolycraftMod.items.get(Polymer.nylon11.itemNameFiber), 8));

		GameRegistry.addRecipe(new ItemStack(PolycraftMod.itemScubaMask), "xxx", "xyx", "xxx",
				'x', new ItemStack(PolycraftMod.items.get(Polymer.LDPE.itemNameFiber)),
				'y', new ItemStack(Blocks.glass_pane));

		GameRegistry.addRecipe(new ItemStack(PolycraftMod.itemScubaTank), "xzx", "yxy", "x x",
				'x', new ItemStack(PolycraftMod.items.get(Polymer.HDPE.itemNameFiber), 8),
				'y', createItemStack(Element.oxygen));
		// allow refilling tanks
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(PolycraftMod.itemScubaTank), createItemStack(Element.oxygen, 2) },
				new ItemStack[] { new ItemStack(PolycraftMod.itemScubaTank) }));

		GameRegistry.addRecipe(new ItemStack(PolycraftMod.itemScubaFins), "x x", "x x", "x x",
				'x', new ItemStack(PolycraftMod.items.get(Polymer.LDPE.itemNameFiber)));

		for (final Polymer polymer : ItemGripped.allowedPolymers) {
			final Item polymerGrip = PolycraftMod.items.get(polymer.gameName + "_grip");
			GameRegistry.addRecipe(new ItemStack(polymerGrip), "x x", "x x", "xxx", 'x', new ItemStack(PolycraftMod.items.get(polymer.itemNamePellet)));
			for (final Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet()) {
				final String materialName = materialEntry.getKey();
				final ToolMaterial material = materialEntry.getValue();
				for (final String type : ItemGripped.allowedTypes.keySet())
					GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.items.get(ItemGripped.getName(polymer, materialName, type))),
							new ItemStack((Item) Item.itemRegistry.getObject(ItemGripped.getNameBase(materialName, type))), new ItemStack(polymerGrip));
			}
		}
	}

	private void createOreRecipes() {
		for (final Ore ore : Ore.registry.values())
			if (ore.smeltingEntity != null)
				GameRegistry.addSmelting(PolycraftMod.blocks.get(ore.gameName), ore.smeltingEntityIsItem
						? createItemStack(ore.smeltingEntity, ore.smeltingEntitiesPerBlock)
						: createItemStack(ore.smeltingEntity, ore.smeltingEntitiesPerBlock), ore.smeltingExperience);
	}

	private void createCompressedBlockRecipes() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values()) {
			final ItemStack blockCompressed = createItemStack(compressedBlock);
			final Item compressedItem = PolycraftMod.items.get(compressedBlock.type.gameName);
			final Object[] compressedItems = new ItemStack[compressedBlock.itemsPerBlock];
			for (int i = 0; i < compressedItems.length; i++)
				compressedItems[i] = new ItemStack(compressedItem);
			GameRegistry.addShapelessRecipe(blockCompressed, compressedItems);
			GameRegistry.addShapelessRecipe(new ItemStack(compressedItem, compressedBlock.itemsPerBlock), blockCompressed);
		}
	}

	private void createPolymerRecipes() {
		for (final Polymer polymer : Polymer.registry.values()) {
			final Block polymerBlock = PolycraftMod.blocks.get(polymer.gameName);
			final Item polymerPellet = PolycraftMod.items.get(polymer.itemNamePellet);
			final Item polymerFiber = PolycraftMod.items.get(polymer.itemNameFiber);
			// convert between blocks and pellets
			GameRegistry.addShapelessRecipe(new ItemStack(polymerPellet, polymer.craftingPelletsPerBlock), new ItemStack(polymerBlock));
			GameRegistry.addShapelessRecipe(new ItemStack(polymerBlock), new ItemStack(polymerPellet, polymer.craftingPelletsPerBlock));
			// convert between pellets and fibers
			GameRegistry.addRecipe(new ItemStack(polymerFiber), "x  ", " x ", "  x", 'x', new ItemStack(polymerPellet));
			GameRegistry.addShapelessRecipe(new ItemStack(polymerPellet, 3), new ItemStack(polymerFiber));
		}
	}

	private void createChemicalProcessorRecipes() {
		GameRegistry.addRecipe(new ItemStack(PolycraftMod.blockChemicalProcessor), "xxx", "xzx", "xyx",
				'x', createItemStack(Ingot.steel),
				'y', new ItemStack(Blocks.furnace),
				'z', new ItemStack(Blocks.glass_pane));

		createAlloyRecipes();
		createCatalystRecipes();
		createMineralRecipes();
		createElementRecipes();
		createCompoundRecipes();
	}

	private void createAlloyRecipes() {
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(Blocks.iron_block, 9), createItemStack(Ingot.carbon) },
				new ItemStack[] { createItemStack(CompressedBlock.steel, 9) }));
	}

	private void createCatalystRecipes() {
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Ingot.platinum) },
				new ItemStack[] { createItemStack(Catalyst.platinum, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Ingot.titanium) },
				new ItemStack[] { createItemStack(Catalyst.titanium, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Ingot.palladium) },
				new ItemStack[] { createItemStack(Catalyst.palladium, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Ingot.cobalt) },
				new ItemStack[] { createItemStack(Catalyst.cobalt, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Ingot.manganese) },
				new ItemStack[] { createItemStack(Catalyst.manganese, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Ingot.magnesium) },
				new ItemStack[] { createItemStack(Catalyst.magnesiumOxide, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Ingot.antimony) },
				new ItemStack[] { createItemStack(Catalyst.antimonyTrioxide, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Element.chlorine), createItemStack(Ingot.copper) },
				new ItemStack[] { createItemStack(Catalyst.copperIIChloride, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Element.chlorine), new ItemStack(Items.iron_ingot) },
				new ItemStack[] { createItemStack(Catalyst.ironIIIChloride, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(Items.iron_ingot) },
				new ItemStack[] { createItemStack(Catalyst.ironIIIOxide, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Catalyst.titanium), createItemStack(Ingot.aluminium), createItemStack(Compound.olefins) },
				new ItemStack[] { createItemStack(Catalyst.zieglerNatta, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Catalyst.cobalt), createItemStack(Catalyst.manganese), createItemStack(Element.bromine) },
				new ItemStack[] { createItemStack(Catalyst.cobaltManganeseBromide, 16) }));
	}

	private void createMineralRecipes() {
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Ore.shale) },
				new ItemStack[] { createItemStack(Compound.naturalGas) }));
	}

	private void createElementRecipes() {
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(Items.water_bucket) },
				new ItemStack[] { createItemStack(Element.hydrogen, 2), createItemStack(Element.oxygen), new ItemStack(Items.bucket) }));
	}

	private void createCompoundRecipes() {
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Element.hydrogen, 2), createItemStack(Element.oxygen) },
				new ItemStack[] { createItemStack(Compound.water) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.water) },
				new ItemStack[] { createItemStack(Element.chlorine, 10), createItemStack(Element.bromine), new ItemStack(Items.bucket) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.naturalGas) },
				new ItemStack[] { createItemStack(Compound.ethane), createItemStack(Compound.propane), createItemStack(Compound.butane), createItemStack(Compound.methane) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(PolycraftMod.itemBucketOil) },
				new ItemStack[] { createItemStack(Compound.naphtha), createItemStack(Compound.gasOil), createItemStack(Compound.btx), createItemStack(Compound.bitumen), new ItemStack(Items.bucket) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.ethane), createItemStack(Catalyst.platinum) },
				new ItemStack[] { createItemStack(Compound.ethylene, 2) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.propane), createItemStack(Catalyst.platinum) },
				new ItemStack[] { createItemStack(Compound.propylene, 2) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.butane), createItemStack(Catalyst.platinum) },
				new ItemStack[] { createItemStack(Compound.butadiene, 2) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.ethylene), createItemStack(Catalyst.zieglerNatta) },
				new ItemStack[] { createItemStack(Polymer.HDPE, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.ethylene) },
				new ItemStack[] { createItemStack(Polymer.LDPE, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.ethylene), createItemStack(Catalyst.palladium) },
				new ItemStack[] { createItemStack(Compound.ethyleneOxide) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.ethyleneOxide), createItemStack(Compound.water) },
				new ItemStack[] { createItemStack(Compound.ethyleneGlycol) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.naphtha), createItemStack(Catalyst.platinum) },
				new ItemStack[] { createItemStack(Compound.olefins), createItemStack(Compound.paraffin) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.gasOil), createItemStack(Catalyst.platinum) },
				new ItemStack[] { createItemStack(Compound.diesel), createItemStack(Compound.kerosene) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.btx), createItemStack(Catalyst.cobaltManganeseBromide) },
				new ItemStack[] { createItemStack(Compound.terephthalicAcid) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.terephthalicAcid), createItemStack(Compound.methanol) },
				new ItemStack[] { createItemStack(Compound.dimethylTerephthalate, 2) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.dimethylTerephthalate), createItemStack(Compound.ethyleneGlycol, 2), createItemStack(Catalyst.magnesiumOxide) },
				new ItemStack[] { createItemStack(Polymer.LDPE, 16), createItemStack(Compound.methanol, 2), createItemStack(Compound.ethyleneGlycol) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.dimethylTerephthalate), createItemStack(Compound.ethyleneGlycol, 2), createItemStack(Catalyst.antimonyTrioxide) },
				new ItemStack[] { createItemStack(Polymer.LDPE, 16), createItemStack(Compound.methanol, 2), createItemStack(Compound.ethyleneGlycol) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Element.chlorine), createItemStack(Compound.ethylene, 2), createItemStack(Catalyst.copperIIChloride) },
				new ItemStack[] { createItemStack(Compound.edc, 2) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Element.chlorine), createItemStack(Compound.ethylene, 2), createItemStack(Catalyst.ironIIIChloride) },
				new ItemStack[] { createItemStack(Compound.edc, 2) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.edc) },
				new ItemStack[] { createItemStack(Compound.vinylChloride), createItemStack(Compound.hcl) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.Acetylene), createItemStack(Compound.hcl) },
				new ItemStack[] { createItemStack(Compound.vinylChloride) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.methane) },
				new ItemStack[] { createItemStack(Compound.Acetylene) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Element.chlorine, 16) },
				new ItemStack[] { createItemStack(Compound.cl2) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.h2), createItemStack(Compound.cl2) },
				new ItemStack[] { createItemStack(Compound.hcl, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.vinylChloride), createItemStack(Compound.water, 3) },
				new ItemStack[] { createItemStack(Polymer.PVC, 16), createItemStack(Compound.water, 3) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(Items.iron_ingot), new ItemStack(Items.lava_bucket) },
				new ItemStack[] { createItemStack(Compound.sulfuricAcid, 32), new ItemStack(Items.bucket) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.ethylene), createItemStack(Compound.benzene), createItemStack(Compound.sulfuricAcid) },
				new ItemStack[] { createItemStack(Compound.ethylbenzene, 2) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.ethylbenzene), createItemStack(Compound.water, 10), new ItemStack(Items.coal), createItemStack(Catalyst.ironIIIOxide) },
				new ItemStack[] { createItemStack(Compound.styrene) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.styrene) },
				new ItemStack[] { createItemStack(Polymer.PS, 16) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.btx) },
				new ItemStack[] { createItemStack(Compound.benzene), createItemStack(Compound.toluene), createItemStack(Compound.xylene), createItemStack(Compound.methane) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.xylene) },
				new ItemStack[] { createItemStack(Compound.metaXylene), createItemStack(Compound.paraXylene), createItemStack(Compound.orthoXylene) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.xylene) },
				new ItemStack[] { createItemStack(Compound.metaXylene), createItemStack(Compound.paraXylene), createItemStack(Compound.orthoXylene) }));

		// TODO: fix this so that if the inputs are containers and block, the correct amount of empty containers are on the right side.
		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { new ItemStack(Items.coal) },
				new ItemStack[] { createItemStack(Compound.coke), createItemStack(Compound.naphtha), createItemStack(Compound.bitumen) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.calcium_oxide), createItemStack(Compound.coke, 3) },
				new ItemStack[] { createItemStack(Compound.calcium_carbide), createItemStack(Compound.carbon_monoxide) }));

		ChemicalProcessorRecipe.addRecipe(new ChemicalProcessorRecipe(
				new ItemStack[] { createItemStack(Compound.calcium_carbide), createItemStack(Compound.water) },
				new ItemStack[] { createItemStack(Compound.Acetylene), createItemStack(Compound.calcium_hydroxide) }));

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
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemJetPack), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemParachute), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemScubaTank), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemScubaFins), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemScubaMask), dirtStacks.toArray());

			dirtStacks.add(dirtStack);
			GameRegistry.addShapelessRecipe(new ItemStack(PolycraftMod.itemRunningShoes), dirtStacks.toArray());

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
