package edu.utd.minecraft.mod.polycraft.inventory.solararray;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.ElementVessel;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselMerger;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselUpcycler;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;

public class SolarArrayInventory extends PolycraftInventory {

	public static int slotIndexInput1;
	public static int slotIndexInput2;
	public static int slotIndexInput3;
	public static int slotIndexFirstOutput;
	public static int slotIndexSecondOutput;
	public static int slotIndexLastOutput;

	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput1 = guiSlots.size(), 0, 0, 17, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput2 = guiSlots.size(), 1, 0, 17, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput3 = guiSlots.size(), 2, 0, 17, 0));
		slotIndexFirstOutput = guiSlots.size();
		for (int y = 0; y < 2; y++)
		{
			for (int x = 0; x < 3; x++)
			{
				guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, x, y, 116 + x * 18, 18 + y * 18)); //output
			}

		}
		slotIndexSecondOutput = guiSlots.size() - 3; //specific to this solar plant
		slotIndexLastOutput = guiSlots.size() - 1;
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		SolarArrayInventory.config = config;
		config.containerType = PolycraftContainerType.SOLAR_ARRAY;
		PolycraftInventory.register(new SolarArrayBlock(config, SolarArrayInventory.class));
	}

	private static final int[][] tappedCoordOffsets = new int[][] { new int[] { 1, 0 }, new int[] { 0, 1 }, new int[] { 0, -1 }, new int[] { -1, 0 } };

	private int transferCooldown = -1;
	private int spawnAttempts = -1;
	private final ElementVessel elementVesselAToSpawn;
	private final ElementVessel elementVesselBToSpawn;
	private final int amountToSpawnA, amountToSpawnB;
	private final int spawnFrequencyTicks;
	private int amountOfCompoundHarvested;
	public boolean inWater = false;
	/**
	 * Leave false please.
	 */
	private static final boolean SOLAR_DEBUG = false;

	public SolarArrayInventory() {
		super(PolycraftContainerType.SOLAR_ARRAY, config);
		this.elementVesselAToSpawn = ElementVessel.registry.get(config.params.get(0));
		this.elementVesselBToSpawn = ElementVessel.registry.get(config.params.get(1));
		this.amountToSpawnA = config.params.getInt(2);
		this.amountToSpawnB = config.params.getInt(3);
		this.spawnFrequencyTicks = SOLAR_DEBUG ? 1 : PolycraftMod.convertSecondsToGameTicks(config.params.getInt(4));
		this.amountOfCompoundHarvested = 0;
		this.addBehavior(new VesselUpcycler());
		this.addBehavior(new VesselMerger());
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, 84);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		// return new PolycraftInventoryGui(this, playerInventory, 166, false);
		return new SolarArrayGui(this, playerInventory);
	}

	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);
		this.transferCooldown = p_145839_1_.getInteger("TransferCooldown");
		this.spawnAttempts = p_145839_1_.getInteger("SpawnAttempts");
	}

	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_) {
		super.writeToNBT(p_145841_1_);
		p_145841_1_.setInteger("TransferCooldown", this.transferCooldown);
		p_145841_1_.setInteger("SpawnAttempts", this.spawnAttempts);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (this.worldObj != null && !this.worldObj.isRemote) {
			--this.transferCooldown;

			if (!this.isTransferCoolingDown()) {
				this.setTransferCooldown(0);
				this.harvestItemsIfParamsOK();
			}
		}
	}

	private ItemStack getNextTappedItemA() {

		if (spawnAttempts++ >= spawnFrequencyTicks) {
			spawnAttempts = 0;
			amountOfCompoundHarvested++;
			return elementVesselAToSpawn.getItemStack(amountToSpawnA);

		}
		return null;

	}

	private ItemStack getNextTappedItemB() {

		if (spawnAttempts++ >= spawnFrequencyTicks) {
			spawnAttempts = 0;
			amountOfCompoundHarvested++;
			return elementVesselBToSpawn.getItemStack(amountToSpawnB);

		}
		return null;

	}

	private boolean blockNeighborsWater()
	{
		if ((this.worldObj.getBlock(this.xCoord + 1, this.yCoord, this.zCoord)) == Blocks.water)
			return true;
		else if ((this.worldObj.getBlock(this.xCoord - 1, this.yCoord, this.zCoord)) == Blocks.water)
			return true;
		else if ((this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord + 1)) == Blocks.water)
			return true;
		else if ((this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord - 1)) == Blocks.water)
			return true;
		return false;

	}

	public boolean harvestItemsIfParamsOK() {
		if (this.worldObj != null && !this.worldObj.isRemote) {
			if (!this.isTransferCoolingDown() && isForthMetaDataBitSet(this.getBlockMetadata())) {
				//boolean flag = this.transferInventories();
				boolean flag = false; //for now don't transfer to others automatically 
				flag = harvestItemsNow(this) || flag;

				if (flag) {
					this.setTransferCooldown(8);
					this.markDirty();
					return true;
				}
			}
			return false;
		}
		return false;
	}

	public static boolean harvestItemsNow(SolarArrayInventory spi) {
		ItemStack nextTappedItemStackA = spi.getNextTappedItemA();
		if (nextTappedItemStackA != null)
			testSlotIndiciesToSpawnItems(spi, nextTappedItemStackA, 1);

		ItemStack nextTappedItemStackB = spi.getNextTappedItemB();
		if (nextTappedItemStackB != null)
			testSlotIndiciesToSpawnItems(spi, nextTappedItemStackB, 0);

		IInventory iinventory = getAssociatedIInventory(spi);

		if (iinventory != null)
		{
			byte b0 = 0;

			if (iinventory instanceof ISidedInventory && b0 > -1)
			{
				ISidedInventory isidedinventory = (ISidedInventory) iinventory;
				int[] aint = isidedinventory.getAccessibleSlotsFromSide(b0);

				if (aint != null) {
					for (int k = 0; k < aint.length; ++k)
					{
						if (doesItemFitInNextSlot(spi, iinventory, aint[k], b0))
						{
							return true;
						}
					}
				}
			}
			else
			{
				int i = iinventory.getSizeInventory();

				for (int j = 0; j < i; ++j)
				{
					if (doesItemFitInNextSlot(spi, iinventory, j, b0))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	private static boolean doesItemFitInNextSlot(SolarArrayInventory spi, IInventory iinventory, int p_145892_2_, int p_145892_3_) {
		ItemStack itemstack = iinventory.getStackInSlot(p_145892_2_);

		if (itemstack != null && func_145890_b(iinventory, itemstack, p_145892_2_, p_145892_3_)) {
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = testSlotIndiciesToSpawnItems(spi, iinventory.decrStackSize(p_145892_2_, 1), -1);

			if (itemstack2 == null || itemstack2.stackSize == 0)
			{
				iinventory.markDirty();
				return true;
			}

			iinventory.setInventorySlotContents(p_145892_2_, itemstack1);
		}

		return false;
	}

	private static boolean func_145890_b(IInventory iinventory, ItemStack itemStack, int p_145890_2_, int p_145890_3_) {
		return !(iinventory instanceof ISidedInventory) || ((ISidedInventory) iinventory).canExtractItem(p_145890_2_, itemStack, p_145890_3_);
	}

	public static IInventory getAssociatedIInventory(SolarArrayInventory spi) {
		return getIInventoryAtCoordsHelper(spi.getWorldObj(), spi.xCoord, spi.yCoord + 1.0D, spi.zCoord);
	}

	private boolean transferInventories() {
		IInventory iinventory = this.getFacingIInventory();

		if (iinventory == null)
		{
			return false;
		}

		if (!(iinventory instanceof SolarArrayInventory))
			return false;

		else
		{
			for (int i = 0; i < this.getSizeInventory(); ++i)
			{
				if (this.getStackInSlot(i) != null)
				{
					ItemStack itemstack = this.getStackInSlot(i).copy();
					ItemStack itemstack1 = testSlotIndiciesToSpawnItems(((SolarArrayInventory) iinventory), this.decrStackSize(i, 1), Facing.oppositeSide[getDirectionFromMetadata(this.getBlockMetadata())]);

					if (itemstack1 == null || itemstack1.stackSize == 0)
					{
						iinventory.markDirty();
						return true;
					}

					this.setInventorySlotContents(i, itemstack);
				}
			}

			return false;
		}
	}

	public static ItemStack testSlotIndiciesToSpawnItems(SolarArrayInventory spi, ItemStack itemStack, int flag) {
		int j = spi.getSizeInventory();

		for (int slotIndex = 0; slotIndex < j && itemStack != null && itemStack.stackSize > 0; ++slotIndex) {
			itemStack = spawnItemIntoSpecificSlot(spi, itemStack, slotIndex, flag);

		}

		if (itemStack != null && itemStack.stackSize == 0) {
			itemStack = null;
		}

		return itemStack;
	}

	private static ItemStack spawnItemIntoSpecificSlot(SolarArrayInventory iinventory, ItemStack itemStack, int slotIndex, int decWaterAmt) {
		ItemStack itemstack1 = iinventory.getStackInSlot(slotIndex);

		if (canSpawnIntoSlot(iinventory, itemStack, slotIndex, decWaterAmt))
		{
			boolean flag = false;

			if (sunShiningInputSlotsAreWaterAndDecrementThem(iinventory, decWaterAmt))
			{

				if (itemstack1 == null)
				{
					// Forge: BUGFIX: Again, make things respect max stack sizes.
					int max = Math.min(itemStack.getMaxStackSize(), iinventory.getInventoryStackLimit());
					if (max >= itemStack.stackSize)
					{
						iinventory.setInventorySlotContents(slotIndex, itemStack);
						itemStack = null;
					}
					else
					{
						iinventory.setInventorySlotContents(slotIndex, itemStack.splitStack(max));
					}
					flag = true;
				}
				else if (areItemStacksStackable(itemstack1, itemStack))
				{
					// Forge: BUGFIX: Again, make things respect max stack sizes.
					int max = Math.min(itemStack.getMaxStackSize(), iinventory.getInventoryStackLimit());
					if (max > itemstack1.stackSize)
					{
						int l = Math.min(itemStack.stackSize, max - itemstack1.stackSize);
						itemStack.stackSize -= l;
						itemstack1.stackSize += l;
						flag = l > 0;
					}
				}
			}

			if (flag)
			{
				if (iinventory instanceof SolarArrayInventory)
				{
					((SolarArrayInventory) iinventory).setTransferCooldown(8 * decWaterAmt);
					iinventory.markDirty();
				}

				iinventory.markDirty();
			}
		}

		return itemStack;
	}

	private static boolean sunShiningInputSlotsAreWaterAndDecrementThem(SolarArrayInventory spi, int decWaterAmt) {

		if (!spi.getWorldObj().isDaytime())
			return false;
		if (spi.getWorldObj().isRaining())
			return false;

		int xWeight;
		int zWeight;
		if (spi.blockMetadata == ForgeDirection.NORTH.ordinal())
		{
			xWeight = -1;
			zWeight = 1;
		}
		else if (spi.blockMetadata == ForgeDirection.EAST.ordinal())
		{
			xWeight = -1;
			zWeight = -1;
		}
		else if (spi.blockMetadata == ForgeDirection.SOUTH.ordinal())
		{
			xWeight = 1;
			zWeight = -1;
		}
		else if (spi.blockMetadata == ForgeDirection.WEST.ordinal())
		{
			xWeight = 1;
			zWeight = 1;
		}
		else
			return false; //unexpected condition

		//make sure all the tiles can see the sky
		for (int x = 0; x < 8; x++)
		{
			for (int z = 0; z < 8; z++) {
				if (!spi.getWorldObj().canBlockSeeTheSky(spi.xCoord + (xWeight * x), spi.yCoord, spi.zCoord + (zWeight * z)))
					return false;

			}
		}

		//make sure there are no collision blocks above us in the center because they dont block the light; only test a few areas to make faster
		for (int y = 1; y < 7; y++)
		{
			if (spi.getWorldObj().getBlock(spi.xCoord + (xWeight * 1), spi.yCoord + y, spi.zCoord + (zWeight * 1)) == PolycraftMod.blockCollision)
				return false;
			if (spi.getWorldObj().getBlock(spi.xCoord + (xWeight * 4), spi.yCoord + y, spi.zCoord + (zWeight * 4)) == PolycraftMod.blockCollision)
				return false;
			if (spi.getWorldObj().getBlock(spi.xCoord + (xWeight * 7), spi.yCoord + y, spi.zCoord + (zWeight * 7)) == PolycraftMod.blockCollision)
				return false;
			if (spi.getWorldObj().getBlock(spi.xCoord + (xWeight * 1), spi.yCoord + y, spi.zCoord + (zWeight * 7)) == PolycraftMod.blockCollision)
				return false;
			if (spi.getWorldObj().getBlock(spi.xCoord + (xWeight * 7), spi.yCoord + y, spi.zCoord + (zWeight * 1)) == PolycraftMod.blockCollision)
				return false;

		}

		ItemStack inputStack1 = spi.getStackInSlot(slotIndexInput1);
		ItemStack inputStack2 = spi.getStackInSlot(slotIndexInput2);
		ItemStack inputStack3 = spi.getStackInSlot(slotIndexInput3);

		String vialWater = "Vial (Deionized Water)";

		if (inputStack1 != null && inputStack1.getItem() instanceof ItemVessel)
		{
			if (((ItemVessel) inputStack1.getItem()).config.name.startsWith(vialWater))
			{
				inputStack1.stackSize -= decWaterAmt;
				spi.setInventorySlotContents(slotIndexInput1, inputStack1);
				return true;
			}
		}
		if (inputStack2 != null && inputStack2.getItem() instanceof ItemVessel)
		{
			if (((ItemVessel) inputStack2.getItem()).config.name.startsWith(vialWater))
			{
				inputStack2.stackSize -= decWaterAmt;
				spi.setInventorySlotContents(slotIndexInput2, inputStack2);
				return true;
			}
		}
		if (inputStack3 != null && inputStack3.getItem() instanceof ItemVessel)
		{
			if (((ItemVessel) inputStack3.getItem()).config.name.startsWith(vialWater))
			{
				inputStack3.stackSize -= decWaterAmt;
				spi.setInventorySlotContents(slotIndexInput3, inputStack3);
				return true;
			}
		}

		//try to downsize

		if (decWaterAmt == 0)
		{

			if (inputStack1 != null && inputStack1.getItem() instanceof ItemVessel)
			{
				return downcycle(spi, ((ItemVessel) inputStack1.getItem()), slotIndexInput1, slotIndexInput1, slotIndexInput3);
			}

			if (inputStack2 != null && inputStack2.getItem() instanceof ItemVessel)
			{
				return downcycle(spi, ((ItemVessel) inputStack2.getItem()), slotIndexInput2, slotIndexInput1, slotIndexInput3);
			}
			if (inputStack3 != null && inputStack3.getItem() instanceof ItemVessel)
			{
				return downcycle(spi, ((ItemVessel) inputStack3.getItem()), slotIndexInput3, slotIndexInput1, slotIndexInput3);
			}
		}
		return false;
	}

	private static boolean areItemStacksStackable(ItemStack itemStackA, ItemStack itemStackB) {
		return itemStackA.getItem() != itemStackB.getItem() ? false : (itemStackA.getItemDamage() != itemStackB.getItemDamage() ? false : (itemStackA.stackSize > itemStackA.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(
				itemStackA, itemStackB)));
	}

	public static boolean isOutputSlot(int slotIndex)
	{
		return ((slotIndex >= slotIndexFirstOutput) && (slotIndex <= slotIndexLastOutput));
	}

	private static boolean canSpawnIntoSlot(IInventory iinventory, ItemStack itemStack, int slotIndex, int flag) {
		if (flag == 0)
			return ((slotIndex >= slotIndexFirstOutput) && (slotIndex < slotIndexSecondOutput));
		if (flag == 1)
			return ((slotIndex >= slotIndexSecondOutput) && (slotIndex <= slotIndexLastOutput));
		return false;

		//return !iinventory.isItemValidForSlot(slotIndex, itemStack) ? false : !(iinventory instanceof ISidedInventory) || ((ISidedInventory) iinventory).canExtractItem(slotIndex, itemStack, flag);
	}

	private IInventory getFacingIInventory() {
		int i = getDirectionFromMetadata(this.getBlockMetadata());
		return getIInventoryAtCoordsHelper(this.getWorldObj(), this.xCoord + Facing.offsetsXForSide[i], this.yCoord + Facing.offsetsYForSide[i], this.zCoord + Facing.offsetsZForSide[i]);
	}

	public static IInventory getIInventoryAtCoordsHelper(World worldObj, double xCoord, double yCoord, double zCoord) {
		IInventory iinventory = null;
		int i = MathHelper.floor_double(xCoord);
		int j = MathHelper.floor_double(yCoord);
		int k = MathHelper.floor_double(zCoord);
		TileEntity tileentity = worldObj.getTileEntity(i, j, k);

		if (tileentity != null && tileentity instanceof IInventory)
		{
			iinventory = (IInventory) tileentity;

			if (iinventory instanceof TileEntityChest)
			{
				Block block = worldObj.getBlock(i, j, k);

				if (block instanceof BlockChest)
				{
					iinventory = ((BlockChest) block).func_149951_m(worldObj, i, j, k);
				}
			}
		}

		if (iinventory == null)
		{
			List list = worldObj.getEntitiesWithinAABBExcludingEntity((Entity) null, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1.0D, yCoord + 1.0D, zCoord + 1.0D),
					IEntitySelector.selectInventories);

			if (list != null && list.size() > 0)
			{
				iinventory = (IInventory) list.get(worldObj.rand.nextInt(list.size()));
			}
		}

		return iinventory;
	}

	static int getDirectionFromMetadata(int meta) {
		return meta & 7;
	}

	public void setTransferCooldown(int transferCooldown) {
		this.transferCooldown = transferCooldown;
	}

	public boolean isTransferCoolingDown() {
		return this.transferCooldown > 0;
	}

	private static boolean isForthMetaDataBitSet(int meta) {
		return (meta & 8) != 8;
	}
}
