package edu.utd.minecraft.mod.polycraft.util;

public class SystemUtil {
	public static boolean getPropertyBoolean(final String key, final boolean defaultValue) {
		try {
			return Boolean.parseBoolean(System.getProperty(key));
		}
		catch (final Exception e) {
			
			return defaultValue;
		}
	}
	
	public static long getPropertyLong(final String key, final long defaultValue) {
		try {
			return Long.parseLong(System.getProperty(key));
		}
		catch (final Exception e) {
			
			return defaultValue;
		}
	}
}
