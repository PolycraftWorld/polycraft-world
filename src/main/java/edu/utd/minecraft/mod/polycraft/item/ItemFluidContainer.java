package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Entity;

public class ItemFluidContainer extends Item implements PolycraftItem {
	public final Entity fluidEntity;

	public ItemFluidContainer() {
		this(null);
	}

	public ItemFluidContainer(final Entity entity) {
		fluidEntity = entity;
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName("fluid_container"));
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_FLUID;
	}

	public static String getItemName(final Entity entity) {
		return entity.name + " " + PolycraftMod.itemNameFluidContainer;
	}
}