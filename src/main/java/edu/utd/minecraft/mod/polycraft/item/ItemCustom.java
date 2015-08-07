package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemCustom extends Item implements PolycraftItem {
	public final CustomObject config;

	public ItemCustom(final CustomObject config) {
		Preconditions.checkNotNull(config);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name)));
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.config = config;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ITEMS_CUSTOM;
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
}


