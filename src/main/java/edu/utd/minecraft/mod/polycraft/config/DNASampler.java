package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class DNASampler extends GameIdentifiedConfig {

	public static final ConfigRegistry<DNASampler> registry = new ConfigRegistry<DNASampler>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, DNASampler.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new DNASampler(
						PolycraftMod.getVersionNumeric(line[index++]), //version
						line[index++], //gameID sampler,
						line[index++], //name 
						line[index++], //source animal
						Integer.parseInt(line[index++]),//maxStackSize
						Integer.parseInt(line[index++]) //Level
				));
			}
	}

	public int maxStackSize, level;
	public String sourceEntity;

	public DNASampler(final int[] version, final String gameID, final String name, final String sourceEntity,
			final int maxStackSize, final int level) {
		super(version, gameID, name);
		this.maxStackSize = maxStackSize;
		this.sourceEntity = sourceEntity;
		this.level = level;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
}