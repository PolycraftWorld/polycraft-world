package edu.utd.minecraft.mod.polycraft.inventory.computer;

import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ComputerContainer extends /* Container */PolycraftCraftingContainerGeneric<ComputerInventory> {

	private static ComputerInventory computer;

	public ComputerContainer(InventoryPlayer playerInventory) {
		this(playerInventory, computer);
	}

	public ComputerContainer(InventoryPlayer invPlayer, ComputerInventory computer) {

		super(computer, invPlayer);
		this.computer = computer;

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return computer.isUseableByPlayer(player);
	}

	@Override
	public PolycraftContainerType getContainerType() {
		return PolycraftContainerType.COMPUTER;
	}

}
