package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftBasicTileEntityContainer;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;

public abstract class PolycraftInventory extends PolycraftBasicTileEntityContainer {

	protected static final void register(final PolycraftInventoryBlock inventoryBlock, final ISimpleBlockRenderingHandler renderingHandler) {
		PolycraftRegistry.registerBlock(inventoryBlock.config, inventoryBlock);
		GameRegistry.registerTileEntity(inventoryBlock.tileEntityClass, inventoryBlock.config.tileEntityGameID);
		RenderingRegistry.registerBlockHandler(renderingHandler.getRenderId(), renderingHandler);
	}

	private final ResourceLocation guiTexture;
	protected final PolycraftContainerType containerType;
	private final List<InventoryBehavior> behaviors = Lists.newArrayList();

	public PolycraftInventory(final PolycraftContainerType containerType, final Inventory config) {
		super(containerType, config.gameID);
		Preconditions.checkNotNull(containerType);
		Preconditions.checkNotNull(config);
		this.containerType = containerType;
		this.guiTexture = new ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s.png", PolycraftMod.getFileSafeName(config.name))));
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
	public void onPickupFromSlot(EntityPlayer player, ContainerSlot slot, ItemStack item) {
		for (InventoryBehavior behavior : this.getBehaviors())
			if (behavior.onPickupFromSlot(this, player, slot, item))
				return;
	}

	//////////////////////////////////////////////////////////////////////////////////
	// TileEntity Methods
	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack stack) {
		super.setInventorySlotContents(slotIndex, stack);
		ContainerSlot slot = this.getContainerType().getContainerSlotByIndex(slotIndex);
		for (InventoryBehavior behavior : this.getBehaviors())
			if (behavior.setInventorySlotContents(this, slot, stack))
				return;
	}

	@Override
	public void updateEntity() {
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
}
