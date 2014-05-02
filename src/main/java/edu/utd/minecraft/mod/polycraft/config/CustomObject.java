package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class CustomObject extends GameIdentifiedConfig {

	public static final ConfigRegistry<CustomObject> registry = new ConfigRegistry<CustomObject>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, CustomObject.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				List<String> params = null;
				if (line.length > 7) {
					params = Lists.newArrayList();
					for (int i = 7; i < line.length; i++)
						params.add(line[i]);
				}

				registry.register(new CustomObject(
						line[0], //gameID
						line[1], //name
						params//params
				));
			}
	}

	public final List<String> params;

	public CustomObject(final String gameID, final String name, final List<String> params) {
		super(gameID, name);
		this.params = params;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getItem(this), size);
	}

	public String getParamString(final int index) {
		return params.get(index);
	}

	public int getParamInteger(final int index) {
		return Integer.parseInt(params.get(index));
	}

	public float getParamFloat(final int index) {
		return Float.parseFloat(params.get(index));
	}
}