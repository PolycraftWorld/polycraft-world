package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemScubaFins extends PolycraftArmorFeet {

	public final float swimSpeedBuff;
	public final float walkSpeedBuff;

	public ItemScubaFins(final float swimSpeedBuff, final float walkSpeedBuff) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.CHAIN);
		this.setTextureName(PolycraftMod.getAssetName("scuba_fins"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.swimSpeedBuff = swimSpeedBuff;
		this.walkSpeedBuff = walkSpeedBuff;
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getAssetName("textures/models/armor/scuba_layer_1.png");
	}
}
