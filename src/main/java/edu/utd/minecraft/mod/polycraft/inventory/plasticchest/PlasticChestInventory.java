package edu.utd.minecraft.mod.polycraft.inventory.plasticchest;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.WateredInventory;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.CraftingBehavior;

public class PlasticChestInventory extends Container {


	   private IInventory lowerChestInventory;
	    private int numRows;
	    //private static final String __OBFID = "CL_00001742";

	    public PlasticChestInventory(IInventory par1IInventory, IInventory par2IInventory)
	    {
	        this.lowerChestInventory = par2IInventory;
	        this.numRows = par2IInventory.getSizeInventory() / 9;
	        par2IInventory.openInventory();
	        int i = (this.numRows - 4) * 18;
	        int j;
	        int k;

	        for (j = 0; j < this.numRows; ++j)
	        {
	            for (k = 0; k < 9; ++k)
	            {
	                this.addSlotToContainer(new Slot(par2IInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
	            }
	        }

	        for (j = 0; j < 3; ++j)
	        {
	            for (k = 0; k < 9; ++k)
	            {
	                this.addSlotToContainer(new Slot(par1IInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
	            }
	        }

	        for (j = 0; j < 9; ++j)
	        {
	            this.addSlotToContainer(new Slot(par1IInventory, j, 8 + j * 18, 161 + i));
	        }
	    }

	    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
	    {
	        return this.lowerChestInventory.isUseableByPlayer(par1EntityPlayer);
	    }

	    /**
	     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	     */
	    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	    {
	        ItemStack itemstack = null;
	        Slot slot = (Slot)this.inventorySlots.get(par2);

	        if (slot != null && slot.getHasStack())
	        {
	            ItemStack itemstack1 = slot.getStack();
	            itemstack = itemstack1.copy();

	            if (par2 < this.numRows * 9)
	            {
	                if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
	                {
	                    return null;
	                }
	            }
	            else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
	            {
	                return null;
	            }

	            if (itemstack1.stackSize == 0)
	            {
	                slot.putStack((ItemStack)null);
	            }
	            else
	            {
	                slot.onSlotChanged();
	            }
	        }

	        return itemstack;
	    }

	    /**
	     * Called when the container is closed.
	     */
	    public void onContainerClosed(EntityPlayer par1EntityPlayer)
	    {
	        super.onContainerClosed(par1EntityPlayer);
	        this.lowerChestInventory.closeInventory();
	    }

	    /**
	     * Return this chest container's lower chest inventory.
	     */
	    public IInventory getLowerChestInventory()
	    {
	        return this.lowerChestInventory;
	    }
	}
	
	
//	
//	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
//	static {
//		//5x5 input grid
//		for (int y = 0; y < 5; y++)
//			for (int x = 0; x < 5; x++)
//				guiSlots.add(GuiContainerSlot.createInput(guiSlots.size(), x, y, 8, 0));
//		//cooling water
//		guiSlots.add(new GuiContainerSlot(slotIndexCoolingWater = guiSlots.size(), SlotType.MISC, -1, -1, 116, 90));
//		//output
//		guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, -1, -1, 152, 54));
//	}
//
//	private static Inventory config;
//
//	public static final void register(final Inventory config) {
//		PlasticChestInventory.config = config;
//		config.containerType = PolycraftContainerType.PLASTIC_CHEST;
//		PolycraftInventory.register(new PolycraftInventoryBlock(config, PlasticChestInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
//	}
//
//	public PlasticChestInventory() {
//		super(PolycraftContainerType.PLASTIC_CHEST, config, 121, slotIndexCoolingWater, -1);
//		this.addBehavior(new CraftingBehavior<PlasticChestInventory>());
//	}
//
//	@Override
//	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
//		return new PolycraftCraftingContainerGeneric(this, playerInventory, playerInventoryOffset, true);
//	}
//
//	@Override
//	@SideOnly(Side.CLIENT)
//	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
//		return new PolycraftInventoryGui(this, playerInventory, 203);
//	}
//}
