package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ElementVessel extends SourcedVesselConfig<Element> {

	public static final SourcedVesselConfigRegistry<Element, ElementVessel> registry = new SourcedVesselConfigRegistry<Element, ElementVessel>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, ElementVessel.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				final int[] version = PolycraftMod.getVersionNumeric(line[0]);
				if (version != null) {
					for (int i = 0; i < 3; i++) {
						registry.register(new ElementVessel(
								version, //version
								line[i + 1], //gameID
								line[i + 5], //name
								Element.registry.get(line[9]), //source
								Vessel.Type.readFromConfig(line[i + 12]) //type
						));
					}
				}
			}
	}

	public ElementVessel(final int[] version, final String gameID, final String name, final Element source, final Vessel.Type vesselType) {
		super(version, gameID, name, source, vesselType);
	}
}