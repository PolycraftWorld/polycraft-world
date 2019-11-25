package edu.utd.minecraft.mod.polycraft;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.block.BlockChallengeBlock;
import edu.utd.minecraft.mod.polycraft.block.BlockCollision;
import edu.utd.minecraft.mod.polycraft.block.BlockCompressed;
import edu.utd.minecraft.mod.polycraft.block.BlockFluid;
import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.block.BlockPasswordDoor;
import edu.utd.minecraft.mod.polycraft.block.BlockPolyDirectional;
import edu.utd.minecraft.mod.polycraft.block.BlockPolyPortal;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerBrick;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerBrickHelper;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerHelper;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerHelper.EnumColor;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerSlab;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerSlabDouble;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerStairs;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerWall;
import edu.utd.minecraft.mod.polycraft.block.HPBlock;
import edu.utd.minecraft.mod.polycraft.block.PlaceBlockPP;
import edu.utd.minecraft.mod.polycraft.block.material.BreakBlockPP;
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
import edu.utd.minecraft.mod.polycraft.config.PolyBlockOrientable;
import edu.utd.minecraft.mod.polycraft.config.PolymerBrick;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;
import edu.utd.minecraft.mod.polycraft.config.PolymerStairs;
import edu.utd.minecraft.mod.polycraft.config.PolymerWall;
import edu.utd.minecraft.mod.polycraft.config.Tool;
import edu.utd.minecraft.mod.polycraft.config.WaferItem;
import edu.utd.minecraft.mod.polycraft.entity.EntityOilSlimeBallProjectile;
import edu.utd.minecraft.mod.polycraft.entity.EntityPellet__Old;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityGravelCannonBall;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityIronCannonBall;
import edu.utd.minecraft.mod.polycraft.entity.boss.TestTerritoryFlagBoss;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityAndroid;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityDummy;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityOilSlime;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityTerritoryFlag;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.ResearchAssistantEntity;
import edu.utd.minecraft.mod.polycraft.handler.BucketHandler;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.CannonInventory;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.GravelCannonInventory;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.GravelCannonInventoryTeir1;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.GravelCannonInventoryTeir2;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.GravelCannonInventoryTeir3;
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
import edu.utd.minecraft.mod.polycraft.item.ItemAITool;
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
import edu.utd.minecraft.mod.polycraft.item.ItemGravelCannonBall;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemHeatedKnife;
import edu.utd.minecraft.mod.polycraft.item.ItemIngot;
import edu.utd.minecraft.mod.polycraft.item.ItemIronCannonBall;
import edu.utd.minecraft.mod.polycraft.item.ItemSlingshot__Old;
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
import net.minecraft.block.state.IBlockState;

import net.minecraft.client.Minecraft;

//import net.minecraft.client.renderer.ItemMeshDefinition;

import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.server.MinecraftServer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.BlockFluidBase;
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
	public static final String assetPath = "C:\\Users\\sxg115630\\Desktop\\Polycraft Forge 1.8.9\\src\\main\\resources\\assets\\polycraft\\";
//	public static final String assetPath = "C:\\Users\\vxg173330\\Desktop\\Polycraft 1.8.9\\src\\main\\resources\\assets\\polycraft\\";
//	public static final String assetPath = "C:\\Users\\mjg150230\\1.8.9 PolycraftForge\\src\\main\\resources\\assets\\polycraft\\";

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
		if (items.containsKey(name.toLowerCase()))
			return items.get(name.toLowerCase());
		else
			return items.get(name);
	}

	public static Block registerBlock(final GameIdentifiedConfig config, final Block block) {
		return registerBlock(config.gameID, config.name, block);
	}

	private static Block registerBlock(final String gameID, final String name, final Block block) {
		//changing naming method for 1.8
//		registerName(gameID, name);	
//		block.setUnlocalizedName(gameID);
//		GameRegistry.registerBlock(block, gameID);
		registerName(PolycraftMod.getFileSafeName(name), name);
		block.setUnlocalizedName(PolycraftMod.getFileSafeName(name));
		GameRegistry.registerBlock(block, PolycraftMod.getFileSafeName(name));
		Item item = GameRegistry.findItem(PolycraftMod.MODID, PolycraftMod.getFileSafeName(name));
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
			ModelLoader.setCustomModelResourceLocation(
				item,
				0,
				new ModelResourceLocation(item.getRegistryName()));
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
		//changing naming method from 1.8
//		registerName(gameID, name);
//		if (!(item instanceof PolycraftItem))
//			throw new IllegalArgumentException("Item " + name + " must implement PolycraftItem (" + item.toString() + ")");
//		item.setRegistryName(gameID);
//		GameRegistry.registerItem(item, gameID);
		registerName(PolycraftMod.getFileSafeName(name), name);
		if (!(item instanceof PolycraftItem))
			throw new IllegalArgumentException("Item " + name + " must implement PolycraftItem (" + item.toString() + ")");
		item.setUnlocalizedName(PolycraftMod.getFileSafeName(name));
		GameRegistry.registerItem(item, PolycraftMod.getFileSafeName(name));
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
			ModelLoader.setCustomModelResourceLocation(
				item,
				0,
				new ModelResourceLocation(item.getRegistryName()));
		items.put(name, item);
		return item;
	}

	public static Block registerBlockWithItem(final String blockGameID, final String blockName, final Block block,
			final String itemBlockGameID, final String itemBlockName, final Class<? extends ItemBlock> itemBlockClass, Object... itemCtorArgs) {
//		block.setUnlocalizedName(blockGameID);
//		GameRegistry.registerBlock(block, itemBlockClass, blockGameID, null, itemCtorArgs);
//		blocks.put(blockName, block);
//		registerName(itemBlockGameID, itemBlockName);
//		registerName(blockGameID, blockName);
//
//		final Item itemBlock = Item.getItemFromBlock(block);
//		itemBlock.setRegistryName(itemBlockGameID);
//		items.put(itemBlockName, itemBlock);
		
		block.setUnlocalizedName(blockGameID);
		GameRegistry.registerBlock(block, itemBlockClass, blockGameID, itemCtorArgs);
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
	
	@SideOnly(Side.CLIENT)
	public static void registerClientSideResources() {
		registerClientPolymers();
		registerFluidModels();
		
		OBJLoader.instance.addDomain(PolycraftMod.MODID.toLowerCase());
        
		for(final Inventory inv: Inventory.registry.values()) {
			if(inv.render3D) {
				Item item2 = GameRegistry.findItem(PolycraftMod.MODID, PolycraftMod.getFileSafeName(inv.name));
				if(item2 != null)
					if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
						ModelLoader.setCustomModelResourceLocation(item2, 0, new ModelResourceLocation(item2.getRegistryName(), "inventory"));
			}
		}
//		Item item2 = GameRegistry.findItem(PolycraftMod.MODID, "flood_light");
//        ModelLoader.setCustomModelResourceLocation(item2, 0, new ModelResourceLocation(PolycraftMod.MODID.toLowerCase() + ":" + "flood_light", "inventory"));

		
//		for (final MoldedItem moldedItem : MoldedItem.registry.values()) {
//			if (moldedItem.loadCustomTexture) {
//				ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(PolycraftMod.getFileSafeName(MoldedItem.class.getSimpleName() + "_" + config.name));
//			    ModelLoader.setCustomModelResourceLocation(itemBlockVariants, 0, itemModelResourceLocation);
//			}else {
//				ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(MoldedItem.class.getSimpleName() + "_" + config.source.polymerObject.name);
//			    ModelLoader.setCustomModelResourceLocation(itemBlockVariants, 0, itemModelResourceLocation);
//			}
	}
	
	public static void registerFluidModels()
	{
//		ModelBakery.registerItemVariants(Item.getItemFromBlock(PolycraftMod.blockOil));
//		
//		final ModelResourceLocation modelResourceLocation = new ModelResourceLocation("polycraft:oil",((BlockFluidBase)PolycraftMod.blockOil).getFluid().getName());
//	
//		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(PolycraftMod.blockOil), new ItemMeshDefinition()
//        {
//            @Override
//            public ModelResourceLocation getModelLocation(ItemStack stack)
//            {
//                return modelResourceLocation;
//            }
//        });
//		
//		ModelLoader.setCustomStateMapper(PolycraftMod.blockOil,new StateMapperBase() {
//			@Override
//			protected ModelResourceLocation getModelResourceLocation(IBlockState p_178132_1_) {
//				return modelResourceLocation;
//			}
//		});
		
	}
	
	/**
	 * Registers the blocks renders
	 * 
	 * @param block
	 *            The block
	 */
	public static void registerRender(Block block) {
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(
				new ResourceLocation(PolycraftMod.MODID, block.getUnlocalizedName().substring(5)), "inventory"));
		PolycraftMod.logger.info("Registered render for " + block.getUnlocalizedName().substring(5));
	}
	
	private static void registerMinigames()
	{
		KillWall.register(KillWall.id,KillWall.class);
		RaceGame.register(RaceGame.id,RaceGame.class);
		PolycraftMinigameManager.registerMinigame(RaidGame.id, RaidGame.class);
		
	}

	private static void registerMinecraftItems() {
		for (final ResourceLocation MinecraftItem : Item.itemRegistry.getKeys()) {
			Item mcItem = Item.itemRegistry.getObject(MinecraftItem);
			if(mcItem.getRegistryName().contains("minecraft:"))
			{
				if(mcItem.getRegistryName().contains("iron"))
				{
					int i = 1;//do something
				}
				String name=mcItem.getRegistryName().substring(10);
				name=name.replace('_', ' ');
			
			
				if(!items.containsKey(mcItem.getUnlocalizedName())) {
	                items.put(mcItem.getUnlocalizedName(), mcItem);
	                
	                items.put(name, mcItem);
	                
	                minecraftItems.add(mcItem);
	                registerName(mcItem.getRegistryName(), mcItem.getUnlocalizedName());
	            }
			}
			else
			{
				if(!items.containsKey(mcItem.getUnlocalizedName())) {
	                items.put(mcItem.getUnlocalizedName(), mcItem);
	                
	                minecraftItems.add(mcItem);
	                registerName(mcItem.getRegistryName(), mcItem.getUnlocalizedName());
	            }
			}
//			if (isTargetVersion(minecraftItem.version)) {
//				final Item item = GameData.getItemRegistry().getObjectById(minecraftItem.id);
//				if (item == null)
//					logger.warn("Missing item: {}", minecraftItem.name);
//				else {
//					logger.debug("Found item: {}", minecraftItem.name);
//					items.put(minecraftItem.name, item);
//					minecraftItems.add(item);
//
//					//					if ((minecraftItem.id == 405) || //Nether brick
//					//							(minecraftItem.id == 404) || //Comparator
//					//							(minecraftItem.id == 397) || //Skull
//					//							(minecraftItem.id == 390) || //Flower Pot
//					//							(minecraftItem.id == 380) || //Cauldron 
//					//							(minecraftItem.id == 379) || //Brewing Stand
//					//							(minecraftItem.id == 372) || //Nether Wart
//					//							(minecraftItem.id == 355) || //Bed
//					//							(minecraftItem.id == 354) || //Cake
//					//							(minecraftItem.id == 338) || //Reeds
//					//							(minecraftItem.id == 330) || //Iron Door
//					//							(minecraftItem.id == 324) || //Wooden Door
//					//							(minecraftItem.id == 296)) //Wheat
//					//						registerName(PolycraftMod.MC_PREFIX + String.valueOf(minecraftItem.id), minecraftItem.name + "(Item)");
//					//					else
//					registerName(PolycraftMod.MC_PREFIX + String.format("%04d", minecraftItem.id), minecraftItem.name);
//
//				}
//			}
		}
	}

	private static void registerMinecraftBlocks() {
		for (final MinecraftBlock minecraftBlock : MinecraftBlock.registry.values()) {
			if (isTargetVersion(minecraftBlock.version)) {
				final Block block = GameData.getBlockRegistry().getObjectById(minecraftBlock.id);
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
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 258), "hatchetIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 257), "pickaxeIron");
			registerSpecialNames(PolycraftMod.MC_PREFIX + String.format("%04d", 256), "shovelIron");
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
				if(PolycraftMod.GEN_JSON_DATA)
					ore.checkBlockJSONs(ore, assetPath);
				registerBlock(ore, new BlockOre(ore));
			}
		}

	}

	private static void registerIngots() {
		for (final Ingot ingot : Ingot.registry.values()) {
			if (isTargetVersion(ingot.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					ingot.checkItemJSONs(ingot, assetPath);
				registerItem(ingot, new ItemIngot(ingot));
			}
		}

	}

	private static void registerNuggets() {
		for (final Nugget nugget : Nugget.registry.values()) {
			if (isTargetVersion(nugget.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					nugget.checkItemJSONs(nugget, assetPath);
				registerItem(nugget, new ItemNugget(nugget));
			}
		}

	}

	private static void registerCompressedBlocks() {
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values()) {
			if (isTargetVersion(compressedBlock.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					compressedBlock.checkBlockJSONs(compressedBlock, assetPath);
				registerBlock(compressedBlock, new BlockCompressed(compressedBlock));
			}
		}

	}

	private static void registerCatalysts() {
		for (final Catalyst catalyst : Catalyst.registry.values()) {
			if (isTargetVersion(catalyst.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					catalyst.checkItemJSONs(catalyst, assetPath);
				registerItem(catalyst, new ItemCatalyst(catalyst));
			}
		}

	}

	private static void registerFlashcards() {
		for (final Flashcard flashcard : Flashcard.registry.values()) {
			if (isTargetVersion(flashcard.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					flashcard.checkItemJSONs(flashcard, assetPath);
				registerItem(flashcard, new ItemFlashcard(flashcard));
			}
		}

	}

	private static void registerExams() {
		for (final Exam exam : Exam.registry.values()) {
			if (isTargetVersion(exam.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					exam.checkItemJSONs(exam, assetPath);
				registerItem(exam, new ItemExam(exam));
			}
		}

	}

	private static void registerVessels() {
		for (final ElementVessel vessel : ElementVessel.registry.values()) {
			if (isTargetVersion(vessel.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					vessel.checkItemJSONs(vessel, assetPath);
				registerItem(vessel, new ItemVessel<ElementVessel>(vessel));
			}
		}

		for (final CompoundVessel vessel : CompoundVessel.registry.values()) {
			if (isTargetVersion(vessel.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					vessel.checkItemJSONs(vessel, assetPath);
				registerItem(vessel, new ItemVessel<CompoundVessel>(vessel));
			}
		}

	}

	private static void registerPolymers() {
		for (final PolymerPellets polymerPellets : PolymerPellets.registry.values()) {
			if (isTargetVersion(polymerPellets.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					polymerPellets.checkItemJSONs(polymerPellets, assetPath);
				registerItem(polymerPellets, new ItemVessel<PolymerPellets>(polymerPellets));
			}
		}

		for (final PolymerBlock polymerBlock : PolymerBlock.registry.values()) {
			{
				if (isTargetVersion(polymerBlock.version)) {
					final BlockPolymer block = new BlockPolymer(polymerBlock);
					if(PolycraftMod.GEN_JSON_DATA)
						polymerBlock.checkBlockJSONs(polymerBlock, assetPath);
					registerBlockWithItem(PolycraftMod.getFileSafeName(polymerBlock.name), polymerBlock.name, block, PolycraftMod.getFileSafeName(polymerBlock.itemName), polymerBlock.itemName,
							ItemPolymerBlock.class, new Object[] {});
				}
			}

		}

		for (final PolymerSlab polymerSlab : PolymerSlab.registry.values()) {
			{
				if (isTargetVersion(polymerSlab.version)) {
					final BlockSlab slab = new BlockPolymerSlab(polymerSlab);
					final BlockSlab doubleSlab = new BlockPolymerSlabDouble(polymerSlab);
					if(PolycraftMod.GEN_JSON_DATA)
						polymerSlab.checkSlabJSONs(polymerSlab, assetPath);
					registerBlockWithItem(PolycraftMod.getFileSafeName(polymerSlab.blockSlabName) + "_half", polymerSlab.blockSlabName, slab, PolycraftMod.getFileSafeName(polymerSlab.itemSlabName), polymerSlab.itemSlabName,
							ItemPolymerSlab.class, new Object[] { slab, doubleSlab });
					doubleSlab.setUnlocalizedName(PolycraftMod.getFileSafeName(polymerSlab.itemDoubleSlabName));
					doubleSlab.setRegistryName(PolycraftMod.getFileSafeName(polymerSlab.itemDoubleSlabName));
					GameRegistry.registerBlock(doubleSlab);
				}
			}
		}
		
		for (final PolymerWall polymerWall : PolymerWall.registry.values()) {
			{
				if (isTargetVersion(polymerWall.version)) {
					final BlockPolymerWall wall = new BlockPolymerWall(polymerWall);
					if(PolycraftMod.GEN_JSON_DATA)
						polymerWall.checkWallJSONs(polymerWall, assetPath);
					registerBlockWithItem(PolycraftMod.getFileSafeName(polymerWall.blockWallName), polymerWall.blockWallName, wall, PolycraftMod.getFileSafeName(polymerWall.itemWallName), polymerWall.itemWallName,
							ItemPolymerWall.class, new Object[] {});

				}
			}
			
		}
		
		for (final PolymerStairs polymerStairs : PolymerStairs.registry.values()) {
			{
				if (isTargetVersion(polymerStairs.version)) {
					final BlockStairs stairs = new BlockPolymerStairs(polymerStairs);
					if(PolycraftMod.GEN_JSON_DATA)
						PolymerStairs.checkStairsJSONs(polymerStairs, assetPath);
					registerBlockWithItem(PolycraftMod.getFileSafeName(polymerStairs.blockStairsName), polymerStairs.blockStairsName, stairs, PolycraftMod.getFileSafeName(polymerStairs.itemStairsName), polymerStairs.itemStairsName,
							ItemPolymerStairs.class, new Object[] {});
				}
			}

		}

	}
	
	private static void registerClientPolymers() {
		for (final PolymerBlock polymerBlock : PolymerBlock.registry.values()) {
			{
				Item itemBlockVariants = GameRegistry.findItem("polycraft", PolycraftMod.getFileSafeName(polymerBlock.name));
				if(itemBlockVariants != null)
					for(EnumColor color: EnumColor.values()) {
						ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation("polycraft:" + PolycraftMod.getFileSafeName(polymerBlock.name) + "_" + color.toString());
						if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
							ModelLoader.setCustomModelResourceLocation(itemBlockVariants, color.getMetadata(), itemModelResourceLocation);
					}
			}

		}

		for (final PolymerSlab polymerSlab : PolymerSlab.registry.values()) {
			{
				Item slab = GameRegistry.findItem("polycraft", PolycraftMod.getFileSafeName(polymerSlab.name) + "_half");
				ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation("polycraft:" + PolycraftMod.getFileSafeName(polymerSlab.name) + "_half");
				if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
					ModelLoader.setCustomModelResourceLocation(slab, 0, itemModelResourceLocation);
			    slab = GameRegistry.findItem("polycraft", "double_" + PolycraftMod.getFileSafeName(polymerSlab.name) + "_item");
				itemModelResourceLocation = new ModelResourceLocation("polycraft:" + "double_" + PolycraftMod.getFileSafeName(polymerSlab.name) + "_item");
				if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
					ModelLoader.setCustomModelResourceLocation(slab, 0, itemModelResourceLocation);
			}
		}

		for (final PolymerStairs polymerStairs : PolymerStairs.registry.values()) {
			{
				Item wall = GameRegistry.findItem("polycraft", PolycraftMod.getFileSafeName(polymerStairs.name));
				ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation("polycraft:" + PolycraftMod.getFileSafeName(polymerStairs.name));
				if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
					ModelLoader.setCustomModelResourceLocation(wall, 0, itemModelResourceLocation);
			}

		}

		for (final PolymerWall polymerWall : PolymerWall.registry.values()) {
			{
				Item wall = GameRegistry.findItem("polycraft", PolycraftMod.getFileSafeName(polymerWall.name));
				ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation("polycraft:" + PolycraftMod.getFileSafeName(polymerWall.name));
				if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
					ModelLoader.setCustomModelResourceLocation(wall, 0, itemModelResourceLocation);
			}

		}
		
		for (final PolymerBrick brick : PolymerBrick.registry.values()) {
			{
				Item itemBlockVariants = GameRegistry.findItem("polycraft", PolycraftMod.getFileSafeName(brick.name));
				if(itemBlockVariants != null)
					for(EnumColor color: EnumColor.values()) {
						ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation("polycraft:" + PolycraftMod.getFileSafeName(brick.name) + "_" + color.toString());
						if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
							ModelLoader.setCustomModelResourceLocation(itemBlockVariants, color.getMetadata(), itemModelResourceLocation);
					}
			}

		}

	}

	private static void registerMolds() {
		for (final Mold mold : Mold.registry.values()) {
			if (isTargetVersion(mold.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					((Mold)mold).checkItemJSONs((Mold)mold, assetPath);
				registerItem(mold, new ItemMold(mold));
			}
		}

	}

	private static void registerMoldedItems() {
		for (final MoldedItem moldedItem : MoldedItem.registry.values()) {
			if(PolycraftMod.GEN_JSON_DATA)
				moldedItem.checkItemJSONs(moldedItem, assetPath);
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
				if(PolycraftMod.GEN_JSON_DATA)
					brick.checkBlockJSONs(brick, assetPath);
				final BlockPolymerBrick blockBrick = new BlockPolymerBrick(brick, brick.length, brick.width);
				registerBlockWithItem(PolycraftMod.getFileSafeName(brick.name), brick.name, blockBrick,PolycraftMod.getFileSafeName(brick.itemName), brick.itemName,
						ItemPolymerBrick.class, new Object[] {});
			}

		}

	}

	private static void registerMaskItems() {
		for (final Mask maskItem : Mask.registry.values()) {
			if (isTargetVersion(maskItem.version)) {
			if(PolycraftMod.GEN_JSON_DATA)
				maskItem.checkItemJSONs(maskItem, assetPath);
				registerItem(maskItem, new ItemMask(maskItem));
			}
		}

	}

	private static void registerWaferItems() {
		for (final WaferItem waferItem : WaferItem.registry.values()) {
			if (isTargetVersion(waferItem.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					waferItem.checkItemJSONs(waferItem, assetPath);
				registerItem(waferItem, new ItemWafer(waferItem));
			}
		}

	}

	private static void registerElectronics() {
		for (final Electronics electronics : Electronics.registry.values()) {
			if (isTargetVersion(electronics.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					electronics.checkItemJSONs(electronics, assetPath);
				registerItem(electronics, new ItemElectronics(electronics));
			}
		}

	}

	private static void registerDNASamplers() {
		for (final DNASampler dnaSampler : DNASampler.registry.values()) {
			if (isTargetVersion(dnaSampler.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					dnaSampler.checkItemJSONs(dnaSampler, assetPath);
				registerItem(dnaSampler, new ItemDNASampler(dnaSampler));
			}
		}

	}

	private static void registerCellCultureDishes() {
		for (final CellCultureDish cellCultureDish : CellCultureDish.registry.values()) {
			if (isTargetVersion(cellCultureDish.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					cellCultureDish.checkItemJSONs(cellCultureDish, assetPath);
				registerItem(cellCultureDish, new ItemCellCultureDish(cellCultureDish));
			}
		}

	}

	private static void registerGrippedTools() {
		for (final GrippedTool grippedTool : GrippedTool.registry.values()) {
			if (isTargetVersion(grippedTool.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					grippedTool.checkItemJSONs(grippedTool, assetPath);
				registerItem(grippedTool, ItemGripped.create(grippedTool));
			}
		}

	}
	
	private static void registerGrippedSyntheticTools() {
		for (final GrippedSyntheticTool grippedSyntheticTool : GrippedSyntheticTool.registry.values()) {
			if (isTargetVersion(grippedSyntheticTool.version)) {
				if(PolycraftMod.GEN_JSON_DATA)
					grippedSyntheticTool.checkItemJSONs(grippedSyntheticTool, assetPath);
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
				if(PolycraftMod.GEN_JSON_DATA)
					pogoStick.checkItemJSONs(pogoStick, assetPath);
				registerItem(pogoStick, new ItemPogoStick(pogoStick));
			}
		}
	}

	private static void registerArmors() {
		for (final Armor armor : Armor.registry.values()) {
			if (isTargetVersion(armor.version)) {
				
				final ArmorMaterial material = EnumHelper.addArmorMaterial(
						armor.name, "", armor.durability, armor.reductionAmounts, armor.enchantability);
				material.customCraftingMaterial = PolycraftRegistry.getItem(armor.craftingItemName);
				if(PolycraftMod.GEN_JSON_DATA) {
					armor.checkItemJSONs(armor, assetPath, PolycraftMod.getFileSafeName(armor.getFullComponentName(ArmorSlot.HEAD)));
					armor.checkItemJSONs(armor, assetPath, PolycraftMod.getFileSafeName(armor.getFullComponentName(ArmorSlot.CHEST)));
					armor.checkItemJSONs(armor, assetPath, PolycraftMod.getFileSafeName(armor.getFullComponentName(ArmorSlot.LEGS)));
					armor.checkItemJSONs(armor, assetPath, PolycraftMod.getFileSafeName(armor.getFullComponentName(ArmorSlot.FEET)));
				}
					
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
				if(PolycraftMod.GEN_JSON_DATA) {
					tool.checkItemJSONs(tool, assetPath, PolycraftMod.getFileSafeName(tool.getFullTypeName(Tool.Type.HOE)));
					tool.checkItemJSONs(tool, assetPath, PolycraftMod.getFileSafeName(tool.getFullTypeName(Tool.Type.SWORD)));
					tool.checkItemJSONs(tool, assetPath, PolycraftMod.getFileSafeName(tool.getFullTypeName(Tool.Type.SHOVEL)));
					tool.checkItemJSONs(tool, assetPath, PolycraftMod.getFileSafeName(tool.getFullTypeName(Tool.Type.PICKAXE)));
					tool.checkItemJSONs(tool, assetPath, PolycraftMod.getFileSafeName(tool.getFullTypeName(Tool.Type.AXE)));
				}
					
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
				if(PolycraftMod.GEN_JSON_DATA)
					inventory.checkInventoryJSONs(inventory, assetPath);
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
//				else if (GameID.InventoryCannonGravelTier1.matches(inventory))
//					GravelCannonInventoryTeir1.register(inventory);
//				else if (GameID.InventoryCannonGravelTier2.matches(inventory))
//					GravelCannonInventoryTeir2.register(inventory);
//				else if (GameID.InventoryCannonGravelTier3.matches(inventory))
//					GravelCannonInventoryTeir3.register(inventory);
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
//				else if(GameID.EntityTerritoryFlag.matches(polycraftEntity)){
//					EntityTerritoryFlag.register(polycraftEntity);
//				}
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
					EntityPellet__Old.register(polycraftEntity);
				}
				else if (GameID.EntityGravelCannonBall.matches(polycraftEntity)){
					EntityGravelCannonBall.register(polycraftEntity);
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
			if(PolycraftMod.GEN_JSON_DATA) 
				light.checkBlockJSONs(light, assetPath);
			PolycraftMod.blockLight = registerBlock(light, new BlockLight(1.0f));
		}

		final InternalObject oil = InternalObject.registry.get("Oil");
		Fluid fluidOil = null;
		if (oil != null && isTargetVersion(oil.version)) {
			fluidOil = new Fluid(oil.name.toLowerCase(), PolycraftMod.getAssetName("/blocks/oil_still") , PolycraftMod.getAssetName("/blocks/oil_flow") ).setDensity(PolycraftMod.oilFluidDensity).setViscosity(PolycraftMod.oilFluidViscosity);
			FluidRegistry.registerFluid(fluidOil);
		}

		final InternalObject blockPipe = InternalObject.registry.get("BlockPipe");
		if (blockPipe != null && isTargetVersion(blockPipe.version)) {
			if(PolycraftMod.GEN_JSON_DATA) 
				blockPipe.checkBlockJSONs(blockPipe, assetPath);
			TileEntityBlockPipe.register(blockPipe);
		}

		final InternalObject collision = InternalObject.registry.get("BlockCollision");
		if (collision != null && isTargetVersion(collision.version)) {
			if(PolycraftMod.GEN_JSON_DATA) 
				collision.checkBlockJSONs(collision, assetPath);
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
				if(PolycraftMod.GEN_JSON_DATA)
					customObject.checkItemJSONs(customObject, assetPath);
				switch(customObject.type)
				{
					case "PC Item":
						if(PolycraftMod.GEN_JSON_DATA) 
							customObject.checkItemJSONs(customObject, assetPath);
						break;
					case "Weapon":
						if(PolycraftMod.GEN_JSON_DATA) 
							customObject.checkItemJSONs(customObject, assetPath);
						break;
					case "Tool":
						if(PolycraftMod.GEN_JSON_DATA) 
							customObject.checkItemJSONs(customObject, assetPath);
						break;
					case "Armor":
						if(PolycraftMod.GEN_JSON_DATA) 
							customObject.checkItemJSONs(customObject, assetPath);
						break;
					case "Food":
						if(PolycraftMod.GEN_JSON_DATA) 
							customObject.checkItemJSONs(customObject, assetPath);
						break;
					case "Currency":
						if(PolycraftMod.GEN_JSON_DATA) 
							customObject.checkItemJSONs(customObject, assetPath);
						break;
					case "PC Block":
						if(PolycraftMod.GEN_JSON_DATA) 
							customObject.checkBlockJSONs(customObject, assetPath); //only custom block all else are items...
						break;
					case "PC Directional":
						if(PolycraftMod.GEN_JSON_DATA) 
							PolyBlockOrientable.checkBlockOrientableJSONs(customObject, assetPath); //only custom orientable blocks all else are items...
						break;
					default:
						if(PolycraftMod.GEN_JSON_DATA) 
							customObject.checkBlockJSONs(customObject, assetPath); 
						break;
				}
				if (GameID.CustomBucketOil.matches(customObject)) {
					PolycraftMod.itemOilBucket = registerItem(customObject,
							new PolycraftBucket(PolycraftMod.blockOil));
									//.setTextureName(PolycraftMod.getAssetName("bucket_oil")));
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
				} else if (GameID.ItemGravelCannonBall.matches(customObject)) {
					registerItem(customObject, new ItemGravelCannonBall(customObject));
				} else if (GameID.PlaceBlockPP.matches(customObject)) {
					registerBlock(customObject, new PlaceBlockPP(customObject));
				} else if (GameID.BreakBlockPP.matches(customObject)) {
					registerBlock(customObject, new BreakBlockPP(customObject));
				} else if (GameID.CustomWoodSlingshot.matches(customObject)) {
					registerItem(customObject, new ItemSlingshot__Old(customObject));
				} else if (GameID.CustomTacticalSlingshot.matches(customObject)) {
					registerItem(customObject, new ItemSlingshot__Old(customObject));
				} else if (GameID.CustomScatterSlingshot.matches(customObject)) {
					registerItem(customObject, new ItemSlingshot__Old(customObject));
				} else if (GameID.CustomBurstSlingshot.matches(customObject)) {
					registerItem(customObject, new ItemSlingshot__Old(customObject));
				} else if (GameID.CustomGravitySlingshot.matches(customObject)) {
					registerItem(customObject, new ItemSlingshot__Old(customObject));
				} else if (GameID.CustomIceSlingshot.matches(customObject)) {
					registerItem(customObject, new ItemSlingshot__Old(customObject));
				} else if (GameID.Paintball.matches(customObject)) {
					registerItem(customObject, new ItemPaintball(customObject));
				}else if (GameID.CustomAITool.matches(customObject)) {
					registerItem(customObject, new ItemAITool(customObject));
				}else if (customObject.type.equals("PC Directional")) {
					registerBlock(customObject, new BlockPolyDirectional(customObject));
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
		final String colorFormat = "tile.%s_%s.name=%s %s";
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
				langEntries.add(String.format(blockFormat, PolycraftMod.getFileSafeName(ore.name), "[" + ((Element) ore.source).symbol + " " + ((Element) ore.source).atomicNumber + "]  " + ore.name));
			} else {
				langEntries.add(String.format(blockFormat, PolycraftMod.getFileSafeName(ore.name), ore.name));
			}
		}

		for (final Ingot ingot : Ingot.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(ingot.name), ingot.name));

		for (final Nugget nugget : Nugget.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(nugget.name), nugget.name));

		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			langEntries.add(String.format(blockFormat, PolycraftMod.getFileSafeName(compressedBlock.name), compressedBlock.name));

		for (final Catalyst catalyst : Catalyst.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(catalyst.name), catalyst.name));

		for (final Flashcard flashcard : Flashcard.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(flashcard.name), flashcard.name));

		for (final Exam exam : Exam.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(exam.name), exam.name));

		for (final DNASampler dnaSampler : DNASampler.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(dnaSampler.name), dnaSampler.name));

		for (final CellCultureDish cellCultureDish : CellCultureDish.registry.values())
			langEntries.add(String.format(itemFormat, cellCultureDish.gameID, cellCultureDish.name));

		for (final ElementVessel vessel : ElementVessel.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(vessel.name), vessel.name));

		for (final CompoundVessel vessel : CompoundVessel.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(vessel.name), vessel.name));

		for (final PolymerPellets polymerPellets : PolymerPellets.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(polymerPellets.name), polymerPellets.name));

		for (final PolymerBlock polymerBlock : PolymerBlock.registry.values())
			for (int i = 0; i < BlockPolymerHelper.colors.length; i++)
				langEntries.add(String.format(colorFormat, PolycraftMod.getFileSafeName(polymerBlock.name), BlockPolymerHelper.getColorDisplayName(i), BlockPolymerHelper.getColorDisplayName(i), polymerBlock.name));

		for (final PolymerBrick brick : PolymerBrick.registry.values())
			for (int i = 0; i < BlockPolymerBrickHelper.colors.length; i++)
				langEntries.add(String.format(colorFormat, PolycraftMod.getFileSafeName(brick.name), BlockPolymerHelper.getColorDisplayName(i), BlockPolymerBrickHelper.getColorDisplayName(i), brick.name));

		for (final PolymerSlab polymerSlab : PolymerSlab.registry.values())
			langEntries.add(String.format(baseFormat, PolycraftMod.getFileSafeName(polymerSlab.name), polymerSlab.name));

		for (final PolymerStairs polymerStairs : PolymerStairs.registry.values())
			langEntries.add(String.format(baseFormat, PolycraftMod.getFileSafeName(polymerStairs.name), polymerStairs.name));

		for (final PolymerWall polymerWall : PolymerWall.registry.values())
			for (int i = 0; i < BlockPolymerHelper.colors.length; i++)
				langEntries.add(String.format(colorFormat, PolycraftMod.getFileSafeName(polymerWall.name), i, BlockPolymerHelper.getColorDisplayName(i), polymerWall.name));

		for (final Mold mold : Mold.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(mold.name), mold.name));

		for (final MoldedItem moldedItem : MoldedItem.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(moldedItem.name), moldedItem.name));

		for (final Mask mask : Mask.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(mask.name), mask.name));

		for (final WaferItem waferItem : WaferItem.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(waferItem.name), waferItem.name));

		for (final Electronics electronics : Electronics.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(electronics.name), electronics.name));

		for (final GrippedTool grippedTool : GrippedTool.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(grippedTool.name), grippedTool.name));
		
		for (final GrippedSyntheticTool grippedSyntheticTool : GrippedSyntheticTool.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(grippedSyntheticTool.name), grippedSyntheticTool.name));

		for (final Armor armor : Armor.registry.values()) {
			for (final ArmorSlot armorSlot : ArmorSlot.values()) {
				langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(armor.getFullComponentName(armorSlot)), armor.getFullComponentName(armorSlot)));
			}
		}

		for (final Tool tool : Tool.registry.values()) {
			for (final Tool.Type toolType : Tool.Type.values()) {
				langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(tool.getFullTypeName(toolType)), tool.getFullTypeName(toolType)));
			}
		}

		for (final PogoStick pogoStick : PogoStick.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(pogoStick.name), pogoStick.name));

		for (final Inventory inventory : Inventory.registry.values()) {
			langEntries.add(String.format(containerFormat, PolycraftMod.getFileSafeName(inventory.name), inventory.name));
			langEntries.add(String.format(blockFormat, PolycraftMod.getFileSafeName(inventory.name), inventory.name));
		}

		for (final CustomObject customObject : CustomObject.registry.values())
			langEntries.add(String.format(itemFormat, PolycraftMod.getFileSafeName(customObject.name), customObject.name));
		
		for (final CustomObject customObject : CustomObject.registry.values())
			langEntries.add(String.format(blockFormat, PolycraftMod.getFileSafeName(customObject.name), customObject.name));
		
		for (final PolycraftEntity entity : PolycraftEntity.registry.values())
			langEntries.add(String.format(entityFormat, entity.name, entity.name));
		
		langEntries.addAll(customLangEntries());

		final PrintWriter writer = new PrintWriter(exportFile);
		for (final String line : langEntries) {
			writer.println(line);
		}
		writer.close();
	}
	
	private static Collection<String> customLangEntries(){
		final Collection<String> customEntries = new LinkedList<String>();
		
		//Gui consent text
		customEntries.add("gui.consent.title=Polycraft World Experiments Server Terms, Conditions & User Agreement");
		customEntries.add("gui.consent.longparagraph1=Polycraft World is researching how people find solutions to problems under different circumstances. We'll be doing this by looking at how well players accomplish the goals of these challenge rooms. Thousands of participants will be involved in this study so that we can look at goal-oriented behavior across populations. We think these games are awesome, but you might get tired or bored. You might also have a really fun time and accidentally learn about polymer chemistry! If you don't want to share your behavorial data with us, that's ok - we can send you to a challenge room where data won't be used for this research. If you win in either room, you'll get a prize at the end - maybe diamond blocks, hard-to-make tools, or even some platinum! For some challenges, you might have the chance to win scholarship money to UT Dallas, but you don't have to share your data with us in order to compete for the scholarships.");
		customEntries.add("gui.consent.longparagraph2=You always have the right to choose not to participate, and you can stop doing so at any time - just use your Esc key to exit the game, or type \"/exit\" in your command bar. We'll stop tracking behavior and send you back to the spawn point. All of the information participants provide to investigators as part of this research will be protected and held in confidence within the limits of the law and institutional regulation. Your data will only be identified by your unique, randomly-assigned user ID, and none of your personal contact information will be shared with our research partners (next). The results of this research may appear in publications, but no individual participants will be identified.");
		customEntries.add("gui.consent.longparagraph3=Our research partners include Gallup, Inc. and the Georgia Institute of Technology. Members and associated staff of the Institutional Review Board (IRB) of the University of Texas at Dallas may review the records of your participation in this research. An IRB is a group of people who are responsible for assuring the community that the rights of participants in research are respected. A representative of the UTD IRB may contact you to gather information about your participation in this research. If you wish, you may refuse to answer questions the representative may ask.");
		customEntries.add("gui.consent.longparagraph4=Participants who want more information about their rights as a participant or who want to report a research related injury may contact The UT Dallas IRB at +1-972-883-4579.");
		customEntries.add("gui.consent.contact1=Principal Investigator: Eric Kildebeck, M.D., Ph.D. (972-883-7281)");
		customEntries.add("gui.consent.contact2=Co-Principal Investigator: Walter Voit, Ph.D. (972-883-5788)");
		customEntries.add("gui.consent.contact3=Polycraft Director of Research: Grace Topete (972-883-7286)");
		customEntries.add("gui.consent.please=Please answer the following questions to continue.");
		customEntries.add("gui.consent.tryagain=Incorrect. Try again!");
		customEntries.add("gui.consent.question1=2. What data is being collected for this research?");
		customEntries.add("gui.consent.question10=Where I live.");
		customEntries.add("gui.consent.question11=My name.");
		customEntries.add("gui.consent.question12=What actions I perform in a minigame.");
		customEntries.add("gui.consent.question13=How many friends I have in Polycraft.");
		customEntries.add("gui.consent.question2=3. Practice typing the command to exit the research room and stop sharing your behavioral data.");
		customEntries.add("gui.consent.question3=1. Are you 18 or order?");
		customEntries.add("gui.consent.minor=Thanks for your interest! You can continue to enter and play various minigames in our servers. We are currently limiting our research to participants who are 18+. Have fun in Polycraft World!");
		customEntries.add("gui.consent.question4=4. Please type the phone number you should call if you would like more info about your rights as a participant.");
		customEntries.add("gui.consent.question5=5. Who can you contact if you have questions about the research?");
		customEntries.add("gui.consent.question50=Dr. Walter Voit");
		customEntries.add("gui.consent.question51=Dr. Eric Kildebeck");
		customEntries.add("gui.consent.question52=Grace Topete");
		customEntries.add("gui.consent.question53=All of the above.");
		customEntries.add("gui.consent.more=Is that the only person?");
		customEntries.add("gui.consent.question6=Are you willing to share your anonymous behavior data with Polycraft World?");
		customEntries.add("gui.consent.nonegiven=Thanks for your interest! You can continue to enter and play various minigames in our servers. We are currently limiting our research to participants who consent to share their behavioral data. If you would like to change your mind, you can do so at anytime by right-clicking the consent text at the Trellis at UT Dallas. Have fun in Polycraft World!");
		customEntries.add("gui.consent.finished=Thank you! If you would like to withdraw consent, you can do so at anytime by right-clicking the consent text at the Trellis at UT Dallas. Welcome to Polycraft World!");

		//Experiment Manager Gui
		customEntries.add("gui.expmanager.title=Experiment Manager");
		customEntries.add("gui.expmanager.body=This menu was made to configure the types of experiments that will run on this server. Happy experimenting!");
		
		//Halftime Gui
		customEntries.add("gui.yes=Yes");
		customEntries.add("gui.no=No");
		customEntries.add("gui.halftime.title=Halftime Planning");
		customEntries.add("gui.halftime.nothanks=Enjoy the rest of the game!");
		customEntries.add("gui.halftime.question1=Do you want to change anything about our strategy?");
		customEntries.add("gui.halftime.question2=Do you want to change out base capturing strategy?");
		customEntries.add("gui.halftime.question3=Do you want to change our item use strategy?");
		customEntries.add("gui.halftime.finished=Questions Complete! Enjoy the rest of the game!");
		customEntries.add("gui.halftime.question20=Offense");
		customEntries.add("gui.halftime.question21=Defense");
		customEntries.add("gui.halftime.question30=Make more items");
		customEntries.add("gui.halftime.question31=Make less items");
		
		//Tutorial Gui
		customEntries.add("gui.tutorial.title=Play Tutorial");
		customEntries.add("gui.tutorial.body=In order to get you familiar with the controls of the game, please play through our tutorial mode. You will not be able to participate in experiments until you have completed the tutorial first. If you aren't ready, you can close the screen and press \"x\" at any time to return to this menu. ");
		
		
		return customEntries;
	}
	
}
