package edu.utd.minecraft.mod.polycraft.inventory.pump;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;

public class FlowRegulatorInventory extends PolycraftInventory {

	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		for (int i = 0; i < 4; i++) {
			guiSlots.add(GuiContainerSlot.createInput(i, i, 0, 8, 2));
		}
	}

	private static Inventory config;

	public static void register(final Inventory config) {
		FlowRegulatorInventory.config = config;
		config.containerType = PolycraftContainerType.FLOW_REGULATOR;
		PolycraftInventory.register(new FlowRegulatorBlock(config, FlowRegulatorInventory.class));
	}

	public FlowRegulatorInventory() {
		super(PolycraftContainerType.FLOW_REGULATOR, config);
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, 88);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PolycraftInventoryGui(this, playerInventory, 166, false); //ySize overridden for chest in drawGuiContainerBackgroundLayer
	}
}