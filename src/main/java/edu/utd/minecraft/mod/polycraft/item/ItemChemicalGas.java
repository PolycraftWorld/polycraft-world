package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;

public class ItemChemicalGas extends ItemChemical
{
	public ItemChemicalGas(Material baseMaterial)
	{
		super(baseMaterial);
		//setMaxDamage((getMaxDamage() * 2) + 1);
	}
}
