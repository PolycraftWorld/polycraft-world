package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipeManager;

public abstract class SourcedVesselConfig<S extends Config> extends SourcedConfig<S> {

	public final Vessel.Type vesselType;

	public SourcedVesselConfig(final int[] version, final String gameID, final String name, final S source, final Vessel.Type vesselType) {
		super(version, gameID, name, source);
		this.vesselType = vesselType;
	}

	@Override
	public ItemStack getItemStack(int size) {
		ItemStack newStack = new ItemStack(PolycraftRegistry.getItem(this), size);
		PolycraftRecipeManager.markItemStackAsFromPolycraftRecipe(newStack);
		return newStack;
	}
}
