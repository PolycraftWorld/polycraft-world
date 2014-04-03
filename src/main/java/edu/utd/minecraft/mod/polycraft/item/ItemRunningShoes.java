package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemRunningShoes extends PolycraftArmorFeet {
	public final float walkSpeedBuff;

	public ItemRunningShoes(final float walkSpeedBuff) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.LEATHER);
		this.setTextureName(PolycraftMod.getTextureName("running_shoes"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.walkSpeedBuff = walkSpeedBuff;
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getTextureName("textures/models/armor/track_suit_layer_1.png");
	}
}
