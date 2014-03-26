package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Entity;

public class ItemFluidContainer extends Item {

	public static String getGameName(final Entity entity) {
		return entity.gameName + "_" + PolycraftMod.itemNameFluidContainer;
	}

	public ItemFluidContainer() {
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getTextureName(PolycraftMod.itemNameFluidContainer));
	}
}