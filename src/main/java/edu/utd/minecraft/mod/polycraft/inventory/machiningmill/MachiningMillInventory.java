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
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.CraftingBehavior;

//TODO need to decrement inputs when output is taken...
public class MachiningMillInventory extends PolycraftInventory {

	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		//5x5 input grid
		for (int row = 0; row < 5; row++)
			for (int col = 0; col < 5; col++)
				guiSlots.add(GuiContainerSlot.createInput(guiSlots.size(), row, col, 8, 0));
		//cooling water
		guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.INPUT, 0, 5, 116, 90));
		//output
		guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, -1, -1, 152, 54));
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		MachiningMillInventory.config = config;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, MachiningMillInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	public MachiningMillInventory() {
		super(PolycraftContainerType.MACHINING_MILL, config);
		this.addBehavior(new CraftingBehavior());
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, 121);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PolycraftInventoryGui(this, playerInventory, 203);
	}
}
