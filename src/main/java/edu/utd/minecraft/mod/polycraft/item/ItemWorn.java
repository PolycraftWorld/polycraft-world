package edu.utd.minecraft.mod.polycraft.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Plastic;

public class ItemWorn
{
	public static final Map<String, Class> allowedTypes = new HashMap<String, Class>();
	public static final Map<String, ArmorMaterial> allowedMaterials = new HashMap<String, ArmorMaterial>();
	static
	{
		allowedTypes.put("clothes", ItemArmorClothes.class);

		allowedMaterials.put("plastic", ArmorMaterial.CLOTH);

	}

	public static Item create(String type, String materialName, ArmorMaterial material, double durabilityBonus, int locationOnBody)
	{
		Item itemWorn = null;
		try {
			itemWorn = (Item) allowedTypes.get(type).getConstructor(ArmorMaterial.class, int.class, int.class).newInstance(material, locationOnBody, 0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		itemWorn.setTextureName(PolycraftMod.getTextureName("worn_" + getNameBase(materialName, type)));
		String temp = "worn_" + getNameBase(materialName, type);
		itemWorn.setMaxDamage((int) ((itemWorn.getMaxDamage() * (1 + durabilityBonus)) + 1));
		return itemWorn;
	}

	public static String getName(Plastic plastic, String materialName, String type, int locationOnBody)
	{
		return plastic.gameName + "_worn_" +locationOnBody + "_" + getNameBase(materialName, type);
	}

	public static String getNameBase(String materialName, String type)
	{
		return materialName + "_" + type;
	}
}
