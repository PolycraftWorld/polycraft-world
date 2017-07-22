package edu.utd.minecraft.mod.polycraft.inventory.computer;

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
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;

public class ComputerInventory extends PolycraftInventory {

	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		ComputerInventory.config = config;
		config.containerType = PolycraftContainerType.COMPUTER;
		PolycraftInventory.register(new ComputerBlock(config, ComputerInventory.class));
	}

	public ComputerInventory() {
		super(PolycraftContainerType.COMPUTER, config);
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new ComputerContainer(playerInventory, this);
//		return new PolycraftCraftingContainerGeneric(this, playerInventory, 128);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new ComputerGui(this, playerInventory, 459/2, 511/2);
//		return new ComputerGui(this, playerInventory, 342/2, 330/2);
		//return new PolycraftInventoryGui(this, playerInventory, 200, 208, true);
	}

}
