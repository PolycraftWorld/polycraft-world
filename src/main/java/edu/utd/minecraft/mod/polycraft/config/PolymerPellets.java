package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class PolymerPellets extends SourcedVesselConfig<Polymer> {

	public static final SourcedVesselConfigRegistry<Polymer, PolymerPellets> registry = new SourcedVesselConfigRegistry<Polymer, PolymerPellets>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerPellets.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				final int[] version = PolycraftMod.getVersionNumeric(line[0]);
				if (version != null) {
					for (int i = 0; i < 3; i++) {
						final String name = line[i + 5];
						registry.register(new PolymerPellets(
								version, //version
								line[i + 1], //gameID
								name, //name
								Polymer.registry.get(line[9]), //source
								Vessel.Type.readFromConfig(name.substring(0, name.indexOf("("))), //type
								Integer.parseInt(line[10]), // craftingMinHeatIntensity
								Integer.parseInt(line[11]) // craftingMaxHeatIntensity
						));
					}
				}
			}
	}

	public final int craftingMinHeatIntensity;
	public final int craftingMaxHeatIntensity;

	public PolymerPellets(final int[] version, final String gameID, final String name, final Polymer source, final Vessel.Type vesselType, final int craftingMinHeatIntensity, final int craftingMaxHeatIntensity) {
		super(version, gameID, name, source, vesselType);
		this.craftingMinHeatIntensity = craftingMinHeatIntensity;
		this.craftingMaxHeatIntensity = craftingMaxHeatIntensity;
	}
}