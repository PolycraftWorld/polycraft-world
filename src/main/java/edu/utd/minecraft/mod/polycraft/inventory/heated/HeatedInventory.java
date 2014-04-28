package edu.utd.minecraft.mod.polycraft.inventory.heated;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;

public abstract class HeatedInventory extends PolycraftInventory {

	private final int heatSourceSlotIndex;

	//The number of ticks that the inventory will keep being heated
	public int heatTime;

	//How intense the heat is
	public int heatIntensity;

	//The number of ticks that a fresh copy of the currently-fueled item would keep the inventory heated for
	public int currentItemHeatTime;
	//The number of ticks that the current item has been processing
	public int processingTime;

	public HeatedInventory(final PolycraftContainerType containerType, final Inventory config, final int heatSourceSlotIndex) {
		super(containerType, config);
		this.heatSourceSlotIndex = heatSourceSlotIndex;
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new HeatedContainer(this, playerInventory);
	}

	@Override
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return getGuiHeated(playerInventory);
	}

	protected abstract HeatedGui getGuiHeated(final InventoryPlayer playerInventory);

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.heatTime = tag.getShort("HeatTime");
		this.heatIntensity = tag.getShort("HeatIntensity");
		this.processingTime = tag.getShort("ProcessingTime");
		this.currentItemHeatTime = getHeatDuration(getStackInSlot(heatSourceSlotIndex));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setShort("HeatTime", (short) this.heatTime);
		tag.setShort("HeatIntensity", (short) this.heatIntensity);
		tag.setShort("ProcessingTime", (short) this.processingTime);
	}

	public boolean isHeated() {
		return this.heatTime > 0;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how close the current item is to being completely cooked
	 */
	@SideOnly(Side.CLIENT)
	public int getProcessingProgressScaled(int scale) {
		return this.processingTime * scale / 200;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel item, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	@SideOnly(Side.CLIENT)
	public int getHeatTimeRemainingScaled(int scale) {
		if (this.currentItemHeatTime == 0) {
			this.currentItemHeatTime = 200;
		}
		return heatTime * scale / currentItemHeatTime;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		boolean isDirty = false;

		if (isHeated())
			--heatTime;

		if (!worldObj.isRemote) {
			//TODO canProcess needs to take into account mold damage
			if (heatTime == 0 && canProcess() && getStackInSlot(heatSourceSlotIndex) != null) {
				ItemStack heatSourceStack = getStackInSlot(heatSourceSlotIndex);
				currentItemHeatTime = heatTime = getHeatDuration(heatSourceStack);
				if (isHeated()) {
					heatIntensity = getHeatIntensity(heatSourceStack);
					isDirty = true;
					if (heatSourceStack != null) {
						--heatSourceStack.stackSize;
						if (heatSourceStack.stackSize == 0)
							setInventorySlotContents(heatSourceSlotIndex, heatSourceStack.getItem().getContainerItem(heatSourceStack));
					}
				}
			}

			if (isHeated() && canProcess()) {
				++processingTime;

				//TODO load how long it takes to process the mold from Mold config
				if (processingTime == 200) {
					processingTime = 0;
					//TODO need to override this default behavior to damage the mold instead of destroying it
					craftItems();
					isDirty = true;
				}
			} else
				processingTime = 0;
		}

		if (isDirty)
			markDirty();
	}

	/**
	 * Returns the number of ticks that the supplied fuel item will keep the heat coming, or 0 if the item isn't fuel
	 */
	private static int getHeatDuration(final ItemStack itemStack) {
		if (itemStack == null)
			return 0;
		return PolycraftMod.convertSecondsToGameTicks(Fuel.getHeatDurationSeconds(itemStack.getItem()));
	}

	private static int getHeatIntensity(ItemStack itemStack) {
		if (itemStack == null)
			return 0;
		return Fuel.getHeatIntensity(itemStack.getItem());
	}
}
