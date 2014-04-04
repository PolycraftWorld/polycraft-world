package edu.utd.minecraft.mod.polycraft;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

import com.google.common.base.Charsets;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.config.Alloy;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;
import edu.utd.minecraft.mod.polycraft.config.Compound;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import edu.utd.minecraft.mod.polycraft.config.Element;
import edu.utd.minecraft.mod.polycraft.config.Entity;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Mineral;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.Polymer;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipeManager;
import edu.utd.minecraft.mod.polycraft.item.ItemFluidContainer;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.item.PolycraftItem;
import edu.utd.minecraft.mod.polycraft.proxy.CommonProxy;
import edu.utd.minecraft.mod.polycraft.proxy.PolycraftModWikiMaker;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;

// The ultimate minecraft mod.
@Mod(modid = PolycraftMod.MODID, version = PolycraftMod.VERSION)
public class PolycraftMod {

	public static final String MODID = "polycraft";
	public static final String VERSION = "1.0";

	public static final boolean cheatRecipesEnabled = true;
	public static final int worldTemperatureKelvin = 298;
	public static final int oilDesertBiomeId = 215;
	public static final int oilOceanBiomeId = 216;
	public static final int oilWellScalar = 100; // large values mean more oil will spawn
	public static final int oilFluidDensity = 800;
	public static final int oilFluidViscosity = 1500;
	public static final int oilBlockFlammability = 5;
	public static final int oreWorldGeneratorWeight = 100;
	public static final int guiChemicalProcessorID = 0;
	public static final int renderChemicalProcessorID = 2000;
	public static final float itemGrippedToolDurabilityBuff = 2f;
	public static final float itemRunningShoesWalkSpeedBuff = 1f;
	public static final float itemKevlarArmorBuff = .5f; // x% over diamond armor
	public static final int itemFlameThrowerFuelUnitsFull = 1000;
	public static final int itemFlameThrowerFuelUnitsBurnPerTick = 1;
	public static final int itemFlameThrowerRange = 10; // how many blocks ahead to search for entities
	public static final int itemFlameThrowerSpread = 2; // how many blocks to search around the flame path for entities
	public static final int itemFlameThrowerFireDuration = 5; // how many seconds and entity will burn when hit
	public static final int itemJetPackFuelUnitsFull = 5000;
	public static final int itemJetPackFuelUnitsBurnPerTick = 1;
	public static final float itemJetPackFlySpeedBuff = 1f;
	public static final float itemParachuteDescendVelocity = -.3f;
	public static final int itemFlashlightMaxLightLevel = 15;
	public static final float itemFlashlightLightLevelDecreaseByDistance = .5f;
	public static final int itemFlashlightViewingConeAngle = 15;
	public static final int itemScubaTankAirUnitsFull = 5000;
	public static final int itemScubaTankAirUnitsConsumePerTick = 1;
	public static final float itemScubaMaskFogDensity = .01f;
	public static final float itemScubaFinsSwimSpeedBuff = 2f;
	public static final float itemScubaFinsWalkSpeedBuff = -.5f;

	public static final PolycraftRecipeManager recipeManager = new PolycraftRecipeManager();

	public static void main(final String... args) throws IOException {

		Collection<String> lines = null;
		int arg = 0;

		final String program = args[arg++];
		if ("confexp".equals(program)) {
			lines = exportConfigs(args[arg++]);
		}
		if ("confgen".equals(program)) {
			lines = generateConfigs(args[arg++], Files.readAllLines(Paths.get(args[arg++]), Charsets.ISO_8859_1));
		}
		else if ("lang".equals(program)) {
			final Properties translations = new Properties();
			final InputStream translationsInput = new FileInputStream(args[arg++]);
			translations.load(translationsInput);
			translationsInput.close();
			lines = getLangEntries(translations);
		}

		final PrintWriter writer = new PrintWriter(args[arg++]);
		for (final String line : lines) {
			writer.println(line);
		}
		writer.close();
	}

	@Instance(value = MODID)
	public static PolycraftMod instance;

	@SidedProxy(clientSide = "edu.utd.minecraft.mod.polycraft.proxy.CombinedClientProxy", serverSide = "edu.utd.minecraft.mod.polycraft.proxy.DedicatedServerProxy")
	public static CommonProxy proxy;

	public static final ArmorMaterial armorMaterialNone = EnumHelper.addArmorMaterial("none", 0, new int[] { 0, 0, 0, 0 }, 0);

	public static BiomeGenOilDesert biomeOilDesert;
	public static BiomeGenOilOcean biomeOilOcean;

	public static final Map<String, Block> blocks = new LinkedHashMap<String, Block>();
	// special blocks for fast access
	public static Block blockOil;
	public static Block blockChemicalProcessor;
	public static Block blockChemicalProcessorActive;

	public static final String itemNameFluidContainer = "fluid_container";
	public static final Map<String, Item> items = new LinkedHashMap<String, Item>();
	// special items for fast access
	public static Item itemBucketOil;
	public static Item itemFluidContainer;
	public static Item itemFluidContainerNozzle;
	public static Item itemKevlarVest;
	public static Item itemRunningShoes;
	public static Item itemFlameThrower;
	public static Item itemJetPack;
	public static Item itemParachute;
	public static Item itemFlashlight;
	public static Item itemScubaMask;
	public static Item itemScubaTank;
	public static Item itemScubaFins;

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

		// If "wikiOutputFile" is specified in the environment (e.g. -DwikiOutputFile /tmp/output.txt
		// in VM Args under Run Configuration/Arguments), then a text file is generated that can be
		// used to update the Polycraft wiki (and the program exits).  Hint: adding "nogui" to the program
		// arguments on the same page saves some time!
		if (System.getProperty("wikiOutputFile") != null) {
			PolycraftModWikiMaker.createWikiData(System.getProperty("wikiOutputFile"));
			System.exit(0);
		}
	}

	public static String getTextureName(final String name) {
		return PolycraftMod.MODID + ":" + name;
	}

	public static Block registerBlock(final Entity entity, final Block block) {
		return registerBlock(entity.gameName, block);
	}

	public static Block registerBlock(final String name, final Block block) {
		block.setBlockName(name);
		GameRegistry.registerBlock(block, name);
		blocks.put(name, block);
		return block;
	}

	public static Item registerItem(final Entity entity, final Item item) {
		return registerItem(entity.gameName, item);
	}

	public static Item registerItem(final String name, final Item item) {
		if (!(item instanceof PolycraftItem)) {
			throw new IllegalArgumentException("Item " + name + "/" + item.getUnlocalizedName() + " must implement PolycraftItem (" + item.toString() + ")");
		}
		item.setUnlocalizedName(name);
		GameRegistry.registerItem(item, name);

		items.put(name, item);
		return item;
	}

	public static Collection<String> exportConfigs(final String delimeter) {
		final Collection<String> configs = new LinkedList<String>();

		configs.add(Element.class.getSimpleName());
		for (final Element element : Element.registry.values())
			configs.add(element.export(delimeter));

		configs.add("");
		configs.add(Compound.class.getSimpleName());
		for (final Compound compound : Compound.registry.values())
			configs.add(compound.export(delimeter));

		configs.add("");
		configs.add(Polymer.class.getSimpleName());
		for (final Polymer polymer : Polymer.registry.values())
			configs.add(polymer.export(delimeter));

		configs.add("");
		configs.add(Alloy.class.getSimpleName());
		for (final Alloy alloy : Alloy.registry.values())
			configs.add(alloy.name);

		configs.add("");
		configs.add(Mineral.class.getSimpleName());
		for (final Mineral mineral : Mineral.registry.values())
			configs.add(mineral.export(delimeter));

		configs.add("");
		configs.add(Ore.class.getSimpleName());
		for (final Ore ore : Ore.registry.values())
			configs.add(ore.export(delimeter));

		configs.add("");
		configs.add(Ingot.class.getSimpleName());
		for (final Ingot ingot : Ingot.registry.values())
			configs.add(ingot.export(delimeter));

		configs.add("");
		configs.add(CompressedBlock.class.getSimpleName());
		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			configs.add(compressedBlock.export(delimeter));

		configs.add("");
		configs.add(Catalyst.class.getSimpleName());
		for (final Catalyst catalyst : Catalyst.registry.values())
			configs.add(catalyst.export(delimeter));

		return configs;
	}

	public static Collection<String> generateConfigs(final String delimeter, final List<String> exported) {
		final Collection<String> configs = new LinkedList<String>();
		String mode = null;
		for (final String export : exported) {
			if (export.length() > 0) {
				if (mode == null) {
					mode = export;
					configs.add("");
					configs.add(mode);
				}
				else {
					final String[] exportSplit = export.split(delimeter);
					if (mode.equals(Element.class.getSimpleName()))
						configs.add(Element.generate(exportSplit));
					else if (mode.equals(Compound.class.getSimpleName()))
						configs.add(Compound.generate(exportSplit));
					else if (mode.equals(Polymer.class.getSimpleName()))
						configs.add(Polymer.generate(exportSplit));
					else if (mode.equals(Alloy.class.getSimpleName()))
						configs.add(Alloy.generate(exportSplit));
					else if (mode.equals(Mineral.class.getSimpleName()))
						configs.add(Mineral.generate(exportSplit));
					else if (mode.equals(Ore.class.getSimpleName()))
						configs.add(Ore.generate(exportSplit));
					else if (mode.equals(Ingot.class.getSimpleName()))
						configs.add(Ingot.generate(exportSplit));
					else if (mode.equals(CompressedBlock.class.getSimpleName()))
						configs.add(CompressedBlock.generate(exportSplit));
					else if (mode.equals(Catalyst.class.getSimpleName()))
						configs.add(Catalyst.generate(exportSplit));
				}
			}
			else
				mode = null;
		}

		return configs;
	}

	public static Collection<String> getLangEntries(final Properties translations) {
		final Collection<String> langEntries = new LinkedList<String>();

		for (final Element element : Element.registry.values())
			if (element.fluid)
				langEntries.add(String.format("item.%s.name=%s %s", ItemFluidContainer.getGameName(element), translations.getProperty("containerof"), element.name));

		for (final Compound compound : Compound.registry.values())
			if (compound.fluid)
				langEntries.add(String.format("item.%s.name=%s %s", ItemFluidContainer.getGameName(compound), translations.getProperty("containerof"), compound.name));
			else
				langEntries.add(String.format("tile.%s.name=%s", compound.gameName, compound.name));

		for (final Polymer polymer : Polymer.registry.values()) {
			langEntries.add(String.format("tile.%s.name=%s", polymer.gameName, polymer.name));
			langEntries.add(String.format("item.%s.name=%s %s", polymer.itemNamePellet, polymer.name, translations.getProperty("pellet")));
			langEntries.add(String.format("item.%s.name=%s %s", polymer.itemNameFiber, polymer.name, translations.getProperty("fiber")));
		}

		for (final Ore ore : Ore.registry.values())
			langEntries.add(String.format("tile.%s.name=%s %s", ore.gameName, ore.name, translations.getProperty("ore")));

		for (final Ingot ingot : Ingot.registry.values())
			langEntries.add(String.format("item.%s.name=%s %s", ingot.gameName, ingot.name, translations.getProperty("ingot")));

		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			langEntries.add(String.format("tile.%s.name=%s %s", compressedBlock.gameName, translations.getProperty("blockof"), compressedBlock.name));

		for (final Catalyst catalyst : Catalyst.registry.values())
			langEntries.add(String.format("item.%s.name=%s %s", catalyst.gameName, catalyst.name, translations.getProperty("catalyst")));

		for (final Polymer polymer : ItemGripped.allowedPolymers) {
			for (final String materialName : ItemGripped.allowedMaterials.keySet()) {
				final String materialNameUpper = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);
				for (final String type : ItemGripped.allowedTypes.keySet())
					langEntries.add(String.format("item.%s.name=%s %s %s (%s)", ItemGripped.getName(polymer, materialName, type),
							translations.getProperty("gripped"), materialNameUpper,
							Character.toUpperCase(type.charAt(0)) + type.substring(1), polymer.name));
			}
		}

		return langEntries;
	}
}
