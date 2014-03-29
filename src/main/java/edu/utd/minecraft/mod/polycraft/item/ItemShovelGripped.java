package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemSpade;

public class ItemShovelGripped extends PolycraftSpade
{
	public ItemShovelGripped(ToolMaterial material)
	{
		super(material);
		setCreativeTab(CreativeTabs.tabTools);
	}
}
