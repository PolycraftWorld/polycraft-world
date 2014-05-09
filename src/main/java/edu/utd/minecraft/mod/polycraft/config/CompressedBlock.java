package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class CompressedBlock extends SourcedConfig<GameIdentifiedConfig> {

	public static final ConfigRegistry<CompressedBlock> registry = new ConfigRegistry<CompressedBlock>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, CompressedBlock.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new CompressedBlock(
						line[0], //gameID
						line[1], //name
						(GameIdentifiedConfig) Config.find(line[2], line[3]) //source
				));
	}

	public CompressedBlock(final String gameID, final String name, final GameIdentifiedConfig source) {
		super(gameID, name, source);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size);
	}
}