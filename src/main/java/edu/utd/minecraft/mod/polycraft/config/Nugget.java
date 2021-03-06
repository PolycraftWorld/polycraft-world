package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class Nugget extends SourcedConfig {

	public static final ConfigRegistry<Nugget> registry = new ConfigRegistry<Nugget>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Nugget.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new Nugget(
						PolycraftMod.getVersionNumeric(line[index++]),
						line[index++], //gameID
						line[index++], //name
						Ingot.registry.get(line[index++]), //source
						Integer.parseInt(line[index++]) //moldDamagePerUse
				));
			}
	}

	public final int maskDamagePerUse;

	public Nugget(final int[] version, final String gameID, final String name, final Ingot source, final int maskDamagePerUse) {
		super(version, gameID, name, source);
		this.maskDamagePerUse = maskDamagePerUse;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Mask Damage Per Use");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(
				PolycraftMod.numFormat.format(maskDamagePerUse));
	}
}
