package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.Vessel;

public class ItemPellets extends Item implements PolycraftItem {

	public final PolymerPellets polymerPellets;

	public ItemPellets(final PolymerPellets polymerPellets) {
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(Vessel.class.getSimpleName() + "_" + polymerPellets.vesselType.toString())));
		this.polymerPellets = polymerPellets;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_PELLET;
	}
}
