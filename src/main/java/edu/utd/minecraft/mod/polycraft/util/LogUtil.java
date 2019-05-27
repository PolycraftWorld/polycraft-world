package edu.utd.minecraft.mod.polycraft.util;

import java.util.Collection;
import java.util.Map;

import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;

public class LogUtil {
	// TODO: Flush the map every so often?
	static Map<StackTraceElement, Long> lastMessageMap = Maps.newHashMap();
	
	public static String toStringRecipeComponents(Collection<RecipeComponent> components) {
		if (components == null) {
			return "null";
		}
		String ret = "[";
		boolean first = true;
		for (RecipeComponent comp : components) {
			if (!first) {
				ret += ", ";
			}
			ret += toString(comp);
			first = false;
		}
		ret += "]";
		return ret;
	}
	
	public static String toString(RecipeComponent comp) {
		if (comp == null) {
			return "null";
		}
		return comp.toString();
	}
	
	public static String toStringRecipeInputs(Collection<RecipeInput> inputs) {
		if (inputs == null) {
			return "null";
		}
		String ret = "[";
		boolean first = true;
		for (RecipeInput input : inputs) {
			if (!first) {
				ret += ", ";
			}
			ret += toString(input);
			first = false;
		}
		ret += "]";
		return ret;
	}
	
	public static String toStringItemStack(Collection<ItemStack> items) {
		if (items == null) {
			return "null";
		}
		String ret = "[";
		boolean first = true;
		for (ItemStack item : items) {
			if (!first) {
				ret += ", ";
			}
			ret += toString(item);
			first = false;
		}
		ret += "]";
		return ret;
	}
	
	public static String toString(RecipeInput input) {
		if (input == null) {
			return "null";
		}
		return input.toString();
	}
	
	public static String toString(ItemStack itemStack) {
		if (itemStack == null) {
			return "null";
		}
		if (itemStack.getItem() == null) {
			return "null";
		}
		return itemStack.getItem().getUnlocalizedName() +
				"(" + PolycraftRegistry.registryIdToNameUpper.get( itemStack.getItem().getUnlocalizedName().replaceAll("item.", "")) + ")" +
				": " + itemStack.stackSize + ":" + itemStack.getItemDamage();
	}
	
	/**
	 * Logs an error message only once a specified interval.
	 * @param seconds The number of seconds that must elapse before the warning is logged again.
	 */
	public static void niceError(Logger logger, String message, int seconds) {
		StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
		Long lastMessage = lastMessageMap.get(stackTrace);
		if (lastMessage == null || (lastMessage != null
				&& (System.currentTimeMillis() - lastMessage.longValue() > seconds * 1000))) {
			logger.error(message);
			lastMessageMap.put(stackTrace, System.currentTimeMillis());
		}
	}
	
	/**
	 * Logs an error message only once a specified interval.
	 * @param seconds The number of seconds that must elapse before the warning is logged again.
	 */
	public static void niceError(Logger logger, String message, Throwable ex, int seconds) {
		StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
		Long lastMessage = lastMessageMap.get(stackTrace);
		if (lastMessage == null || (lastMessage != null
				&& (System.currentTimeMillis() - lastMessage.longValue() > seconds * 1000))) {
			logger.error(message, ex);
			lastMessageMap.put(stackTrace, System.currentTimeMillis());
		}
	}	
	/**
	 * Logs a warning message only once a specified interval.
	 * @param seconds The number of seconds that must elapse before the warning is logged again.
	 */
	public static void niceWarn(Logger logger, String message, int seconds) {
		StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
		Long lastMessage = lastMessageMap.get(stackTrace);
		if (lastMessage == null || (lastMessage != null
				&& (System.currentTimeMillis() - lastMessage.longValue() > seconds * 1000))) {
			logger.warn(message);
			lastMessageMap.put(stackTrace, System.currentTimeMillis());
		}
	}
	
	/**
	 * Logs a warning message only once a specified interval.
	 * @param seconds The number of seconds that must elapse before the warning is logged again.
	 */
	public static void niceWarn(Logger logger, String message, Throwable ex, int seconds) {
		StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
		Long lastMessage = lastMessageMap.get(stackTrace);
		if (lastMessage == null || (lastMessage != null
				&& (System.currentTimeMillis() - lastMessage.longValue() > seconds * 1000))) {
			logger.warn(message, ex);
			lastMessageMap.put(stackTrace, System.currentTimeMillis());
		}
	}
}
