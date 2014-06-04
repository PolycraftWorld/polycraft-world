package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public abstract class SourcedVesselConfig<S extends Config> extends SourcedConfig<S> {

	public final Vessel.Type vesselType;

	public SourcedVesselConfig(final int[] version, final String gameID, final String name, final S source, final Vessel.Type vesselType) {
		super(version, gameID, name, source);
		this.vesselType = vesselType;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
}
