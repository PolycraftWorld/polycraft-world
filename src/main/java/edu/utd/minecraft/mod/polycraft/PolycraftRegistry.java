package edu.utd.minecraft.mod.polycraft;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockChallengeBlock;
import edu.utd.minecraft.mod.polycraft.block.BlockCollision;
import edu.utd.minecraft.mod.polycraft.block.BlockCompressed;
import edu.utd.minecraft.mod.polycraft.block.BlockFluid;
import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.block.BlockPasswordDoor;
import edu.utd.minecraft.mod.polycraft.block.BlockPolyPortal;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerBrick;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerBrickHelper;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerHelper;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerSlab;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerStairs;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerWall;
import edu.utd.minecraft.mod.polycraft.block.HPBlock;
import edu.utd.minecraft.mod.polycraft.block.material.PolycraftMaterial;
import edu.utd.minecraft.mod.polycraft.client.TileEntityPolymerBrick;
import edu.utd.minecraft.mod.polycraft.config.Armor;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;
import edu.utd.minecraft.mod.polycraft.config.CellCultureDish;
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.Config;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.DNASampler;
import edu.utd.minecraft.mod.polycraft.config.Electronics;
import edu.utd.minecraft.mod.polycraft.config.Element;
import edu.utd.minecraft.mod.polycraft.config.ElementVessel;
import edu.utd.minecraft.mod.polycraft.config.Exam;
import edu.utd.minecraft.mod.polycraft.config.Flashcard;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.GameID;
import edu.utd.minecraft.mod.polycraft.config.GameIdentifiedConfig;
import edu.utd.minecraft.mod.polycraft.config.GrippedSyntheticTool;
import edu.utd.minecraft.mod.polycraft.config.GrippedTool;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.InternalObject;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.Mask;
import edu.utd.minecraft.mod.polycraft.config.MinecraftBlock;
import edu.utd.minecraft.mod.polycraft.config.MinecraftItem;
import edu.utd.minecraft.mod.polycraft.config.Mold;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;
import edu.utd.minecraft.mod.polycraft.config.Nugget;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.PogoStick;
import edu.utd.minecraft.mod.polycraft.config.PolycraftEntity;
import edu.utd.minecraft.mod.polycraft.config.PolymerBlock;
import edu.utd.minecraft.mod.polycraft.config.PolymerBrick;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;
import edu.utd.minecraft.mod.polycraft.config.PolymerStairs;
import edu.utd.minecraft.mod.polycraft.config.PolymerWall;
import edu.utd.minecraft.mod.polycraft.config.Tool;
import edu.utd.minecraft.mod.polycraft.config.WaferItem;
import edu.utd.minecraft.mod.polycraft.entity.EntityOilSlimeBallProjectile;
import edu.utd.minecraft.mod.polycraft.entity.EntityPellet;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityIronCannonBall;
import edu.utd.minecraft.mod.polycraft.entity.boss.TestTerritoryFlagBoss;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityAndroid;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityDummy;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityOilSlime;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityTerritoryFlag;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.ResearchAssistantEntity;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.CannonInventory;
import edu.utd.minecraft.mod.polycraft.inventory.computer.ComputerInventory;
import edu.utd.minecraft.mod.polycraft.inventory.condenser.CondenserInventory;
import edu.utd.minecraft.mod.polycraft.inventory.courseblock.CHEM2323Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.fluorescentlamp.FluorescentLampInventory;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.FloodlightInventory;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.GaslampInventory;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.SpotlightInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.chemicalprocessor.ChemicalProcessorInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.contactprinter.ContactPrinterInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.distillationcolumn.DistillationColumnInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.extruder.ExtruderInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.industrialoven.IndustrialOvenInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.injectionmolder.InjectionMolderInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.meroxtreatmentunit.MeroxTreatmentUnitInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.steamcracker.SteamCrackerInventory;
import edu.utd.minecraft.mod.polycraft.inventory.hospitalgenerator.HospitalGeneratorInventory;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.MachiningMillInventory;
import edu.utd.minecraft.mod.polycraft.inventory.maskwriter.MaskWriterInventory;
import edu.utd.minecraft.mod.polycraft.inventory.oilderrick.OilDerrickInventory;
import edu.utd.minecraft.mod.polycraft.inventory.plasticchest.PlasticChestInventory;
import edu.utd.minecraft.mod.polycraft.inventory.polycrafting.PolycraftingInventory;
import edu.utd.minecraft.mod.polycraft.inventory.portalchest.PortalChestInventory;
import edu.utd.minecraft.mod.polycraft.inventory.printingpress.PrintingPressInventory;
import edu.utd.minecraft.mod.polycraft.inventory.pump.FlowRegulatorInventory;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpInventory;
import edu.utd.minecraft.mod.polycraft.inventory.solararray.SolarArrayInventory;
import edu.utd.minecraft.mod.polycraft.inventory.territoryflag.TerritoryFlagInventory;
import edu.utd.minecraft.mod.polycraft.inventory.textwall.TextWallInventory;
import edu.utd.minecraft.mod.polycraft.inventory.tierchest.TierChestInventory;
import edu.utd.minecraft.mod.polycraft.inventory.tradinghouse.TradingHouseInventory;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import edu.utd.minecraft.mod.polycraft.item.ArmorSlot;
import edu.utd.minecraft.mod.polycraft.item.ItemAirQualityDetector;
import edu.utd.minecraft.mod.polycraft.item.ItemArmorChest;
import edu.utd.minecraft.mod.polycraft.item.ItemArmorFeet;
import edu.utd.minecraft.mod.polycraft.item.ItemArmorHead;
import edu.utd.minecraft.mod.polycraft.item.ItemArmorLegs;
import edu.utd.minecraft.mod.polycraft.item.ItemCatalyst;
import edu.utd.minecraft.mod.polycraft.item.ItemCellCultureDish;
import edu.utd.minecraft.mod.polycraft.item.ItemCleats;
import edu.utd.minecraft.mod.polycraft.item.ItemCommunication;
import edu.utd.minecraft.mod.polycraft.item.ItemConstitutionClaim;
import edu.utd.minecraft.mod.polycraft.item.ItemCustom;
import edu.utd.minecraft.mod.polycraft.item.ItemDNASampler;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool;
import edu.utd.minecraft.mod.polycraft.item.ItemElectronics;
import edu.utd.minecraft.mod.polycraft.item.ItemExam;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemFlashcard;
import edu.utd.minecraft.mod.polycraft.item.ItemFlashlight;
import edu.utd.minecraft.mod.polycraft.item.ItemFluorescentBulbs;
import edu.utd.minecraft.mod.polycraft.item.ItemFreezeRay;
import edu.utd.minecraft.mod.polycraft.item.ItemFreezingKnockbackBomb;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemHeatedKnife;
import edu.utd.minecraft.mod.polycraft.item.ItemIngot;
import edu.utd.minecraft.mod.polycraft.item.ItemIronCannonBall;
import edu.utd.minecraft.mod.polycraft.item.ItemSlingshot;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemKnockbackBomb;
import edu.utd.minecraft.mod.polycraft.item.ItemMask;
import edu.utd.minecraft.mod.polycraft.item.ItemMiningHammer;
import edu.utd.minecraft.mod.polycraft.item.ItemMold;
import edu.utd.minecraft.mod.polycraft.item.ItemMoldedItem;
import edu.utd.minecraft.mod.polycraft.item.ItemNugget;
import edu.utd.minecraft.mod.polycraft.item.ItemOilSlimeBall;
import edu.utd.minecraft.mod.polycraft.item.ItemPaintball;
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemPhaseShifter;
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick;
import edu.utd.minecraft.mod.polycraft.item.ItemPolycraftDoor;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerBlock;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerBrick;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerSlab;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerStairs;
import edu.utd.minecraft.mod.polycraft.item.ItemPolymerWall;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaMask;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.item.ItemSuperInk;
import edu.utd.minecraft.mod.polycraft.item.ItemSyntheticGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemToolAxe;
import edu.utd.minecraft.mod.polycraft.item.ItemToolHoe;
import edu.utd.minecraft.mod.polycraft.item.ItemToolPickaxe;
import edu.utd.minecraft.mod.polycraft.item.ItemToolShovel;
import edu.utd.minecraft.mod.polycraft.item.ItemToolSword;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
import edu.utd.minecraft.mod.polycraft.item.ItemWafer;
import edu.utd.minecraft.mod.polycraft.item.ItemWaterCannon;
import edu.utd.minecraft.mod.polycraft.item.PolycraftBucket;
import edu.utd.minecraft.mod.polycraft.item.PolycraftItem;
import edu.utd.minecraft.mod.polycraft.minigame.KillWall;
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigame;
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigameManager;
import edu.utd.minecraft.mod.polycraft.minigame.RaceGame;
import edu.utd.minecraft.mod.polycraft.minigame.RaidGame;
import edu.utd.minecraft.mod.polycraft.render.TileEntityBlockPipe;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

public class PolycraftRegistry {

	private static final Logger logger = LogManager.getLogger();

	public static final Map<String, String> registryIdToNameUpper = Maps.newHashMap();
	public static final Map<String, String> registrySafeNameToId = Maps.newHashMap();
	public static final Map<String, Block> blocks = Maps.newHashMap();
	public static final Map<String, Item> items = Maps.newHashMap();
	public static final Map<Item, CustomObject> customObjectItems = Maps.newHashMap();
	public static final Set<Item> minecraftItems = Sets.newHashSet();

	private static void registerName(final String registryName, final String name) {
		if (registryIdToNameUpper.containsKey(registryName))
			throw new Error("Registry name already used: " + registryName + " (" + name + ")");
		registryIdToNameUpper.put(registryName, name);

		if (registrySafeNameToId.containsKey(PolycraftMod.getSafeRegistryName(name)))
			throw new Error("Registry name already used: " + PolycraftMod.getSafeRegistryName(name) + " (" + registryName + ")");
		registrySafeNameToId.put(PolycraftMod.getSafeRegistryName(name), registryName);
	}

	private static void registerSpecialNames(final String registryName, final String name) {
		if (registrySafeNameToId.containsKey(PolycraftMod.getSafeRegistryName(name)))
			throw new Error("Registry name already used: " + PolycraftMod.getSafeRegistryName(name) + " (" + registryName + ")");
		registrySafeNameToId.put(PolycraftMod.getSafeRegistryName(name), registryName);
	}

	public static String getRegistryIdFromName(String name) {
		return registrySafeNameToId.get(PolycraftMod.getSafeRegistryName(name));
	}

	public static String getRegistryIdFromItem(Item item) {
		return getRegistryIdFromName(PolycraftMod.getRegistryName(item));
	}

	public static String getRegistryIdFromBlock(Block block) {
		return getRegistryIdFromName(PolycraftMod.getRegistryName(block));
	}

	public static String getRegistryIdFromItemStack(ItemStack itemStack) {
		String s = PolycraftRegistry.getRegistryIdFromName(PolycraftMod.getRegistryName(itemStack));
		return (s == null) ? PolycraftMod.getRegistryName(itemStack) : s; //for polycraft items, unlocalized name is already the ID
	}

	public static String getRegistryNameFromId(String id) {
		return registryIdToNameUpper.get(id);
	}

	public static boolean isIdBlockId(String id) {
		return (blocks.get(getRegistryNameFromId(id)) == null) ? false : true;
	}

	public static boolean isIdItemId(String id) {
		return (items.get(getRegistryNameFromId(id)) == null) ? false : true;
	}

	public static ItemStack getItemStack(final String name, final int size) {
		final int metadataSeparatorIndex = name.indexOf(":");
		//final int damageSeparatorIndex = name.indexOf(";");
		if (metadataSeparatorIndex > -1) {
			final String nameClean = name.substring(0, metadataSeparatorIndex);
			final int metadata = Integer.parseInt(name.substring(
					metadataSeparatorIndex + 1, name.length()));
			final Item item = getItem(nameClean);
			if (item != null)
				return new ItemStack(item, size, metadata);
			//		} else if (damageSeparatorIndex > -1) {
			//			final String nameClean = name.substring(0, damageSeparatorIndex);
			//			final int damage = Integer.parseInt(name.substring(
			//					damageSeparatorIndex + 1, name.length()));
			//			final Item item = getItem(nameClean);
			//			if (item != null)
			//				return new ItemStack(item, size, damage);

		} else {
			final Item item = getItem(name);
			if (item != null)
				return new ItemStack(item, size);
		}
		final Block block = getBlock(name);
		if (block != null)
			return new ItemStack(block, size);
		return null;

	}

	public static Object getItemOrBlock(final String name) {
		final Item item = getItem(name);
		if (item != null)
			return item;
		final Block block = getBlock(name);
		if (block != null)
			return block;
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

	private static Block registerBlock(final String gameID, final String name, final Block block) {
		registerName(gameID, name);
		block.setBlockName(gameID);
		GameRegistry.registerBlock(block, gameID);
		blocks.put(name, block);
		return block;
	}

	private static Item registerItem(final GameIdentifiedConfig config, final Item item) {
		if (config instanceof CustomObject) {
			customObjectItems.put(item, (CustomObject) config);
		}
		return registerItem(config.gameID, config.name, item);
	}

	private static Item registerItem(final String gameID, final String name, final Item item) {
		registerName(gameID, name);
		if (!(item instanceof PolycraftItem))
			throw new IllegalArgumentException("Item " + name + " must implement PolycraftItem (" + item.toString() + ")");
		item.setUnlocalizedName(gameID);
		GameRegistry.registerItem(item, gameID);
		items.put(name, item);
		return item;
	}

	public static Block registerBlockWithItem(final String blockGameID, final String blockName, final Block block,
			final String itemBlockGameID, final String itemBlockName, final Class<? extends ItemBlock> itemBlockClass, Object... itemCtorArgs) {
		block.setBlockName(blockGameID);
		GameRegistry.registerBlock(block, itemBlockClass, blockGameID, null, itemCtorArgs);
		blocks.put(blockName, block);
		registerName(itemBlockGameID, itemBlockName);
		registerName(blockGameID, blockName);

		final Item itemBlock = Item.getItemFromBlock(block);
		itemBlock.setUnlocalizedName(itemBlockGameID);
		items.put(itemBlockName, itemBlock);

		return block;
	}

	private static int[] targetVersion = null;

	public static final boolean isTargetVersion(final int[] version) {
		if (version == null || targetVersion == null || version.length != targetVersion.length)
			return false;
		if (PolycraftMod.isVersionCompatible(targetVersion, new int[] { 1, 3, 0 }))
			if (PolycraftMod.isVersionCompatible(version, new int[] { 1, 3, 0 }))
				return true;
		for (int i = 0; i < targetVersion.length; i++) {
			if (targetVersion[i] != version[i])
				return false;
		}
		return true;
	}

	public static void registerFromResources() {
		boolean foundVersions = false;
		for (final String[] line : PolycraftMod.readResourceFileDelimeted("config", "enums")) {
			if (!foundVersions) {
				if (line.length == 0)
					continue;
				else if (!line[0].toString().equalsIgnoreCase("Version"))
					continue;
				else {
					foundVersions = true;
					continue;
				}

			}

			if (line.length == 0)
				break;

			targetVersion = PolycraftMod.getVersionNumeric(line[0]);
			// System.out.println("Target Version: " + Arrays.toString(targetVersion));
			if (!PolycraftMod.isVersionCompatible(targetVersion)) {
				break;
			}
			if (PolycraftMod.isVersionCompatible(targetVersion, new int[] { 1, 2, 5 })) {
				continue;
			}

			Config.registerFromResources("config");

			registerMinecraftItems();
			registerMinecraftBlocks();
			registerBiomes();
			registerOres();
			registerIngots();
			registerNuggets();
			registerCompressedBlocks();
			registerCatalysts();
			registerVessels();
			registerPolymers();
			registerMolds();
			registerMoldedItems();
			registerGrippedTools();
			registerPogoSticks();
			registerArmors();
			registerTools();
			registerGrippedSyntheticTools();
			registerInventories();
			registerCustom();
			registerMaskItems();
			registerWaferItems();
			registerElectronics();
			registerDNASamplers();
			registerCellCultureDishes();
			registerFlashcards();
			registerExams();
			Fuel.registerQuantifiedFuels();
			registerPolycraftEntities();


		}
		registerMinigames();
		targetVersion = PolycraftMod.VERSION_NUMERIC;
	}
	
	private static void registerMinigames()
	{
		KillWall.register(KillWall.id,KillWall.class);
		RaceGame.register(RaceGame.id,RaceGame.class);
		PolycraftMinigameManager.registerMinigame(RaidGame.id, RaidGame.class);
		
	}

	private static void registerMinecraftItems() {
		for (final MinecraftItem minecraftItem : MinecraftItem.registry.values()) {
			if (isTargetVersion(minecraftItem.version)) {
				final Item item = GameData.itemRegistry.get(minecraftItem.id);
				if (item == null)
					logger.warn("Missing item: {}", minecraftItem.name);
				else {
					logger.debug("Found item: {}", minecraftItem.name);
					items.put(minecraftItem.name, item);
					minecraftItems.add(item);

					//					if ((minecraftItem.id == 405) || //Nether brick
					//							(minecraftItem.id == 404) || //Comparator
					//							(minecraftItem.id == 397) || //Skull
					//							(minecraftItem.id == 390) || //Flower Pot
					//							(minecraftItem.id == 380) || //Cauldron 
					//							(minecraftItem.id == 379) || //Brewing Stand
					//							(minecraftItem.id == 372) || //Nether Wart
					//							(minecraftItem.id == 355) || //Bed
					//							(minecraftItem.id == 354) || //Cake
					//							(minecraftItem.id == 338) || //Reeds
					//							(minecraftItem.id == 330) || //Iron Door
					//							(minecraftItem.id == 324) || //Wooden Door
					//							(minecraftItem.id == 296)) //Wheat
					//						registerName(PolycraftMod.MC_PREFIX + String.valueOf(minecraftItem.id), minecraftItem.name + "(Item)");
					//					else
					registerName(PolycraftMod.MC_PREFIX + String.format("%04d", minecraftItem.id), minecraftItem.name);

				}
			}
		}
	}

	private static void registerMinecraftBlocks() {
		for (final MinecraftBlock minecraftBlock : MinecraftBlock.registry.values()) {
			if (isTargetVersion(minecraftBlock.version)) {
				final Block block = GameData.blockRegistry.get(minecraftBlock.id);
				if (block == null)
					logger.warn("Missing block: {}", minecraftBlock.name);
				else {
					logger.debug("Found block: {}", minecraftBlock.name);
					blocks.put(minecraftBlock.name, block);
					minecraftItems.add(Item.getItemFromBlock(block));

					if ((minecraftBlock.id == 112) || //Nether brick
							(minecraftBlock.id == 144) || //Skull
							(minecraftBlock.id == 140) || //Flower Pot
							(minecraftBlock.id == 118) || //Cauldron 
							(minecraftBlock.id == 117) || //Brewing Stand
							(minecraftBlock.id == 115) || //Nether Wart
							(minecraftBlock.id == 26) || //Bed
							(minecraftBlock.id == 92) || //Cake
							(minecraftBlock.id == 83) || //Reeds
							(minecraftBlock.id == 71) || //Iron Door
							(minecraftBlock.id == 64) || //Wooden Door
							(minecraftBlock.id == 59)) //Wheat
						registerName(PolycraftMod.MC_PREFIX + String.format("%04d", minecraftBlock.id), minecraftBlock.name + " (Block)");
					else

						registerName(PolycraftMod.MC_PREFIX + String.format("%04d", minecraftBlock.id), minecraftBlock.name);

				}
			}
		}
		if (isTargetVersion(new int[] { 1, 0, 0 })) {
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 2256), "record");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 420), "leash");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 419), "horsearmordiamond");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 418), "horsearmorgold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 417), "horsearmormetal");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 408), "minecartHopper");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 407), "minecartTNT");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 406), "netherquartz");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 402), "fireworksCharge");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 396), "carrotGolden");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 395), "emptyMap");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 394), "potatoPoisonous");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 393), "potatoBaked");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 389), "frame");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 386), "writingBook");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 385), "fireball");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 384), "expBottle");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 383), "monsterPlacer");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 381), "eyeOfEnder");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 372), "netherStalkSeeds");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 366), "chickenCooked");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 365), "chickenRaw");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 364), "beefCooked");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 363), "beefRaw");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 362), "seeds_melon");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 361), "seeds_pumpkin");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 356), "diode");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 350), "dyePowder");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 348), "yellowDust");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 343), "minecartFurnace");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 342), "minecartChest");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 335), "milk");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 330), "doorIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 327), "bucketLava");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 326), "bucketWater");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 324), "doorWood");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 322), "appleGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 319), "porkchopCooked");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 320), "porkchopRaw");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 317), "bootsGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 316), "leggingsGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 315), "helmetGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 314), "chestplateGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 313), "bootsDiamond");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 312), "leggingsDiamond");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 311), "helmetDiamond");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 310), "chestplateDiamond");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 309), "bootsIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 308), "leggingsIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 307), "helmetIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 306), "chestplateIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 305), "bootsChain");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 304), "leggingsChain");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 303), "helmetChain");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 302), "chestplateChain");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 301), "bootsCloth");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 300), "leggingsCloth");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 299), "helmetCloth");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 298), "chestplateCloth");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 295), "seeds");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 294), "hoeGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 293), "hoeDiamond");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 292), "hoeIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 291), "hoeStone");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 290), "hoeWood");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 289), "sulphur");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 286), "pickaxeGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 285), "hatchetGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 284), "shovelGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 283), "swordGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 279), "pickaxeDiamond");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 278), "hatchetDiamond");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 277), "shovelDiamond");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 276), "swordDiamond");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 275), "pickaxeStone");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 274), "hatchetStone");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 273), "shovelStone");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 272), "swordStone");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 271), "hatchetWood");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 270), "pickaxeWood");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 269), "shovelWood");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 268), "swordWood");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 267), "swordIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 266), "ingotGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 265), "ingotIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 174), "icePacked");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 173), "blockCoal");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 172), "clayHardened");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 171), "woolCarpet");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 164), "stairsWoodDarkOak");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 163), "stairsWoodAcacia");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 159), "clayHardenedStained");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 156), "stairsQuartz");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 152), "blockRedstone");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 148), "weightedPlate_heavy");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 147), "weightedPlate_light");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 146), "chestTrap");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 139), "cobbleWall");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 136), "stairsWoodJungle");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 135), "stairsWoodBirch");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 134), "stairsWoodSpruce");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 133), "blockEmerald");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 131), "tripWireSource");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 129), "oreEmerald");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 128), "stairsSandStone");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 126), "woodSlab");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 123), "redstoneLight");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 121), "whiteStone");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 116), "enchantmentTable");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 114), "stairsNetherBrick");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 113), "netherFence");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 110), "mycel");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 109), "stairsStoneBrickSmooth");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 108), "stairsBrick");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 102), "thinGlass");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 101), "fenceIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 97), "monsterStoneEgg");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 95), "thinStainedGlass");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 89), "lightgem");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 88), "hellsand");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 87), "hellrock");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 84), "musicBlock");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 77), "button");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 76), "notGate");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 73), "oreRedstone");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 70), "pressurePlate");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 67), "stairsStone");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 58), "workbench");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 57), "blockDiamond");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 56), "oreDiamond");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 53), "stairsWood");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 48), "stoneMoss");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 45), "stonebricksmooth");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 42), "blockIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 41), "blockGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 40), "mushroom_red");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 39), "mushroom");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 38), "flower2");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 37), "flower1");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 35), "cloth");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 33), "pistonBase");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 29), "pistonStickyBase");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 22), "blockLapis");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 21), "oreLapis");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 16), "oreCoal");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 15), "oreIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 14), "oreGold");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 5), "wood");

			//problem with snow packed to unpacked
			//problem with melon
			//carrots to tops
			//pressure plates
			//mushrooms

		}

	}

	private static void registerBiomes() {
		if (isTargetVersion(new int[] { 1, 0, 0 })) {
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
	}

	private static void registerOres() {
		for (final Ore ore : Ore.registry.values()) {
			if (isTargetVersion(ore.version)) {
				registerBlock(ore, new BlockOre(ore));
			}
		}

	}

	private static void registerIngots() {
		for (final Ingot ingot : Ingot.registry.values()) {
			if (isTargetVersion(ingot.version)) {
				registerItem(ingot, new ItemIngot(ingot));
			}
		}

	}

	private static void registerNuggets() {
		for (final Nugget nugget : Nugget.registry.values()) {
			if (isTargetVersion(nugget.version)) {
				registerItem(nugget, new ItemNugget(nugget));
			}
		}

	}

	private static void registerCompressedBlocks() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values()) {
			if (isTargetVersion(compressedBlock.version)) {
				registerBlock(compressedBlock, new BlockCompressed(compressedBlock));
			}
		}

	}

	private static void registerCatalysts() {
		for (final Catalyst catalyst : Catalyst.registry.values()) {
			if (isTargetVersion(catalyst.version)) {
				registerItem(catalyst, new ItemCatalyst(catalyst));
			}
		}

	}

	private static void registerFlashcards() {
		for (final Flashcard flashcard : Flashcard.registry.values()) {
			if (isTargetVersion(flashcard.version)) {
				registerItem(flashcard, new ItemFlashcard(flashcard));
			}
		}

	}

	private static void registerExams() {
		for (final Exam exam : Exam.registry.values()) {
			if (isTargetVersion(exam.version)) {
				registerItem(exam, new ItemExam(exam));
			}
		}

	}

	private static void registerVessels() {
		for (final ElementVessel vessel : ElementVessel.registry.values()) {
			if (isTargetVersion(vessel.version)) {
				registerItem(vessel, new ItemVessel<ElementVessel>(vessel));
			}
		}

		for (final CompoundVessel vessel : CompoundVessel.registry.values()) {
			if (isTargetVersion(vessel.version)) {
				registerItem(vessel, new ItemVessel<CompoundVessel>(vessel));
			}
		}

	}

	private static void registerPolymers() {
		for (final PolymerPellets polymerPellets : PolymerPellets.registry.values()) {
			if (isTargetVersion(polymerPellets.version)) {
				registerItem(polymerPellets, new ItemVessel<PolymerPellets>(polymerPellets));
			}
		}

		for (final PolymerBlock polymerBlock : PolymerBlock.registry.values()) {
			{
				if (isTargetVersion(polymerBlock.version)) {
					final BlockPolymer block = new BlockPolymer(polymerBlock);
					registerBlockWithItem(polymerBlock.gameID, polymerBlock.name, block, polymerBlock.itemGameID, polymerBlock.itemName,
							ItemPolymerBlock.class, new Object[] {});
				}
			}

		}

		for (final PolymerSlab polymerSlab : PolymerSlab.registry.values()) {
			{
				if (isTargetVersion(polymerSlab.version)) {
					final BlockSlab slab = new BlockPolymerSlab(polymerSlab, false);
					final BlockSlab doubleSlab = new BlockPolymerSlab(polymerSlab, true);
					registerBlockWithItem(polymerSlab.blockSlabGameID, polymerSlab.blockSlabName, slab, polymerSlab.itemSlabGameID, polymerSlab.itemSlabName,
							ItemPolymerSlab.class, new Object[] { slab, doubleSlab, false });
					registerBlockWithItem(polymerSlab.blockDoubleSlabGameID, polymerSlab.blockDoubleSlabName, doubleSlab, polymerSlab.itemDoubleSlabGameID, polymerSlab.itemDoubleSlabName,
							ItemPolymerSlab.class, new Object[] { slab, doubleSlab, true });
				}
			}
		}

		for (final PolymerStairs polymerStairs : PolymerStairs.registry.values()) {
			{
				if (isTargetVersion(polymerStairs.version)) {
					final BlockStairs stairs = new BlockPolymerStairs(polymerStairs, 15);
					registerBlockWithItem(polymerStairs.blockStairsGameID, polymerStairs.blockStairsName, stairs, polymerStairs.itemStairsGameID, polymerStairs.itemStairsName,
							ItemPolymerStairs.class, new Object[] {});
				}
			}

		}

		for (final PolymerWall polymerWall : PolymerWall.registry.values()) {
			{
				if (isTargetVersion(polymerWall.version)) {
					final BlockWall wall = new BlockPolymerWall(polymerWall);
					registerBlockWithItem(polymerWall.blockWallGameID, polymerWall.blockWallName, wall, polymerWall.itemWallGameID, polymerWall.itemWallName,
							ItemPolymerWall.class, new Object[] {});

				}
			}

		}

	}

	private static void registerMolds() {
		for (final Mold mold : Mold.registry.values()) {
			if (isTargetVersion(mold.version)) {
				registerItem(mold, new ItemMold(mold));
			}
		}

	}

	private static void registerMoldedItems() {
		for (final MoldedItem moldedItem : MoldedItem.registry.values()) {

			if (isTargetVersion(moldedItem.version)) {
				Item item = null;
				if (GameID.MoldRunningShoes.matches(moldedItem.source))
					item = new ItemRunningShoes(moldedItem);
				else if (GameID.MoldScubaFins.matches(moldedItem.source))
					item = new ItemScubaFins(moldedItem);
				else if (GameID.MoldScubaMask.matches(moldedItem.source))
					item = new ItemScubaMask(moldedItem);
				else
					item = new ItemMoldedItem(moldedItem);
				registerItem(moldedItem, item);
			}

		}

		for (final PolymerBrick brick : PolymerBrick.registry.values()) {
			if (isTargetVersion(brick.version)) {
				final BlockPolymerBrick blockBrick = new BlockPolymerBrick(brick, brick.length, brick.width);
				registerBlockWithItem(brick.gameID, brick.name, blockBrick, brick.itemGameID, brick.itemName,
						ItemPolymerBrick.class, new Object[] {});
			}

		}

	}

	private static void registerMaskItems() {
		for (final Mask maskItem : Mask.registry.values()) {
			if (isTargetVersion(maskItem.version)) {
				registerItem(maskItem, new ItemMask(maskItem));
			}
		}

	}

	private static void registerWaferItems() {
		for (final WaferItem waferItem : WaferItem.registry.values()) {
			if (isTargetVersion(waferItem.version)) {
				registerItem(waferItem, new ItemWafer(waferItem));
			}
		}

	}

	private static void registerElectronics() {
		for (final Electronics electronics : Electronics.registry.values()) {
			if (isTargetVersion(electronics.version)) {
				registerItem(electronics, new ItemElectronics(electronics));
			}
		}

	}

	private static void registerDNASamplers() {
		for (final DNASampler dnaSampler : DNASampler.registry.values()) {
			if (isTargetVersion(dnaSampler.version)) {
				registerItem(dnaSampler, new ItemDNASampler(dnaSampler));
			}
		}

	}

	private static void registerCellCultureDishes() {
		for (final CellCultureDish cellCultureDish : CellCultureDish.registry.values()) {
			if (isTargetVersion(cellCultureDish.version)) {
				registerItem(cellCultureDish, new ItemCellCultureDish(cellCultureDish));
			}
		}

	}

	private static void registerGrippedTools() {
		for (final GrippedTool grippedTool : GrippedTool.registry.values()) {
			if (isTargetVersion(grippedTool.version)) {
				registerItem(grippedTool, ItemGripped.create(grippedTool));
			}
		}

	}
	
	private static void registerGrippedSyntheticTools() {
		

		
		for (final GrippedSyntheticTool grippedSyntheticTool : GrippedSyntheticTool.registry.values()) {
			if (isTargetVersion(grippedSyntheticTool.version)) {
				final ToolMaterial material = EnumHelper.addToolMaterial(
						grippedSyntheticTool.name, grippedSyntheticTool.harvestLevel, grippedSyntheticTool.maxUses, 
						grippedSyntheticTool.efficiency, grippedSyntheticTool.damage, grippedSyntheticTool.enchantability);
				//material.customCraftingMaterial = PolycraftRegistry.getItem(grippedSyntheticTool.craftingHeadItemName);
				registerItem(grippedSyntheticTool, ItemSyntheticGripped.create(grippedSyntheticTool, material));
			}
		}

	}

	private static void registerPogoSticks() {
		for (final PogoStick pogoStick : PogoStick.registry.values()) {
			if (isTargetVersion(pogoStick.version)) {
				registerItem(pogoStick, new ItemPogoStick(pogoStick));
			}
		}

	}

	private static void registerArmors() {
		for (final Armor armor : Armor.registry.values()) {
			if (isTargetVersion(armor.version)) {

				final ArmorMaterial material = EnumHelper.addArmorMaterial(
						armor.name, armor.durability, armor.reductionAmounts, armor.enchantability);
				material.customCraftingMaterial = PolycraftRegistry.getItem(armor.craftingItemName);
				registerItem(
						armor.componentGameIDs[ArmorSlot.HEAD.getValue()],
						armor.getFullComponentName(ArmorSlot.HEAD),
						new ItemArmorHead(armor, material));
				registerItem(
						armor.componentGameIDs[ArmorSlot.CHEST.getValue()],
						armor.getFullComponentName(ArmorSlot.CHEST),
						new ItemArmorChest(armor, material));
				registerItem(
						armor.componentGameIDs[ArmorSlot.LEGS.getValue()],
						armor.getFullComponentName(ArmorSlot.LEGS),
						new ItemArmorLegs(armor, material));
				registerItem(
						armor.componentGameIDs[ArmorSlot.FEET.getValue()],
						armor.getFullComponentName(ArmorSlot.FEET),
						new ItemArmorFeet(armor, material));
			}
		}
	}

	private static void registerTools() {
		for (final Tool tool : Tool.registry.values()) {
			if (isTargetVersion(tool.version)) {
				final ToolMaterial material = EnumHelper.addToolMaterial(
						tool.name, tool.harvestLevel, tool.maxUses, tool.efficiency, tool.damage, tool.enchantability);
				material.customCraftingMaterial = PolycraftRegistry.getItem(tool.craftingHeadItemName);
				registerItem(
						tool.typeGameIDs[Tool.Type.HOE.ordinal()],
						tool.getFullTypeName(Tool.Type.HOE),
						new ItemToolHoe(tool, material));
				registerItem(
						tool.typeGameIDs[Tool.Type.SWORD.ordinal()],
						tool.getFullTypeName(Tool.Type.SWORD),
						new ItemToolSword(tool, material));
				registerItem(
						tool.typeGameIDs[Tool.Type.SHOVEL.ordinal()],
						tool.getFullTypeName(Tool.Type.SHOVEL),
						new ItemToolShovel(tool, material));
				registerItem(
						tool.typeGameIDs[Tool.Type.PICKAXE.ordinal()],
						tool.getFullTypeName(Tool.Type.PICKAXE),
						new ItemToolPickaxe(tool, material));
				registerItem(
						tool.typeGameIDs[Tool.Type.AXE.ordinal()],
						tool.getFullTypeName(Tool.Type.AXE),
						new ItemToolAxe(tool, material));
			}
		}
	}

	private static void registerInventories() {
		for (final Inventory inventory : Inventory.registry.values()) {
			if (isTargetVersion(inventory.version)) {
				if (GameID.InventoryTreeTap.matches(inventory))
					TreeTapInventory.register(inventory);
				else if (GameID.InventoryMachiningMill.matches(inventory))
					MachiningMillInventory.register(inventory);
				else if (GameID.InventoryExtruder.matches(inventory))
					ExtruderInventory.register(inventory);
				else if (GameID.InventoryInjectionMolder.matches(inventory))
					InjectionMolderInventory.register(inventory);
				else if (GameID.InventoryDistillationColumn.matches(inventory))
					DistillationColumnInventory.register(inventory);
				else if (GameID.InventorySteamCracker.matches(inventory))
					SteamCrackerInventory.register(inventory);
				else if (GameID.InventoryMeroxTreatmentUnit.matches(inventory))
					MeroxTreatmentUnitInventory.register(inventory);
				else if (GameID.InventoryChemicalProcessor.matches(inventory))
					ChemicalProcessorInventory.register(inventory);
				else if (GameID.InventoryContactPrinter.matches(inventory))
					ContactPrinterInventory.register(inventory);
				else if (GameID.InventoryTradingHouse.matches(inventory))
					TradingHouseInventory.register(inventory);
				else if (GameID.InventoryFloodlight.matches(inventory))
					FloodlightInventory.register(inventory);
				else if (GameID.InventorySpotlight.matches(inventory))
					SpotlightInventory.register(inventory);
				else if (GameID.InventoryGaslamp.matches(inventory))
					GaslampInventory.register(inventory);
				else if (GameID.InventoryOilDerrick.matches(inventory))
					OilDerrickInventory.register(inventory);
				else if (GameID.InventoryCondenser.matches(inventory))
					CondenserInventory.register(inventory);
				else if (GameID.InventorySolarArray.matches(inventory))
					SolarArrayInventory.register(inventory);
				else if (GameID.InventoryPlasticChest.matches(inventory))
					PlasticChestInventory.register(inventory);
				else if (GameID.InventoryPortalChest.matches(inventory))
					PortalChestInventory.register(inventory);
				else if (GameID.InventoryIndustrialOven.matches(inventory))
					IndustrialOvenInventory.register(inventory);
				else if (GameID.InventoryPump.matches(inventory))
					PumpInventory.register(inventory);
				else if (GameID.InventoryFlowRegulator.matches(inventory))
					FlowRegulatorInventory.register(inventory);
				else if (GameID.InventoryMaskWriter.matches(inventory))
					MaskWriterInventory.register(inventory);
				else if (GameID.InventoryPrintingPress.matches(inventory))
					PrintingPressInventory.register(inventory);
				else if (GameID.InventoryTerritoryFlag.matches(inventory))
					TerritoryFlagInventory.register(inventory);
				else if (GameID.InventoryCHEM2323.matches(inventory))
					CHEM2323Inventory.register(inventory);
				else if (GameID.InventoryComputer.matches(inventory))
					ComputerInventory.register(inventory);
				else if (GameID.InventoryHospital.matches(inventory))
					HospitalGeneratorInventory.register(inventory);
				else if (GameID.InventoryFluorescentLamp.matches(inventory))
					FluorescentLampInventory.register(inventory);
				else if (GameID.InventoryTierChest.matches(inventory))
					TierChestInventory.register(inventory);
				else if (GameID.TextWall.matches(inventory))
					TextWallInventory.register(inventory);
				else if (GameID.InventoryPolycrafting.matches(inventory))
					PolycraftingInventory.register(inventory);
				else if (GameID.InventoryCannon.matches(inventory))
					CannonInventory.register(inventory);
				else
					logger.warn("Unhandled inventory: {} ({})", inventory.name, inventory.gameID);
			}
		}
	}

	// added for testing model uploads
	private static void registerTileEntity(Class<? extends TileEntity> tileEntity, String id) {
		GameRegistry.registerTileEntity(tileEntity, PolycraftMod.MODID + ":" + id);
	}

	private static void registerPolycraftEntities() {
		for (final PolycraftEntity polycraftEntity : PolycraftEntity.registry.values()) {
			if (isTargetVersion(polycraftEntity.version)) {
				if (GameID.EntityResearchAssistant.matches(polycraftEntity)){
					ResearchAssistantEntity.register(polycraftEntity);
				}
				else if(GameID.EntityTerritoryFlag.matches(polycraftEntity)){
					EntityTerritoryFlag.register(polycraftEntity);
				}
				else if(GameID.EntityOilSlime.matches(polycraftEntity)){
					EntityOilSlime.register(polycraftEntity);
				}
				else if(GameID.EntityOilSlimeBall.matches(polycraftEntity)){
					EntityOilSlimeBallProjectile.register(polycraftEntity);
				}
				else if(GameID.EntityDummy.matches(polycraftEntity)){
					EntityDummy.register(polycraftEntity);
				}
				else if (GameID.EntityTestTerritoryFlagBoss.matches(polycraftEntity)) {
					TestTerritoryFlagBoss.register(polycraftEntity);
				}
				else if (GameID.EntityAndroid.matches(polycraftEntity)){
					EntityAndroid.register(polycraftEntity);
				}
				else if (GameID.EntityIronCannonBall.matches(polycraftEntity)){
					EntityIronCannonBall.register(polycraftEntity);
				}
				else if (GameID.EPaintball.matches(polycraftEntity)) {
					EntityPellet.register(polycraftEntity);
				}
					
				//else if (GameID.EntityTerritoryFlag.matches(polycraftEntity))
				//	TerritoryFlagEntity.register(polycraftEntity);
				else
					logger.warn("Unhandled inventory: {} ({})", polycraftEntity.name, polycraftEntity.gameID);
			}
		}
	}

	private static void registerCustom() {
		final InternalObject light = InternalObject.registry.get("BlockLight");
		if (light != null && isTargetVersion(light.version)) {
			PolycraftMod.blockLight = registerBlock(light, new BlockLight(1.0f));
		}

		final InternalObject oil = InternalObject.registry.get("Oil");
		Fluid fluidOil = null;
		if (oil != null && isTargetVersion(oil.version)) {
			fluidOil = new Fluid(oil.name.toLowerCase()).setDensity(PolycraftMod.oilFluidDensity).setViscosity(PolycraftMod.oilFluidViscosity);
			FluidRegistry.registerFluid(fluidOil);
		}

		final InternalObject blockPipe = InternalObject.registry.get("BlockPipe");
		if (blockPipe != null && isTargetVersion(blockPipe.version)) {
			TileEntityBlockPipe.register(blockPipe);
		}

		final InternalObject collision = InternalObject.registry.get("BlockCollision");
		if (collision != null && isTargetVersion(collision.version)) {
			PolycraftMod.blockCollision = registerBlock(collision, new BlockCollision(collision));
		}

		if (isTargetVersion(new int[] { 1, 0, 0 })) {
			registerTileEntity(TileEntityPolymerBrick.class, "model_of_brick");// + id);
		}

		if (fluidOil != null) //do not reorder this even though it seems more efficient, because registration order matters!
		{
			PolycraftMod.blockOil = registerBlock(oil,
					new BlockFluid(fluidOil, Material.water) 
							.setFlammable(true)
							.setFlammability(PolycraftMod.oilBlockFlammability)
							.setParticleColor(0.7F, 0.7F, 0.0F));
			fluidOil.setBlock(PolycraftMod.blockOil);
		}

		for (final CustomObject customObject : CustomObject.registry.values()) {
			if (isTargetVersion(customObject.version)) {
				if (GameID.CustomBucketOil.matches(customObject)) {
					PolycraftMod.itemOilBucket = registerItem(customObject,
							new PolycraftBucket(PolycraftMod.blockOil)
									.setTextureName(PolycraftMod.getAssetName("bucket_oil")));
					PolycraftMod.itemOilBucket.setContainerItem(Items.bucket);
					FluidContainerRegistry.registerFluidContainer(
							FluidRegistry.getFluidStack(fluidOil.getName(), FluidContainerRegistry.BUCKET_VOLUME),
							new ItemStack(PolycraftMod.itemOilBucket),
							new ItemStack(Items.bucket));
					BucketHandler.INSTANCE.buckets.put(PolycraftMod.blockOil, PolycraftMod.itemOilBucket);
					MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);
				} else if (GameID.CustomFlameThrower.matches(customObject)) {
					registerItem(customObject, new ItemFlameThrower(customObject, "flame_thrower"));
				} else if (GameID.CustomFlameTosser.matches(customObject)) {
					registerItem(customObject, new ItemFlameThrower(customObject, "flame_tosser"));
				} else if (GameID.CustomFlameHurler.matches(customObject)) {
					registerItem(customObject, new ItemFlameThrower(customObject, "flame_hurler"));
				} else if (GameID.CustomFlameChucker.matches(customObject)) {
					registerItem(customObject, new ItemFlameThrower(customObject, "flame_chucker"));
				} else if (GameID.CustomFreezeRayBeginner.matches(customObject)) {
					registerItem(customObject, new ItemFreezeRay(customObject, "freeze_ray_beginner"));
				} else if (GameID.CustomFreezeRayIntermediate.matches(customObject)) {
					registerItem(customObject, new ItemFreezeRay(customObject, "freeze_ray_intermediate"));
				} else if (GameID.CustomFreezeRayAdvanced.matches(customObject)) {
					registerItem(customObject, new ItemFreezeRay(customObject, "freeze_ray_advanced"));
				} else if (GameID.CustomFreezeRayPro.matches(customObject)) {
					registerItem(customObject, new ItemFreezeRay(customObject, "freeze_ray_pro"));
				} else if (GameID.CustomWaterCannonBeginner.matches(customObject)) {
					registerItem(customObject, new ItemWaterCannon(customObject, "water_cannon_beginner"));
				} else if (GameID.CustomWaterCannonIntermediate.matches(customObject)) {
					registerItem(customObject, new ItemWaterCannon(customObject, "water_cannon_intermediate"));
				} else if (GameID.CustomWaterCannonAdvanced.matches(customObject)) {
					registerItem(customObject, new ItemWaterCannon(customObject, "water_cannon_advanced"));
				} else if (GameID.CustomWaterCannonPro.matches(customObject)) {
					registerItem(customObject, new ItemWaterCannon(customObject, "water_cannon_pro"));
				} else if (GameID.CustomFlashlight.matches(customObject)) {
					registerItem(customObject, new ItemFlashlight(customObject));
				} else if (GameID.CustomJetPackBeginner.matches(customObject)) {
					registerItem(customObject, new ItemJetPack(customObject));
				} else if (GameID.CustomJetPackIntermediate.matches(customObject)) {
					registerItem(customObject, new ItemJetPack(customObject));
				} else if (GameID.CustomJetPackAdvanced.matches(customObject)) {
					registerItem(customObject, new ItemJetPack(customObject));
				} else if (GameID.CustomJetPackPro.matches(customObject)) {
					registerItem(customObject, new ItemJetPack(customObject));
				} else if (GameID.CustomParachute.matches(customObject)) {
					registerItem(customObject, new ItemParachute(customObject));
				} else if (GameID.CustomPhaseShifter.matches(customObject)) {
					registerItem(customObject, new ItemPhaseShifter(customObject));
				} else if (GameID.CustomScubaTankBeginner.matches(customObject)) {
					registerItem(customObject, new ItemScubaTank(customObject, "scuba_tank_beginner"));
				} else if (GameID.CustomScubaTankIntermediate.matches(customObject)) {
					registerItem(customObject, new ItemScubaTank(customObject, "scuba_tank_intermediate"));
				} else if (GameID.CustomScubaTankAdvanced.matches(customObject)) {
					registerItem(customObject, new ItemScubaTank(customObject, "scuba_tank_advanced"));
				} else if (GameID.CustomScubaTankPro.matches(customObject)) {
					registerItem(customObject, new ItemScubaTank(customObject, "scuba_tank_pro"));
				} else if (GameID.CustomHeatedKnifeDiamondPolyIsoPrene.matches(customObject)) {
					registerItem(customObject, new ItemHeatedKnife(customObject, "heated_knife_diamond_NR"));
				} else if (GameID.CustomHeatedKnifeDiamondPolyPropylene.matches(customObject)) {
					registerItem(customObject, new ItemHeatedKnife(customObject, "heated_knife_diamond_PP"));
				} else if (GameID.CustomHeatedKnifeDiamondPEEK.matches(customObject)) {
					registerItem(customObject, new ItemHeatedKnife(customObject, "heated_knife_diamond_PEEK"));
				} else if (GameID.CustomHeatedKnifeStainlessPolyIsoPrene.matches(customObject)) {
					registerItem(customObject, new ItemHeatedKnife(customObject, "heated_knife_stainless_NR"));
				} else if (GameID.CustomHeatedKnifeStainlessPolyPropylene.matches(customObject)) {
					registerItem(customObject, new ItemHeatedKnife(customObject, "heated_knife_stainless_PP"));
				} else if (GameID.CustomHeatedKnifeStainlessPEEK.matches(customObject)) {
					registerItem(customObject, new ItemHeatedKnife(customObject, "heated_knife_stainless_PEEK"));
				} else if (GameID.CustomRunningShoesSprinter.matches(customObject)) {
					registerItem(customObject, new ItemRunningShoes(customObject, "running_shoes_sprinter"));
				} else if (GameID.CustomScubaMaskLightBeginner.matches(customObject)) {
					registerItem(customObject, new ItemScubaMask(MoldedItem.registry.get("Scuba Mask (Beginner)"), "scuba_mask_light"));
				} else if (GameID.CustomScubaMaskLightIntermediate.matches(customObject)) {
					registerItem(customObject, new ItemScubaMask(MoldedItem.registry.get("Scuba Mask (Intermediate)"), "scuba_mask_light"));
				} else if (GameID.CustomScubaMaskLightAdvanced.matches(customObject)) {
					registerItem(customObject, new ItemScubaMask(MoldedItem.registry.get("Scuba Mask (Advanced)"), "scuba_mask_light"));
				} else if (GameID.CustomScubaMaskLightPro.matches(customObject)) {
					registerItem(customObject, new ItemScubaMask(MoldedItem.registry.get("Scuba Mask (Pro)"), "scuba_mask_light"));
				} else if (GameID.CustomVoiceCone.matches(customObject)) {
					registerItem(customObject, new ItemCommunication(customObject));
				} else if (GameID.CustomMegaphone.matches(customObject)) {
					registerItem(customObject, new ItemCommunication(customObject));
				} else if (GameID.CustomWalkyTalky.matches(customObject)) {
					registerItem(customObject, new ItemCommunication(customObject));
				} else if (GameID.CustomHAMRadio.matches(customObject)) {
					registerItem(customObject, new ItemCommunication(customObject));
				} else if (GameID.CustomCellPhone.matches(customObject)) {
					registerItem(customObject, new ItemCommunication(customObject));
				} else if (GameID.CustomSmartPhone.matches(customObject)) {
					registerItem(customObject, new ItemCommunication(customObject));
				} else if (GameID.CustomAirQualityDetecctor.matches(customObject)) {
					registerItem(customObject, new ItemAirQualityDetector(customObject));
				} else if (GameID.FluorescentBulbs.matches(customObject)) {
					registerItem(customObject, new ItemFluorescentBulbs(customObject));
				} else if (GameID.CustomOilSlimeBall.matches(customObject)) {
					registerItem(customObject, new ItemOilSlimeBall(customObject, "Oil_Slime_Ball"));
				} else if (GameID.PasswordDoor.matches(customObject)) {
					BlockPasswordDoor passwordDoor = new BlockPasswordDoor(customObject, Material.iron, "test");
					registerBlock(customObject, passwordDoor);
					registerItem(customObject.params.get(0), "item" + customObject.name, new ItemPolycraftDoor(Material.iron, passwordDoor));
				} else if (GameID.CustomDevTool.matches(customObject)) {
					registerItem(customObject, new ItemDevTool(customObject));
				} else if (GameID.CustomChallengeBlock.matches(customObject)) {
					registerBlock(customObject, new BlockChallengeBlock(customObject));
				} else if (GameID.CustomConstitutionClaim.matches(customObject)) {
					registerItem(customObject, new ItemConstitutionClaim(customObject));
				} else if (GameID.CustomSuperInk.matches(customObject)) {
					registerItem(customObject, new ItemSuperInk(customObject));
				} else if (GameID.CustomPolyPortal.matches(customObject)) {
					registerBlock(customObject, new BlockPolyPortal(customObject));
				} else if (GameID.KnockbackBomb.matches(customObject)) {
					registerItem(customObject, new ItemKnockbackBomb(customObject));
				} else if (GameID.FreezingKnockbackBomb.matches(customObject)) {
					registerItem(customObject, new ItemFreezingKnockbackBomb(customObject));
				} else if (GameID.Cleats.matches(customObject)) {
					registerItem(customObject, new ItemCleats(customObject));
				} else if (GameID.CustomMiningHammer.matches(customObject)) {
					registerItem(customObject, new ItemMiningHammer(customObject));
				} else if (GameID.HPBlock.matches(customObject)) {
					registerBlock(customObject, new HPBlock(customObject));
				} else if (GameID.ItemIronCannonball.matches(customObject)) {
					registerItem(customObject, new ItemIronCannonBall(customObject));
				} else if (GameID.Slingshot.matches(customObject)) {
					registerItem(customObject, new ItemSlingshot(customObject));
				} else if (GameID.Paintball.matches(customObject)) {
					registerItem(customObject, new ItemPaintball(customObject));
					
					
				}else
					// TODO should we throw an exception if we don't have a true custom item (needed an implementation)
					registerItem(customObject, new ItemCustom(customObject));
			}
		}
	}

	public static void exportLangEntries(final String exportFile) throws IOException {
		final String fluidFormat = "fluid.%s=%s";
		final String containerFormat = "container.%s=%s";
		final String baseFormat = "%s.name=%s";
		final String colorFormat = "%s.%d.name=%s %s";
		final String blockFormat = "tile." + baseFormat;
		final String itemFormat = "item." + baseFormat;
		final String entityFormat = "entity.polycraft." + baseFormat;

		final Collection<String> langEntries = new LinkedList<String>();

		for (final InternalObject internalObject : InternalObject.registry.values()) {
			if (GameID.InternalOil.matches(internalObject)) {
				langEntries.add(String.format(fluidFormat, internalObject.name.toLowerCase(), internalObject.display));
				langEntries.add(String.format(blockFormat, internalObject.gameID, internalObject.display));
			} else if (GameID.InternalBlockPipe.matches(internalObject)) {
				langEntries.add(String.format(baseFormat, internalObject.gameID, internalObject.display));
			} else if (GameID.InternalItemPipe.matches(internalObject)) {
				langEntries.add(String.format(itemFormat, internalObject.gameID, internalObject.display));
			}
		}

		for (final Ore ore : Ore.registry.values()) {
			if (ore.source instanceof Element) {
				langEntries.add(String.format(blockFormat, ore.gameID, "[" + ((Element) ore.source).symbol + " " + ((Element) ore.source).atomicNumber + "]  " + ore.name));
			} else {
				langEntries.add(String.format(blockFormat, ore.gameID, ore.name));
			}
		}

		for (final Ingot ingot : Ingot.registry.values())
			langEntries.add(String.format(itemFormat, ingot.gameID, ingot.name));

		for (final Nugget nugget : Nugget.registry.values())
			langEntries.add(String.format(itemFormat, nugget.gameID, nugget.name));

		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			langEntries.add(String.format(blockFormat, compressedBlock.gameID, compressedBlock.name));

		for (final Catalyst catalyst : Catalyst.registry.values())
			langEntries.add(String.format(itemFormat, catalyst.gameID, catalyst.name));

		for (final Flashcard flashcard : Flashcard.registry.values())
			langEntries.add(String.format(itemFormat, flashcard.gameID, flashcard.name));

		for (final Exam exam : Exam.registry.values())
			langEntries.add(String.format(itemFormat, exam.gameID, exam.name));

		for (final DNASampler dnaSampler : DNASampler.registry.values())
			langEntries.add(String.format(itemFormat, dnaSampler.gameID, dnaSampler.name));

		for (final CellCultureDish cellCultureDish : CellCultureDish.registry.values())
			langEntries.add(String.format(itemFormat, cellCultureDish.gameID, cellCultureDish.name));

		for (final ElementVessel vessel : ElementVessel.registry.values())
			langEntries.add(String.format(itemFormat, vessel.gameID, vessel.name));

		for (final CompoundVessel vessel : CompoundVessel.registry.values())
			langEntries.add(String.format(itemFormat, vessel.gameID, vessel.name));

		for (final PolymerPellets polymerPellets : PolymerPellets.registry.values())
			langEntries.add(String.format(itemFormat, polymerPellets.gameID, polymerPellets.name));

		for (final PolymerBlock polymerBlock : PolymerBlock.registry.values())
			for (int i = 0; i < BlockPolymerHelper.colors.length; i++)
				langEntries.add(String.format(colorFormat, polymerBlock.gameID, i, BlockPolymerHelper.getColorDisplayName(i), polymerBlock.name));

		for (final PolymerBrick brick : PolymerBrick.registry.values())
			for (int i = 0; i < BlockPolymerBrickHelper.colors.length; i++)
				langEntries.add(String.format(colorFormat, brick.gameID, i, BlockPolymerBrickHelper.getColorDisplayName(i), brick.name));

		for (final PolymerSlab polymerSlab : PolymerSlab.registry.values())
			langEntries.add(String.format(baseFormat, polymerSlab.blockSlabGameID, polymerSlab.name));

		for (final PolymerStairs polymerStairs : PolymerStairs.registry.values())
			langEntries.add(String.format(baseFormat, polymerStairs.blockStairsGameID, polymerStairs.name));

		for (final PolymerWall polymerWall : PolymerWall.registry.values())
			for (int i = 0; i < BlockPolymerHelper.colors.length; i++)
				langEntries.add(String.format(colorFormat, polymerWall.blockWallGameID, i, BlockPolymerHelper.getColorDisplayName(i), polymerWall.name));

		for (final Mold mold : Mold.registry.values())
			langEntries.add(String.format(itemFormat, mold.gameID, mold.name));

		for (final MoldedItem moldedItem : MoldedItem.registry.values())
			langEntries.add(String.format(itemFormat, moldedItem.gameID, moldedItem.name));

		for (final Mask mask : Mask.registry.values())
			langEntries.add(String.format(itemFormat, mask.gameID, mask.name));

		for (final WaferItem waferItem : WaferItem.registry.values())
			langEntries.add(String.format(itemFormat, waferItem.gameID, waferItem.name));

		for (final Electronics electronics : Electronics.registry.values())
			langEntries.add(String.format(itemFormat, electronics.gameID, electronics.name));

		for (final GrippedTool grippedTool : GrippedTool.registry.values())
			langEntries.add(String.format(itemFormat, grippedTool.gameID, grippedTool.name));
		
		for (final GrippedSyntheticTool grippedSyntheticTool : GrippedSyntheticTool.registry.values())
			langEntries.add(String.format(itemFormat, grippedSyntheticTool.gameID, grippedSyntheticTool.name));

		for (final Armor armor : Armor.registry.values()) {
			for (final ArmorSlot armorSlot : ArmorSlot.values()) {
				langEntries.add(String.format(itemFormat, armor.componentGameIDs[armorSlot.getValue()], armor.getFullComponentName(armorSlot)));
			}
		}

		for (final Tool tool : Tool.registry.values()) {
			for (final Tool.Type toolType : Tool.Type.values()) {
				langEntries.add(String.format(itemFormat, tool.typeGameIDs[toolType.ordinal()], tool.getFullTypeName(toolType)));
			}
		}

		for (final PogoStick pogoStick : PogoStick.registry.values())
			langEntries.add(String.format(itemFormat, pogoStick.gameID, pogoStick.name));

		for (final Inventory inventory : Inventory.registry.values()) {
			langEntries.add(String.format(containerFormat, inventory.gameID, inventory.name));
			langEntries.add(String.format(blockFormat, inventory.gameID, inventory.name));
		}

		for (final CustomObject customObject : CustomObject.registry.values())
			langEntries.add(String.format(itemFormat, customObject.gameID, customObject.name));
		
		for (final PolycraftEntity entity : PolycraftEntity.registry.values())
			langEntries.add(String.format(entityFormat, entity.name, entity.name));

		final PrintWriter writer = new PrintWriter(exportFile);
		for (final String line : langEntries) {
			writer.println(line);
		}
		writer.close();
	}
}
