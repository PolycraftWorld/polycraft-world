package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftBasicTileEntityContainer;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;

public abstract class PolycraftInventory extends PolycraftBasicTileEntityContainer {

	private static ResourceLocation guiTexture;

	protected static final void register(final PolycraftInventoryBlock inventoryBlock, final ISimpleBlockRenderingHandler renderingHandler) {
		PolycraftMod.registerBlock(inventoryBlock.config, inventoryBlock);
		GameRegistry.registerTileEntity(inventoryBlock.tileEntityClass, inventoryBlock.config.tileEntityGameID);
		RenderingRegistry.registerBlockHandler(renderingHandler.getRenderId(), renderingHandler);
		guiTexture = new ResourceLocation(PolycraftMod.getAssetName(String.format("textures/gui/container/%s.png", PolycraftMod.getFileSafeName(inventoryBlock.config.name))));
	}

	private final PolycraftContainerType containerType;
	private final List<InventoryBehavior> behaviors = Lists.newArrayList();

	public PolycraftInventory(final PolycraftContainerType containerType, final String containerName) {
		super(containerType, containerName);
		Preconditions.checkNotNull(containerType);
		Preconditions.checkNotNull(containerName);
		this.containerType = containerType;
	}

	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory);
	}

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
	// TileEntity Methods
	@Override
	public void updateEntity() {
		for (InventoryBehavior behavior : this.getBehaviors()) {
			Boolean result = behavior.updateEntity(this);
			if (result != null) {
				return;
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		for (InventoryBehavior behavior : this.getBehaviors()) {
			Boolean result = behavior.isItemValidForSlot(var1, var2, this);
			if (result != null) {
				return result;
			}
		}
		ContainerSlot containerSlotByIndex = this.getContainerType().getContainerSlotByIndex(var1);
		if (containerSlotByIndex.getSlotType() == SlotType.INPUT) {
			return true;
		}
		return false;
	}
}
