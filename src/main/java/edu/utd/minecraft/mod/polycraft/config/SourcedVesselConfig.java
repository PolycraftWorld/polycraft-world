package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public abstract class SourcedVesselConfig<S extends Config> extends SourcedConfig<S> {

	public final Vessel.Type vesselType;

	public SourcedVesselConfig(final String gameID, final String name, final S source, final Vessel.Type vesselType) {
		super(gameID, name, source);
		this.vesselType = vesselType;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getItem(this), size);
	}
}
