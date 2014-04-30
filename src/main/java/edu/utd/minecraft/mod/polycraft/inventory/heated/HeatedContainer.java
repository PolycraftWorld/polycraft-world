package edu.utd.minecraft.mod.polycraft.inventory.heated;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory.State;

public class HeatedContainer<I extends HeatedInventory> extends PolycraftCraftingContainerGeneric<I> {

	private final Map<State, Integer> previousStateValues = Maps.newHashMap();

	public HeatedContainer(final I inventory, final InventoryPlayer playerInventory) {
		super(inventory, playerInventory);
		for (final State state : State.values())
			previousStateValues.put(state, 0);
	}

	public HeatedContainer(final I inventory, final InventoryPlayer playerInventory, final int playerInventoryOffset) {
		super(inventory, playerInventory, playerInventoryOffset);
		for (final State state : State.values())
			previousStateValues.put(state, 0);
	}

	@Override
	public void addCraftingToCrafters(ICrafting crafting) {
		super.addCraftingToCrafters(crafting);
		for (final State state : State.values())
			crafting.sendProgressBarUpdate(this, state.ordinal(), inventory.getState(state));
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			for (final State state : State.values())
				if (previousStateValues.get(state) != inventory.getState(state))
					icrafting.sendProgressBarUpdate(this, state.ordinal(), inventory.getState(state));
		}

		for (final State state : State.values())
			previousStateValues.put(state, inventory.getState(state));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int stateOrdinal, int amount) {
		inventory.setState(State.values()[stateOrdinal], amount);
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return inventory.isUseableByPlayer(par1EntityPlayer);
	}
}