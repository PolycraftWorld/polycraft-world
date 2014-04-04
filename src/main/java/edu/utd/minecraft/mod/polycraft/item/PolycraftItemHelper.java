package edu.utd.minecraft.mod.polycraft.item;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import edu.utd.minecraft.mod.polycraft.util.LogUtil;

public class PolycraftItemHelper {
	private final static Logger logger = LogManager.getLogger();

	public static void createTagCompound(final ItemStack itemStack) {
		if (itemStack == null) {
			LogUtil.niceWarn(logger, "createTagCompound called with a null item", 3);
		}
		if (itemStack.stackTagCompound == null) {
			itemStack.setTagCompound(new NBTTagCompound());
		}
	}
	
	/**
	 * Safely sets an integer value on the tag compound of an item stack.
	 */
	public static void setInteger(final ItemStack itemStack, String key, int value) {
		if (key == null || key.length() == 0) {
			LogUtil.niceWarn(logger, "setInteger called with an invalid key", 3);
		}
		if (itemStack == null || itemStack.getItem() == null) {
			LogUtil.niceWarn(logger, "setInteger called with null item", 3);
			return;
		}		
		if (itemStack.stackTagCompound == null) {
			PolycraftItemHelper.createTagCompound(itemStack);
		}
		itemStack.stackTagCompound.setInteger(key, value);	
	}
	
	/**
	 * Safely gets an integer value, or a default if invalid items are passed in.
	 */
	public static int getIntegerOrDefault(final ItemStack itemStack, String key, int defaultValue) {
		if (key == null || key.length() == 0) {
			LogUtil.niceWarn(logger, "getIntegerOrDefault called with an invalid key", 3);
		}
		if (itemStack == null || itemStack.getItem() == null) {
			LogUtil.niceWarn(logger, "getIntegerOrDefault called with null item", 3);
			return 0;
		}		
		if (itemStack.stackTagCompound == null) {
			LogUtil.niceWarn(logger, "getIntegerOrDefault called with no stackTagCompound on item " + itemStack, 3);
			return 0;
		}
		Integer value = itemStack.stackTagCompound.getInteger(key);
		if (value == null) {
			LogUtil.niceWarn(logger, "getIntegerOrDefault called but the return value was null", 3);
			return 0;
		}
		return value;
	}
}
