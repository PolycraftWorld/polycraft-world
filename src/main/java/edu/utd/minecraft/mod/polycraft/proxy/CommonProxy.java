package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.Map.Entry;

import net.minecraft.block.BlockSlab;
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
import edu.utd.minecraft.mod.polycraft.block.BlockCompressed;
import edu.utd.minecraft.mod.polycraft.block.BlockFluid;
import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerSlab;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.Polymer;
import edu.utd.minecraft.mod.polycraft.config.PolymerFibers;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeGenerator;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.handler.GuiHandler;
import edu.utd.minecraft.mod.polycraft.handler.PolycraftEventHandler;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.BlockChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.RenderChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.TileEntityChemicalProcessor;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.BlockMachiningMill;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.RenderMachiningMill;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.TileEntityMachiningMill;
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
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick.Settings;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerGrip;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerSlab;
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

	//register blocks and items (order matters for backwards compatibility between mod releases!)
	public void preInit() {
		// TODO: Only enable on debug mode
		DynamicValue.start();

		registerBiomes();
		registerFluids();

		registerOres();
		registerIngots();
		registerCompressedBlocks();
		registerCatalysts();
		registerVessels();
		registerPolymers();
		registerCustom();
	}

	public void init() {
		RecipeGenerator.generateRecipes();
		GameRegistry.registerWorldGenerator(new OreWorldGenerator(), PolycraftMod.oreWorldGeneratorWeight);
		RenderingRegistry.registerBlockHandler(PolycraftMod.renderTreeTapID, RenderTreeTap.INSTANCE);
		RenderingRegistry.registerBlockHandler(PolycraftMod.renderMachiningMillID, RenderMachiningMill.INSTANCE);
		RenderingRegistry.registerBlockHandler(PolycraftMod.renderChemicalProcessorID, RenderChemicalProcessor.INSTANCE);
		NetworkRegistry.INSTANCE.registerGuiHandler(PolycraftMod.instance, new GuiHandler());
	}

	public void postInit() {
		final PolycraftEventHandler eventHandler = new PolycraftEventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		MinecraftForge.EVENT_BUS.register(OilPopulate.INSTANCE);
		MinecraftForge.TERRAIN_GEN_BUS.register(new BiomeInitializer());
	}

	private void registerBiomes() {
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

	private void registerFluids() {
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

	private void registerOres() {
		for (final Ore ore : Ore.registry.values())
			PolycraftMod.registerBlock(PolycraftMod.RegistryNamespace.Ore, ore, new BlockOre(ore));
	}

	private void registerIngots() {
		for (final Ingot ingot : Ingot.registry.values())
			PolycraftMod.registerItem(PolycraftMod.RegistryNamespace.Ingot, ingot, new ItemIngot(ingot));
	}

	private void registerCompressedBlocks() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			PolycraftMod.registerBlock(PolycraftMod.RegistryNamespace.CompressedBlock, compressedBlock, new BlockCompressed(compressedBlock));

	}

	private void registerCatalysts() {
		for (final Catalyst catalyst : Catalyst.registry.values())
			PolycraftMod.registerItem(PolycraftMod.RegistryNamespace.Catalyst, catalyst, new ItemCatalyst(catalyst));
	}

	private void registerVessels() {
		//TODO
	}

	private void registerPolymers() {
		final PolycraftMod.RegistryNamespace namespace = PolycraftMod.RegistryNamespace.Polymer;

		for (final PolymerPellets polymerPellets : PolymerPellets.registry.values())
			PolycraftMod.registerItem(namespace, polymerPellets, new ItemPellet());

		for (final PolymerFibers polymerFibers : PolymerFibers.registry.values())
			PolycraftMod.registerItem(namespace, polymerFibers, new ItemFiber());

		for (final PolymerSlab polymerSlab : PolymerSlab.registry.values()) {
			final BlockSlab slab = new BlockPolymerSlab(polymerSlab, false);
			final BlockSlab doubleSlab = new BlockPolymerSlab(polymerSlab, true);
			PolycraftMod.registerBlockAndItem(namespace, polymerSlab.blockNameDoubleSlab, slab, polymerSlab.itemNameSlab, ItemPolymerSlab.class, new Object[] { slab, doubleSlab, false });
			PolycraftMod.registerBlockAndItem(namespace, polymerSlab.blockNameSlab, doubleSlab, polymerSlab.itemNameDoubleSlab, ItemPolymerSlab.class, new Object[] { slab, doubleSlab, true });
		}

		for (final Polymer polymer : Polymer.registry.values())
			PolycraftMod.registerBlock(namespace, polymer, new BlockPolymer(polymer));
	}

	private void registerCustom() {
		registerInventories();
		registerTools();
		registerArmor();
		registerWeapons();
		registerUtilities();
	}

	private void registerInventories() {
		final PolycraftMod.RegistryNamespace namespace = PolycraftMod.RegistryNamespace.Inventory;

		PolycraftMod.blockTreeTap = (BlockTreeTap) PolycraftMod.registerBlock(namespace, PolycraftMod.blockNameTreeTap, new BlockTreeTap());
		GameRegistry.registerTileEntity(TileEntityTreeTap.class, "tile_entity_" + PolycraftMod.blockNameTreeTap);

		PolycraftMod.blockMachiningMill = PolycraftMod.registerBlock(namespace, PolycraftMod.blockNameMachiningMill, new BlockMachiningMill());
		GameRegistry.registerTileEntity(TileEntityMachiningMill.class, "tile_entity_" + PolycraftMod.blockNameMachiningMill);

		PolycraftMod.blockChemicalProcessor = PolycraftMod.registerBlock(namespace, PolycraftMod.blockNameChemicalProcessor, new BlockChemicalProcessor(false));
		PolycraftMod.blockChemicalProcessorActive = PolycraftMod.registerBlock(namespace, PolycraftMod.blockNameChemicalProcessorActive, new BlockChemicalProcessor(true));
		GameRegistry.registerTileEntity(TileEntityChemicalProcessor.class, "tile_entity_" + PolycraftMod.blockNameChemicalProcessor);
	}

	private void registerTools() {
		final PolycraftMod.RegistryNamespace namespace = PolycraftMod.RegistryNamespace.Tool;

		for (final Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet()) {
			final String materialName = materialEntry.getKey();
			final ToolMaterial material = materialEntry.getValue();
			for (final String type : ItemGripped.allowedTypes.keySet())
				PolycraftMod.registerItem(namespace, ItemGripped.getName(materialName, type),
						ItemGripped.create(type, materialName, material, PolycraftMod.itemGrippedToolDurabilityBuff));
		}
	}

	private void registerArmor() {
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

	private void registerWeapons() {
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

	private void registerUtilities() {
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

		for (final Settings settings : PolycraftMod.itemPogoStickSettings)
			PolycraftMod.registerItem(namespace, settings.itemName, new ItemPogoStick(settings));
	}
}
