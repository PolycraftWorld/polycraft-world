package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Ingot;

public class ItemIngot extends Item implements PolycraftItem {

	public final Ingot ingot;

	public ItemIngot(final Ingot ingot) {
		this.ingot = ingot;
		this.setCreativeTab(CreativeTabs.tabMaterials);
		//this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(ingot.name)));
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_INGOT;
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
}
