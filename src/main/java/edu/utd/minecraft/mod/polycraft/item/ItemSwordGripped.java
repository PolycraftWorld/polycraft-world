package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemSword;

public class ItemSwordGripped extends ItemSword
{
	public ItemSwordGripped(String materialName, ToolMaterial material, double durabilityBonus)
	{
		super(material);
		setTextureName(PolycraftMod.getTextureName("gripped_" + materialName + "_sword"));
		setCreativeTab(CreativeTabs.tabCombat);
		ItemGripped.applyDurabilityBonus(this, durabilityBonus);
	}
}
