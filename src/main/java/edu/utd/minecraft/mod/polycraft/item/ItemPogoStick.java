package edu.utd.minecraft.mod.polycraft.item;

import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.PogoStick;

public class ItemPogoStick extends PolycraftUtilityItem {

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemPogoStick.class, true);
	}

	public static ItemPogoStick getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	public static ItemStack getEquippedItemStack(final EntityPlayer player) {
		return player.getCurrentEquippedItem();
	}

	public static void damage(final EntityPlayer player, final Random random) {
		getEquippedItemStack(player).attemptDamageItem(1, random);
		if (!isEquipped(player))
			player.destroyCurrentEquippedItem();
	}

	public final PogoStick config;

	public ItemPogoStick(final PogoStick config) {
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name)));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.setMaxDamage(config.maxBounces);
		this.config = config;
	}
}
