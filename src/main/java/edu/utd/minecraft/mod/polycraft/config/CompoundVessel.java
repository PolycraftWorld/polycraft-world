package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class CompoundVessel extends SourcedVesselConfig<Compound> {

	public static final SourcedVesselConfigRegistry<Compound, CompoundVessel> registry = new SourcedVesselConfigRegistry<Compound, CompoundVessel>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, CompoundVessel.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				final int[] version = PolycraftMod.getVersionNumeric(line[0]);
				if (version != null) {
					for (int i = 0; i < 3; i++) {
						registry.register(new CompoundVessel(
								version, //version
								line[i + 1], //gameID
								line[i + 5], //name
								Compound.registry.get(line[9]), //source
								Vessel.Type.readFromConfig(line[i + 11]) //type
						));
					}
				}
			}
	}

	public CompoundVessel(final int[] version, final String gameID, final String name, final Compound source, final Vessel.Type vesselType) {
		super(version, gameID, name, source, vesselType);
	}
}