package edu.utd.minecraft.mod.polycraft.inventory.territoryflag;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;

public class TerritoryFlagInventory extends PolycraftInventory {

	Ticket ticket;
	
	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		TerritoryFlagInventory.config = config;
		config.containerType = PolycraftContainerType.TERRITORY_FLAG;
		PolycraftInventory.register(new TerritoryFlagBlock(config, TerritoryFlagInventory.class));
	}

	public TerritoryFlagInventory() {
		super(PolycraftContainerType.TERRITORY_FLAG, config);
	}
	/*
	@Override
	public void update()
	{
		super.update();
		System.out.println("TEST");
		if (!this.worldObj.isRemote && this.ticket == null) 
		 {
				//ticketFlag=true;
	            this.ticket = ForgeChunkManager.requestTicket(PolycraftMod.instance, this.worldObj, ForgeChunkManager.Type.NORMAL);
	            if (this.ticket != null) 
	            {
	                ForgeChunkManager.forceChunk(this.ticket, new ChunkCoordIntPair(this.xCoord/16, this.zCoord/16));
	                //FMLLog.info("Forcing chunk ( %d , %d )", 0, 0);
	            }
	    }
		
	}
*/
	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, 128);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PolycraftInventoryGui(this, playerInventory, 200, 208, true);
	}

}
