package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;

public class ItemSwordGripped extends PolycraftSword
{
	public ItemSwordGripped(ToolMaterial material)
	{
		super(material);
		setCreativeTab(CreativeTabs.tabCombat);
	}
}
