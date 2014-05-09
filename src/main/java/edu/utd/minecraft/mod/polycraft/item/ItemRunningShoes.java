package edu.utd.minecraft.mod.polycraft.item;

import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;

public class ItemRunningShoes extends PolycraftArmorFeet implements PolycraftMoldedItem {

	private static final ArmorSlot armorSlot = ArmorSlot.FEET;

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkArmor(player, armorSlot, ItemRunningShoes.class);
	}

	public static ItemRunningShoes getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItem(player, armorSlot);
	}

	public static ItemStack getEquippedItemStack(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItemStack(player, armorSlot);
	}

	public static boolean allowsRunning(final EntityPlayer player) {
		return isEquipped(player) && PolycraftItemHelper.checkArmor(player, armorSlot, ItemRunningShoes.class, true);
	}

	public static void damageIfMovingOnGround(final EntityPlayer player, final Random random) {
		if (player.onGround && (player.posX != player.lastTickPosX || player.posZ != player.lastTickPosZ)) {
			getEquippedItemStack(player).attemptDamageItem(1, random);
			if (!PolycraftItemHelper.checkArmor(player, armorSlot, ItemRunningShoes.class, true))
				player.setCurrentItemOrArmor(1 + armorSlot.getInventoryArmorSlot(), null);
		}
	}

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
