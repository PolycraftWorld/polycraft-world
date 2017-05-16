package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public abstract class PolycraftArmorHeadgear extends PolycraftArmor {
	public PolycraftArmorHeadgear(ArmorMaterial armorMaterial, ArmorAppearance armorAppearance) {
		super(armorMaterial, armorAppearance, ArmorSlot.HEAD);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ARMOR_HEADGEAR;
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
}
