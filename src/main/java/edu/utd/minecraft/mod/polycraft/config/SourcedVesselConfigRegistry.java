package edu.utd.minecraft.mod.polycraft.config;

public class SourcedVesselConfigRegistry<S extends Config, C extends SourcedVesselConfig<S>> extends ConfigRegistry<C> {

	public C find(final S source, final Vessel.Type vesselType) {
		for (final C sourcedVesselConfig : values())
			if (sourcedVesselConfig.source == source && sourcedVesselConfig.vesselType == vesselType)
				return sourcedVesselConfig;
		return null;
	}
}
