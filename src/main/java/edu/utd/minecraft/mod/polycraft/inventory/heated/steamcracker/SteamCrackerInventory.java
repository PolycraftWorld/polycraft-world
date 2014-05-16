package edu.utd.minecraft.mod.polycraft.inventory.heated.steamcracker;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.InventoryPlayer;
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

public class SteamCrackerInventory extends HeatedInventory {

	private static Random random = new Random();

	private static int slotIndexInput1;
	private static int slotIndexInput2;
	private static int slotIndexCoolingWater;
	private static int slotIndexHeatingWater;
	private static int slotIndexHeatSource;
	private static int slotIndexFirstOutput;
	private static int slotIndexLastOutput;
	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput1 = guiSlots.size(), 0, 0, 17, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput2 = guiSlots.size(), 1, 0, 17, 0));
		guiSlots.add(new GuiContainerSlot(slotIndexCoolingWater = guiSlots.size(), SlotType.INPUT, 2, 0, 62, 18)); //cooling water
		guiSlots.add(new GuiContainerSlot(slotIndexHeatingWater = guiSlots.size(), SlotType.INPUT, 3, 0, 62, 54)); //heating water
		guiSlots.add(new GuiContainerSlot(slotIndexHeatSource = guiSlots.size(), SlotType.MISC, -1, -1, 26, 54)); //heat source
		slotIndexFirstOutput = guiSlots.size();
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, x, y, 116 + x * 18, 18 + y * 18)); //output
		slotIndexLastOutput = guiSlots.size() - 1;
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		SteamCrackerInventory.config = config;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, SteamCrackerInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	public SteamCrackerInventory() {
		super(PolycraftContainerType.STEAM_CRACKER, config, slotIndexHeatSource, 84);
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