package edu.utd.minecraft.mod.polycraft.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.helpers.Closer;

import com.google.common.collect.Maps;

/**
 * Class to facilitate "Dynamic" values that can be read any time into the game while
 * it is running.  Useful for debugging without having to restart minecraft over
 * and over to check values.
 */
public class DynamicValue implements Runnable {
	private static Logger logger = LogManager.getLogger();
	
	/**
	 * The file to read the dynamic values from, in the format:
	 * key=value
	 * One per line.
	 */
	public static final String VALUES_FILE = "d:\\temp\\minecraft.values";
	
	/**
	 * Update delay in milliseconds
	 */
	public static final int UPDATE_DELAY = 2000;
	
	private static Map<String, String> values = Maps.newHashMap();
	private static Thread dynamicValueThread;
	
	public static int getInteger(String key, int defaultValue) {
		String value = values.get(key);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberEx) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	public static double getDouble(String key, double defaultValue) {
		String value = values.get(key);
		if (value != null) {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException numberEx) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		String value = values.get(key);
		if (value != null) {
			if (value.equals("true")) {
				return true;
			}
			return false;
		}
		return defaultValue;
	}
	
	public static String getString(String key, String defaultValue) {
		String value = values.get(key);
		if (value != null) {
			return value;
		}
		return defaultValue;
	}

	private DynamicValue() {
	}
	
	public static void start() {
		dynamicValueThread = new Thread(new DynamicValue());
		dynamicValueThread.start();
	}
	
	@Override
	public void run() {
		logger.debug("Dynamic values thread is started.");
		while(1 < 2) {
			File valuesFile = new File(VALUES_FILE);
			if (valuesFile.exists()) {
				Scanner scanner = null;
				try {
					Map<String, String> newMap = Maps.newHashMap();
					scanner = new Scanner(valuesFile);
					while (scanner.hasNextLine()) {
						String line = scanner.nextLine().trim();
						if (line.isEmpty() || line.startsWith("#")) {
							continue;
						}
						String [] split = line.split("=");
						if (split.length != 2) {
							continue;
						}
						if (split[0].isEmpty() || split[1].isEmpty()) {
							continue;
						}
						newMap.put(split[0], split[1]);
					}
					values = newMap;
				} catch (IOException ioEx) {
					LogUtil.niceError(logger, "Unable to read dynamic values file", ioEx, 5);					
				} finally {
					if (scanner != null) {
						Closer.closeSilent(scanner);
					}
				}				
			}
			
			try {
				Thread.sleep(UPDATE_DELAY);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
