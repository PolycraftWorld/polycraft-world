package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;

public abstract class GameIdentifiedConfig<S extends Config> extends Config {

	public final String gameID;

	public GameIdentifiedConfig(final String gameID, final String name) {
		super(name);
		this.gameID = gameID;
	}

	public GameIdentifiedConfig(final String gameID, final String name, final String[] params, final int paramsOffset) {
		super(name, params, paramsOffset);
		this.gameID = gameID;
	}

	public ItemStack getItemStack() {
		return getItemStack(1);
	}

	public abstract ItemStack getItemStack(final int size);
}
