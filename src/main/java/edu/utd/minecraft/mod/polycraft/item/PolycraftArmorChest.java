package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;


public abstract class PolycraftArmorChest extends PolycraftArmor {
	public PolycraftArmorChest(ArmorMaterial armorMaterial, ArmorAppearance armorAppearance) {
		super(armorMaterial, armorAppearance, ArmorSlot.CHEST);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ARMOR_CHEST;
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
}
