package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPickaxe;

public class ItemPickaxeGripped extends PolycraftPickaxe
{
	public ItemPickaxeGripped(ToolMaterial material) {
		super(material);
		setCreativeTab(CreativeTabs.tabTools);
	}
}