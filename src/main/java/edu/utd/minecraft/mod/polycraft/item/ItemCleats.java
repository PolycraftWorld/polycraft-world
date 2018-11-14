package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;

public class ItemCleats extends PolycraftArmorHeadgear {

	private static final ArmorSlot armorSlot = ArmorSlot.FEET;

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkArmor(player, armorSlot, ItemCleats.class);
	}

	public static ItemCleats getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItem(player, armorSlot);
	}
	
	public ItemCleats(CustomObject config) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.LEATHER);
		this.setTextureName(PolycraftMod.getAssetName("cleats"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
	}
	
	

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getAssetName("textures/models/armor/running_shoes_layer_1.png");
	}

}