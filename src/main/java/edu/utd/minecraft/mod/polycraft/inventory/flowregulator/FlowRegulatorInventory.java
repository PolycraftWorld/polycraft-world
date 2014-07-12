package edu.utd.minecraft.mod.polycraft.inventory.flowregulator;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.AutomaticInputBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselMerger;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselUpcycler;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;


public class FlowRegulatorInventory extends PolycraftInventory {
	
	public static int slotIndexFirstNorth, slotIndexLastNorth, slotIndexBlockNorth;
	public static int slotIndexFirstSouth, slotIndexLastSouth, slotIndexBlockSouth;
	public static int slotIndexFirstEast, slotIndexLastEast, slotIndexBlockEast;
	public static int slotIndexFirstWest, slotIndexLastWest, slotIndexBlockWest;
	public static int slotIndexFirstUp, slotIndexLastUp, slotIndexBlockUp;
	public static int slotIndexFirstDown, slotIndexLastDown, slotIndexBlockDown;
	
	
	//TODO: implementation needs much love...
	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		slotIndexFirstNorth = guiSlots.size();
		for (int x = 0; x < 2; x++)
			for (int y = 0; y < 3; y++)
				guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.INPUT, x, y, 8 + x * 18, 18 + y * 18)); //North
		slotIndexLastNorth = guiSlots.size()-1;
		slotIndexFirstUp = guiSlots.size();
		for (int x = 0; x < 2; x++)
			for (int y = 4; y < 8; y++)
			{
				if ((y==4)&& (x==1))
					continue;
				else if ((y==7)&& (x==1))
					continue;
				else
					guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.INPUT, x, y, 8 + x * 18, 18 + y * 18)); //Up
			}
		slotIndexLastUp = guiSlots.size()-1;
		slotIndexFirstEast = guiSlots.size();
		
		for (int x = 0; x < 2; x++)
			for (int y = 9; y < 12; y++)
				guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.INPUT, x, y, 8 + x * 18, 18 + y * 18)); //East
		slotIndexLastEast = guiSlots.size()-1;
		slotIndexFirstWest = guiSlots.size();
		for (int x = 4; x < 6; x++)
			for (int y = 0; y < 3; y++)
				guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.INPUT, x, y, 8 + x * 18, 18 + y * 18)); //West
		slotIndexLastWest = guiSlots.size()-1;
		slotIndexFirstDown = guiSlots.size();
		for (int x = 4; x < 6; x++)
			for (int y = 4; y < 8; y++)
			{
				if ((y==4)&& (x==4))
					continue;
				else if ((y==7)&& (x==4))
					continue;
				else
					guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.INPUT, x, y, 8 + x * 18, 18 + y * 18)); //Down
			}
		slotIndexLastDown = guiSlots.size()-1;
		slotIndexFirstSouth = guiSlots.size();
		
		for (int x = 4; x < 6; x++)
			for (int y = 9; y < 12; y++)
				guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.INPUT, x, y, 8 + x * 18, 18 + y * 18)); //South
		slotIndexLastSouth = guiSlots.size()-1;
		
		slotIndexBlockNorth = slotIndexFirstNorth;
		slotIndexBlockUp = slotIndexFirstUp;
		slotIndexBlockEast = slotIndexFirstEast+2;
		slotIndexBlockWest = slotIndexFirstWest+3;
		slotIndexBlockDown = slotIndexLastDown;
		slotIndexBlockSouth = slotIndexLastSouth;
		
			
				
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		FlowRegulatorInventory.config = config;
		config.containerType = PolycraftContainerType.FLOW_REGULATOR;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, FlowRegulatorInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}
	
	public FlowRegulatorInventory() {
		super(PolycraftContainerType.FLOW_REGULATOR, config);
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
		return new PolycraftInventoryGui(this, playerInventory, 232, 221, true); //ySize overridden for chest in drawGuiContainerBackgroundLayer
	}
	
	
	
	
}
