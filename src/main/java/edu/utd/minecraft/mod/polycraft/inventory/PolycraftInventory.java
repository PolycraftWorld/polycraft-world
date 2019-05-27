package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.ElementVessel;
import edu.utd.minecraft.mod.polycraft.config.GameIdentifiedConfig;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.Vessel;
import edu.utd.minecraft.mod.polycraft.config.WaferItem;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftBasicTileEntityContainer;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipeManager;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
import edu.utd.minecraft.mod.polycraft.item.ItemWafer;
import edu.utd.minecraft.mod.polycraft.util.Analytics;

public abstract class PolycraftInventory extends PolycraftBasicTileEntityContainer implements ITickable {

	protected static final void register(final PolycraftInventoryBlock inventoryBlock) {
		PolycraftRegistry.registerBlock(inventoryBlock.config, inventoryBlock);
		GameRegistry.registerTileEntity(inventoryBlock.tileEntityClass, inventoryBlock.config.tileEntityGameID);
	}

	private final ResourceLocation guiTexture;
	protected final PolycraftContainerType containerType;
	private final List<InventoryBehavior> behaviors = Lists.newArrayList();

	public PolycraftInventory(final PolycraftContainerType containerType, final Inventory config) {
		super(containerType, config.gameID);
		Preconditions.checkNotNull(containerType);
		Preconditions.checkNotNull(config);
		this.containerType = containerType;
		this.guiTexture = new ResourceLocation(PolycraftMod.getAssetNameString(String.format("textures/gui/container/%s.png", PolycraftMod.getFileSafeName(config.name))));
	}

	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory);
	}

	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PolycraftInventoryGui(this, playerInventory);
	}

	public ResourceLocation getGuiResourceLocation() {
		return guiTexture;
	}

	/**
	 * @param behavior
	 *            The behavior to add to this container.
	 */
	protected void addBehavior(final InventoryBehavior behavior) {
		Preconditions.checkNotNull(behavior);
		this.behaviors.add(behavior);
	}

	/**
	 * @return iterable of inventory behaviors
	 */
	public Iterable<InventoryBehavior> getBehaviors() {
		return this.behaviors;
	}

	/**
	 * @return the container type this inventory belongs to.
	 */
	public PolycraftContainerType getContainerType() {
		return this.containerType;
	}

	//////////////////////////////////////////////////////////////////////////////////
	// SlotCrafting methods
	public void onPickupFromSlot(EntityPlayer player, ContainerSlot slot, ItemStack itemStack) {

		Analytics.INSTANCE.onItemPolycrafted(player, itemStack, this);
		itemStack.getItem().onCreated(itemStack, this.worldObj, player);
		for (InventoryBehavior behavior : this.getBehaviors())
			if (behavior.onPickupFromSlot(this, player, slot, itemStack))
				return;
	}

	//////////////////////////////////////////////////////////////////////////////////
	// TileEntity Methods
	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack stack) {
		if (stack != null && stack.getItem() instanceof ItemWafer)
		{
			if (((ItemWafer) stack.getItem()).waferItem.mustBeInCleanroom)
			{

				if (!this.worldObj.isRemote && !PolycraftCleanroom.isLocationClean(this.worldObj, this.pos.getX(), this.pos.getY(), this.pos.getZ()))
				{
					stack = WaferItem.registry.get("Silicon Wafer (Ruined)").getItemStack();
				}

			}
		}
		super.setInventorySlotContents(slotIndex, stack);
		ContainerSlot slot = this.getContainerType().getContainerSlotByIndex(slotIndex);
		for (InventoryBehavior behavior : this.getBehaviors())
			if (behavior.setInventorySlotContents(this, slot, stack))
				return;
	}

	@Override
	public void update() {
		for (InventoryBehavior behavior : this.getBehaviors())
			if (behavior.updateEntity(this, this.worldObj))
				return;
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		for (InventoryBehavior behavior : this.getBehaviors()) {
			Boolean result = behavior.isItemValidForSlot(this, var1, var2);
			if (result != null) {
				return result;
			}
		}
		ContainerSlot containerSlotByIndex = this.getContainerType().getContainerSlotByIndex(var1);
		if (containerSlotByIndex.getSlotType() == SlotType.INPUT || containerSlotByIndex.getSlotType() == SlotType.MISC) {
			return true;
		}
		return false;
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	public ItemStack removeStackFromSlot(int index)
	{
		if (this.slotToIndexMap.get(index) != null)
		{
			if(slotHasItem(this.slotToIndexMap.get(index))){
				ItemStack itemStack = this.getStackInSlot(index);
				this.clearSlotContents(this.slotToIndexMap.get(index));
				return itemStack;
			}
		}
		return null;
	}

	protected static boolean downcycle(IInventory iinventory, ItemVessel newVessel, int slotIndex, int slotIndexMin, int slotIndexMax)
	{

		if (newVessel.config.vesselType == Vessel.Type.Vial)
			return false;
		if (newVessel.config.vesselType == Vessel.Type.Beaker)
		{

			for (int y = slotIndexMin; y <= slotIndexMax; y++)
			{
				if (y != slotIndex)
				{
					if (iinventory.getStackInSlot(y) == null)
					{

						GameIdentifiedConfig smallerConfig = null;
						ItemVessel smallerItem = null;

						if (newVessel.config instanceof ElementVessel)
							smallerConfig = ElementVessel.registry.find(((ElementVessel) newVessel.config).source, newVessel.config.vesselType.smallerType);
						if (newVessel.config instanceof CompoundVessel)
							smallerConfig = CompoundVessel.registry.find(((CompoundVessel) newVessel.config).source, newVessel.config.vesselType.smallerType);
						else if (newVessel.config instanceof PolymerPellets)
							smallerConfig = PolymerPellets.registry.find(((PolymerPellets) newVessel.config).source, newVessel.config.vesselType.smallerType);
						if (smallerConfig != null)
							smallerItem = (ItemVessel) PolycraftRegistry.getItem(smallerConfig);

						ItemStack smallStack = new ItemStack(smallerItem, 63);
						ItemStack largerStack = iinventory.getStackInSlot(slotIndex).copy();
						PolycraftRecipeManager.markItemStackAsFromPolycraftRecipe(smallStack);
						PolycraftRecipeManager.markItemStackAsFromPolycraftRecipe(largerStack);
						largerStack.stackSize -= 1;
						if (largerStack.stackSize > 0)
							iinventory.setInventorySlotContents(slotIndex, largerStack);
						else
							iinventory.setInventorySlotContents(slotIndex, null);
						iinventory.setInventorySlotContents(y, smallStack);
						return true;

					}

				}
			}

		}
		//dont combine these yet, so we look first for beakers
		if (newVessel.config.vesselType == Vessel.Type.Drum)
		{

			for (int y = slotIndexMin; y <= slotIndexMax; y++)
			{
				if (y != slotIndex)
				{
					if (iinventory.getStackInSlot(y) == null)
					{
						if (iinventory.getStackInSlot(slotIndex) != null)
						{

							GameIdentifiedConfig smallerConfig = null;
							ItemVessel smallerItem = null;

							if (newVessel.config instanceof ElementVessel)
								smallerConfig = ElementVessel.registry.find(((ElementVessel) newVessel.config).source, newVessel.config.vesselType.smallerType);
							if (newVessel.config instanceof CompoundVessel)
								smallerConfig = CompoundVessel.registry.find(((CompoundVessel) newVessel.config).source, newVessel.config.vesselType.smallerType);
							else if (newVessel.config instanceof PolymerPellets)
								smallerConfig = PolymerPellets.registry.find(((PolymerPellets) newVessel.config).source, newVessel.config.vesselType.smallerType);
							if (smallerConfig != null)
								smallerItem = (ItemVessel) PolycraftRegistry.getItem(smallerConfig);

							ItemStack smallStack = new ItemStack(smallerItem, 64);

							ItemStack largerStack = iinventory.getStackInSlot(slotIndex).copy();
							PolycraftRecipeManager.markItemStackAsFromPolycraftRecipe(smallStack);
							PolycraftRecipeManager.markItemStackAsFromPolycraftRecipe(largerStack);
							largerStack.stackSize -= 1;
							if (largerStack.stackSize > 0)
								iinventory.setInventorySlotContents(slotIndex, largerStack);
							else
								iinventory.setInventorySlotContents(slotIndex, null);
							iinventory.setInventorySlotContents(y, smallStack);
							return downcycle(iinventory, ((ItemVessel) smallStack.getItem()), y, slotIndexMin, slotIndexMax);
						}
						return false;

					}

				}
			}

		}

		return false;

	}

	//new methods in 1.8


	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		return;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public IChatComponent getDisplayName() {
		return null;
	}
}
