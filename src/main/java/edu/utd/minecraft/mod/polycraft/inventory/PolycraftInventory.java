package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockCollision;
import edu.utd.minecraft.mod.polycraft.block.BlockCompressed;
import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.block.BlockPipe;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerSlab;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerStairs;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerWall;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.WaferItem;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftBasicTileEntityContainer;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpInventory;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpState;
import edu.utd.minecraft.mod.polycraft.item.ItemWafer;
import edu.utd.minecraft.mod.polycraft.util.Analytics;

public abstract class PolycraftInventory extends PolycraftBasicTileEntityContainer {

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
				if (!this.worldObj.isRemote && !isInventoryInCleanroom())
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

	public boolean isInventoryInCleanroom() {

		int cleanroomWidth = 24;
		int cleanroomLength = 24;
		int cleanroomHeight = 10;
		boolean foundPump = false;
		Vec3 pumpCoords = null;
		int pumpFlowDirection;

		int x = this.xCoord;
		int y = this.yCoord;
		int z = this.zCoord;
		Block block = this.worldObj.getBlock(x, y, z);

		Vec3 nwCoords = findNWBottomCornerOfCleanroom(block, x, y, z);

		if (nwCoords == null)
			return false;

		x = (int) nwCoords.xCoord;
		y = (int) nwCoords.yCoord;
		z = (int) nwCoords.zCoord;

		if (this.worldObj.getBlock(x, y, z) != PolycraftRegistry.getBlock("Block (PP)"))
			return false;

		//NORTH Wall
		for (int i = 0; i < cleanroomWidth; i++)
		{
			for (int j = 0; j < cleanroomHeight; j++)
			{
				if (!isCleanroomWallBlock(block = this.worldObj.getBlock(x + i, y + j, z)))
					return false;
				if (!foundPump && isPump(block))
				{
					foundPump = true;
					pumpCoords = Vec3.createVectorHelper(x + i, y + j, z);
				}
			}
		}

		//EAST Wall
		for (int k = 0; k < cleanroomLength; k++)
		{
			for (int j = 0; j < cleanroomHeight; j++)
			{
				if (!isCleanroomWallBlock(block = this.worldObj.getBlock(x + cleanroomWidth - 1, y + j, z + k)))
					return false;
				if (!foundPump && isPump(block))
				{
					foundPump = true;
					pumpCoords = Vec3.createVectorHelper(x + cleanroomWidth - 1, y + j, z + k);
				}
			}
		}

		//SOUTH Wall
		for (int i = 0; i < cleanroomWidth; i++)
		{
			for (int j = 0; j < cleanroomHeight; j++)
			{
				if (!isCleanroomWallBlock(this.worldObj.getBlock(x + i, y + j, z + cleanroomLength - 1)))
					return false;
				if (!foundPump && isPump(block))
				{
					foundPump = true;
					pumpCoords = Vec3.createVectorHelper(x + i, y + j, z + cleanroomLength - 1);
				}
			}
		}

		//WEST Wall
		for (int k = 0; k < cleanroomLength; k++)
		{
			for (int j = 0; j < cleanroomHeight; j++)
			{
				if (!isCleanroomWallBlock(this.worldObj.getBlock(x, y + j, z + k)))
					return false;
				if (!foundPump && isPump(block))
				{
					foundPump = true;
					pumpCoords = Vec3.createVectorHelper(x, y + j, z + k);
				}
			}
		}

		//Ceiling
		for (int k = 0; k < cleanroomLength; k++)
		{
			for (int i = 0; i < cleanroomWidth; i++)
			{
				if (!isCleanroomCeilingBlock(this.worldObj.getBlock(x + i, y + cleanroomHeight - 1, z + k)))
					return false;
			}
		}

		//Floor
		for (int k = 1; k < cleanroomLength - 1; k++)
		{
			for (int i = 1; i < cleanroomWidth - 1; i++)
			{
				if (!isCleanroomFloorBlock(this.worldObj.getBlock(x + i, y, z + k)))
					return false;
			}
		}

		//Internal
		for (int k = 1; k < cleanroomLength - 1; k++)
		{
			for (int j = 1; j < cleanroomHeight - 1; j++)
			{
				for (int i = 1; i < cleanroomWidth - 1; i++)
				{
					if (!isCleanroomBlock(this.worldObj.getBlock(x + i, y + j, z + k)))
						return false;
				}
			}
		}

		//Is pump working
		if (pumpCoords != null)
		{
			pumpFlowDirection = worldObj.getBlockMetadata((int) pumpCoords.xCoord, (int) pumpCoords.yCoord, (int) pumpCoords.zCoord);
			//TODO: Walter - test to see if HEPA Filter is next to Pump
		}
		IInventory inventory = PolycraftMod.getInventoryAt(worldObj, (int) pumpCoords.xCoord, (int) pumpCoords.yCoord, (int) pumpCoords.zCoord);
		if (!(inventory instanceof PumpInventory))
			return false;
		if (((PumpInventory) inventory).getState(PumpState.FuelTicksRemaining) <= 0)
			return false;

		return true;
	}

	private boolean isPump(Block block) {
		if ((block == PolycraftRegistry.getBlock("Pump")))
			return true;
		return false;
	}

	private boolean isCleanroomWallBlock(Block block) {

		if ((block == PolycraftRegistry.getBlock("Block (PP)")) ||
				(block == Blocks.iron_door) ||
				(block == Blocks.stained_glass) ||
				(block == Blocks.stained_glass_pane) ||
				(block instanceof BlockPipe) ||
				(block == PolycraftRegistry.getBlock("Pump")))
			return true;

		return false;
	}

	private boolean isCleanroomCeilingBlock(Block block) {

		if ((block == PolycraftRegistry.getBlock("Block (PP)")) ||
				(block instanceof BlockPipe) ||
				(block == Blocks.stained_glass))
			return true;

		return false;
	}

	private boolean isCleanroomFloorBlock(Block block) {

		if (block == PolycraftRegistry.getBlock("Block (PVC)") ||
				(block instanceof BlockPipe))
			return true;

		return false;
	}

	private boolean isCleanroomBlock(Block block) {

		if ((block instanceof PolycraftInventoryBlock) ||
				(block instanceof BlockCollision) ||
				(block == Blocks.air) ||
				(block instanceof BlockLight) ||
				(block instanceof BlockCompressed) ||
				(block instanceof BlockPolymer) ||
				(block instanceof BlockPolymerSlab) ||
				(block instanceof BlockPolymerStairs) ||
				(block instanceof BlockPolymerWall) ||
				(block instanceof BlockPipe) ||
				(block == Blocks.iron_door) ||
				(block == Blocks.iron_block) ||
				(block == Blocks.brewing_stand) ||
				(block == Blocks.crafting_table) ||
				(block == Blocks.diamond_block) ||
				(block == Blocks.dispenser) ||
				(block == Blocks.emerald_block) ||
				(block == Blocks.enchanting_table) ||
				(block == Blocks.ender_chest) ||
				(block == Blocks.gold_block) ||
				(block == Blocks.sticky_piston) ||
				(block == Blocks.piston) ||
				(block == Blocks.piston_extension) ||
				(block == Blocks.piston_head) ||
				(block == Blocks.hopper) ||
				(block == Blocks.anvil) ||
				(block == Blocks.beacon) ||
				(block == Blocks.lapis_block) ||
				(block == Blocks.light_weighted_pressure_plate) ||
				(block == Blocks.heavy_weighted_pressure_plate) ||
				(block == Blocks.obsidian) ||
				(block == Blocks.powered_comparator) ||
				(block == Blocks.redstone_block) ||
				(block == Blocks.redstone_lamp) ||
				(block == Blocks.redstone_torch) ||
				(block == Blocks.redstone_wire) ||
				(block == Blocks.stained_glass) ||
				(block == Blocks.stained_glass_pane) ||
				(block == Blocks.unlit_redstone_torch) ||
				(block == Blocks.unpowered_comparator) ||
				(block == Blocks.unpowered_repeater) ||
				(block == Blocks.wall_sign) ||
				(block == Blocks.water) ||
				(block == Blocks.chest) ||
				(block == Blocks.quartz_block))
		{
			return true;
		}
		return false;
	}

	private Vec3 findNWBottomCornerOfCleanroom(Block block, int x, int y, int z) {

		int counter = 0;
		while (!isCleanroomFloorBlock(block))
		{
			block = this.worldObj.getBlock(x, --y, z);
			if (y == 3)
				return null;
			if (counter++ >= 50)
				return null;
		}

		counter = 0;
		while (isCleanroomFloorBlock(block))
		{
			block = this.worldObj.getBlock(--x, y, z);
			if (counter++ >= 50)
				return null;
		}

		block = this.worldObj.getBlock(++x, y, z);
		counter = 0;
		while (isCleanroomFloorBlock(block))
		{
			block = this.worldObj.getBlock(x, y, --z);
			if (counter++ >= 50)
				return null;
		}
		x--;
		return Vec3.createVectorHelper(x, y, z);
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
