package edu.utd.minecraft.mod.polycraft.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.StatCollector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipeManager;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.item.PolycraftItem;

//TODO remove this class, it has been replaced by edu.utd.minecraft.mod.polycraft.util.WikiMaker
public class PolycraftModWikiMaker {
	private static Logger logger = LogManager.getLogger();

	private static final String ITEM_IMAGE_PATH = "../src/main/resources/assets/polycraft/textures/items";

	private static Map<String, String> images = new HashMap<String, String>();

	private static StringBuilder wikiJson = new StringBuilder();
	private static String indent = "";

	private static void WriteLine(String str) {
		System.out.println(indent + str);
		wikiJson.append(indent);
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

		WriteLine("'items' [");
		Indent();
		for (String itemName : PolycraftRegistry.items.keySet()) {
			Item item = PolycraftRegistry.items.get(itemName);
			String key = item.getUnlocalizedName() + ".name";
			String iconName = null;
			try {
				iconName = (String) iconStringField.get(item);
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			}

			if (iconName != null) {
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
					WriteString("type", ((PolycraftItem) item).getCategory().getValue());
				}

				if (item instanceof ItemArmor) {
					ItemArmor armor = (ItemArmor) item;
					WriteInteger("armorType", armor.armorType);
					WriteInteger("damageReduction", armor.damageReduceAmount);
					WriteInteger("enchantability", armor.getItemEnchantability());
				} else if (item instanceof ItemTool) {
					ItemTool tool = (ItemTool) item;
					WriteInteger("enchantability", tool.getItemEnchantability());
					WriteFloat("damageVsEntity", (Float) getFieldOrDefault(damageVsEntityField, tool, -1.0f));
				}

				Unindent();
				WriteLine("},");
			}
		}
		Unindent();
		WriteLine("],");
	}

	private static void createRecipes(PolycraftRecipeManager recipeManager) {
		WriteLine("'recipes': [");
		Indent();

		Collection<PolycraftRecipe> allRecipes = recipeManager.getAllRecipies();
		for (PolycraftRecipe recipe : allRecipes) {
			WriteString("container", recipe.getContainerType().name());

			WriteLine("'inputs': [");
			Indent();

			for (RecipeInput input : recipe.getInputs()) {
				WriteLine("'item': [");
				Indent();

				for (ItemStack stack : input.inputs) {
					WriteLine("{ 'item': '" + stack.getItem().getUnlocalizedName() + "', 'count': " + stack.stackSize + "  },");
				}

				WriteInteger("slot", input.slot.getSlotIndex());

				Unindent();
				WriteLine("],");
			}
			Unindent();
			WriteLine("],");

			WriteLine("'outputs': [");
			Indent();

			for (RecipeComponent output : recipe.getOutputs(null)) {
				WriteString("item", output.itemStack.getUnlocalizedName());
				WriteInteger("count", output.itemStack.stackSize);
			}

			Unindent();
			WriteLine("],");

		}

		Unindent();
		WriteLine("],");
	}

	public static void createWikiData(String outputFile) {
		createItems();
		createRecipes(PolycraftMod.recipeManager);

		WriteJson(outputFile);
	}
}
