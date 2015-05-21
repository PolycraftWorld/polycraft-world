package edu.utd.minecraft.mod.polycraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import edu.utd.minecraft.mod.polycraft.block.BlockCollision;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipeManager;
import edu.utd.minecraft.mod.polycraft.item.PolycraftItemHelper;
import edu.utd.minecraft.mod.polycraft.proxy.CommonProxy;
import edu.utd.minecraft.mod.polycraft.util.WikiMaker;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;

// The ultimate minecraft mod.
@Mod(modid = PolycraftMod.MODID, version = PolycraftMod.VERSION)
public class PolycraftMod {
	public static final String MODID = "polycraft";
	public static final String VERSION = "1.2.5";
	private static final int[] VERSION_NUMERIC = new int[] { 1, 2, 5 };
	public static final Logger logger = LogManager.getFormatterLogger(MODID);
	public static final NumberFormat numFormat = NumberFormat.getInstance();

	public static final int[] getVersionNumeric(final String version) {
		if (StringUtils.isEmpty(version))
			return null;
		final String[] split = version.split("\\.");
		if (split.length != VERSION_NUMERIC.length)
			return null;
		final int[] versionNumeric = new int[split.length];
		for (int i = 0; i < VERSION_NUMERIC.length; i++)
			versionNumeric[i] = Integer.parseInt(split[i]);
		return versionNumeric;
	}

	public static final String getVersionText(final int[] version) {
		return String.format("%d.%d.%d", version[0], version[1], version[2]);
	}

	public static final boolean isVersionCompatible(final int[] version) {
		if (version == null || version.length != VERSION_NUMERIC.length)
			return false;
		for (int i = 0; i < VERSION_NUMERIC.length; i++) {
			if (VERSION_NUMERIC[i] > version[i])
				return true;
			if (VERSION_NUMERIC[i] < version[i])
				return false;
		}
		return true;
	}

	@Instance(value = MODID)
	public static PolycraftMod instance;

	@SidedProxy(clientSide = "edu.utd.minecraft.mod.polycraft.proxy.ClientProxy", serverSide = "edu.utd.minecraft.mod.polycraft.proxy.ServerProxy")
	public static CommonProxy proxy;

	public static final ArmorMaterial armorMaterialNone = EnumHelper.addArmorMaterial("none", 0, new int[] { 0, 0, 0, 0 }, 0);

	public static final int oilDesertBiomeId = 215;
	public static final int oilOceanBiomeId = 216;
	public static final int oilWellScalar = 100; // large values mean more oil will spawn
	public static final int oilFluidDensity = 800;
	public static final int oilFluidViscosity = 1500;
	public static final int oilBlockFlammability = 5;
	public static final int oreWorldGeneratorWeight = 100;

	public static final String[] recipeCompressedBlockFromItems = new String[] { "xxx", "xxx", "xxx" };
	public static final int recipeItemsPerCompressedBlock = 9;
	public static final int recipePolymerPelletsPerBlock = 1;
	public static final int recipeSmallerVesselsPerLargerVessel = 64;
	public static final int recipeGripsPerTool = 1;
	public static final int recipeGripsPerPogoStick = 2;

	public static final int itemPogoStickBouncesUntilStable = 3; // how many bounces it takes to stabilize at stableBounceHeight
	public static final float itemPogoStickMaxFallNoDamageMultiple = 3; // how many times the stableBounceHeight a player can fall without taking damage
	public static final float itemPogoStickMaxFallExcedeDamageReduction = .5f; // the amount of damage the pogo stick will absorb if the max fall height is exceded
	public static final Map<Integer, String> itemJetPackLandingWarnings = Maps.newLinkedHashMap();
	static {
		itemJetPackLandingWarnings.put(0, "Hope you packed a parachute...");
		itemJetPackLandingWarnings.put(1, "EJECT EJECT EJECT!!!");
		itemJetPackLandingWarnings.put(5, "vapor lock!!");
		itemJetPackLandingWarnings.put(10, "we are way low on fuel Mav!");
		itemJetPackLandingWarnings.put(20, "daredevil are we?");
		itemJetPackLandingWarnings.put(30, "might want to start thinking about landing...");
	}

	public static BiomeGenOilDesert biomeOilDesert;
	public static BiomeGenOilOcean biomeOilOcean;
	public static Block blockOil;
	public static Item itemOilBucket;
	public static Block blockLight;
	public static Block blockCollision;

	public static final PolycraftRecipeManager recipeManager = new PolycraftRecipeManager();

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

		// If wiki params are specified in the environment in VM Args under Run Configuration/Arguments
		// (e.g. -DwikiUrl=www.polycraftworld.com -DwikiScriptPath=/wiki -DwikiUsername=Polycraftbot -DwikiPassword=gmratst6zf -DwikiOverwritePages=true)
		// then data is uploaded to the Polycraft wiki and the program exits. Hint: adding "nogui" to the program arguments on the same page saves some time!
		if (System.getProperty("wikiUrl") != null) {
			WikiMaker.generate(
					System.getProperty("wikiUrl"),
					System.getProperty("wikiScriptPath"),
					System.getProperty("wikiUsername"),
					System.getProperty("wikiPassword"),
					Boolean.parseBoolean(System.getProperty("wikiOverwritePages")),
					System.getProperty("wikiDebugOutputDirectory"));
			System.exit(0);
		}

		// If "langOutputFile" is specified in the environment (e.g. -DlangOutputFile=/tmp/lang.txt),
		// then a text file is generated that can be used as the resources lang file. Hint: adding "nogui" to the program
		// arguments on the same page saves some time!
		if (System.getProperty("langOutputFile") != null) {
			try {
				PolycraftRegistry.exportLangEntries(System.getProperty("langOutputFile"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("***************************** LANG FILE FINISHED PROCESSING ***************************** ");
			System.exit(0);
		}
	}

	public static String getAssetName(final String name) {
		return PolycraftMod.MODID + ":" + name;
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

	public final static int convertSecondsToGameTicks(final double seconds) {
		return (int) Math.ceil(seconds * 20);
	}

	public final static int convertGameTicksToSeconds(final int ticks) {
		return (int) Math.ceil(ticks / 20d);
	}

	public final static String getFileSafeName(final String name) {
		return name.replaceAll("[()]", "").replaceAll("[^_A-Za-z0-9]", "_").toLowerCase();
	}

	private static final float minecraftPlayerGravity = .08f;

	public static double getVelocityRequiredToReachHeight(double height) {
		return Math.sqrt(2 * minecraftPlayerGravity * height);
	}

	public static Collection<String[]> readResourceFileDelimeted(final String directory, final String name) {
		return readResourceFileDelimeted(directory, name, "tsv", "\t");
	}

	public static Collection<String[]> readResourceFileDelimeted(final String directory, final String name, final String extension, final String delimeter) {
		Collection<String[]> config = new LinkedList<String[]>();
		final BufferedReader br = new BufferedReader(new InputStreamReader(PolycraftMod.class.getClassLoader().getResourceAsStream(directory + "/" + name + "." + extension)));
		try {
			br.readLine();// skip the first line (headers)
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

	public static void setPolycraftStackCompoundTag(ItemStack par1ItemStack)
	{
		//TODO: this may not be the best way to do things, but works by overiding 
		if ((par1ItemStack != null) && (par1ItemStack.stackTagCompound == null))
		{
			PolycraftItemHelper.createTagCompound(par1ItemStack);
			par1ItemStack.stackTagCompound.setByte("polycraft-recipe", (byte) 1);
		}

	}

	//added this in so we can support java 1.6 (doesn't have Integer.compare yet)
	public static int compareInt(int x, int y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	public static Vec3 getAdjacentCoords(final Vec3 currentCoords, final int direction, final boolean opposite)
	{
		ForgeDirection forgeDirection = ForgeDirection.values()[direction];
		if (opposite)
			forgeDirection = forgeDirection.getOpposite();
		return Vec3.createVectorHelper(
				currentCoords.xCoord + forgeDirection.offsetX,
				currentCoords.yCoord + forgeDirection.offsetY,
				currentCoords.zCoord + forgeDirection.offsetZ);
	}

	public static IInventory getInventoryAt(final World world, final double x, final double y, final double z) {
		IInventory iinventory = null;
		final int i = MathHelper.floor_double(x);
		final int j = MathHelper.floor_double(y);
		final int k = MathHelper.floor_double(z);
		TileEntity tileentity = world.getTileEntity(i, j, k);
		if (tileentity != null && tileentity instanceof IInventory) {
			iinventory = (IInventory) tileentity;
			if (iinventory instanceof TileEntityChest) {
				final Block block = world.getBlock(i, j, k);
				if (block instanceof BlockChest)
					iinventory = ((BlockChest) block).func_149951_m(world, i, j, k);
			}
		} else if (world.getBlock(i, j, k) instanceof BlockCollision)
		{
			tileentity = BlockCollision.findConnectedInventory(world, i, j, k);
			return getInventoryAt(world, tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);

		}
		return iinventory;
	}
}
