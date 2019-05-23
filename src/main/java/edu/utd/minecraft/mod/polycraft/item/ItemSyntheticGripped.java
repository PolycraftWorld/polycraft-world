package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.GrippedSyntheticTool;
import edu.utd.minecraft.mod.polycraft.config.GrippedTool;

public class ItemSyntheticGripped {
	private static final Logger logger = LogManager.getLogger();


	public static Item create(final GrippedSyntheticTool grippedSyntheticTool, ToolMaterial material) {
		Preconditions.checkNotNull(grippedSyntheticTool);
		Preconditions.checkNotNull(grippedSyntheticTool);

		//System.out.println(grippedSyntheticTool.name);
		
		Item itemSyntheticGripped = null;
		if (grippedSyntheticTool.name.contains("Shovel"))
			itemSyntheticGripped = new ItemShovelSyntheticGripped(material, grippedSyntheticTool);
		else if (grippedSyntheticTool.name.contains("Pickaxe"))
			itemSyntheticGripped = new ItemPickaxeSyntheticGripped(material, grippedSyntheticTool);
		else if (grippedSyntheticTool.name.contains("Axe"))
			itemSyntheticGripped = new ItemAxeSyntheticGripped(material, grippedSyntheticTool);
		else if (grippedSyntheticTool.name.contains("Sword"))
			itemSyntheticGripped = new ItemSwordSyntheticGripped(material, grippedSyntheticTool);
		else if (grippedSyntheticTool.name.contains("Hoe"))
			itemSyntheticGripped = new ItemHoeSyntheticGripped(material, grippedSyntheticTool);
		
		
		//itemSyntheticGripped.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(grippedSyntheticTool.name)));
		itemSyntheticGripped.setMaxDamage((int) ((grippedSyntheticTool.source.maxUses * (1 + grippedSyntheticTool.durabilityBuff)) + 1));
		
		return itemSyntheticGripped;
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
