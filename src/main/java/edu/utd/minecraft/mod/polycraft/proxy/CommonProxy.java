package edu.utd.minecraft.mod.polycraft.proxy;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
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
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.InternalObject;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.Mold;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.PolymerBlock;
import edu.utd.minecraft.mod.polycraft.config.PolymerFibers;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;
import edu.utd.minecraft.mod.polycraft.config.Vessel;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeGenerator;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.handler.GuiHandler;
import edu.utd.minecraft.mod.polycraft.handler.PolycraftEventHandler;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import edu.utd.minecraft.mod.polycraft.item.ItemCatalyst;
import edu.utd.minecraft.mod.polycraft.item.ItemFiber;
import edu.utd.minecraft.mod.polycraft.item.ItemIngot;
import edu.utd.minecraft.mod.polycraft.item.ItemMold;
import edu.utd.minecraft.mod.polycraft.item.ItemMoldedItem;
import edu.utd.minecraft.mod.polycraft.item.ItemPellet;
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick;
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick.Settings;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerSlab;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
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

		registerBiomes();
		registerOres();
		registerIngots();
		registerCompressedBlocks();
		registerCatalysts();
		registerVessels();
		registerPolymers();
		registerMolds();
		registerMoldedItems();
		registerInventories();
		registerCustom();
	}

	public void init() {
		RecipeGenerator.generateRecipes();
		GameRegistry.registerWorldGenerator(new OreWorldGenerator(), PolycraftMod.oreWorldGeneratorWeight);
		NetworkRegistry.INSTANCE.registerGuiHandler(PolycraftMod.instance, new GuiHandler());
	}

	public void postInit() {
		MinecraftForge.EVENT_BUS.register(new PolycraftEventHandler());
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

	private void registerOres() {
		for (final Ore ore : Ore.registry.values())
			PolycraftMod.registerBlock(ore, new BlockOre(ore));
	}

	private void registerIngots() {
		for (final Ingot ingot : Ingot.registry.values())
			PolycraftMod.registerItem(ingot, new ItemIngot(ingot));
	}

	private void registerCompressedBlocks() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			PolycraftMod.registerBlock(compressedBlock, new BlockCompressed(compressedBlock));
	}

	private void registerCatalysts() {
		for (final Catalyst catalyst : Catalyst.registry.values())
			PolycraftMod.registerItem(catalyst, new ItemCatalyst(catalyst));
	}

	private void registerVessels() {
		for (final Vessel vessel : Vessel.registry.values())
			PolycraftMod.registerItem(vessel, new ItemVessel(vessel));
	}

	private void registerPolymers() {
		for (final PolymerPellets polymerPellets : PolymerPellets.registry.values())
			PolycraftMod.registerItem(polymerPellets, new ItemPellet());

		for (final PolymerFibers polymerFibers : PolymerFibers.registry.values())
			PolycraftMod.registerItem(polymerFibers, new ItemFiber());

		for (final PolymerBlock polymerBlock : PolymerBlock.registry.values())
			PolycraftMod.registerBlock(polymerBlock, new BlockPolymer(polymerBlock));

		for (final PolymerSlab polymerSlab : PolymerSlab.registry.values()) {
			final BlockSlab slab = new BlockPolymerSlab(polymerSlab, false);
			final BlockSlab doubleSlab = new BlockPolymerSlab(polymerSlab, true);
			PolycraftMod.registerBlockWithItem(polymerSlab.blockSlabGameID, polymerSlab.blockSlabName, slab, polymerSlab.itemSlabGameID, polymerSlab.itemSlabName,
					ItemPolymerSlab.class, new Object[] { slab, doubleSlab, false });
			PolycraftMod.registerBlockWithItem(polymerSlab.blockDoubleSlabGameID, polymerSlab.blockDoubleSlabName, doubleSlab, polymerSlab.itemDoubleSlabGameID, polymerSlab.itemDoubleSlabName,
					ItemPolymerSlab.class, new Object[] { slab, doubleSlab, true });
		}
	}

	private void registerMolds() {
		for (final Mold mold : Mold.registry.values())
			PolycraftMod.registerItem(mold, new ItemMold(mold));
	}

	private void registerMoldedItems() {
		for (final MoldedItem moldedItem : MoldedItem.registry.values())
			PolycraftMod.registerItem(moldedItem, new ItemMoldedItem(moldedItem));
	}

	private void registerCustom() {
		registerFluids();
		registerUtilities();
		/*
		registerTools();
		registerArmor();
		registerWeapons();
		*/
	}

	private void registerUtilities() {
		for (final Settings settings : PolycraftMod.itemPogoStickSettings)
			PolycraftMod.registerItem(CustomObject.registry.get(settings.itemName), new ItemPogoStick(settings));

		/*
		PolycraftMod.itemFlashlight = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameFlashlight,
				new ItemFlashlight(
						PolycraftMod.itemFlashlightMaxLightLevel,
						PolycraftMod.itemFlashlightLightLevelDecreaseByDistance,
						PolycraftMod.itemFlashlightViewingConeAngle));

		PolycraftMod.itemParachute = PolycraftMod.registerItem(namespace, PolycraftMod.itemNameParachute,
				new ItemParachute(PolycraftMod.itemParachuteDescendVelocity));
				*/
	}

	private void registerFluids() {
		final InternalObject oil = InternalObject.registry.get("Oil");
		final Fluid fluidOil = new Fluid(oil.name.toLowerCase()).setDensity(PolycraftMod.oilFluidDensity).setViscosity(PolycraftMod.oilFluidViscosity);
		FluidRegistry.registerFluid(fluidOil);

		PolycraftMod.blockOil = PolycraftMod.registerBlock(oil,
				new BlockFluid(fluidOil, Material.water)
						.setFlammable(true)
						.setFlammability(PolycraftMod.oilBlockFlammability)
						.setParticleColor(0.7F, 0.7F, 0.0F));
		fluidOil.setBlock(PolycraftMod.blockOil);

		final Item itemBucketOil = PolycraftMod.registerItem(CustomObject.registry.get("Bucket (Crude Oil)"),
				new PolycraftBucket(PolycraftMod.blockOil)
						.setTextureName(PolycraftMod.getAssetName("bucket_oil")));
		FluidContainerRegistry.registerFluidContainer(
				FluidRegistry.getFluidStack(fluidOil.getName(), FluidContainerRegistry.BUCKET_VOLUME),
				new ItemStack(itemBucketOil),
				new ItemStack(Items.bucket));
		BucketHandler.INSTANCE.buckets.put(PolycraftMod.blockOil, itemBucketOil);
		MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

	}

	private void registerInventories() {
		TreeTapInventory.register(Inventory.registry.get("Tree Tap"));

		//PolycraftMod.registerBlock(Inventory.registry.get("Machining Mill"), new BlockMachiningMill());
		//GameRegistry.registerTileEntity(TileEntityMachiningMill.class, InternalObject.registry.get("Machining Mill Tile Entity").gameID);

		//TODO
		/*
		final CustomObject chemicalProcessor = CustomObject.registry.get("Chemical Processor");
		PolycraftMod.blockChemicalProcessor = PolycraftMod.registerBlock(chemicalProcessor, new BlockChemicalProcessor(false));
		PolycraftMod.blockChemicalProcessorActive = PolycraftMod.registerBlock(chemicalProcessor.gameID + "_active", chemicalProcessor.name + " Active", new BlockChemicalProcessor(true));
		GameRegistry.registerTileEntity(TileEntityChemicalProcessor.class, "tile_entity_" + chemicalProcessor.gameID);
		*/
	}

	/*
	private void registerTools() {
		for (final Entry<String, ToolMaterial> materialEntry : ItemGripped.allowedMaterials.entrySet()) {
			final String materialName = materialEntry.getKey();
			final ToolMaterial material = materialEntry.getValue();
			for (final String type : ItemGripped.allowedTypes.keySet())
				PolycraftMod.registerItem(namespace, ItemGripped.getName(materialName, type),
						ItemGripped.create(type, materialName, material, PolycraftMod.itemGrippedToolDurabilityBuff));
		}
	}

	private void registerArmor() {
		PolycraftMod.itemRunningShoes = PolycraftMod.registerItem(CustomObject.registry.get("Running Shoes"),
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
		
		// Register some sample recipes.  TODO: Remove
		PolycraftMod.recipeManager.addShapelessRecipe(PolycraftContainerType.TEST_INVENTORY,
			    ImmutableList.of(new ItemStack(Blocks.dirt, 1)),
				ImmutableList.of(new ItemStack(PolycraftMod.itemJetPack, 1)));
	}

	private void registerWeapons() {
		PolycraftMod.itemFlameThrower = PolycraftMod.registerItem(
				namespace, PolycraftMod.itemNameFlameThrower,
				new ItemFlameThrower(PolycraftMod.itemFlameThrowerFuelUnitsFull,
						PolycraftMod.itemFlameThrowerFuelUnitsBurnPerTick,
						PolycraftMod.itemFlameThrowerRange,
						PolycraftMod.itemFlameThrowerSpread,
						PolycraftMod.itemFlameThrowerFireDuration,
						PolycraftMod.itemFlameThrowerDamage));

	}
	*/
}
