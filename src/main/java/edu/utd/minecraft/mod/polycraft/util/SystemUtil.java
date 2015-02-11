package edu.utd.minecraft.mod.polycraft.util;

public class SystemUtil {
	public static long getPropertyLong(final String key, final long defaultValue) {
		try {
			return Long.parseLong(System.getProperty(key));
		}
		catch (final Exception e) {
			
			return defaultValue;
		}
	}
}
