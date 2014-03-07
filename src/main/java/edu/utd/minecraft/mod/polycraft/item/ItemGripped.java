package edu.utd.minecraft.mod.polycraft.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.utd.minecraft.mod.polycraft.Plastic;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.Item.ToolMaterial;

public class ItemGripped
{
	public static final Collection<String> allowedItems = new ArrayList<String>();
	public static final Map<String, ToolMaterial> allowedMaterials = new HashMap<String, ToolMaterial>();
	static
	{
		allowedItems.add("shovel");
		allowedItems.add("axe");
		allowedItems.add("pickaxe");
		allowedItems.add("hoe");
		allowedItems.add("sword");
		
		allowedMaterials.put("wooden", ToolMaterial.WOOD);
		allowedMaterials.put("stone", ToolMaterial.STONE);
		allowedMaterials.put("iron", ToolMaterial.IRON);
		allowedMaterials.put("gold", ToolMaterial.GOLD);
		allowedMaterials.put("diamond", ToolMaterial.EMERALD);
	}
	
	public static String getNamePrefix(Plastic plastic, String materialName)
	{
		return plastic.gameName + "_gripped_" + materialName;
	}
	
	public static void applyDurabilityBonus(Item item, double durabilityBonus)
	{
		item.setMaxDamage((int) ((item.getMaxDamage() * (1 + durabilityBonus)) + 1));
	}
}
