package edu.utd.minecraft.mod.polycraft.config;

public class ConfigParams {

	private final String[] params;
	private final int offset;

	public ConfigParams(final String[] params, final int offset) {
		this.params = params;
		this.offset = offset;
	}

	public String get(final int index) {
		return params[offset + index];
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