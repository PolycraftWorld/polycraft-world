package edu.utd.minecraft.mod.polycraft.inventory.computer;

import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ComputerContainer extends /*Container */PolycraftCraftingContainer{
	
	private ComputerInventory computer;
	
	
	public ComputerContainer(InventoryPlayer invPlayer, PolycraftContainerType computer) {

		super(invPlayer, computer);
//		this.computer=computer;
		

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return computer.isUseableByPlayer(player);
	}

	@Override
	public PolycraftContainerType getContainerType() {
		// TODO Auto-generated method stub
		return PolycraftContainerType.COMPUTER;
	}


}
