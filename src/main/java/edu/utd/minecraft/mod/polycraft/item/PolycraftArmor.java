package edu.utd.minecraft.mod.polycraft.item;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class PolycraftArmor extends ItemArmor implements PolycraftItem {
	public PolycraftArmor(ArmorMaterial armorMaterial, ArmorAppearance armorAppearance, ArmorSlot armorSlot) {
		super(armorMaterial, armorAppearance.getValue(), armorSlot.getValue());
		Preconditions.checkNotNull(armorMaterial);
		Preconditions.checkNotNull(armorAppearance);
		Preconditions.checkNotNull(armorSlot);
	}

	@Override
	public abstract ItemCategory getCategory();
	
	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) 
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
}
