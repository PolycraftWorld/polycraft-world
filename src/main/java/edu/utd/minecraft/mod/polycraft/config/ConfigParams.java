package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ConfigParams {

	public final String[] names;
	public final List<String> values;

	public ConfigParams(final String[] names, final String[] params, final int offset) {
		this.names = names;
		this.values = Lists.newArrayList();
		for (int i = 0; i < names.length; i++)
			this.values.add(params[offset + i]);
	}

	public String get(final int index) {
		return values.get(index);
	}

	public String getPretty(final int index) {
		final String value = get(index);
		try {
			return PolycraftMod.numFormat.format(Double.parseDouble(value));
		} catch (final NumberFormatException nfe) {
			return value;
		}
	}

	public boolean hasParam(final int index) {
		return values.size() > index;
	}

	public boolean getBoolean(final int index) {
		return Boolean.parseBoolean(get(index));
	}

	public int getInt(final int index) {
		return Integer.parseInt(get(index));
	}

	public float getFloat(final int index) {
		return Float.parseFloat(get(index));
	}

	public double getDouble(final int index) {
		return Double.parseDouble(get(index));
	}
}