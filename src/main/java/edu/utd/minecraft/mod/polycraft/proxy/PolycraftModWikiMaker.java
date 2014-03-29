package edu.utd.minecraft.mod.polycraft.proxy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemTool;
import net.minecraft.util.StatCollector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CORBA.Environment;

import cpw.mods.fml.common.SidedProxy;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.*;
import edu.utd.minecraft.mod.polycraft.item.*;

public class PolycraftModWikiMaker {
	private static Logger logger = LogManager.getLogger();
	
	private static final String ITEM_IMAGE_PATH = "../src/main/resources/assets/polycraft/textures/items";
	
	private static Map<String, String> images = new HashMap<String, String>();
	
	private static StringBuilder wikiJson = new StringBuilder();
	private static String indent = "";
	private static void WriteLine(String str) {
		System.out.println(indent + str);
		wikiJson.append(str);
		wikiJson.append("\n");
	}
	private static void WriteJson(String outputFile) {
		File file = new File(outputFile);
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pw.print(wikiJson.toString());
		pw.close();
	}
	private static void Indent() {
		indent += "\t";
	}
	private static void Unindent() {
		indent = indent.substring(1);
	}
	private static void WriteInteger(String key, int val) {
		WriteLine("\"" + key + "\": " + val + ",");
	}
	private static void WriteFloat(String key, float val) {
		WriteLine("\"" + key + "\": " + val + ",");
	}
	private static void WriteString(String key, String val) {
		WriteLine("\"" + key + "\": \"" + val + "\",");
	}
	private static void WriteBool(String key, boolean bool) {
		String val = bool ? "true" : "false";
		WriteLine("\"" + key + "\": \"" + val + "\",");
	}

	private static Object getFieldOrDefault(Field field, Object obj, Object defaultValue) {
		try {
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return defaultValue;
	}
	
	private static void createItems() {
		Field iconStringField = null;
		Field damageVsEntityField = null;
		try {
			iconStringField = Item.class.getDeclaredField("iconString");
			damageVsEntityField = ItemTool.class.getDeclaredField("damageVsEntity");
			iconStringField.setAccessible(true);
			damageVsEntityField.setAccessible(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		WriteLine("items [");
		Indent();
		for (String itemName : PolycraftMod.items.keySet()) {
			Item item = PolycraftMod.items.get(itemName);
			String key = item.getUnlocalizedName() + ".name";
			String iconName = null;
			try {
				iconName = (String)iconStringField.get(item);
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) { }
			
			iconName = iconName.replaceAll("polycraft:", "");
			File iconFile = new File(ITEM_IMAGE_PATH, iconName + ".png");
			images.put(iconName, iconFile.getAbsolutePath());
			
			WriteLine("{");
			Indent();
			
			WriteString("name", StatCollector.translateToLocal(key));
			WriteString("icon", iconName);
			WriteBool("isRepairable", item.isRepairable());
			WriteBool("isDamagable", item.isDamageable());
			WriteInteger("durability", item.getMaxDamage());
			
			//System.out.println("ITEM: " + StatCollector.translateToLocal(key) + " => " + iconName + " (" + iconFile.exists() + ")");
			if (item instanceof PolycraftItem) {
				WriteString("type", ((PolycraftItem)item).getCategory().getValue());
			}
			
			if (item instanceof ItemFluidContainer) {
				ItemFluidContainer fluidContainer = (ItemFluidContainer)item;
				if (fluidContainer.fluidEntity != null) {
					WriteString("entity", fluidContainer.fluidEntity.name);
				}				
			} else if (item instanceof ItemArmor) {
				ItemArmor armor = (ItemArmor)item;
				WriteInteger("armorType", armor.armorType);
				WriteInteger("damageReduction", armor.damageReduceAmount);
				WriteInteger("enchantability", armor.getItemEnchantability());
			} else if(item instanceof ItemTool) {
				ItemTool tool = (ItemTool)item;
				WriteInteger("enchantability", tool.getItemEnchantability());	
				WriteFloat("damageVsEntity", (Float)getFieldOrDefault(damageVsEntityField, tool, -1.0f));
			}
			
			Unindent();
			WriteLine("},");
		}
		Unindent();
		WriteLine("],");		
	}
	
	private static void createRecipes() {
		
	}
	
	public static void createWikiData(String outputFile) {
		createItems();
		createRecipes();
		
		WriteJson(outputFile);
	}
}
