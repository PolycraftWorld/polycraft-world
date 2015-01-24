package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemHoe;

public class ItemHoeGripped extends PolycraftHoe {
	public ItemHoeGripped(ToolMaterial material) {
		super(material);
		setCreativeTab(CreativeTabs.tabTools);
	}
}
