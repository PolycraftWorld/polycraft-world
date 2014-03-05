package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemPickaxe;

public class ItemPickaxePlasticHandle extends ItemPickaxe
{
	public ItemPickaxePlasticHandle(ToolMaterial p_i45347_1_)
	{
		super(p_i45347_1_);
		setMaxDamage((getMaxDamage() * 2) + 1);
	}
}
