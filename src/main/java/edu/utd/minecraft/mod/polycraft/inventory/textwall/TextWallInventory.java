package edu.utd.minecraft.mod.polycraft.inventory.textwall;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.computer.ComputerBlock;
import edu.utd.minecraft.mod.polycraft.inventory.computer.ComputerInventory;

public class TextWallInventory extends PolycraftInventory {
	
	private static Inventory config;
	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	
	public static final void register(final Inventory config) {
		TextWallInventory.config = config;
		config.containerType = PolycraftContainerType.TEXT_WALL;
		PolycraftInventory.register(new TextWallBlock(config, TextWallInventory.class));
	}

	
	public TextWallInventory(PolycraftContainerType containerType, Inventory config) {
		super(containerType, config);
		// TODO Auto-generated constructor stub
	}

}
