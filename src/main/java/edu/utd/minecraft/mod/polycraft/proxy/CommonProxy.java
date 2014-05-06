package edu.utd.minecraft.mod.polycraft.proxy;

import net.minecraft.block.Block;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockCompressed;
import edu.utd.minecraft.mod.polycraft.block.BlockFluid;
import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerSlab;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.Config;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.GrippedTool;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.InternalObject;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.MinecraftBlock;
import edu.utd.minecraft.mod.polycraft.config.MinecraftItem;
import edu.utd.minecraft.mod.polycraft.config.Mold;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.PogoStick;
import edu.utd.minecraft.mod.polycraft.config.PolymerBlock;
import edu.utd.minecraft.mod.polycraft.config.PolymerFibers;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeGenerator;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.handler.GuiHandler;
import edu.utd.minecraft.mod.polycraft.handler.PolycraftEventHandler;
import edu.utd.minecraft.mod.polycraft.inventory.heated.distillationcolumn.DistillationColumnInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.extruder.ExtruderInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.injectionmolder.InjectionMolderInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.steamcracker.SteamCrackerInventory;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.MachiningMillInventory;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import edu.utd.minecraft.mod.polycraft.item.ItemCatalyst;
import edu.utd.minecraft.mod.polycraft.item.ItemCustom;
import edu.utd.minecraft.mod.polycraft.item.ItemFibers;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemFlashlight;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemIngot;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemKevlarVest;
import edu.utd.minecraft.mod.polycraft.item.ItemMold;
import edu.utd.minecraft.mod.polycraft.item.ItemMoldedItem;
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemPellets;
import edu.utd.minecraft.mod.polycraft.item.ItemPhaseShifter;
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerSlab;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaMask;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
import edu.utd.minecraft.mod.polycraft.item.PolycraftBucket;
import edu.utd.minecraft.mod.polycraft.util.DynamicValue;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeInitializer;
import edu.utd.minecraft.mod.polycraft.worldgen.OilPopulate;
import edu.utd.minecraft.mod.polycraft.worldgen.OreWorldGenerator;

public class CommonProxy {

	private static final Logger logger = LogManager.getLogger();

	public void preInit() {

		// TODO: Only enable on debug mode
		DynamicValue.start();

		Config.registerFromResources("config");
		registerMinecraftItems();
		registerMinecraftBlocks();
		registerBiomes();
		registerOres();
		registerIngots();
		registerCompressedBlocks();
		registerCatalysts();
		registerVessels();
		registerPolymers();
		registerMolds();
		registerMoldedItems();
		registerGrippedTools();
		registerPogoSticks();
		registerInventories();
		registerCustom();
		Fuel.registerQuantifiedFuels();
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

	private void registerMinecraftItems() {
		for (final MinecraftItem minecraftItem : MinecraftItem.registry.values()) {
			final Item item = GameData.itemRegistry.get(minecraftItem.id);
			if (item == null)
				logger.warn("Missing item: {}", minecraftItem.name);
			else {
				logger.debug("Found item: {}", minecraftItem.name);
				PolycraftMod.items.put(minecraftItem.name, item);
			}
		}
	}

	private void registerMinecraftBlocks() {
		for (final MinecraftBlock minecraftBlock : MinecraftBlock.registry.values()) {
			final Block block = GameData.blockRegistry.get(minecraftBlock.id);
			if (block == null)
				logger.warn("Missing block: {}", minecraftBlock.name);
			else {
				logger.debug("Found block: {}", minecraftBlock.name);
				PolycraftMod.blocks.put(minecraftBlock.name, block);
			}
		}
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
		for (final CompoundVessel vessel : CompoundVessel.registry.values())
			PolycraftMod.registerItem(vessel, new ItemVessel(vessel));
	}

	private void registerPolymers() {
		for (final PolymerPellets polymerPellets : PolymerPellets.registry.values())
			PolycraftMod.registerItem(polymerPellets, new ItemPellets(polymerPellets));

		for (final PolymerFibers polymerFibers : PolymerFibers.registry.values())
			PolycraftMod.registerItem(polymerFibers, new ItemFibers());

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
		for (final MoldedItem moldedItem : MoldedItem.registry.values()) {
			Item item = null;
			if ("E".equals(moldedItem.source.gameID))
				item = new ItemRunningShoes(moldedItem);
			else if ("G".equals(moldedItem.source.gameID))
				item = new ItemScubaFins(moldedItem);
			else if ("H".equals(moldedItem.source.gameID))
				item = new ItemScubaMask(moldedItem);
			else
				item = new ItemMoldedItem(moldedItem);
			PolycraftMod.registerItem(moldedItem, item);
		}
	}

	private void registerGrippedTools() {
		for (final GrippedTool grippedTool : GrippedTool.registry.values())
			PolycraftMod.registerItem(grippedTool, ItemGripped.create(grippedTool));
	}

	private void registerPogoSticks() {
		for (final PogoStick pogoStick : PogoStick.registry.values())
			PolycraftMod.registerItem(pogoStick, new ItemPogoStick(pogoStick));
	}

	private void registerInventories() {
		TreeTapInventory.register(Inventory.registry.get("Tree Tap"));
		MachiningMillInventory.register(Inventory.registry.get("Machining Mill"));
		InjectionMolderInventory.register(Inventory.registry.get("Injection Molder"));
		ExtruderInventory.register(Inventory.registry.get("Extruder"));
		DistillationColumnInventory.register(Inventory.registry.get("Distillation Column"));
		SteamCrackerInventory.register(Inventory.registry.get("Steam Cracker"));
	}

	private void registerCustom() {
		final InternalObject oil = InternalObject.registry.get("Oil");
		final Fluid fluidOil = new Fluid(oil.name.toLowerCase()).setDensity(PolycraftMod.oilFluidDensity).setViscosity(PolycraftMod.oilFluidViscosity);
		FluidRegistry.registerFluid(fluidOil);

		PolycraftMod.blockOil = PolycraftMod.registerBlock(oil,
				new BlockFluid(fluidOil, Material.water)
						.setFlammable(true)
						.setFlammability(PolycraftMod.oilBlockFlammability)
						.setParticleColor(0.7F, 0.7F, 0.0F));
		fluidOil.setBlock(PolycraftMod.blockOil);

		for (final CustomObject customObject : CustomObject.registry.values()) {
			if ("3m".equals(customObject.gameID)) {
				final Item itemBucketOil = PolycraftMod.registerItem(customObject,
						new PolycraftBucket(PolycraftMod.blockOil)
								.setTextureName(PolycraftMod.getAssetName("bucket_oil")));
				FluidContainerRegistry.registerFluidContainer(
						FluidRegistry.getFluidStack(fluidOil.getName(), FluidContainerRegistry.BUCKET_VOLUME),
						new ItemStack(itemBucketOil),
						new ItemStack(Items.bucket));
				BucketHandler.INSTANCE.buckets.put(PolycraftMod.blockOil, itemBucketOil);
				MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);
			}
			else if ("3n".equals(customObject.gameID)) {
				PolycraftMod.registerItem(customObject, new ItemFlameThrower(customObject));
			}
			else if ("3o".equals(customObject.gameID)) {
				PolycraftMod.registerItem(customObject, new ItemFlashlight(customObject));
			}
			else if ("3p".equals(customObject.gameID)) {
				PolycraftMod.registerItem(customObject, new ItemJetPack(customObject));
			}
			else if ("5a".equals(customObject.gameID)) {
				PolycraftMod.registerItem(customObject, new ItemParachute(customObject));
			}
			else if ("3r".equals(customObject.gameID)) {
				PolycraftMod.registerItem(customObject, new ItemPhaseShifter(customObject));
			}
			else if ("3x".equals(customObject.gameID)) {
				PolycraftMod.registerItem(customObject, new ItemScubaTank(customObject));
			}
			else if ("5b".equals(customObject.gameID)) {
				PolycraftMod.registerItem(customObject, new ItemKevlarVest(customObject));
			}
			else
				//TODO really should throw an exception if we don't have a true custom item (needed an implentation)
				PolycraftMod.registerItem(customObject, new ItemCustom(customObject));
		}
	}
}
