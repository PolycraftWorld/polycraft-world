package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemSpade;

public class ItemShovelGripped extends ItemSpade
{
	public ItemShovelGripped(String materialName, ToolMaterial material, double durabilityBonus)
	{
		super(material);
		setTextureName(PolycraftMod.getTextureName("gripped_" + materialName + "_shovel"));
		setCreativeTab(CreativeTabs.tabTools);
		ItemGripped.applyDurabilityBonus(this, durabilityBonus);
	}
}
