package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;


public abstract class PolycraftArmorFeet extends PolycraftArmor {
	public PolycraftArmorFeet(ArmorMaterial armorMaterial, ArmorAppearance armorAppearance) {
		super(armorMaterial, armorAppearance, ArmorSlot.FEET);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ARMOR_FEET;
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
}
