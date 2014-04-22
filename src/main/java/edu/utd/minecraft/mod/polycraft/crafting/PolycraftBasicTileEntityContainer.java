package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

// Implementation of a basic tile entity container.
public abstract class PolycraftBasicTileEntityContainer extends TileEntity implements PolycraftTileEntityContainer, ISidedInventory {
	private final Collection<ContainerSlot> inputSlots;
	private final Collection<ContainerSlot> outputSlots;
	private final Collection<ContainerSlot> miscSlots;
	private final int totalSlots;

	private final Map<Integer, ContainerSlot> slotToIndexMap = Maps.newHashMap();
	// Maintain the current set of inputs so it doesn't need to be recomputed every frame.
	private final Set<RecipeComponent> inputMaterialSet = Sets.newHashSet();
	private final Set<Integer> inputSlotSet = Sets.newHashSet();
	private final RecipeComponent[] inputArray;

	private String inventoryName;
	private final PolycraftContainerType containerType;
	private final String containerName;

	public PolycraftBasicTileEntityContainer(PolycraftContainerType containerType, String containerName) {
		this.containerType = containerType;
		this.containerName = containerName;
		inputSlots = ImmutableList.copyOf(containerType.getSlots(SlotType.INPUT));
		outputSlots = ImmutableList.copyOf(containerType.getSlots(SlotType.INPUT));
		miscSlots = ImmutableList.copyOf(containerType.getSlots(SlotType.INPUT));

		for (final ContainerSlot input : inputSlots) {
			slotToIndexMap.put(input.getSlotIndex(), input);
			inputSlotSet.add(input.getSlotIndex());
		}
		for (final ContainerSlot output : outputSlots) {
			slotToIndexMap.put(output.getSlotIndex(), output);
		}
		for (final ContainerSlot misc : miscSlots) {
			slotToIndexMap.put(misc.getSlotIndex(), misc);
		}

		totalSlots = inputSlots.size() + outputSlots.size() + miscSlots.size();
		inputArray = new RecipeComponent[totalSlots];
	}

	/**
	 * @return the name of the inventory.
	 */
	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName()
				? this.inventoryName
			    : "container." + PolycraftMod.getRegistryName(PolycraftMod.RegistryNamespace.Inventory, containerName);
	}

	/**
	 * Gets the input slots available to this container.
	 */
	@Override
	public Collection<ContainerSlot> getInputSlots() {
		return this.inputSlots;
	}

	/**
	 * Gets the output slots available to this container.
	 */
	@Override
	public Collection<ContainerSlot> getOutputSlots() {
		return this.outputSlots;
	}

	/**
	 * Get the material set currently inside the container.
	 */
	@Override
	public Set<RecipeComponent> getMaterials() {
		return inputMaterialSet;
	}

	/**
	 * Clears the container of all items. Items are destroyed.
	 */
	public void clear() {
		for (int i = 0; i < totalSlots; i++) {
			inputArray[i] = null;
		}
		inputMaterialSet.clear();
	}

	/**
	 * Turn one item from the container source stack into the appropriate processed items in the result stack.
	 */
	public void craftItems() {
		Set<RecipeComponent> inputs = getMaterials();
		final PolycraftRecipe recipe = PolycraftMod.recipeManager.findRecipe(containerType, inputs);
		if (recipe != null) {
			recipe.process(inputs, this);
		}
	}

	/**
	 * Returns true if an item can be processed based on the item at the inputs.
	 */
	public boolean canProcess() {
		// Check that the inputs are valid
		Set<RecipeComponent> materials = getMaterials();
		final PolycraftRecipe recipe = PolycraftMod.recipeManager.findRecipe(containerType, materials);
		if (recipe == null) {
			return false;
		}

		for (final RecipeComponent output : recipe.getOutputs()) {
			final ItemStack desiredResult = output.itemStack;
			final ItemStack currentResult = getStackInSlot(output.slot);
			if (currentResult != null) {
				if (!currentResult.isItemEqual(desiredResult)) {
					// Result is blocked by a different item
					return false;
				}
				int newTotal = currentResult.stackSize + desiredResult.stackSize;
				if (newTotal > getInventoryStackLimit() || newTotal > desiredResult.getMaxStackSize()) {
					// Can't add anymore here
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Gets the itemstack at the specified slot, or null if no item is in the specified slot.
	 */
	@Override
	public ItemStack getStackInSlot(final ContainerSlot slot) {
		return getStackInSlot(slot.getSlotIndex());
	}

	/**
	 * @return True if the specified slot is holding an item.
	 */
	public boolean slotHasItem(final ContainerSlot slot) {
		return getStackInSlot(slot.getSlotIndex()) != null;
	}

	/**
	 * Clears the itemstack in the specified slot. Item is destroyed.
	 */
	@Override
	public void clearSlotContents(final ContainerSlot slot) {
		setInventorySlotContents(slot.getSlotIndex(), null);
	}

	/**
	 * Sets the itemstack in the specified slot. Previous item is destroyed.
	 */
	@Override
	public void setStackInSlot(final ContainerSlot slot, final ItemStack stack) {
		setInventorySlotContents(slot.getSlotIndex(), stack);
	}

	/**
	 * Returns if the inventory is named
	 */
	@Override
	public boolean hasCustomInventoryName() {
		return this.inventoryName != null && this.inventoryName.length() > 0;
	}

	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return this.totalSlots;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if (slotIndex >= inputArray.length) {
			return null;
		}
		if (inputArray[slotIndex] == null) {
			return null;
		}
		return inputArray[slotIndex].itemStack;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack stack) {
		if (slotIndex < 0 || slotIndex >= totalSlots) {
			return;
		}

		boolean isInput = inputSlotSet.contains(slotIndex);
		if (isInput) {
			// If setting an input element, update the set of inputs
			RecipeComponent oldInput = inputArray[slotIndex];
			if (oldInput != null) {
				inputMaterialSet.remove(oldInput);
			}
		}

		if (stack == null) {
			inputArray[slotIndex] = null;
		} else {
			RecipeComponent newInput = new RecipeComponent(slotIndex, stack);
			inputArray[slotIndex] = newInput;
			if (isInput) {
				inputMaterialSet.add(newInput);
			}
		}
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int slot, int count) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) {
			return null;
		}

		if (count >= stack.stackSize) {
			setInventorySlotContents(slot, null);
			return stack;
		}

		stack = stack.splitStack(count);
		return stack;
	}

	/**
	 * Returns the maximum stack size for a inventory slot.
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this
				? false
			    : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	/**
	 * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this block.
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int par1) {
		return null;
	}

	/**
	 * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item, side
	 */
	@Override
	public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3) {
		return this.isItemValidForSlot(par1, par2ItemStack);
	}

	/**
	 * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item, side
	 */
	@Override
	public boolean canExtractItem(int par1, ItemStack par2ItemStack, int par3) {
		return par3 != 0 || par1 != 1;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem - like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (this.inputArray[slot] != null) {
			ItemStack itemstack = this.inputArray[slot].itemStack;
			setInventorySlotContents(slot, null);
			return itemstack;
		}
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList nbttaglist = tag.getTagList("Items", 10);
		clear();

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");
			setInventorySlotContents(b0, ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}
		if (tag.hasKey("CustomName", 8)) {
			this.inventoryName = tag.getString("CustomName");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < totalSlots; ++i) {
			if (this.inputArray[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.inputArray[i].itemStack.writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		tag.setTag("Items", nbttaglist);
		if (this.hasCustomInventoryName()) {
			tag.setString("CustomName", this.inventoryName);
		}
	}

}
