package edu.utd.minecraft.mod.polycraft.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Base62 {

	private static final String baseDigits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int BASE = baseDigits.length();
	private static final char[] digitsChar = baseDigits.toCharArray();
	private static final int FAST_SIZE = 'z';
	private static final int[] digitsIndex = new int[FAST_SIZE + 1];

	static {
		for (int i = 0; i < FAST_SIZE; i++) {
			digitsIndex[i] = -1;
		}
		//
		for (int i = 0; i < BASE; i++) {
			digitsIndex[digitsChar[i]] = i;
		}
	}

	public static long decode(String s) {
		long result = 0L;
		long multiplier = 1;
		for (int pos = s.length() - 1; pos >= 0; pos--) {
			int index = getIndex(s, pos);
			result += index * multiplier;
			multiplier *= BASE;
		}
		return result;
	}

	public static String encode(long number) {
		if (number < 0)
			throw new IllegalArgumentException("Number(Base62) must be positive: " + number);
		if (number == 0)
			return "0";
		StringBuilder buf = new StringBuilder();
		while (number != 0) {
			buf.append(digitsChar[(int) (number % BASE)]);
			number /= BASE;
		}
		return buf.reverse().toString();
	}

	private static int getIndex(String s, int pos) {
		char c = s.charAt(pos);
		if (c > FAST_SIZE) {
			throw new IllegalArgumentException("Unknow character for Base62: " + s);
		}
		int index = digitsIndex[c];
		if (index == -1) {
			throw new IllegalArgumentException("Unknow character for Base62: " + s);
		}
		return index;
	}

	public static void export(final long start, final long finish, final String file) {
		PrintWriter out;
		try {
			out = new PrintWriter(file);
			for (long i = start; i <= finish; i++) {
				out.write(Base62.encode(i) + "\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}