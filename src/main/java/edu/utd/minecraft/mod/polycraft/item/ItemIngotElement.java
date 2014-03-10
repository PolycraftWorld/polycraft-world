package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import edu.utd.minecraft.mod.polycraft.Element;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemIngotElement extends Item
{
	public final Element element;

	public ItemIngotElement(Element element)
	{
		this.element = element;
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getTextureName(element.itemNameIngot));
	}
}
