package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class CompoundVessel extends SourcedVesselConfig<Compound> {

	public static final SourcedVesselConfigRegistry<Compound, CompoundVessel> registry = new SourcedVesselConfigRegistry<Compound, CompoundVessel>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, CompoundVessel.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new CompoundVessel(
						line[0], //gameID
						line[1], //name
						Compound.registry.get(line[2]), //source
						Vessel.Type.valueOf(line[3]) //type
				));
	}

	public CompoundVessel(final String gameID, final String name, final Compound source, final Vessel.Type vesselType) {
		super(gameID, name, source, vesselType);
	}
}