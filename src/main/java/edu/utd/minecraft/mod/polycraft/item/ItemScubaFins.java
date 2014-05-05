package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;

public class ItemScubaFins extends PolycraftArmorFeet implements PolycraftMoldedItem {

	private final MoldedItem moldedItem;

	public final float swimSpeedBuff;
	public final float walkSpeedBuff;

	public ItemScubaFins(final MoldedItem moldedItem) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.CHAIN);
		this.setTextureName(PolycraftMod.getAssetName("scuba_fins"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.moldedItem = moldedItem;
		this.swimSpeedBuff = moldedItem.params.getFloat(0);
		this.walkSpeedBuff = moldedItem.params.getFloat(1);
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
