package edu.utd.minecraft.mod.polycraft.inventory.machiningmill;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.ItemPresentCraftingBehavior;

public class MachiningMillInventory extends PolycraftInventory {

	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		//cooling water
		guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.MISC, -1, -1, 116, 90));
		//5x5 input grid
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++)
				guiSlots.add(GuiContainerSlot.createInput(guiSlots.size(), j, i, 8, 0));
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
		addBehavior(new ItemPresentCraftingBehavior(ImmutableMap.of((ContainerSlot) guiSlots.get(0), Items.water_bucket)));
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, 121);
	}

	@Override
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PolycraftInventoryGui(this, playerInventory, 203);
	}
}
