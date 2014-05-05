package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;

public class ItemRunningShoes extends PolycraftArmorFeet implements PolycraftMoldedItem {

	private final MoldedItem moldedItem;
	public final float walkSpeedBuff;

	public ItemRunningShoes(final MoldedItem moldedItem) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.LEATHER);
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(moldedItem.source.polymerObject.name)));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.setMaxDamage(PolycraftMod.convertSecondsToGameTicks(moldedItem.params.getInt(1) * 60));
		this.moldedItem = moldedItem;
		this.walkSpeedBuff = moldedItem.params.getFloat(0);
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getAssetName("textures/models/armor/track_suit_layer_1.png");
	}

	@Override
	public MoldedItem getMoldedItem() {
		return moldedItem;
	}
}
