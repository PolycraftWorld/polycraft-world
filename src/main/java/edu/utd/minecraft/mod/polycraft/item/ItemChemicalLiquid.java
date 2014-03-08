package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;

public class ItemChemicalLiquid extends ItemChemical
{
	public ItemChemicalLiquid(Material baseMaterial)
	{
		super(baseMaterial);
		//setMaxDamage((getMaxDamage() * 2) + 1);
	}
}
