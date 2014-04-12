package edu.utd.minecraft.mod.polycraft.item;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Polymer;

public class ItemGripped {
	private static final Logger logger = LogManager.getLogger();

	public static final Map<String, Class> allowedTypes = new LinkedHashMap<String, Class>();
	public static final Map<String, ToolMaterial> allowedMaterials = new LinkedHashMap<String, ToolMaterial>();

	static {
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

	public static Item create(final String type, final String materialName, final ToolMaterial material, final double durabilityBonus) {
		Preconditions.checkNotNull(materialName);
		Preconditions.checkNotNull(material);
		Item itemGripped = null;
		try {
			itemGripped = (Item) allowedTypes.get(type).getConstructor(ToolMaterial.class).newInstance(material);
		} catch (Exception e) {
			logger.fatal("Error creating gripped item", e);
			throw new IllegalStateException("Unable to create gripped item.", e);
		}
		itemGripped.setTextureName(PolycraftMod.getAssetName("gripped_" + PolycraftMod.getFileSafeName(getNameBase(materialName, type))));
		itemGripped.setMaxDamage((int) ((itemGripped.getMaxDamage() * (1 + durabilityBonus)) + 1));
		return itemGripped;
	}

	public static String getName(final Polymer polymer, final String materialName, final String type) {
		return polymer.name + " Gripped " + getNameBase(materialName, type);
	}

	public static String getNameBase(final String materialName, final String type) {
		Preconditions.checkNotNull(materialName);
		Preconditions.checkNotNull(type);
		return materialName + "_" + type;
	}
}
