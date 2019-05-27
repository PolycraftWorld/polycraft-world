package edu.utd.minecraft.mod.polycraft.inventory.tierchest;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapGui;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TierChestInventory extends PolycraftInventory{
	
	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		for (int i = 0; i < 5; i++)
			guiSlots.add(GuiContainerSlot.createOutput(i, i, 0, 44, 2));
	}

	private static Inventory config;
	private boolean active= true;
	

	public static final void register(final Inventory config) {
		TierChestInventory.config = config;
		config.containerType = PolycraftContainerType.TIER_CHEST;
		PolycraftInventory.register(new TierChestBlock(config, TierChestInventory.class));
	}

	public TierChestInventory() {
		super(PolycraftContainerType.TIER_CHEST, config);

	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, 51); 
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		// return new PolycraftInventoryGui(this, playerInventory, 133, false);
		return new TierChestGui(this, playerInventory);
	}
	
//	public void setActive(boolean active)
//	{
//		NBTTagCompound nbt = new NBTTagCompound();
//		nbt.setBoolean("Active", active);
//		this.writeToNBT(nbt);
//	}
//	
//	public boolean getActive()
//	{
//		NBTTagCompound nbt = new NBTTagCompound();
//		this.readFromNBT(nbt);
//		return nbt.getBoolean("Active");
//	}
	
	

	
	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);
		this.active = p_145839_1_.getBoolean("Active");
	}

	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_) {
		super.writeToNBT(p_145841_1_);
		p_145841_1_.setBoolean("Active", this.active);
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public boolean getActive() {
		return this.active; 
	}

}
