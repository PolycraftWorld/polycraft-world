package edu.utd.minecraft.mod.polycraft.config;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class PolymerPellets extends SourcedVesselConfig<Polymer> {

	public static final SourcedVesselConfigRegistry<Polymer, PolymerPellets> registry = new SourcedVesselConfigRegistry<Polymer, PolymerPellets>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerPellets.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new PolymerPellets(
						line[0], //gameID
						line[1], //name
						Polymer.registry.get(line[2]), //source
						Vessel.Type.valueOf(line[3]), //type
						Integer.parseInt(line[4]), //craftingMinHeatIntensity
						Integer.parseInt(line[5]) //craftingMaxHeatIntensity
				));
	}

	public final int craftingMinHeatIntensity;
	public final int craftingMaxHeatIntensity;

	public PolymerPellets(final String gameID, final String name, final Polymer source, final Vessel.Type vesselType, final int craftingMinHeatIntensity, final int craftingMaxHeatIntensity) {
		super(gameID, name, source, vesselType);
		this.craftingMinHeatIntensity = craftingMinHeatIntensity;
		this.craftingMaxHeatIntensity = craftingMaxHeatIntensity;
	}
}