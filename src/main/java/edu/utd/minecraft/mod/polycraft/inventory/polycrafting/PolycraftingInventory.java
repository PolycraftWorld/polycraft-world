package edu.utd.minecraft.mod.polycraft.inventory.polycrafting;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.StatefulInventoryState;
import edu.utd.minecraft.mod.polycraft.inventory.WateredContainer;
import edu.utd.minecraft.mod.polycraft.inventory.WateredInventory;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.CraftingBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.MachiningMillInventory;
import net.minecraft.entity.player.InventoryPlayer;

public class PolycraftingInventory extends PolycraftInventory {

	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		//5x5 input grid
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				guiSlots.add(GuiContainerSlot.createInput(guiSlots.size(), x, y, 8, 0));
		//output
		guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, -1, -1, 152, 54));
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		PolycraftingInventory.config = config;
		config.containerType = PolycraftContainerType.POLYCRAFTING_TABLE;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, PolycraftingInventory.class));
	}

	public PolycraftingInventory() {
		super(PolycraftContainerType.POLYCRAFTING_TABLE, config);
		this.addBehavior(new CraftingBehavior<PolycraftingInventory>());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PolycraftInventoryGui(this, playerInventory, 203);
	}
}
