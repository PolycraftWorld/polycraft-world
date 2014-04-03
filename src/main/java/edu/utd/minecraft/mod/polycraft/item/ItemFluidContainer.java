package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Entity;

public class ItemFluidContainer extends Item implements PolycraftItem {
	public final Entity fluidEntity;
	
	public ItemFluidContainer(final Entity entity) {
		fluidEntity = entity;
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getTextureName(PolycraftMod.itemNameFluidContainer));
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_FLUID;
	}
	
	public static String getGameName(final Entity entity) {
		return entity.gameName + "_" + PolycraftMod.itemNameFluidContainer;
	}
}