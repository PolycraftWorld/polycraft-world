package edu.utd.minecraft.mod.polycraft;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
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
import edu.utd.minecraft.mod.polycraft.item.ItemFluidContainer;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.proxy.CommonProxy;
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
	public static final float itemRunningShoesWalkSpeedBuff = 2f;
	public static final float itemKevlarArmorBuff = .5f; // x% over diamond armor
	public static final int itemJetPackFuelUnitsFull = 5000;
	public static final int itemJetPackFuelUnitsBurnPerTick = 1;
	public static final float itemJetPackFlySpeedBuff = 2;
	public static final float itemParachuteDescendVelocity = -.3f;
	public static final int itemScubaTankAirUnitsFull = 5000;
	public static final int itemScubaTankAirUnitsConsumePerTick = 1;
	public static final float itemScubaFlippersSwimSpeedBuff = 6f;

	public static void main(final String... args) throws IOException {

		Collection<String> lines = null;
		int arg = 0;
		final String program = args[arg++];
		if ("conf".equals(program)) {
			lines = getConfigs(args[arg++]);
		}
		else if ("lang".equals(program)) {
			final Properties translations = new Properties();
			final InputStream translationsInput = new FileInputStream(args[arg++]);
			translations.load(translationsInput);
			translationsInput.close();
			lines = getLangEntries(translations);
		}

		final PrintWriter writer = new PrintWriter(args[arg++]);
		for (final String line : lines)
			writer.println(line);
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
	public static Item itemJetPack;
	public static Item itemParachute;
	public static Item itemScubaMask;
	public static Item itemScubaTank;
	public static Item itemScubaFlippers;

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
		item.setUnlocalizedName(name);
		GameRegistry.registerItem(item, name);
		items.put(name, item);
		return item;
	}

	public static Collection<String> getConfigs(final String delimeter) {
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
