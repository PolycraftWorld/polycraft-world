package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.Map.Entry;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.Polymer;
import edu.utd.minecraft.mod.polycraft.config.Polymer.ResinCode;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.handler.GuiHandler;
import edu.utd.minecraft.mod.polycraft.handler.PolycraftEventHandler;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.BlockChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.RenderChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.TileEntityChemicalProcessor;
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
		RecipeGenerator recipeGenerator = new RecipeGenerator();
		recipeGenerator.generateRecipes();

		GameRegistry.registerWorldGenerator(new OreWorldGenerator(), PolycraftMod.oreWorldGeneratorWeight);
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
		final Fluid fluidOil = new Fluid("oil").setDensity(PolycraftMod.oilFluidDensity).setViscosity(PolycraftMod.oilFluidViscosity);
		FluidRegistry.registerFluid(fluidOil);

		PolycraftMod.blockOil = PolycraftMod.registerBlock("oil",
				new BlockFluid(fluidOil, Material.water)
						.setFlammable(true)
						.setFlammability(PolycraftMod.oilBlockFlammability)
						.setParticleColor(0.7F, 0.7F, 0.0F));
		fluidOil.setBlock(PolycraftMod.blockOil);

		PolycraftMod.itemBucketOil = PolycraftMod.registerItem("bucket_oil",
				new PolycraftBucket(PolycraftMod.blockOil)
						.setContainerItem(Items.bucket)
						.setTextureName(PolycraftMod.getTextureName("bucket_oil")));
		FluidContainerRegistry.registerFluidContainer(
				FluidRegistry.getFluidStack("oil", FluidContainerRegistry.BUCKET_VOLUME),
				new ItemStack(PolycraftMod.itemBucketOil),
				new ItemStack(Items.bucket));
		BucketHandler.INSTANCE.buckets.put(PolycraftMod.blockOil, PolycraftMod.itemBucketOil);
		MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

	}

	private void createCustomItems() {
		PolycraftMod.itemFluidContainer = PolycraftMod.registerItem(
				PolycraftMod.itemNameFluidContainer, new ItemFluidContainer(null));
		PolycraftMod.itemFluidContainerNozzle = PolycraftMod.registerItem(
				PolycraftMod.itemNameFluidContainer + "_nozzle",
				new ItemFluidContainerNozzle()
						.setCreativeTab(CreativeTabs.tabMaterials)
						.setTextureName(PolycraftMod.getTextureName(PolycraftMod.itemNameFluidContainer + "_nozzle")));

		PolycraftMod.itemRunningShoes = PolycraftMod.registerItem(
				"running_shoes",
				new ItemRunningShoes(PolycraftMod.itemRunningShoesWalkSpeedBuff));
		PolycraftMod.itemKevlarVest = PolycraftMod.registerItem("kevlar_vest", new ItemKevlarVest());
		PolycraftMod.itemFlameThrower = PolycraftMod.registerItem(
				"flame_thrower",
				new ItemFlameThrower(PolycraftMod.itemFlameThrowerFuelUnitsFull,
						PolycraftMod.itemFlameThrowerFuelUnitsBurnPerTick,
						PolycraftMod.itemFlameThrowerRange,
						PolycraftMod.itemFlameThrowerSpread,
						PolycraftMod.itemFlameThrowerFireDuration));
		PolycraftMod.itemJetPack = PolycraftMod.registerItem(
				"jet_pack",
				new ItemJetPack(PolycraftMod.itemJetPackFuelUnitsFull,
						PolycraftMod.itemJetPackFuelUnitsBurnPerTick,
						PolycraftMod.itemJetPackFlySpeedBuff));
		PolycraftMod.itemParachute = PolycraftMod.registerItem(
				"parachute",
				new ItemParachute(PolycraftMod.itemParachuteDescendVelocity));
		PolycraftMod.itemFlashlight = PolycraftMod.registerItem(
				"flashlight",
				new ItemFlashlight(
						PolycraftMod.itemFlashlightMaxLightLevel,
						PolycraftMod.itemFlashlightLightLevelDecreaseByDistance,
						PolycraftMod.itemFlashlightViewingConeAngle));
		PolycraftMod.itemScubaMask = PolycraftMod.registerItem("scuba_mask", new ItemScubaMask(PolycraftMod.itemScubaMaskFogDensity));
		PolycraftMod.itemScubaTank = PolycraftMod.registerItem(
				"scuba_tank",
				new ItemScubaTank(PolycraftMod.itemScubaTankAirUnitsFull, PolycraftMod.itemScubaTankAirUnitsConsumePerTick));
		PolycraftMod.itemScubaFins = PolycraftMod.registerItem(
				"scuba_fins",
				new ItemScubaFins(PolycraftMod.itemScubaFinsSwimSpeedBuff, PolycraftMod.itemScubaFinsWalkSpeedBuff));

		for (final Polymer polymer : ItemGripped.allowedPolymers) {
			PolycraftMod.registerItem(
					polymer.gameName + "_grip",
					new ItemPolymerGrip()
							.setCreativeTab(CreativeTabs.tabTools)
							.setTextureName(PolycraftMod.getTextureName("polymer_grip")));
			for (final Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet()) {
				final String materialName = materialEntry.getKey();
				final ToolMaterial material = materialEntry.getValue();
				for (final String type : ItemGripped.allowedTypes.keySet()) {
					PolycraftMod.registerItem(
							ItemGripped.getName(polymer, materialName, type),
							ItemGripped.create(type, materialName, material, PolycraftMod.itemGrippedToolDurabilityBuff));
				}
			}
		}
	}

	private void createElements() {
		for (final Element element : Element.registry.values()) {
			if (element.fluid) {
				PolycraftMod.registerItem(
						ItemFluidContainer.getGameName(element), new ItemFluidContainer(element));
			}
		}
	}

	private void createCompounds() {
		for (final Compound compound : Compound.registry.values()) {
			if (compound.fluid) {
				PolycraftMod.registerItem(
						ItemFluidContainer.getGameName(compound), new ItemFluidContainer(compound));
			}
		}
	}

	private void createPolymers() {
		for (final Polymer polymer : Polymer.registry.values()) {
			//TODO remove this check when forge fixes their bug for EntityEnderMan.carriableBlocks
			// array index out of bounds exception (cannot make blocks with ids bigger than 255!)
			if (polymer.resinCode != ResinCode.NONE)
				PolycraftMod.registerBlock(polymer, new BlockPolymer(polymer));
			PolycraftMod.registerItem(
					polymer.itemNamePellet,
					new ItemPellet()
							.setCreativeTab(CreativeTabs.tabMaterials)
							.setTextureName(PolycraftMod.getTextureName("polymer_pellet")));
			PolycraftMod.registerItem(
					polymer.itemNameFiber,
					new ItemFiber()
							.setCreativeTab(CreativeTabs.tabMaterials)
							.setTextureName(PolycraftMod.getTextureName("polymer_fiber")));
		}
	}

	private void createCatalysts() {
		for (final Catalyst catalyst : Catalyst.registry.values()) {
			PolycraftMod.registerItem(catalyst,
					new ItemCatalyst(catalyst)
							.setCreativeTab(CreativeTabs.tabMaterials)
							.setTextureName(PolycraftMod.getTextureName("catalyst")));
		}
	}

	private void createOres() {
		for (final Ore ore : Ore.registry.values()) {
			PolycraftMod.registerBlock(ore, new BlockOre(ore));
		}
	}

	private void createIngots() {
		for (final Ingot ingot : Ingot.registry.values()) {
			PolycraftMod.registerItem(ingot,
					new ItemIngot()
							.setCreativeTab(CreativeTabs.tabMaterials)
							.setTextureName(PolycraftMod.getTextureName(ingot.gameName)));
		}
	}

	private void createCompressedBlocks() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values()) {
			PolycraftMod.registerBlock(compressedBlock, new BlockCompressed(compressedBlock));
		}
	}

	private void createInventories() {
		PolycraftMod.blockChemicalProcessor = PolycraftMod.registerBlock(
				"chemical_processor", new BlockChemicalProcessor(false));
		PolycraftMod.blockChemicalProcessorActive =
				PolycraftMod.registerBlock(
						"chemical_processor_active", new BlockChemicalProcessor(true));
		GameRegistry.registerTileEntity(TileEntityChemicalProcessor.class, "tile_entity_chemical_processor");
	}
}
