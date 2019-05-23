package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;

public class ItemCatalyst extends Item implements PolycraftItem {
	public final Catalyst catalyst;

	public ItemCatalyst(final Catalyst catalyst) {
		Preconditions.checkNotNull(catalyst);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		//this.setTextureName(PolycraftMod.getAssetName("catalyst"));
		this.catalyst = catalyst;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_CATALYST;
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
}
