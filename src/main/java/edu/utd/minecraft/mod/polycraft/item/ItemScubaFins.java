package edu.utd.minecraft.mod.polycraft.item;

import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;

public class ItemScubaFins extends PolycraftArmorFeet implements PolycraftMoldedItem {

	private static final ArmorSlot armorSlot = ArmorSlot.FEET;

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkArmor(player, armorSlot, ItemScubaFins.class);
	}

	public static ItemScubaFins getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItem(player, armorSlot);
	}

	public static ItemStack getEquippedItemStack(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItemStack(player, armorSlot);
	}

	public static boolean allowsFastSwimming(final EntityPlayer player) {
		return isEquipped(player) && PolycraftItemHelper.checkArmor(player, armorSlot, ItemScubaFins.class, true);
	}

	public static void damageIfMoving(final EntityPlayer player, final Random random) {
		if ((player.onGround || player.isInWater()) && (player.posX != player.lastTickPosX || player.posZ != player.lastTickPosZ)) {
			getEquippedItemStack(player).attemptDamageItem(1, random);
			if (!PolycraftItemHelper.checkArmor(player, armorSlot, ItemScubaFins.class, true))
				player.setCurrentItemOrArmor(1 + armorSlot.getInventoryArmorSlot(), null);
		}
	}

	private final MoldedItem moldedItem;

	public final float velocityInWater;
	public final float velocityOnGround;

	public ItemScubaFins(final MoldedItem moldedItem) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.CHAIN);
		//this.setTextureName(PolycraftMod.getAssetName("scuba_fins"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.setMaxDamage(PolycraftMod.convertSecondsToGameTicks(moldedItem.params.getInt(2) * 60));
		if (moldedItem.maxStackSize > 0)
			this.setMaxStackSize(moldedItem.maxStackSize);
		this.moldedItem = moldedItem;
		this.velocityInWater = moldedItem.params.getFloat(0);
		this.velocityOnGround = moldedItem.params.getFloat(1);
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getAssetNameString("textures/models/armor/scuba_layer_1.png");
	}

	@Override
	public MoldedItem getMoldedItem() {
		return moldedItem;
	}
}
