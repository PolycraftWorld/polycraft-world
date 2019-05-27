package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CellCultureDish;

public class ItemCellCultureDish extends Item implements PolycraftItem {
	public final CellCultureDish cellCultureDish;

	public ItemCellCultureDish(final CellCultureDish cellCultureDish) {
		Preconditions.checkNotNull(cellCultureDish);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		//this.setTextureName(PolycraftMod.getAssetName("cell_culture_dish"));
		this.cellCultureDish = cellCultureDish;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_CELL_CULTURE_DISH;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);
	}
}
