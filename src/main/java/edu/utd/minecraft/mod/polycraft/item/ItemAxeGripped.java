package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemAxe;

public class ItemAxeGripped extends ItemAxe
{
	public ItemAxeGripped(ToolMaterial material)
	{
		super(material);
		setCreativeTab(CreativeTabs.tabTools);
	}
}
