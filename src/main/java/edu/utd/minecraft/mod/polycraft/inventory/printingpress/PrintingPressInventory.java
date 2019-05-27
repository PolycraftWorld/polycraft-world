package edu.utd.minecraft.mod.polycraft.inventory.printingpress;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;

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
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.CraftingBehavior;

public class PrintingPressInventory extends PolycraftInventory {

	public static final int slotIndexItemInput;
	public static final int slotIndexItemOutput;
	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {

		//7x5 input grid
		for (int y = 0; y < 5; y++)
			for (int x = 0; x < 7; x++)
				guiSlots.add(GuiContainerSlot.createInput(guiSlots.size(), x, y, 8 + 36, 2));

		//item input
		guiSlots.add(new GuiContainerSlot(slotIndexItemInput = guiSlots.size(), SlotType.INPUT, 5, 0, 8, 20));
		//output
		guiSlots.add(new GuiContainerSlot(slotIndexItemOutput = guiSlots.size(), SlotType.OUTPUT, 5, 1, 8, 92));
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		PrintingPressInventory.config = config;
		config.containerType = PolycraftContainerType.PRINTING_PRESS;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, PrintingPressInventory.class));
	}

	public PrintingPressInventory() {
		super(PolycraftContainerType.PRINTING_PRESS, config);
		this.addBehavior(new CraftingBehavior<PrintingPressInventory>());
	}

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
