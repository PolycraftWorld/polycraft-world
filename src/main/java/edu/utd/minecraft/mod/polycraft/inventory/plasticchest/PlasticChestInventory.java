package edu.utd.minecraft.mod.polycraft.inventory.plasticchest;

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
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselMerger;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselUpcycler;

public class PlasticChestInventory extends PolycraftInventory {

	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		for (int y = 0; y < 6; y++)
			for (int x = 0; x < 12; x++)
				guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.INPUT, x, y, 8 + x * 18, 18 + y * 18)); //output
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		PlasticChestInventory.config = config;
		config.containerType = PolycraftContainerType.PLASTIC_CHEST;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, PlasticChestInventory.class));
	}

	public PlasticChestInventory() {
		super(PolycraftContainerType.PLASTIC_CHEST, config);
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
