package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class Flashcard extends GameIdentifiedConfig {

	public static final ConfigRegistry<Flashcard> registry = new ConfigRegistry<Flashcard>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Flashcard.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new Flashcard(
						PolycraftMod.getVersionNumeric(line[index++]), //version
						line[index++], //gameID
						line[++index] //name
				));
			}
	}

	public Flashcard(final int[] version, final String gameID, final String name) {
		super(version, gameID, name);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
}