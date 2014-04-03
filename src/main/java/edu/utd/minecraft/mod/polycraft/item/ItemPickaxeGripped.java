package edu.utd.minecraft.mod.polycraft.item;

import com.google.common.base.Preconditions;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPickaxe;

public class ItemPickaxeGripped extends PolycraftPickaxe
{
	public ItemPickaxeGripped(ToolMaterial material) {
		super(material);
		Preconditions.checkNotNull(material);
		setCreativeTab(CreativeTabs.tabTools);
	}
}