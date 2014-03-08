package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;

public class ItemCatalyst extends Item
{
	public ItemCatalyst(Material ore)
	{
		super();
		setMaxDamage((getMaxDamage() * 2) + 1);
	}
}
