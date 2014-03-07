package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemHoe;

public class ItemHoeGripped extends ItemHoe
{
	public ItemHoeGripped(String materialName, ToolMaterial material, double durabilityBonus)
	{
		super(material);
		setTextureName(PolycraftMod.getTextureName("gripped_" + materialName + "_hoe"));
		setCreativeTab(CreativeTabs.tabTools);
		ItemGripped.applyDurabilityBonus(this, durabilityBonus);
	}
}
