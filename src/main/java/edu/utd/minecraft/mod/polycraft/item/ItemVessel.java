package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.SourcedVesselConfig;
import edu.utd.minecraft.mod.polycraft.config.Vessel;

public class ItemVessel extends Item implements PolycraftItem {
	public final SourcedVesselConfig vessel;

	public ItemVessel(final SourcedVesselConfig vessel) {
		Preconditions.checkNotNull(vessel);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(Vessel.class.getSimpleName() + "_" + vessel.vesselType.toString())));
		this.vessel = vessel;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ITEMS_VESSEL;
	}
}
