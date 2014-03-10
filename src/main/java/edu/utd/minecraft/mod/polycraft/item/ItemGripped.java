package edu.utd.minecraft.mod.polycraft.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import edu.utd.minecraft.mod.polycraft.Plastic;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemGripped
{
	public static final Map<String, Class> allowedTypes = new HashMap<String, Class>();
	public static final Map<String, ToolMaterial> allowedMaterials = new HashMap<String, ToolMaterial>();
	static
	{
		allowedTypes.put("shovel", ItemShovelGripped.class);
		allowedTypes.put("axe", ItemAxeGripped.class);
		allowedTypes.put("pickaxe", ItemPickaxeGripped.class);
		allowedTypes.put("hoe", ItemHoeGripped.class);
		allowedTypes.put("sword", ItemSwordGripped.class);

		allowedMaterials.put("wooden", ToolMaterial.WOOD);
		allowedMaterials.put("stone", ToolMaterial.STONE);
		allowedMaterials.put("iron", ToolMaterial.IRON);
		allowedMaterials.put("golden", ToolMaterial.GOLD);
		allowedMaterials.put("diamond", ToolMaterial.EMERALD);
	}

	public static Item create(String type, String materialName, ToolMaterial material, double durabilityBonus)
	{
		Item itemGripped = null;
		try {
			itemGripped = (Item) allowedTypes.get(type).getConstructor(ToolMaterial.class).newInstance(material);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		itemGripped.setTextureName(PolycraftMod.getTextureName("gripped_" + getNameBase(materialName, type)));
		itemGripped.setMaxDamage((int) ((itemGripped.getMaxDamage() * (1 + durabilityBonus)) + 1));
		return itemGripped;
	}

	public static String getName(Plastic plastic, String materialName, String type)
	{
		return plastic.gameName + "_gripped_" + getNameBase(materialName, type);
	}

	public static String getNameBase(String materialName, String type)
	{
		return materialName + "_" + type;
	}
}
