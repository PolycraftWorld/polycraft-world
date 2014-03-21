package edu.utd.minecraft.mod.polycraft;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;
import edu.utd.minecraft.mod.polycraft.config.Compound;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.Element;
import edu.utd.minecraft.mod.polycraft.config.Entity;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.Plastic;
import edu.utd.minecraft.mod.polycraft.item.ItemFluidContainer;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemWorn;
import edu.utd.minecraft.mod.polycraft.proxy.CommonProxy;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;

// The ultimate minecraft mod.
@Mod(modid = PolycraftMod.MODID, version = PolycraftMod.VERSION)
public class PolycraftMod {

	public static final boolean cheatRecipesEnabled = true;
	public static final int oilDesertBiomeId = 215;
	public static final int oilOceanBiomeId = 216;
	public static final int oilWellScalar = 1000; // large values mean more oil will spawn
	public static final int oilFluidDensity = 800;
	public static final int oilFluidViscosity = 1500;
	public static final int oilBlockFlammability = 5;
	public static final int oreWorldGeneratorWeight = 100;
	public static final int guiChemicalProcessorID = 0;
	public static final int renderChemicalProcessorID = 2000;

	public static void main(final String... args) throws IOException {
		int arg = 0;
		final Properties translations = new Properties();
		final InputStream translationsInput = new FileInputStream(args[arg++]);
		translations.load(translationsInput);
		translationsInput.close();

		final PrintWriter writer = new PrintWriter(args[arg++], "UTF-8");
		for (final String langEntry : getLangEntries(translations))
			writer.println(langEntry);
		writer.close();
	}

	public static final String MODID = "polycraft";
	public static final String VERSION = "1.0";

	@Instance(value = MODID)
	public static PolycraftMod instance;

	@SidedProxy(clientSide = "edu.utd.minecraft.mod.polycraft.proxy.CombinedClientProxy", serverSide = "edu.utd.minecraft.mod.polycraft.proxy.DedicatedServerProxy")
	public static CommonProxy proxy;

	public static BiomeGenOilDesert biomeOilDesert;
	public static BiomeGenOilOcean biomeOilOcean;

	public static final Map<String, Block> blocks = new HashMap<String, Block>();
	// special blocks for fast access
	public static Block blockOil;
	public static Block blockChemicalProcessor;
	public static Block blockChemicalProcessorActive;

	public static final String itemFluidContainerName = "fluid_container";
	public static final Map<String, Item> items = new HashMap<String, Item>();
	// special items for fast access
	public static Item itemBucketOil;
	public static Item itemFluidContainer;

	@EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		proxy.init();
	}

	@EventHandler
	public void postInit(final FMLPostInitializationEvent event) {
		proxy.postInit();
	}

	public static String getTextureName(final String name) {
		return PolycraftMod.MODID + ":" + name;
	}

	public static Block registerBlock(final String name, final Block block) {
		block.setBlockName(name);
		GameRegistry.registerBlock(block, name);
		blocks.put(name, block);
		return block;
	}

	public static Item registerItem(final String name, final Item item) {
		item.setUnlocalizedName(name);
		GameRegistry.registerItem(item, name);
		items.put(name, item);
		return item;
	}

	public static Item getItemFluidContainer(final Entity entity) {
		return items.get(ItemFluidContainer.getGameName(entity));
	}

	public static Collection<String> getLangEntries(final Properties translations) {
		final Collection<String> langEntries = new ArrayList<String>();

		for (final Ore ore : Ore.registry.values())
			langEntries.add(String.format("tile.%s.name=%s %s", ore.gameName, ore.name, translations.getProperty("ore")));

		for (final Ingot ingot : Ingot.registry.values())
			langEntries.add(String.format("item.%s.name=%s %s", ingot.gameName, ingot.name, translations.getProperty("ingot")));

		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			langEntries.add(String.format("tile.%s.name=%s %s", compressedBlock.gameName, translations.getProperty("blockof"), compressedBlock.name));

		for (final Catalyst catalyst : Catalyst.registry.values())
			langEntries.add(String.format("item.%s.name=%s %s", catalyst.gameName, catalyst.name, translations.getProperty("catalyst")));

		for (final Plastic plastic : Plastic.registry.values()) {
			langEntries.add(String.format("tile.%s.name=%s (%02d %s: %s)", plastic.gameName, translations.getProperty("plastic"), plastic.type, plastic.abbreviation, plastic.name));
			if (plastic.isDefaultColor())
				langEntries.add(String.format("item.%s.name=%s %s (%02d %s)", plastic.itemNameGrip, translations.getProperty("plastic"), translations.getProperty("grip"), plastic.type, plastic.abbreviation));
			langEntries.add(String.format("item.%s.name=%s (%02d %s)", plastic.itemNameKevlarVest, translations.getProperty("kevlarvest"), plastic.type, plastic.abbreviation));
			langEntries.add(String.format("item.%s.name=%s (%02d %s)", plastic.itemNameRunningShoes, translations.getProperty("runningshoes"), plastic.type, plastic.abbreviation));
			langEntries.add(String.format("item.%s.name=%s (%02d %s)", plastic.itemNameJetPack, translations.getProperty("jetpack"), plastic.type, plastic.abbreviation));
			langEntries.add(String.format("item.%s.name=%s (%02d %s)", plastic.itemNameParachute, translations.getProperty("parachute"), plastic.type, plastic.abbreviation));
			langEntries.add(String.format("item.%s.name=%s (%02d %s)", plastic.itemNameScubaMask, translations.getProperty("scubamask"), plastic.type, plastic.abbreviation));
			langEntries.add(String.format("item.%s.name=%s (%02d %s)", plastic.itemNameScubaTank, translations.getProperty("scubatank"), plastic.type, plastic.abbreviation));
			langEntries.add(String.format("item.%s.name=%s (%02d %s)", plastic.itemNameScubaFlippers, translations.getProperty("scubaflippers"), plastic.type, plastic.abbreviation));
			langEntries.add(String.format("item.%s.name=%s %s (%02d %s)", plastic.itemNamePellet, translations.getProperty("plastic"), translations.getProperty("pellet"), plastic.type, plastic.abbreviation));
			langEntries.add(String.format("item.%s.name=%s %s (%02d %s)", plastic.itemNameFiber, translations.getProperty("plastic"), translations.getProperty("fiber"), plastic.type, plastic.abbreviation));

		}

		for (final String materialName : ItemGripped.allowedMaterials.keySet()) {
			final String materialNameUpper = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);
			for (final Plastic plastic : Plastic.registry.values())
				for (final String type : ItemGripped.allowedTypes.keySet())
					langEntries.add(String.format("item.%s.name=%s %s %s %s (%02d %s)", ItemGripped.getName(plastic, materialName, type), translations.getProperty("plastic"), translations.getProperty("gripped"), materialNameUpper,
							Character.toUpperCase(type.charAt(0)) + type.substring(1), plastic.type, plastic.abbreviation));
		}

		for (final Entry<String, ArmorMaterial> materialEntry : ItemWorn.allowedMaterials.entrySet()) {
			final String materialName = materialEntry.getKey();
			final ArmorMaterial material = materialEntry.getValue();
			final String materialNameUpper = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);
			for (final Plastic plastic : Plastic.registry.values())
				for (final String type : ItemWorn.allowedTypes.keySet()) {

					for (int bodyLocation = 0; bodyLocation < 3; bodyLocation++) {
						langEntries.add(String.format("item.%s.name=%s Worn %s %s (%02d %s)", ItemWorn.getName(plastic, materialName, type, bodyLocation), translations.getProperty("plastic"), materialNameUpper,
								Character.toUpperCase(type.charAt(0)) + type.substring(1), plastic.type, plastic.abbreviation));
					}
				}
		}

		for (final Element element : Element.registry.values())
			if (element.fluid)
				langEntries.add(String.format("item.%s_%s.name=%s %s", element.gameName, itemFluidContainerName, translations.getProperty("containerof"), element.name));

		for (final Compound compound : Compound.registry.values())
			if (compound.fluid)
				langEntries.add(String.format("item.%s_%s.name=%s %s", compound.gameName, itemFluidContainerName, translations.getProperty("containerof"), compound.name));
			else
				langEntries.add(String.format("tile.%s.name=%s", compound.gameName, compound.name));

		return langEntries;
	}
}
