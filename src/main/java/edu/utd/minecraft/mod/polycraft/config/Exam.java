package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class Exam extends GameIdentifiedConfig {

	public static final ConfigRegistry<Exam> registry = new ConfigRegistry<Exam>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Exam.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new Exam(
						PolycraftMod.getVersionNumeric(line[index++]), //version
						line[index++], //gameID
						line[++index] //name
				));
			}
	}

	public Exam(final int[] version, final String gameID, final String name) {
		super(version, gameID, name);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
}