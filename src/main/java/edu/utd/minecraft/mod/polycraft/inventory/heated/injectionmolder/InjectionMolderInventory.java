package edu.utd.minecraft.mod.polycraft.inventory.heated.injectionmolder;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.ItemPresentCraftingBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedGui;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;

//TODO allow pellets to flow into this container from a hopper? maybe it already works?
public class InjectionMolderInventory extends HeatedInventory {

	private static int slotIndexHeatSource;
	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		//1x5 input grid for pellets
		for (int i = 0; i < 5; i++)
			guiSlots.add(GuiContainerSlot.createInput(guiSlots.size(), i, 0, 8, 0));
		guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.INPUT, 0, 1, 90, 55)); //mold
		guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.MISC, -1, -1, 110, 55)); //cooling water
		guiSlots.add(new GuiContainerSlot(slotIndexHeatSource = guiSlots.size(), SlotType.MISC, -1, -1, 44, 91)); //fuel
		guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, 1, 0, 152, 55)); //output
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		InjectionMolderInventory.config = config;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, InjectionMolderInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	public InjectionMolderInventory() {
		super(PolycraftContainerType.INJECTION_MOLDER, config.gameID, slotIndexHeatSource);
		addBehavior(new ItemPresentCraftingBehavior(ImmutableMap.of((ContainerSlot) guiSlots.get(0), Items.water_bucket)));
	}

	@Override
	protected HeatedGui getGuiHeated(InventoryPlayer playerInventory) {
		return new HeatedGui(this, playerInventory, new HeatedGui.ProgressDisplayOffsets(45, 85, 131, 55), 203);
	}
}
