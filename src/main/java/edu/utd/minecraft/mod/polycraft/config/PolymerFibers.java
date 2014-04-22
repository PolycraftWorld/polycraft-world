package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class PolymerFibers extends SourcedConfig<Polymer> {

	public static final ConfigRegistry<PolymerFibers> registry = new ConfigRegistry<PolymerFibers>();

	public static void registerFromResource(final String directory, final String extension, final String delimeter) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerFibers.class.getSimpleName().toLowerCase(), extension, delimeter))
			if (line.length > 0)
				registry.register(new PolymerFibers(
						line[0], //gameID
						line[1], //name
						(Polymer) Config.find(line[2], line[3]) //source
				));
	}

	public PolymerFibers(final String gameID, final String name, final Polymer source) {
		super(gameID, name, source);
	}
}