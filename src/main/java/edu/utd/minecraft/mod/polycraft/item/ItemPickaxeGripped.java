package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;

import com.google.common.base.Preconditions;

public class ItemPickaxeGripped extends PolycraftPickaxe
{
	public ItemPickaxeGripped(ToolMaterial material) {
		super(material);
		Preconditions.checkNotNull(material);
		setCreativeTab(CreativeTabs.tabTools);
	}
}