package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item.ToolMaterial;

public class ItemArmorClothes extends ItemArmor {
	
	public ItemArmorClothes(ArmorMaterial material, int renderIndex, int type)
	{
		//type: 0 - helmet, 1-plate, 2-legs, 3-boots		
		super(material, renderIndex, type);
		setCreativeTab(CreativeTabs.tabCombat);
	}

}
