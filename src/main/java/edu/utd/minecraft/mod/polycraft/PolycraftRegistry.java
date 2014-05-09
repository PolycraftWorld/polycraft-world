package edu.utd.minecraft.mod.polycraft;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
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
import edu.utd.minecraft.mod.polycraft.config.GameIdentifiedConfig;
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
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
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
import edu.utd.minecraft.mod.polycraft.item.PolycraftItem;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;

public class PolycraftRegistry {

	private static final Logger logger = LogManager.getLogger();

	public static final Map<String, String> registryNames = new HashMap<String, String>();
	public static final Map<String, Block> blocks = new HashMap<String, Block>();
	public static final Map<String, Item> items = new HashMap<String, Item>();

	private static void registerName(final String registryName, final String name) {
		if (registryNames.containsKey(registryName))
			throw new Error("Registry name already used: " + registryName + " (" + name + ")");
		registryNames.put(registryName, name);
	}

	public static ItemStack getItemStack(final String name, final int size) {
		final Item item = getItem(name);
		if (item != null)
			return new ItemStack(item, size);
		final Block block = getBlock(name);
		if (block != null)
			return new ItemStack(block, size);
		return null;
	}

	public static Block getBlock(final GameIdentifiedConfig config) {
		return getBlock(config.name);
	}

	public static Block getBlock(final String name) {
		return blocks.get(name);
	}

	public static Item getItem(final GameIdentifiedConfig config) {
		return items.get(config.name);
	}

	public static Item getItem(final String name) {
		return items.get(name);
	}

	public static Block registerBlock(final GameIdentifiedConfig config, final Block block) {
		return registerBlock(config.gameID, config.name, block);
	}

	private static Block registerBlock(final String registryName, final String name, final Block block) {
		registerName(registryName, name);
		block.setBlockName(registryName);
		GameRegistry.registerBlock(block, registryName);
		blocks.put(name, block);
		return block;
	}

	private static Item registerItem(final GameIdentifiedConfig config, final Item item) {
		return registerItem(config.gameID, config.name, item);
	}

	private static Item registerItem(final String registryName, final String name, final Item item) {
		registerName(registryName, name);
		if (!(item instanceof PolycraftItem))
			throw new IllegalArgumentException("Item " + name + " must implement PolycraftItem (" + item.toString() + ")");
		item.setUnlocalizedName(registryName);
		GameRegistry.registerItem(item, registryName);
		items.put(name, item);
		return item;
	}

	private static Block registerBlockWithItem(final String blockGameID, final String blockName, final Block block,
			final String itemBlockGameID, final String itemBlockName, final Class<? extends ItemBlock> itemBlockClass, Object... itemCtorArgs) {
		block.setBlockName(blockGameID);
		GameRegistry.registerBlock(block, itemBlockClass, blockGameID, null, itemCtorArgs);
		blocks.put(blockName, block);

		final Item itemBlock = Item.getItemFromBlock(block);
		itemBlock.setUnlocalizedName(itemBlockGameID);
		items.put(itemBlockName, itemBlock);

		return block;
	}

	public static void registerFromResources() {
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

	private static void registerMinecraftItems() {
		for (final MinecraftItem minecraftItem : MinecraftItem.registry.values()) {
			final Item item = GameData.itemRegistry.get(minecraftItem.id);
			if (item == null)
				logger.warn("Missing item: {}", minecraftItem.name);
			else {
				logger.debug("Found item: {}", minecraftItem.name);
				PolycraftRegistry.items.put(minecraftItem.name, item);
			}
		}
	}

	private static void registerMinecraftBlocks() {
		for (final MinecraftBlock minecraftBlock : MinecraftBlock.registry.values()) {
			final Block block = GameData.blockRegistry.get(minecraftBlock.id);
			if (block == null)
				logger.warn("Missing block: {}", minecraftBlock.name);
			else {
				logger.debug("Found block: {}", minecraftBlock.name);
				PolycraftRegistry.blocks.put(minecraftBlock.name, block);
			}
		}
	}

	private static void registerBiomes() {
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

	private static void registerOres() {
		for (final Ore ore : Ore.registry.values())
			PolycraftRegistry.registerBlock(ore, new BlockOre(ore));
	}

	private static void registerIngots() {
		for (final Ingot ingot : Ingot.registry.values())
			PolycraftRegistry.registerItem(ingot, new ItemIngot(ingot));
	}

	private static void registerCompressedBlocks() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			PolycraftRegistry.registerBlock(compressedBlock, new BlockCompressed(compressedBlock));
	}

	private static void registerCatalysts() {
		for (final Catalyst catalyst : Catalyst.registry.values())
			PolycraftRegistry.registerItem(catalyst, new ItemCatalyst(catalyst));
	}

	private static void registerVessels() {
		for (final CompoundVessel vessel : CompoundVessel.registry.values())
			PolycraftRegistry.registerItem(vessel, new ItemVessel(vessel));
	}

	private static void registerPolymers() {
		for (final PolymerPellets polymerPellets : PolymerPellets.registry.values())
			PolycraftRegistry.registerItem(polymerPellets, new ItemPellets(polymerPellets));

		for (final PolymerFibers polymerFibers : PolymerFibers.registry.values())
			PolycraftRegistry.registerItem(polymerFibers, new ItemFibers());

		for (final PolymerBlock polymerBlock : PolymerBlock.registry.values())
			PolycraftRegistry.registerBlock(polymerBlock, new BlockPolymer(polymerBlock));

		for (final PolymerSlab polymerSlab : PolymerSlab.registry.values()) {
			final BlockSlab slab = new BlockPolymerSlab(polymerSlab, false);
			final BlockSlab doubleSlab = new BlockPolymerSlab(polymerSlab, true);
			PolycraftRegistry.registerBlockWithItem(polymerSlab.blockSlabGameID, polymerSlab.blockSlabName, slab, polymerSlab.itemSlabGameID, polymerSlab.itemSlabName,
					ItemPolymerSlab.class, new Object[] { slab, doubleSlab, false });
			PolycraftRegistry.registerBlockWithItem(polymerSlab.blockDoubleSlabGameID, polymerSlab.blockDoubleSlabName, doubleSlab, polymerSlab.itemDoubleSlabGameID, polymerSlab.itemDoubleSlabName,
					ItemPolymerSlab.class, new Object[] { slab, doubleSlab, true });
		}
	}

	private static void registerMolds() {
		for (final Mold mold : Mold.registry.values())
			PolycraftRegistry.registerItem(mold, new ItemMold(mold));
	}

	private static void registerMoldedItems() {
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
			PolycraftRegistry.registerItem(moldedItem, item);
		}
	}

	private static void registerGrippedTools() {
		for (final GrippedTool grippedTool : GrippedTool.registry.values())
			PolycraftRegistry.registerItem(grippedTool, ItemGripped.create(grippedTool));
	}

	private static void registerPogoSticks() {
		for (final PogoStick pogoStick : PogoStick.registry.values())
			PolycraftRegistry.registerItem(pogoStick, new ItemPogoStick(pogoStick));
	}

	private static void registerInventories() {
		for (final Inventory inventory : Inventory.registry.values()) {
			if ("3h".equals(inventory.gameID))
				TreeTapInventory.register(inventory);
			else if ("3i".equals(inventory.gameID))
				MachiningMillInventory.register(inventory);
			else if ("3j".equals(inventory.gameID))
				ExtruderInventory.register(inventory);
			else if ("3k".equals(inventory.gameID))
				InjectionMolderInventory.register(inventory);
			else if ("3E".equals(inventory.gameID))
				DistillationColumnInventory.register(inventory);
			else if ("3D".equals(inventory.gameID))
				SteamCrackerInventory.register(inventory);
			else
				logger.warn("Unhandled inventory: {} ({})", inventory.name, inventory.gameID);
		}
	}

	private static void registerCustom() {
		final InternalObject oil = InternalObject.registry.get("Oil");
		final Fluid fluidOil = new Fluid(oil.name.toLowerCase()).setDensity(PolycraftMod.oilFluidDensity).setViscosity(PolycraftMod.oilFluidViscosity);
		FluidRegistry.registerFluid(fluidOil);

		PolycraftMod.blockOil = PolycraftRegistry.registerBlock(oil,
				new BlockFluid(fluidOil, Material.water)
						.setFlammable(true)
						.setFlammability(PolycraftMod.oilBlockFlammability)
						.setParticleColor(0.7F, 0.7F, 0.0F));
		fluidOil.setBlock(PolycraftMod.blockOil);

		for (final CustomObject customObject : CustomObject.registry.values()) {
			if ("3m".equals(customObject.gameID)) {
				final Item itemBucketOil = PolycraftRegistry.registerItem(customObject,
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
				PolycraftRegistry.registerItem(customObject, new ItemFlameThrower(customObject));
			}
			else if ("3o".equals(customObject.gameID)) {
				PolycraftRegistry.registerItem(customObject, new ItemFlashlight(customObject));
			}
			else if ("3p".equals(customObject.gameID)) {
				PolycraftRegistry.registerItem(customObject, new ItemJetPack(customObject));
			}
			else if ("5a".equals(customObject.gameID)) {
				PolycraftRegistry.registerItem(customObject, new ItemParachute(customObject));
			}
			else if ("3r".equals(customObject.gameID)) {
				PolycraftRegistry.registerItem(customObject, new ItemPhaseShifter(customObject));
			}
			else if ("3x".equals(customObject.gameID)) {
				PolycraftRegistry.registerItem(customObject, new ItemScubaTank(customObject));
			}
			else if ("5b".equals(customObject.gameID)) {
				PolycraftRegistry.registerItem(customObject, new ItemKevlarVest(customObject));
			}
			else
				//TODO should we throw an exception if we don't have a true custom item (needed an implentation)
				PolycraftRegistry.registerItem(customObject, new ItemCustom(customObject));
		}
	}

	public static void exportLangEntries(final String exportFile) throws IOException {
		final String fluidFormat = "fluid.%s=%s";
		final String containerFormat = "container.%s=%s";
		final String baseFormat = "%s.name=%s";
		final String blockFormat = "tile." + baseFormat;
		final String itemFormat = "item." + baseFormat;

		final Collection<String> langEntries = new LinkedList<String>();

		for (final InternalObject internalObject : InternalObject.registry.values()) {
			if ("1D".equals(internalObject.gameID)) {
				langEntries.add(String.format(fluidFormat, internalObject.name.toLowerCase(), internalObject.name));
				langEntries.add(String.format(blockFormat, internalObject.gameID, internalObject.name));
			}
		}

		for (final Ore ore : Ore.registry.values())
			langEntries.add(String.format(blockFormat, ore.gameID, ore.name));

		for (final Ingot ingot : Ingot.registry.values())
			langEntries.add(String.format(itemFormat, ingot.gameID, ingot.name));

		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			langEntries.add(String.format(blockFormat, compressedBlock.gameID, compressedBlock.name));

		for (final Catalyst catalyst : Catalyst.registry.values())
			langEntries.add(String.format(itemFormat, catalyst.gameID, catalyst.name));

		for (final CompoundVessel vessel : CompoundVessel.registry.values())
			langEntries.add(String.format(itemFormat, vessel.gameID, vessel.name));

		for (final PolymerPellets polymerPellets : PolymerPellets.registry.values())
			langEntries.add(String.format(itemFormat, polymerPellets.gameID, polymerPellets.name));

		for (final PolymerFibers polymerFibers : PolymerFibers.registry.values())
			langEntries.add(String.format(itemFormat, polymerFibers.gameID, polymerFibers.name));

		for (final PolymerBlock polymerBlock : PolymerBlock.registry.values())
			langEntries.add(String.format(blockFormat, polymerBlock.gameID, polymerBlock.name));

		for (final PolymerSlab polymerSlab : PolymerSlab.registry.values())
			langEntries.add(String.format(baseFormat, polymerSlab.blockSlabGameID, polymerSlab.name));

		for (final Mold mold : Mold.registry.values())
			langEntries.add(String.format(itemFormat, mold.gameID, mold.name));

		for (final MoldedItem moldedItem : MoldedItem.registry.values())
			langEntries.add(String.format(itemFormat, moldedItem.gameID, moldedItem.name));

		for (final GrippedTool grippedTool : GrippedTool.registry.values())
			langEntries.add(String.format(itemFormat, grippedTool.gameID, grippedTool.name));

		for (final PogoStick pogoStick : PogoStick.registry.values())
			langEntries.add(String.format(itemFormat, pogoStick.gameID, pogoStick.name));

		for (final Inventory inventory : Inventory.registry.values()) {
			langEntries.add(String.format(containerFormat, inventory.gameID, inventory.name));
			langEntries.add(String.format(blockFormat, inventory.gameID, inventory.name));
		}

		for (final CustomObject customObject : CustomObject.registry.values())
			langEntries.add(String.format(itemFormat, customObject.gameID, customObject.name));

		final PrintWriter writer = new PrintWriter(exportFile);
		for (final String line : langEntries) {
			writer.println(line);
		}
		writer.close();
	}
}
