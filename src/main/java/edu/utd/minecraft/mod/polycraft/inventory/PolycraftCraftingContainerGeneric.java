package edu.utd.minecraft.mod.polycraft.inventory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.inventory.computer.ComputerInventory;
import edu.utd.minecraft.mod.polycraft.util.Analytics;
import edu.utd.minecraft.mod.polycraft.util.PlayerKnockBackEvent;
import edu.utd.minecraft.mod.polycraft.util.ShiftClickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PolycraftCraftingContainerGeneric<I extends PolycraftInventory> extends PolycraftCraftingContainer {
	protected final I inventory;
	private static final Logger logger = LogManager.getLogger();
	private final int firstPlayerInventorySlot;
	private final int lastPlayerInventorySlot;
	private final int firstPlayerHotbarSlot;
	private final int totalPlayerInventoryAndHotbarSlots = 36;
	private final boolean dropInputsOnClosing;

	public PolycraftCraftingContainerGeneric(final I inventory, final InventoryPlayer playerInventory) {
		this(inventory, playerInventory, 121);
	}

	public PolycraftCraftingContainerGeneric(final I inventory, final InventoryPlayer playerInventory, final int playerInventoryOffset) {
		super(inventory, inventory.getContainerType());
		
		//TODO: this part might need to be cleaned up
		if (inventory.getContainerType().displayPlayerInventory(inventory.getContainerType()))
			this.firstPlayerInventorySlot = addPlayerInventorySlots(playerInventory, playerInventoryOffset);		
		else
			firstPlayerInventorySlot = 0;
		
		this.firstPlayerHotbarSlot = firstPlayerInventorySlot + 27;
		this.lastPlayerInventorySlot = firstPlayerInventorySlot + totalPlayerInventoryAndHotbarSlots;
		this.inventory = inventory;
		this.dropInputsOnClosing = false;
	}

	public PolycraftCraftingContainerGeneric(final I inventory, final InventoryPlayer playerInventory, final int playerInventoryOffset, final boolean dropInputsOnClosing) {
		super(inventory, inventory.getContainerType());
		this.firstPlayerInventorySlot = addPlayerInventorySlots(playerInventory, playerInventoryOffset);
		this.firstPlayerHotbarSlot = firstPlayerInventorySlot + 27;
		this.lastPlayerInventorySlot = firstPlayerInventorySlot + totalPlayerInventoryAndHotbarSlots;
		this.inventory = inventory;
		this.dropInputsOnClosing = dropInputsOnClosing;
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		if (dropInputsOnClosing) {
			if (!par1EntityPlayer.worldObj.isRemote)
			{
				for (final ContainerSlot slot : inventory.getInputSlots()) {
					ItemStack itemstack = inventory.getStackInSlotOnClosing(slot.getSlotIndex());
					if (itemstack != null)
						par1EntityPlayer.dropPlayerItemWithRandomChoice(itemstack, false);
				}
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
		//System.out.println(inventory.containerType+" is being shift right clicked by "+entityPlayer.getDisplayName()+" with slot index: "+slotIndex);
		
		
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			/**
			 * 18 events on Tree Tap in experiment log files as well as in polycraft-analytics log file. 
			 */
			ShiftClickEvent event = new ShiftClickEvent(entityPlayer,itemstack1,inventory.containerType);
			Analytics.onShiftClickEvent(event);
			
				//DO NOT ADD CRAFTING INVENTORIES TO THIS. WILL CAUSE SHIFT CLICKING TO DUPLICATE ITEMS (OR FIX THE BUG)
			if ((inventory.containerType == PolycraftContainerType.PLASTIC_CHEST) ||
					(inventory.containerType == PolycraftContainerType.FURNACE) ||
					(inventory.containerType == PolycraftContainerType.CRAFTING_TABLE) ||
					(inventory.containerType == PolycraftContainerType.POLYCRAFTING_TABLE) ||
					(inventory.containerType == PolycraftContainerType.INDUSTRIAL_OVEN) ||
					(inventory.containerType == PolycraftContainerType.PORTAL_CHEST) ||
					(inventory.containerType == PolycraftContainerType.CONTACT_PRINTER) ||
					(inventory.containerType == PolycraftContainerType.OIL_DERRICK) ||
					(inventory.containerType == PolycraftContainerType.FLOODLIGHT) ||
					(inventory.containerType == PolycraftContainerType.PUMP) ||
					(inventory.containerType == PolycraftContainerType.CONDENSER) ||
					(inventory.containerType == PolycraftContainerType.PRINTING_PRESS) ||
					(inventory.containerType == PolycraftContainerType.SOLAR_ARRAY) ||
					(inventory.containerType == PolycraftContainerType.TREE_TAP) ||
					(inventory.containerType == PolycraftContainerType.SPOTLIGHT) ||
					(inventory.containerType == PolycraftContainerType.GASLAMP) ||
					(inventory.containerType == PolycraftContainerType.TEXT_WALL) ||
					(inventory.containerType == PolycraftContainerType.TIER_CHEST))
			{
				if (slotIndex < firstPlayerInventorySlot)
				{
					if (!this.mergeItemStack(itemstack1, firstPlayerInventorySlot, this.inventorySlots.size(), true))
					{
						return null;
					}
				}
				else if (!this.mergeItemStack(itemstack1, 0, firstPlayerInventorySlot, false))
				{
					return null;
				}

				if (itemstack1.stackSize == 0)
				{
					slot.putStack((ItemStack) null);
				}
				else
				{
					slot.onSlotChanged();
				}
			}
			else
			{
				if (slotIndex == 0)
				{
					if (!this.mergeItemStack(itemstack1, firstPlayerInventorySlot, lastPlayerInventorySlot, true))
					{
						return null;
					}

					slot.onSlotChange(itemstack1, itemstack);
				}
				else if (slotIndex >= firstPlayerInventorySlot && slotIndex < firstPlayerHotbarSlot)
				{
					if (!this.mergeItemStack(itemstack1, firstPlayerHotbarSlot, lastPlayerInventorySlot, false))
					{
						return null;
					}
				}
				else if (slotIndex >= firstPlayerHotbarSlot && slotIndex < lastPlayerInventorySlot)
				{
					if (!this.mergeItemStack(itemstack1, firstPlayerInventorySlot, firstPlayerHotbarSlot, false))
					{
						return null;
					}
				}
				else if (!this.mergeItemStack(itemstack1, firstPlayerInventorySlot, lastPlayerInventorySlot, false))
				{
					return null;
				}

				if (itemstack1.stackSize == 0)
				{
					slot.putStack((ItemStack) null);
				}
				else
				{
					slot.onSlotChanged();
				}

				if (itemstack1.stackSize == itemstack.stackSize)
				{
					return null;
				}

				slot.onPickupFromSlot(entityPlayer, itemstack1);
			}
		}

		return itemstack;
	}

	@Override
	public PolycraftContainerType getContainerType() {
		return inventory.getContainerType();
	}

	public int getFirstPlayerInventorySlot() {
		return firstPlayerInventorySlot;
	}

	public int getLastPlayerInventorySlot() {
		return lastPlayerInventorySlot;
	}

	public int getFirstPlayerHotbarSlot() {
		return firstPlayerHotbarSlot;
	}
}