package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.Item;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.GrippedTool;

public class ItemGripped {
	private static final Logger logger = LogManager.getLogger();

	public static Item create(final GrippedTool grippedTool) {
		Preconditions.checkNotNull(grippedTool);
		Preconditions.checkNotNull(grippedTool);


		System.out.println(grippedTool.name);
		Item itemGripped = null;
		if (grippedTool.source.name.contains("Shovel"))
			itemGripped = new ItemShovelGripped(grippedTool.toolMaterial.minecraftMaterial);
		else if (grippedTool.source.name.contains("Pickaxe"))
			itemGripped = new ItemPickaxeGripped(grippedTool.toolMaterial.minecraftMaterial);
		else if (grippedTool.source.name.contains("Axe"))
			itemGripped = new ItemAxeGripped(grippedTool.toolMaterial.minecraftMaterial);
		else if (grippedTool.source.name.contains("Sword"))
			itemGripped = new ItemSwordGripped(grippedTool.toolMaterial.minecraftMaterial);
		else if (grippedTool.source.name.contains("Hoe"))
			itemGripped = new ItemHoeGripped(grippedTool.toolMaterial.minecraftMaterial);
		

		itemGripped.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(grippedTool.name)));
		itemGripped.setMaxDamage((int) ((itemGripped.getMaxDamage() * (1 + grippedTool.durabilityBuff)) + 1));
		return itemGripped;
	}

	public static String getName(final String materialName, final String type) {
		return "Gripped " + getNameBase(materialName, type);
	}

	public static String getNameBase(final String materialName, final String type) {
		Preconditions.checkNotNull(materialName);
		Preconditions.checkNotNull(type);
		return materialName + "_" + type;
	}
}
