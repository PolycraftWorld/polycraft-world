package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.Map.Entry;

import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item.ToolMaterial;
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
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.config.Alloy;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;
import edu.utd.minecraft.mod.polycraft.config.Compound;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.Element;
import edu.utd.minecraft.mod.polycraft.config.Entity;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Mineral;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.Polymer;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.handler.GuiHandler;
import edu.utd.minecraft.mod.polycraft.handler.PolycraftEventHandler;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.BlockChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.RenderChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.TileEntityChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.BlockTreeTap;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.RenderTreeTap;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TileEntityTreeTap;
import edu.utd.minecraft.mod.polycraft.item.ItemCatalyst;
import edu.utd.minecraft.mod.polycraft.item.ItemFiber;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemFlashlight;
import edu.utd.minecraft.mod.polycraft.item.ItemFluidContainer;
import edu.utd.minecraft.mod.polycraft.item.ItemFluidContainerNozzle;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemIngot;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemKevlarVest;
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemPellet;
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerGrip;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaMask;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.item.PolycraftBucket;
import edu.utd.minecraft.mod.polycraft.util.DynamicValue;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeInitializer;
import edu.utd.minecraft.mod.polycraft.worldgen.OilPopulate;
import edu.utd.minecraft.mod.polycraft.worldgen.OreWorldGenerator;

public class CommonProxy {

	public void preInit() {
		// TODO: Only enable on debug mode
		DynamicValue.start();

		createBiomes();

		//create blocks and items (order matters for backwards compatibility between mod releases!)
		createFluids();
		createElements();
		createCompounds();
		createPolymers();
		createCatalysts();
		createAlloys();
		createMinerals();
		createOres();
		createTools();
		createArmor();
		createWeapons();
		createUtilities();
		createInventories();
	}

	public void init() {
		RecipeGenerator recipeGenerator = new RecipeGenerator();
		recipeGenerator.generateRecipes();
		recipeGenerator.generateCheatRecipes();

		GameRegistry.registerWorldGenerator(new OreWorldGenerator(), PolycraftMod.oreWorldGeneratorWeight);

		RenderingRegistry.registerBlockHandler(PolycraftMod.renderTreeTapID, RenderTreeTap.INSTANCE);
		RenderingRegistry.registerBlockHandler(PolycraftMod.renderChemicalProcessorID, RenderChemicalProcessor.INSTANCE);

		NetworkRegistry.INSTANCE.registerGuiHandler(PolycraftMod.instance, new GuiHandler());
	}

	public void postInit() {
		final PolycraftEventHandler eventHandler = new PolycraftEventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
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
		final PolycraftMod.RegistryNamespace namespace = PolycraftMod.RegistryNamespace.Fluid;

		final Fluid fluidOil = new Fluid(PolycraftMod.fluidNameOil).setDensity(PolycraftMod.oilFluidDensity).setViscosity(PolycraftMod.oilFluidViscosity);
		FluidRegistry.registerFluid(fluidOil);

		PolycraftMod.blockOil = PolycraftMod.registerBlock(namespace, PolycraftMod.blockNameOil,
				new BlockFluid(fluidOil, Material.water)
						.setFlammable(true)
						.setFlammability(PolycraftMod.oilBlockFlammability)
						.setParticleColor(0.7F, 0.7F, 0.0F));
		fluidOil.setBlock(PolycraftMod.blockOil);

		PolycraftMod.itemBucketOil = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameOilBucket,
				new PolycraftBucket(PolycraftMod.blockOil)
						.setTextureName(PolycraftMod.getAssetName("bucket_oil")));
		FluidContainerRegistry.registerFluidContainer(
				FluidRegistry.getFluidStack(fluidOil.getName(), FluidContainerRegistry.BUCKET_VOLUME),
				new ItemStack(PolycraftMod.itemBucketOil),
				new ItemStack(Items.bucket));
		BucketHandler.INSTANCE.buckets.put(PolycraftMod.blockOil, PolycraftMod.itemBucketOil);
		MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

	}

	private void createElements() {
		for (final String[] line : PolycraftMod.readConfig("elements")) {
			final Element element = Element.registry.register(new Element(
					line[0], //name
					line[1], //symbol
					Integer.parseInt(line[2]), //atomicNumber
					Integer.parseInt(line[3]), //group
					Integer.parseInt(line[4]), //period
					Double.parseDouble(line[5]), //weight
					Double.parseDouble(line[6]), //density
					Double.parseDouble(line[7]), //melt
					Double.parseDouble(line[8]), //boil
					Double.parseDouble(line[9]), //heat
					Double.parseDouble(line[10]), //electronegativity
					Double.parseDouble(line[11]) //abundance
					));
			if (element.fluid) {
				PolycraftMod.registerItem(PolycraftMod.RegistryNamespace.Element,
						ItemFluidContainer.getItemName(element), new ItemFluidContainer(element));
			}
		}
	}

	private void createCompounds() {
		for (final String[] line : PolycraftMod.readConfig("compounds")) {
			if (line.length > 0) {
				final Compound compound = Compound.registry.register(new Compound(
						line[0], //name
						true, //fluid TODO need to add fluid boolean to config
						line.length > 1 ? line[1] : "", //formula
						line.length > 2 ? line[2] : "", //uses
						line.length > 3 ? line[3] : "" //sources
				));
				if (compound.fluid) {
					PolycraftMod.registerItem(PolycraftMod.RegistryNamespace.Compound,
							ItemFluidContainer.getItemName(compound), new ItemFluidContainer(compound));
				}
				else
					PolycraftMod.registerBlock(PolycraftMod.RegistryNamespace.Compound, compound, new BlockCompound(compound));
			}
		}
	}

	private void createPolymers() {
		for (final String[] line : PolycraftMod.readConfig("polymers")) {
			int resinCodeValue = Integer.parseInt(line[0]);
			if (resinCodeValue > 7)
				resinCodeValue = 0;

			final Polymer polymer = Polymer.registry.register(new Polymer(
					line[1], //name
					line[2], //shortName
					line[5], //pelletName
					line[4], //fiberName
					Boolean.parseBoolean(line[7]), //degradable
					Polymer.Category.valueOf(line[3].replaceAll(" ", "").trim()), //category
					Polymer.ResinCode.values()[resinCodeValue], //resinCode
					9 //craftingPelletsPerBlock
					));

			PolycraftMod.registerBlock(PolycraftMod.RegistryNamespace.Polymer, polymer, new BlockPolymer(polymer));
			PolycraftMod.registerItem(PolycraftMod.RegistryNamespace.Polymer, polymer.pelletName, new ItemPellet());
			PolycraftMod.registerItem(PolycraftMod.RegistryNamespace.Polymer, polymer.fiberName, new ItemFiber());
		}
	}

	private void createCatalysts() {
		for (final String[] line : PolycraftMod.readConfig("catalysts")) {
			final Catalyst catalyst = Catalyst.registry.register(new Catalyst(
					line[0], //name
					line[1] //formula
					));
			PolycraftMod.registerItem(PolycraftMod.RegistryNamespace.Catalyst, catalyst, new ItemCatalyst(catalyst));
		}
	}

	private void createAlloys() {
		for (final String[] line : PolycraftMod.readConfig("alloys")) {
			createIngot(Alloy.registry.register(new Alloy(line[0])));
		}
	}

	private void createMinerals() {
		for (final String[] line : PolycraftMod.readConfig("minerals")) {
			Mineral.registry.register(new Mineral(line[0]));
		}
	}

	private void createOres() {
		for (final String[] line : PolycraftMod.readConfig("ores")) {

			Entity type = null;
			if (line[2].equals(Element.class.getSimpleName())) {
				type = Element.registry.get(line[3]);
				createIngot(type);
			}
			else {
				type = Mineral.registry.get(line[3]);
				if (type.name.equals("Bauxite")) { //TODO figure out how to deal with this in the config
					createIngot(Element.registry.get("Aluminum"));
				}
			}

			Entity smeltingEntity = null;
			int smeltingEntitiesPerBlock = 0;
			float smeltingExperience = 0;
			if (line.length > 12 && !line[12].isEmpty()) {

				if (line[2].equals(Element.class.getSimpleName())) {
					smeltingEntity = Ingot.registry.get(line[13]);
				}
				else
					smeltingEntity = Mineral.registry.get(line[13]);
			}

			final Ore ore = Ore.registry.register(new Ore(
					type, //type
					Float.parseFloat(line[4]), //hardness
					Float.parseFloat(line[5]), //resistance
					Integer.parseInt(line[6]), //dropExperienceMin
					Integer.parseInt(line[7]), //dropExperienceMax
					Integer.parseInt(line[8]), //generationStartYMin
					Integer.parseInt(line[9]), //generationStartYMax
					Integer.parseInt(line[10]), //generationVeinsPerChunk
					Integer.parseInt(line[11]), //generationBlocksPerVein
					smeltingEntity,//smeltingEntity
					smeltingEntitiesPerBlock,
					smeltingExperience));
			PolycraftMod.registerBlock(PolycraftMod.RegistryNamespace.Ore, ore, new BlockOre(ore));
		}
	}

	private void createIngot(final Entity type) {
		final Ingot ingot = Ingot.registry.register(new Ingot(type));
		final CompressedBlock compressedBlock = CompressedBlock.registry.register(new CompressedBlock(ingot));
		PolycraftMod.registerItem(PolycraftMod.RegistryNamespace.Ingot, ingot, new ItemIngot(ingot));
		PolycraftMod.registerBlock(PolycraftMod.RegistryNamespace.CompressedBlock, compressedBlock, new BlockCompressed(compressedBlock));
	}

	private void createTools() {
		final PolycraftMod.RegistryNamespace namespace = PolycraftMod.RegistryNamespace.Tool;

		for (final Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet()) {
			final String materialName = materialEntry.getKey();
			final ToolMaterial material = materialEntry.getValue();
			for (final String type : ItemGripped.allowedTypes.keySet()) {
				PolycraftMod.registerItem(namespace, ItemGripped.getName(Polymer.registry.get("PolyOxymethylene"), materialName, type),
						ItemGripped.create(type, materialName, material, PolycraftMod.itemGrippedToolDurabilityBuff));
			}
		}
	}

	private void createArmor() {
		final PolycraftMod.RegistryNamespace namespace = PolycraftMod.RegistryNamespace.Armor;

		PolycraftMod.itemRunningShoes = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameRunningShoes,
				new ItemRunningShoes(PolycraftMod.itemRunningShoesWalkSpeedBuff));
		PolycraftMod.itemKevlarVest = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameKevlarVest,
				new ItemKevlarVest());
		PolycraftMod.itemJetPack = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameJetPack,
				new ItemJetPack(PolycraftMod.itemJetPackFuelUnitsFull, PolycraftMod.itemJetPackFuelUnitsBurnPerTick, PolycraftMod.itemJetPackFlySpeedBuff));
		PolycraftMod.itemScubaMask = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameScubaMask,
				new ItemScubaMask(PolycraftMod.itemScubaMaskFogDensity));
		PolycraftMod.itemScubaTank = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameScubaTank,
				new ItemScubaTank(PolycraftMod.itemScubaTankAirUnitsFull, PolycraftMod.itemScubaTankAirUnitsConsumePerTick));
		PolycraftMod.itemScubaFins = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameScubaFins,
				new ItemScubaFins(PolycraftMod.itemScubaFinsSwimSpeedBuff, PolycraftMod.itemScubaFinsWalkSpeedBuff));
	}

	private void createWeapons() {
		final PolycraftMod.RegistryNamespace namespace = PolycraftMod.RegistryNamespace.Weapon;

		PolycraftMod.itemFlameThrower = PolycraftMod.registerItem(
				namespace, PolycraftMod.itemNameFlameThrower,
				new ItemFlameThrower(PolycraftMod.itemFlameThrowerFuelUnitsFull,
						PolycraftMod.itemFlameThrowerFuelUnitsBurnPerTick,
						PolycraftMod.itemFlameThrowerRange,
						PolycraftMod.itemFlameThrowerSpread,
						PolycraftMod.itemFlameThrowerFireDuration,
						PolycraftMod.itemFlameThrowerDamage));

	}

	private void createUtilities() {
		final PolycraftMod.RegistryNamespace namespace = PolycraftMod.RegistryNamespace.Utility;

		PolycraftMod.itemFluidContainer = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameFluidContainer,
				new ItemFluidContainer());
		PolycraftMod.itemFluidContainerNozzle = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameFluidContainerNozzle,
				new ItemFluidContainerNozzle());

		PolycraftMod.itemGrip = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameGrip,
				new ItemPolymerGrip());

		PolycraftMod.itemFlashlight = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameFlashlight,
				new ItemFlashlight(
						PolycraftMod.itemFlashlightMaxLightLevel,
						PolycraftMod.itemFlashlightLightLevelDecreaseByDistance,
						PolycraftMod.itemFlashlightViewingConeAngle));

		PolycraftMod.itemParachute = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameParachute,
				new ItemParachute(PolycraftMod.itemParachuteDescendVelocity));

		PolycraftMod.itemPogoStick = PolycraftMod.registerItem(namespace, PolycraftMod.itemNamePogoStick,
				new ItemPogoStick(
						PolycraftMod.itemPogoStickMaxFallProtection,
						PolycraftMod.itemPogoStickJumpMotionY,
						PolycraftMod.itemPogoStickJumpMovementFactorBuff));
	}

	private void createInventories() {
		final PolycraftMod.RegistryNamespace namespace = PolycraftMod.RegistryNamespace.Inventory;

		PolycraftMod.blockTreeTap = (BlockTreeTap) PolycraftMod.registerBlock(namespace, PolycraftMod.blockNameTreeTap, new BlockTreeTap());
		GameRegistry.registerTileEntity(TileEntityTreeTap.class, "tile_entity_tree_tap");

		PolycraftMod.blockChemicalProcessor = PolycraftMod.registerBlock(namespace, PolycraftMod.blockNameChemicalProcessor, new BlockChemicalProcessor(false));
		PolycraftMod.blockChemicalProcessorActive = PolycraftMod.registerBlock(namespace, PolycraftMod.blockNameChemicalProcessorActive, new BlockChemicalProcessor(true));
		GameRegistry.registerTileEntity(TileEntityChemicalProcessor.class, "tile_entity_chemical_processor");
	}
}
