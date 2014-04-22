package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class PolymerBlock extends SourcedConfig<Polymer> {

	public static final ConfigRegistry<PolymerBlock> registry = new ConfigRegistry<PolymerBlock>();

	public static void registerFromResource(final String directory, final String extension, final String delimeter) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerBlock.class.getSimpleName().toLowerCase(), extension, delimeter))
			if (line.length > 0)
				registry.register(new PolymerBlock(
						line[0], //gameID
						line[1], //name
						(Polymer) Config.find(line[2], line[3]), //source
						Integer.parseInt(line[4]) //bounceHeight
				));
	}

	public final int bounceHeight;

	public PolymerBlock(final String gameID, final String name, final Polymer source, final int bounceHeight) {
		super(gameID, name, source);
		this.bounceHeight = bounceHeight;
	}
}