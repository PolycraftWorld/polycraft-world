package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemPickaxe;

public class ItemPickaxeGripped extends ItemPickaxe
{
	public ItemPickaxeGripped(ToolMaterial p_i45347_1_)
	{
		super(p_i45347_1_);
		setMaxDamage((getMaxDamage() * 2) + 1);
	}
}
