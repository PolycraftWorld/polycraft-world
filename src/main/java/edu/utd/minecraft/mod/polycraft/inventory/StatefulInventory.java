package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.Map;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;

public abstract class StatefulInventory<S extends StatefulInventoryState> extends PolycraftInventory {

	private final S[] states;
	private final Map<S, Integer> stateValues = Maps.newHashMap();
	protected final int playerInventoryOffset;

	public StatefulInventory(final PolycraftContainerType containerType, final Inventory config, final int playerInventoryOffset, final S[] states) {
		super(containerType, config);
		this.playerInventoryOffset = playerInventoryOffset;
		this.states = states;
		if (states != null)
			for (S state : states)
				setState(state, state.getDefaultValue());
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		if (playerInventoryOffset > 0)
			return new StatefulContainer(this, playerInventory, playerInventoryOffset, states);
		return new StatefulContainer(this, playerInventory, states);
	}

	public int getState(final S state) {
		return stateValues.get(state);
	}

	public int setState(final S state, final int value) {
		stateValues.put(state, value);
		return value;
	}

	public int updateState(final S state, final int update) {
		return setState(state, getState(state) + update);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (states != null)
			for (final S state : states)
				setState(state, tag.getInteger(state.toString()));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (states != null)
			for (final S state : states)
				tag.setInteger(state.toString(), (int) getState(state));
	}
}
