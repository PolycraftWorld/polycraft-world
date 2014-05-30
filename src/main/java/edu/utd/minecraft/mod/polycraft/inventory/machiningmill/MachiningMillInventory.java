package edu.utd.minecraft.mod.polycraft.inventory.machiningmill;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;

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

public class MachiningMillInventory extends WateredInventory {

	private static final int coolingWaterSlotIndex;
	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		//5x5 input grid
		for (int y = 0; y < 5; y++)
			for (int x = 0; x < 5; x++)
				guiSlots.add(GuiContainerSlot.createInput(guiSlots.size(), x, y, 8, 0));
		//cooling water
		guiSlots.add(new GuiContainerSlot(coolingWaterSlotIndex = guiSlots.size(), SlotType.MISC, -1, -1, 116, 90));
		//output
		guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, -1, -1, 152, 54));
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		MachiningMillInventory.config = config;
		config.containerType = PolycraftContainerType.MACHINING_MILL;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, MachiningMillInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	public MachiningMillInventory() {
		super(PolycraftContainerType.MACHINING_MILL, config, 121, coolingWaterSlotIndex, -1);
		this.addBehavior(new CraftingBehavior<MachiningMillInventory>());
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, playerInventoryOffset, true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PolycraftInventoryGui(this, playerInventory, 203);
	}
}
