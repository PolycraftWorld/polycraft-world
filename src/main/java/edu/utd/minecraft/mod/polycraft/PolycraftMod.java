package edu.utd.minecraft.mod.polycraft;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.Plastic;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemWorn;
import edu.utd.minecraft.mod.polycraft.proxy.CommonProxy;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;

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
	public static final int guiFrackerID = 0;
	public static final int renderFrackerID = 2000;

	// public static final String[] color_names = new String[] {
	// "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray",
	// "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};

	public static void main(final String... args) throws FileNotFoundException, UnsupportedEncodingException {
		int arg = 0;
		final PrintWriter writer = new PrintWriter(args[arg++], "UTF-8");

		final Map<String, String> translations = new HashMap<String, String>();
		translations.put("Ingot", args[arg++]);
		translations.put("Ore", args[arg++]);
		translations.put("Block of", args[arg++]);
		translations.put("Catalyst", args[arg++]);
		translations.put("Plastic", args[arg++]);
		translations.put("Grip", args[arg++]);
		translations.put("Pellet", args[arg++]);
		translations.put("Fiber", args[arg++]);
		translations.put("Container of", args[arg++]);

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
	public static Block blockFracker;
	public static Block blockFrackerActive;

	public static final Map<String, Item> items = new HashMap<String, Item>();

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

	public static Collection<String> getLangEntries(final Map<String, String> translations) {
		final Collection<String> langEntries = new ArrayList<String>();

		for (final Ore ore : Ore.registry.values())
			langEntries.add(String.format("tile.%s.name=%s %s", ore.gameName, ore.name, translations.get("Ore")));

		for (final Ingot ingot : Ingot.registry.values())
			langEntries.add(String.format("item.%s.name=%s %s", ingot.gameName, ingot.name, translations.get("Ingot")));

		for (final CompressedBlock compressedBlock : CompressedBlock.registry.values())
			langEntries.add(String.format("tile.%s.name=%s %s", compressedBlock.gameName, translations.get("Block of"), compressedBlock.name));

		for (final Catalyst catalyst : Catalyst.registry.values())
			langEntries.add(String.format("item.%s.name=%s %s", catalyst.gameName, catalyst.name, translations.get("Catalyst")));

		for (final Plastic plastic : Plastic.registry.values()) {
			langEntries.add(String.format("tile.%s.name=%s (%02d %s: %s)", plastic.gameName, translations.get("Plastic"), plastic.type, plastic.abbreviation, plastic.name));
			langEntries.add(String.format("item.%s.name=%s %s (%02d %s)", plastic.itemNameGrip, translations.get("Plastic"), translations.get("Grip"), plastic.type, plastic.abbreviation));
			langEntries.add(String.format("item.%s.name=%s %s (%02d %s)", plastic.itemNamePellet, translations.get("Plastic"), translations.get("Pellet"), plastic.type, plastic.abbreviation));
			langEntries.add(String.format("item.%s.name=%s %s (%02d %s)", plastic.itemNameFiber, translations.get("Plastic"), translations.get("Fiber"), plastic.type, plastic.abbreviation));

		}

		for (final String materialName : ItemGripped.allowedMaterials.keySet()) {
			final String materialNameUpper = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);
			for (final Plastic plastic : Plastic.registry.values())
				for (final String type : ItemGripped.allowedTypes.keySet())
					langEntries.add(String.format("item.%s.name=Plastic Gripped %s %s (%02d %s)", ItemGripped.getName(plastic, materialName, type), materialNameUpper, Character.toUpperCase(type.charAt(0)) + type.substring(1), plastic.type,
							plastic.abbreviation));
		}

		for (final Entry<String, ArmorMaterial> materialEntry : ItemWorn.allowedMaterials.entrySet()) {
			final String materialName = materialEntry.getKey();
			final ArmorMaterial material = materialEntry.getValue();
			final String materialNameUpper = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);
			for (final Plastic plastic : Plastic.registry.values())
				for (final String type : ItemWorn.allowedTypes.keySet()) {

					for (int bodyLocation = 0; bodyLocation < 3; bodyLocation++) {
						langEntries.add(String.format("item.%s.name=Plastic Worn %s %s (%02d %s)", ItemWorn.getName(plastic, materialName, type, bodyLocation), materialNameUpper, Character.toUpperCase(type.charAt(0)) + type.substring(1),
								plastic.type, plastic.abbreviation));
					}
				}
		}

		for (final Compound compound : Compound.registry.values())
			if (compound.fluid)
				langEntries.add(String.format("item.%s_fluid_container.name=%s %s", compound.gameName, translations.get("Container of"), compound.name));
			else
				langEntries.add(String.format("tile.%s.name=%s", compound.gameName, compound.name));

		return langEntries;
	}
}
