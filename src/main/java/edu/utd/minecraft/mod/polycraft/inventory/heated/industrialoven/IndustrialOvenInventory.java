package edu.utd.minecraft.mod.polycraft.inventory.heated.industrialoven;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedGui;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;

public class IndustrialOvenInventory extends HeatedInventory {

	private static Random random = new Random();

	public static int slotIndexHeatingWater;
	public static int slotIndexHeatSource;
	public static int slotIndexFirstOutput;
	public static int slotIndexLastOutput;
	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {

		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.INPUT, x, y, 9 + x * 18, 18 + y * 18)); //inputs
				
		guiSlots.add(new GuiContainerSlot(slotIndexHeatingWater = guiSlots.size(), SlotType.MISC, -1, -1, 71, 18, Items.water_bucket)); //heating water
		guiSlots.add(new GuiContainerSlot(slotIndexHeatSource = guiSlots.size(), SlotType.MISC, -1, -1, 71, 54)); //heat source
		slotIndexFirstOutput = guiSlots.size();
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, x, y, 116 + x * 18, 18 + y * 18)); //output
		slotIndexLastOutput = guiSlots.size() - 1;
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		IndustrialOvenInventory.config = config;
		config.containerType = PolycraftContainerType.INDUSTRIAL_OVEN;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, IndustrialOvenInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	public IndustrialOvenInventory() {
		super(PolycraftContainerType.INDUSTRIAL_OVEN, config, 84, slotIndexHeatSource, -1, slotIndexHeatingWater);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected HeatedGui getGuiHeated(InventoryPlayer playerInventory) {
		return new HeatedGui(this, playerInventory, new HeatedGui.ProgressDisplayOffsets(26, 49, 89, 36), 166);
	}

	@Override
	protected void finishProcessingInput(final int slotIndex, final ItemStack actualInput, final ItemStack recipeInput) {
		//leave heating water intact
		if (slotIndex != slotIndexHeatingWater) {
			actualInput.stackSize -= recipeInput.stackSize;
			if (actualInput.stackSize <= 0) {
				setInventorySlotContents(slotIndex, null);
			}
		}
	}
}
