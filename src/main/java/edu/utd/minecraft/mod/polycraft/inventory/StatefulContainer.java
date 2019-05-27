package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.Map;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import com.google.common.collect.Maps;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StatefulContainer<S extends StatefulInventoryState, I extends StatefulInventory> extends PolycraftCraftingContainerGeneric<I> {

	private final Map<Integer, S> statesByIdentifier = Maps.newHashMap();
	private final Map<S, Integer> previousStateValues = Maps.newHashMap();

	public StatefulContainer(final I inventory, final InventoryPlayer playerInventory, final S[] states) {
		super(inventory, playerInventory);
		initStates(states);
	}

	public StatefulContainer(final I inventory, final InventoryPlayer playerInventory, final int playerInventoryOffset, final S[] states) {
		super(inventory, playerInventory, playerInventoryOffset);
		initStates(states);
	}

	private void initStates(final S[] states) {
		for (final S state : states) {
			previousStateValues.put(state, 0);
			statesByIdentifier.put(state.getIdentifier(), state);
		}
	}

	@Override
	public void onCraftGuiOpened(ICrafting crafting) {
		super.onCraftGuiOpened(crafting);
		for (final S state : statesByIdentifier.values())
			crafting.sendProgressBarUpdate(this, state.getIdentifier(), inventory.getState(state));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);
			for (final S state : statesByIdentifier.values())
				if (previousStateValues.get(state) != inventory.getState(state))
					icrafting.sendProgressBarUpdate(this, state.getIdentifier(), inventory.getState(state));
		}

		for (final S state : statesByIdentifier.values())
			previousStateValues.put(state, inventory.getState(state));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int stateIdentifier, int amount) {
		inventory.setState(statesByIdentifier.get(stateIdentifier), amount);
	}
}