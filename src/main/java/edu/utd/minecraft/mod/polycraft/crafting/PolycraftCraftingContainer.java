package edu.utd.minecraft.mod.polycraft.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;

public abstract class PolycraftCraftingContainer extends Container {

	/**
	 * @return the enumerated container type
	 */
	public abstract PolycraftContainerType getContainerType();

	public PolycraftCraftingContainer(final IInventory inventory, final Iterable<? extends GuiContainerSlot> slots) {
		for (final GuiContainerSlot slot : slots) {
			addInventorySlot(inventory, slot);
		}
		
		//TODO: I think this is where to override default Shift Click Behaviors
		

	}

	public PolycraftCraftingContainer(final IInventory inventory, final PolycraftContainerType containerType) {
		for (final ContainerSlot slot : containerType.getSlotsByIndex()) { // containerType.getSlots()) {
			if (slot instanceof GuiContainerSlot) {
				addInventorySlot(inventory, (GuiContainerSlot) slot);
			}
		}
		
		//Fixed by Walter: added into PolycraftCraftingContainerGeneric
		
		
	}
	
	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer)
    {		
		
		return super.slotClick(par1, par2, par3, par4EntityPlayer);
		
    }
		


	public static class CraftingSlot extends Slot {
		private final PolycraftCraftingContainer container;
		private PolycraftInventory inventory;
		private final GuiContainerSlot guiSlot;

		public CraftingSlot(PolycraftCraftingContainer container, IInventory par1iInventory, GuiContainerSlot guiSlot) {
			super(par1iInventory, guiSlot.getSlotIndex(), guiSlot.getDisplayX(), guiSlot.getDisplayY());
			this.container = container;
			this.guiSlot = guiSlot;

			// If the inventory is a PolycraftInventory, set the property on the slot so we can pass events to the inventory behaviors.
			if (par1iInventory instanceof PolycraftInventory) {
				inventory = (PolycraftInventory) par1iInventory;
			}
		}

		private static String toString(ItemStack stack) {
			if (stack == null) {
				return "null";
			}
			if (stack.getItem() == null) {
				return "null " + stack.stackSize;
			}
			return stack.getItem().getUnlocalizedName() + " : " + stack.stackSize;
		}

		@Override
		public void onSlotChange(ItemStack par1ItemStack, ItemStack par2ItemStack) {
			super.onSlotChange(par1ItemStack, par2ItemStack);
		}

		@Override
		protected void onCrafting(ItemStack par1ItemStack, int par2) {
			super.onCrafting(par1ItemStack, par2);
		}

		@Override
		protected void onCrafting(ItemStack par1ItemStack) {
			super.onCrafting(par1ItemStack);
		}

		//detects when something is clicked on in the inventory, but not the player's inventory
		@Override
		public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack) {
			super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
			if (inventory != null) {
				inventory.onPickupFromSlot(par1EntityPlayer, guiSlot, par2ItemStack);
			}
		}

		@Override
		public boolean isItemValid(ItemStack par1ItemStack) {
			if (super.isItemValid(par1ItemStack))
				return guiSlot.isItemValid(par1ItemStack);
			return false;
		}
		
	}
	

	private void addInventorySlot(final IInventory inventory, final GuiContainerSlot guiSlot) {
		Slot newSlot = new CraftingSlot(this, inventory, guiSlot);
		addSlotToContainer(newSlot);
	}

	protected int addPlayerInventorySlots(InventoryPlayer playerInventory, int offset) {
		final int firstSlotIndex = inventorySlots.size();
		int nextPlayerSlotIndex = 0;
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(playerInventory, nextPlayerSlotIndex++, 8 + i * 18, 58 + offset));
		}
		
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(playerInventory, nextPlayerSlotIndex++, 8 + j * 18, i * 18 + offset));
			}
		

		}
		return firstSlotIndex;
	}
}