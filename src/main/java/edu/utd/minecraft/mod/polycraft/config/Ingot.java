package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class Ingot extends SourcedConfig {

	public static final ConfigRegistry<Ingot> registry = new ConfigRegistry<Ingot>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Ingot.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new Ingot(
						PolycraftMod.getVersionNumeric(line[index++]),
						line[index++], //gameID
						line[index++], //name
						Config.find(line[index++], line[index++]), //source
						Integer.parseInt(line[index++]) //moldDamagePerUse
				));
			}
	}

	public final int moldDamagePerUse;

	public Ingot(final int[] version, final String gameID, final String name, final Config source, final int moldDamagePerUse) {
		super(version, gameID, name, source);
		this.moldDamagePerUse = moldDamagePerUse;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Mold Damage Per Use");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(
				PolycraftMod.numFormat.format(moldDamagePerUse));
	}
}
