package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPickaxe;

public class ItemPickaxeGripped extends ItemPickaxe
{
	public ItemPickaxeGripped(String materialName, ToolMaterial material, double durabilityBonus)
	{
		super(material);
		setTextureName(PolycraftMod.getTextureName("gripped_" + materialName + "_pickaxe"));
		setCreativeTab(CreativeTabs.tabTools);
		setMaxDamage((int) ((getMaxDamage() * (1 + durabilityBonus)) + 1));
	}
}
