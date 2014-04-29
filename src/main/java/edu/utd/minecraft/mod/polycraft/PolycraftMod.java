package edu.utd.minecraft.mod.polycraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.GameIdentifiedConfig;
import edu.utd.minecraft.mod.polycraft.config.GrippedTool;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.InternalObject;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.Mold;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.PogoStick;
import edu.utd.minecraft.mod.polycraft.config.PolymerBlock;
import edu.utd.minecraft.mod.polycraft.config.PolymerFibers;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipeManager;
import edu.utd.minecraft.mod.polycraft.item.PolycraftItem;
import edu.utd.minecraft.mod.polycraft.proxy.CommonProxy;
import edu.utd.minecraft.mod.polycraft.util.PolycraftModWikiMaker;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;

// The ultimate minecraft mod.
@Mod(modid = PolycraftMod.MODID, version = PolycraftMod.VERSION)
public class PolycraftMod {

	private static final Logger logger = LogManager.getLogger();

	public static final String MODID = "polycraft";
	public static final String VERSION = "1.0";

	@Instance(value = MODID)
	public static PolycraftMod instance;

	@SidedProxy(clientSide = "edu.utd.minecraft.mod.polycraft.proxy.CombinedClientProxy", serverSide = "edu.utd.minecraft.mod.polycraft.proxy.DedicatedServerProxy")
	public static CommonProxy proxy;

	public static final int oilDesertBiomeId = 215;
	public static final int oilOceanBiomeId = 216;
	public static final int oilWellScalar = 100; // large values mean more oil will spawn
	public static final int oilFluidDensity = 800;
	public static final int oilFluidViscosity = 1500;
	public static final int oilBlockFlammability = 5;
	public static final int oreWorldGeneratorWeight = 100;
	public static final int treeTapSpawnRateNaturalRubber = 1000; //in ticks
	public static final String[] recipeCompressedBlockFromItems = new String[] { "xxx", "xxx", "xxx" };
	public static final int recipeItemsPerCompressedBlock = 9;
	public static final int recipePolymerPelletsPerBlock = 4;
	public static final int recipeSmallerVesselsPerLargerVessel = 64;
	public static final int recipeGripsPerTool = 1;
	public static final int recipeGripsPerPogoStick = 2;
	public static final int itemPogoStickBouncesUntilStable = 3; //how many bounces it takes to stabilize at stableBounceHeight
	public static final float itemPogoStickMaxFallNoDamageMultiple = 3; //how many times the stableBounceHeight a player can fall without taking damage
	public static final float itemPogoStickMaxFallExcedeDamageReduction = .5f; //the amound of damage the pogo stick will absorb if the max fall height is exceded
	public static final ArmorMaterial armorMaterialNone = EnumHelper.addArmorMaterial("none", 0, new int[] { 0, 0, 0, 0 }, 0);

	public static BiomeGenOilDesert biomeOilDesert;
	public static BiomeGenOilOcean biomeOilOcean;

	public static final Map<String, String> registryNames = new HashMap<String, String>();
	public static final Map<String, Block> blocks = new HashMap<String, Block>();
	public static final Map<String, Item> items = new HashMap<String, Item>();

	// special blocks for fast access
	public static Block blockOil;

	public static final PolycraftRecipeManager recipeManager = new PolycraftRecipeManager();

	public final static int convertSecondsToGameTicks(final double seconds) {
		return (int) Math.ceil(seconds * 8);
	}

	public final static String getFileSafeName(final String name) {
		return name.replaceAll("[()]", "").replaceAll("[^_A-Za-z0-9]", "_").toLowerCase();
	}

	private static final float minecraftPlayerGravity = .08f;

	public static double getVelocityRequiredToReachHeight(double height) {
		return Math.sqrt(2 * minecraftPlayerGravity * height);
	}

	@EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		fixEnderman();
		proxy.preInit();
	}

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		proxy.init();
	}

	@EventHandler
	public void postInit(final FMLPostInitializationEvent event) {
		proxy.postInit();

		// If "wikiOutputFile" is specified in the environment (e.g. -DwikiOutputFile /tmp/output.txt
		// in VM Args under Run Configuration/Arguments), then a text file is generated that can be
		// used to update the Polycraft wiki (and the program exits).  Hint: adding "nogui" to the program
		// arguments on the same page saves some time!
		if (System.getProperty("wikiOutputFile") != null) {
			PolycraftModWikiMaker.createWikiData(System.getProperty("wikiOutputFile"));
			System.exit(0);
		}

		// If "langOutputFile" is specified in the environment (e.g. -DlangOutputFile=/tmp/lang.txt),
		// then a text file is generated that can be used as the resources lang file.  Hint: adding "nogui" to the program
		// arguments on the same page saves some time!
		if (System.getProperty("langOutputFile") != null) {
			try {
				exportLangEntries(System.getProperty("langOutputFile"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

	public static String getAssetName(final String name) {
		return PolycraftMod.MODID + ":" + name;
	}

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

	public static Block registerBlock(final String registryName, final String name, final Block block) {
		registerName(registryName, name);
		block.setBlockName(registryName);
		GameRegistry.registerBlock(block, registryName);
		blocks.put(name, block);
		return block;
	}

	public static Item registerItem(final GameIdentifiedConfig config, final Item item) {
		return registerItem(config.gameID, config.name, item);
	}

	public static Item registerItem(final String registryName, final String name, final Item item) {
		registerName(registryName, name);
		if (!(item instanceof PolycraftItem))
			throw new IllegalArgumentException("Item " + name + " must implement PolycraftItem (" + item.toString() + ")");
		item.setUnlocalizedName(registryName);
		GameRegistry.registerItem(item, registryName);
		items.put(name, item);
		return item;
	}

	public static Block registerBlockWithItem(final String blockGameID, final String blockName, final Block block,
			final String itemBlockGameID, final String itemBlockName, final Class<? extends ItemBlock> itemBlockClass, Object... itemCtorArgs) {
		block.setBlockName(blockGameID);
		GameRegistry.registerBlock(block, itemBlockClass, blockGameID, null, itemCtorArgs);
		blocks.put(blockName, block);

		final Item itemBlock = Item.getItemFromBlock(block);
		itemBlock.setUnlocalizedName(itemBlockGameID);
		items.put(itemBlockName, itemBlock);

		return block;
	}

	private void exportLangEntries(final String exportFile) throws IOException {
		final String fluidFormat = "fluid.%s=%s";
		final String containerFormat = "container.%s=%s";
		final String baseFormat = "%s.name=%s";
		final String blockFormat = "tile." + baseFormat;
		final String itemFormat = "item." + baseFormat;

		final Collection<String> langEntries = new LinkedList<String>();

		final InternalObject oil = InternalObject.registry.get("Oil");
		langEntries.add(String.format(fluidFormat, oil.name.toLowerCase(), oil.name));
		langEntries.add(String.format(blockFormat, oil.gameID, oil.name));

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

	// TODO: Remove this if they ever fix enderman bug...
	private void fixEnderman() {
		// Look for static fields on enderman
		Field[] declaredFields = EntityEnderman.class.getDeclaredFields();
		for (Field field : declaredFields) {
			int modifiers = field.getModifiers();
			if (java.lang.reflect.Modifier.isStatic(modifiers)) {
				// Look for arrays
				Class<?> c = field.getType();
				if (c.isArray()) {
					field.setAccessible(true);
					try {
						// Copy old array into new array and set it
						boolean[] oldArray = (boolean[]) field.get(null);
						boolean[] newArray = new boolean[4096];
						for (int i = 0; i < oldArray.length; ++i) {
							newArray[i] = oldArray[i];
						}
						field.set(null, newArray);
						logger.info("Set enderman carriable blocks to a reasonable value.");
						return;
					} catch (IllegalArgumentException e) {
						logger.warn("Unable to set enderman carriable blocks: ", e);
						return;
					} catch (IllegalAccessException e) {
						logger.warn("Unable to set enderman carriable blocks: ", e);
						return;
					}
				}
			}

		}
		logger.info("Unable to find enderman carriable blocks field.");
	}

	public static Collection<String[]> readResourceFileDelimeted(final String directory, final String name) {
		return readResourceFileDelimeted(directory, name, "tsv", "\t");
	}

	public static Collection<String[]> readResourceFileDelimeted(final String directory, final String name, final String extension, final String delimeter) {
		Collection<String[]> config = new LinkedList<String[]>();
		final BufferedReader br = new BufferedReader(new InputStreamReader(PolycraftMod.class.getClassLoader().getResourceAsStream(directory + "/" + name + "." + extension)));
		try {
			br.readLine();//skip the first line (headers)
			for (String line; (line = br.readLine()) != null;) {
				config.add(line.split(delimeter));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return config;
	}
}
