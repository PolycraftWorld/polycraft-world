package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

// Implementation of a basic tile entity container.
public abstract class PolycraftBasicTileEntityContainer extends TileEntity implements PolycraftTileEntityContainer, IInventory {
	protected final List<ContainerSlot> inputSlots;
	protected final Collection<ContainerSlot> outputSlots;
	protected final Collection<ContainerSlot> miscSlots;
	protected final int totalSlots;

	protected final Map<Integer, ContainerSlot> slotToIndexMap = Maps.newHashMap();
	// Maintain the current set of inputs so it doesn't need to be recomputed every frame.
	private final Set<RecipeComponent> inputMaterialSet = Sets.newHashSet();
	private final Set<Integer> inputSlotSet = Sets.newHashSet();
	private final RecipeComponent[] inputArray;

	private final String containerName;
	private String inventoryName;
	private final PolycraftContainerType containerType;

	public PolycraftBasicTileEntityContainer(final PolycraftContainerType containerType, final String containerName) {
		this.containerType = containerType;
		this.containerName = containerName;
		inputSlots = ImmutableList.copyOf(containerType.getSlots(SlotType.INPUT));
		outputSlots = ImmutableList.copyOf(containerType.getSlots(SlotType.OUTPUT));
		miscSlots = ImmutableList.copyOf(containerType.getSlots(SlotType.MISC));

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


	public int[] getSlotsForFace(EnumFacing facing) {
		return new int[]{1};
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return AxisAlignedBB.fromBounds(pos.getX() - 5, pos.getY(), pos.getZ() - 5, pos.getX() + 5, pos.getY() + 8, pos.getZ() + 5);
	}

	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 16384.0D;
	}

	/**
	 * @return the name of the inventory.
	 */
	@Override
	public String getName() {
		return this.hasCustomName() ? this.inventoryName : "container." + containerName;
	}

	/**
	 * Gets the input slots available to this container.
	 */
	@Override
	public List<ContainerSlot> getInputSlots() {
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
	public void craftItems(boolean createOutputs) {
		Set<RecipeComponent> inputs = getMaterials();
		final PolycraftRecipe recipe = PolycraftMod.recipeManagerRuntime.findRecipe(containerType, inputs);
		if (recipe != null) {
			recipe.process(inputs, this, createOutputs);
		}
	}

	/**
	 * Returns true if an item can be processed based on the item at the inputs.
	 */
	public boolean canProcess() {
		// Check that the inputs are valid
		Set<RecipeComponent> materials = getMaterials();
		final PolycraftRecipe recipe = PolycraftMod.recipeManagerRuntime.findRecipe(containerType, materials);
		if (recipe == null) {
			return false;
		}

		for (final RecipeComponent output : recipe.getOutputs(this)) {
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
	
	public Map<Integer, ContainerSlot> getSlotToIndexMap() {
		return slotToIndexMap;
	}

	/**
	 * Returns if the inventory is named
	 */
	@Override
	public boolean hasCustomName() {
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

		// TODO: Needed?
		this.markDirty();

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
		} else if (stack.stackSize == 0) {
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
		return this.worldObj.getTileEntity(this.pos) != this
				? false
				: par1EntityPlayer.getDistanceSq(this.pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem - like when you close a workbench GUI.
	 */
	//@Override no longer overriden in 1.8
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
		if (this.hasCustomName()) {
			tag.setString("CustomName", this.inventoryName);
		}
	}

}
