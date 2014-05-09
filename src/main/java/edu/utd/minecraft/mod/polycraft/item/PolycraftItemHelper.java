package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utd.minecraft.mod.polycraft.util.LogUtil;

public class PolycraftItemHelper {
	private final static Logger logger = LogManager.getLogger();

	public static void createTagCompound(final ItemStack itemStack) {
		if (itemStack.stackTagCompound == null)
			itemStack.setTagCompound(new NBTTagCompound());
	}

	private static boolean hasKey(final ItemStack itemStack, final String key) {
		PolycraftItemHelper.createTagCompound(itemStack);
		return itemStack.stackTagCompound.hasKey(key);
	}

	private static boolean validParams(final ItemStack itemStack, final String key) {
		if (key == null || key.length() == 0) {
			LogUtil.niceWarn(logger, "validParams called with an invalid key", 3);
			return false;
		}
		if (itemStack == null || itemStack.getItem() == null) {
			LogUtil.niceWarn(logger, "validParams called with null item", 3);
			return false;
		}
		return true;
	}

	/**
	 * Safely sets a boolean value on the tag compound of an item stack.
	 */
	public static void setBoolean(final ItemStack itemStack, final String key, final boolean value) {
		if (validParams(itemStack, key)) {
			PolycraftItemHelper.createTagCompound(itemStack);
			itemStack.stackTagCompound.setBoolean(key, value);
		}
	}

	/**
	 * Safely sets an integer value on the tag compound of an item stack.
	 */
	public static void setInteger(final ItemStack itemStack, final String key, final int value) {
		if (validParams(itemStack, key)) {
			PolycraftItemHelper.createTagCompound(itemStack);
			itemStack.stackTagCompound.setInteger(key, value);
		}
	}

	/**
	 * Safely gets a boolean value, or a default if invalid items are passed in.
	 */
	public static boolean getBoolean(final ItemStack itemStack, final String key, final boolean defaultValue) {
		if (validParams(itemStack, key) && hasKey(itemStack, key))
			return itemStack.stackTagCompound.getBoolean(key);
		return defaultValue;
	}

	/**
	 * Safely gets an integer value, or a default if invalid items are passed in.
	 */
	public static int getInteger(final ItemStack itemStack, final String key, final int defaultValue) {
		if (validParams(itemStack, key) && hasKey(itemStack, key))
			return itemStack.stackTagCompound.getInteger(key);
		return defaultValue;
	}

	public static <I extends Item> I getCurrentEquippedItem(final EntityPlayer player) {
		return (I) player.getCurrentEquippedItem().getItem();
	}

	public static ItemStack getArmorItemStack(final EntityPlayer player, final ArmorSlot armorSlot) {
		return player.getCurrentArmor(armorSlot.getInventoryArmorSlot());
	}

	public static <I extends Item> I getArmorItem(final EntityPlayer player, final ArmorSlot armorSlot) {
		return (I) getArmorItemStack(player, armorSlot).getItem();
	}

	public static boolean checkCurrentEquippedItem(final EntityPlayer player, final Class itemClass) {
		return checkCurrentEquippedItem(player, itemClass, false);
	}

	public static boolean checkCurrentEquippedItem(final EntityPlayer player, final Class itemClass, boolean notBroken) {
		return checkItem(player.getCurrentEquippedItem(), itemClass, notBroken);
	}

	public static boolean checkArmor(final EntityPlayer player, final ArmorSlot armorSlot, final Class itemClass) {
		return checkItem(player.getCurrentArmor(armorSlot.getInventoryArmorSlot()), itemClass);
	}

	public static boolean checkArmor(final EntityPlayer player, final ArmorSlot armorSlot, final Class itemClass, final boolean notBroken) {
		return checkItem(player.getCurrentArmor(armorSlot.getInventoryArmorSlot()), itemClass, notBroken);
	}

	private static boolean checkItem(final ItemStack itemStack, final Class itemClass) {
		return checkItem(itemStack, itemClass, false);
	}

	private static boolean checkItem(final ItemStack itemStack, final Class itemClass, final boolean notBroken) {
		return (itemStack != null && itemStack.getItem().getClass().equals(itemClass) && (!notBroken || itemStack.getItemDamage() < itemStack.getMaxDamage()));
	}
}
