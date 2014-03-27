package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemScubaFins extends ItemArmor {

	public final float swimSpeedBuff;

	public ItemScubaFins(final float swimSpeedBuff) {
		super(PolycraftMod.armorMaterialNone, 1, 3);
		this.setTextureName(PolycraftMod.getTextureName("scuba_fins"));
		this.swimSpeedBuff = swimSpeedBuff;
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getTextureName("textures/models/armor/scuba_layer_1.png");
	}
}
