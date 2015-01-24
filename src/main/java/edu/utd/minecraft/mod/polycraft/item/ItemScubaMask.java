package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;

public class ItemScubaMask extends PolycraftArmorHeadgear implements PolycraftMoldedItem {

	private static final ArmorSlot armorSlot = ArmorSlot.HEAD;

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkArmor(player, armorSlot, ItemScubaMask.class);
	}

	public static ItemScubaMask getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItem(player, armorSlot);
	}

	private final MoldedItem moldedItem;
	public final float fogDensity;

	public ItemScubaMask(final MoldedItem moldedItem) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.LEATHER);
		this.setTextureName(PolycraftMod.getAssetName("scuba_mask"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		if (moldedItem.maxStackSize > 0)
			this.setMaxStackSize(moldedItem.maxStackSize);
		this.moldedItem = moldedItem;
		this.fogDensity = moldedItem.params.getFloat(0);
	}
	
	public ItemScubaMask(final MoldedItem moldedItem, final String iconName) {
		this(moldedItem);
		this.setTextureName(PolycraftMod.getAssetName(iconName));	
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