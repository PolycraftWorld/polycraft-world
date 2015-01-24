package edu.utd.minecraft.mod.polycraft.util;

import java.lang.reflect.Array;

public class ArrayUtil {
	/**
	 * Copies an array. assumes all T are of the same type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[][] clone(T[][] source) {
	    Class<? extends T[][]> type = (Class<? extends T[][]>) source.getClass();
	    T[][] copy = (T[][]) Array.newInstance(type.getComponentType(), source.length);
	    for (int i = 0; i < source.length; i++) {
	    	copy[i] = source[i].clone();
	        System.arraycopy(source[i], 0, copy[i], 0, source[i].length);
	    }
	    return copy;
	}
}
