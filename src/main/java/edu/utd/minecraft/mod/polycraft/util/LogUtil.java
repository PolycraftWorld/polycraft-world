package edu.utd.minecraft.mod.polycraft.util;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

public class LogUtil {
	// TODO: Flush the map every so often?
	static Map<StackTraceElement, Long> lastMessageMap = Maps.newHashMap();
	
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
