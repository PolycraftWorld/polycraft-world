package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemAxe;

public class ItemAxeGripped extends ItemAxe
{
	public ItemAxeGripped(String materialName, ToolMaterial material, double durabilityBonus)
	{
		super(material);
		setTextureName(PolycraftMod.getTextureName("gripped_" + materialName + "_axe"));
		setCreativeTab(CreativeTabs.tabTools);
		ItemGripped.applyDurabilityBonus(this, durabilityBonus);
	}
}
