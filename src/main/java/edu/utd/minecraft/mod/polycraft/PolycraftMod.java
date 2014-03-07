package edu.utd.minecraft.mod.polycraft;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockPlastic;
import edu.utd.minecraft.mod.polycraft.item.ItemAxeGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemHoeGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemPickaxeGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemShovelGripped;
import edu.utd.minecraft.mod.polycraft.item.ItemSwordGripped;
import edu.utd.minecraft.mod.polycraft.proxy.CommonProxy;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilDesert;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeGenOilOcean;

@Mod(modid = PolycraftMod.MODID, version = PolycraftMod.VERSION)
public class PolycraftMod
{
    public static final int oilWellScalar = 1000; //large values mean more oil will spawn
    public static final int oilFluidDensity = 800;
    public static final int oilFluidViscosity = 1500;
    public static final int oilBlockFlammability = 5;
    public static final int plasticPelletsPerBlock = 8;
    
	public static void main(String... args) throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter(args[0], "UTF-8");
		for (String langEntry : getLangEntries())
			writer.println(langEntry);
		writer.close();
	}
	
    public static final String MODID = "polycraft";
    public static final String VERSION = "1.0";
    
    @SidedProxy(clientSide="edu.utd.minecraft.mod.polycraft.proxy.CombinedClientProxy", serverSide="edu.utd.minecraft.mod.polycraft.proxy.DedicatedServerProxy")
    public static CommonProxy proxy;
    
	public static BiomeGenOilDesert biomeOilDesert;
	public static BiomeGenOilOcean biomeOilOcean;
	
	public static final Map<String, Block> blocks = new HashMap<String, Block>();
	//special blocks for fast access
	public static Block blockOil;
	
	public static final Map<String, Item> items = new HashMap<String, Item>();
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	proxy.postInit();
    }
    
    public static String getTextureName(String name)
    {
    	return PolycraftMod.MODID + ":" + name;
    }

    public static Block registerBlock(String name, Block block)
    {
		block.setBlockName(name);
		GameRegistry.registerBlock(block, name);
		blocks.put(name, block);
		return block;
    }
	
    public static Item registerItem(String name, Item item)
	{
		item.setUnlocalizedName(name);
		GameRegistry.registerItem(item, name);
		items.put(name,  item);
		return item;
	}

	public static Collection<String> getLangEntries()
	{
		Collection<String> langEntries = new ArrayList<String>();

		for (Plastic plastic : Plastic.plastics)
			langEntries.add(String.format("tile.%s.name=Plastic (%02d %s: %s)", plastic.gameName, plastic.type, plastic.abbrevation, plastic.name));
		
		for (Plastic plastic : Plastic.plastics)
			langEntries.add(String.format("item.%s_grip.name=Plastic Grip (%02d %s)", plastic.gameName, plastic.type, plastic.abbrevation));
		
		for (Plastic plastic : Plastic.plastics)
			langEntries.add(String.format("item.%s_pellet.name=Plastic Pellet (%02d %s)", plastic.gameName, plastic.type, plastic.abbrevation));

		for (String materialName : ItemGripped.allowedMaterials.keySet())
		{
			String materialNameUpper = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);
			for (Plastic plastic : Plastic.plastics)
				for (String item : ItemGripped.allowedItems)
					langEntries.add(String.format("item.%s_gripped_%s_%s.name=Plastic Gripped %s %s (%02d %s)",
							plastic.gameName, materialName, item, materialNameUpper, Character.toUpperCase(item.charAt(0)) + item.substring(1), plastic.type, plastic.abbrevation));
		}

		return langEntries;
	}
}
