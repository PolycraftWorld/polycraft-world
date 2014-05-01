package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;

public class ItemScubaMask extends PolycraftArmorHeadgear implements PolycraftMoldedItem {

	private final MoldedItem moldedItem;
	public final float fogDensity;

	public ItemScubaMask(final MoldedItem moldedItem) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.LEATHER);
		this.setTextureName(PolycraftMod.getAssetName("scuba_mask"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.moldedItem = moldedItem;
		this.fogDensity = moldedItem.getParamFloat(0);
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getAssetName("textures/models/armor/scuba_layer_1.png");
	}

	@Override
	public MoldedItem getMoldedItem() {
		return moldedItem;
	}
}