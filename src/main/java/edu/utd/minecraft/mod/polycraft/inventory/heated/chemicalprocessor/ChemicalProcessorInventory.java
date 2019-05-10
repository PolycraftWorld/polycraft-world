package edu.utd.minecraft.mod.polycraft.inventory.heated.chemicalprocessor;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedGui;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;

public class ChemicalProcessorInventory extends HeatedInventory {

	private static Random random = new Random();

	public static int slotIndexInput1;
	public static int slotIndexInput2;
	public static int slotIndexInput3;
	public static int slotIndexInput4;
	public static int slotIndexInput5;
	public static int slotIndexCoolingWater;
	public static int slotIndexHeatingWater;
	public static int slotIndexHeatSource;
	public static int slotIndexFirstOutput;
	public static int slotIndexLastOutput;
	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput1 = guiSlots.size(), 0, 0, 8, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput2 = guiSlots.size(), 1, 0, 8, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput3 = guiSlots.size(), 2, 0, 8, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput4 = guiSlots.size(), 0, 1, 8, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput5 = guiSlots.size(), 1, 1, 26, 0));
		guiSlots.add(new GuiContainerSlot(slotIndexCoolingWater = guiSlots.size(), SlotType.MISC, -1, -1, 71, 18, Items.water_bucket)); //cooling water
		guiSlots.add(new GuiContainerSlot(slotIndexHeatingWater = guiSlots.size(), SlotType.MISC, -1, -1, 71, 54, Items.water_bucket)); //heating water
		guiSlots.add(new GuiContainerSlot(slotIndexHeatSource = guiSlots.size(), SlotType.MISC, -1, -1, 26, 54)); //heat source
		slotIndexFirstOutput = guiSlots.size();
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, x, y, 116 + x * 18, 18 + y * 18)); //output
		slotIndexLastOutput = guiSlots.size() - 1;
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		ChemicalProcessorInventory.config = config;
		config.containerType = PolycraftContainerType.CHEMICAL_PROCESSOR;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, ChemicalProcessorInventory.class));
	}

	public ChemicalProcessorInventory() {
		super(PolycraftContainerType.CHEMICAL_PROCESSOR, config, 84, slotIndexHeatSource, slotIndexCoolingWater, slotIndexHeatingWater);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected HeatedGui getGuiHeated(InventoryPlayer playerInventory) {
		return new HeatedGui(this, playerInventory, new HeatedGui.ProgressDisplayOffsets(26, 49, 89, 36), 166);
	}

	@Override
	protected void finishProcessingInput(final int slotIndex, final ItemStack actualInput, final ItemStack recipeInput) {
		//leave cooling and heating water intact
		if (slotIndex != slotIndexCoolingWater && slotIndex != slotIndexHeatingWater) {
			actualInput.stackSize -= recipeInput.stackSize;
			if (actualInput.stackSize <= 0) {
				setInventorySlotContents(slotIndex, null);
			}
		}
	}
}
