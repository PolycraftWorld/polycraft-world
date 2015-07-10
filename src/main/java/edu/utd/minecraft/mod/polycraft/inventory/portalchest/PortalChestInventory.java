package edu.utd.minecraft.mod.polycraft.inventory.portalchest;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselMerger;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselUpcycler;

public class PortalChestInventory extends PolycraftInventory {

	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	public TileEntity associatedChest;
	public int linkedChests;

	static {
		for (int y = 0; y < 6; y++)
		{
			int offset = 0;
			for (int x = 0; x < 12; x++)
			{
				if (x == 6)
				{
					offset++;
				}
				guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.INPUT, x + offset, y, 8 + (x + offset) * 18, 18 + y * 18)); //output
			}
		}
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		PortalChestInventory.config = config;
		config.containerType = PolycraftContainerType.PORTAL_CHEST;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, PortalChestInventory.class));
	}

	public PortalChestInventory() {
		super(PolycraftContainerType.PORTAL_CHEST, config);
		this.addBehavior(new VesselUpcycler());
		this.addBehavior(new VesselMerger());

	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, 138); // Was128
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PolycraftInventoryGui(this, playerInventory, 250, 221, true); //ySize overridden for chest in drawGuiContainerBackgroundLayer
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.associatedChest != null && !this.isPlayerAtThisPortalChest(player, associatedChest) ?
				false : super.isUseableByPlayer(player);
	}

	@Override
	public void openInventory() {

		if (this.associatedChest != null)
		{
			this.linkPortalChests(associatedChest);
		}

		super.openInventory();
	}

	public void linkPortalChests(TileEntity associatedChest)
	{
		++this.linkedChests;
		this.worldObj.addBlockEvent(associatedChest.xCoord, associatedChest.yCoord, associatedChest.zCoord, PolycraftRegistry.getBlock(this.config), 1, this.linkedChests);
	}

	public void unlinkPortalChests(TileEntity associatedChest)
	{
		--this.linkedChests;
		this.worldObj.addBlockEvent(associatedChest.xCoord, associatedChest.yCoord, associatedChest.zCoord, PolycraftRegistry.getBlock(this.config), 1, this.linkedChests);
	}

	@Override
	public void closeInventory() {

		if (this.associatedChest != null)
		{
			this.unlinkPortalChests(associatedChest);
		}

		super.closeInventory();
		this.associatedChest = null;

	}

	public boolean isPlayerAtThisPortalChest(EntityPlayer player, TileEntity associatedChest)
	{
		return this.worldObj.getTileEntity(associatedChest.xCoord, associatedChest.yCoord, associatedChest.zCoord) != associatedChest ?
				false : player.getDistanceSq(
						(double) associatedChest.xCoord + 0.5D,
						(double) associatedChest.yCoord + 0.5D,
						(double) associatedChest.zCoord + 0.5D) <= 64.0D;
	}

	public void loadInventoryFromNBT(NBTTagList p_70486_1_)
	{
		int i;

		for (i = 0; i < this.getSizeInventory(); ++i)
		{
			this.setInventorySlotContents(i, (ItemStack) null);
		}

		for (i = 0; i < p_70486_1_.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = p_70486_1_.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j >= 0 && j < this.getSizeInventory())
			{
				this.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
	}

	public NBTTagList saveInventoryToNBT()
	{
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.getSizeInventory(); ++i)
		{
			ItemStack itemstack = this.getStackInSlot(i);

			if (itemstack != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				itemstack.writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		return nbttaglist;
	}

}
