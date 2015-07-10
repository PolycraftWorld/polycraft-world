package edu.utd.minecraft.mod.polycraft.inventory.heated.contactprinter;

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

public class ContactPrinterInventory extends HeatedInventory {

	private static Random random = new Random();

	public static int slotIndexUVLight;
	public static int slotIndexChromeMask;
	public static int slotIndexPhotoresist;
	public static int slotIndexWafer;
	public static int slotIndexHeatSource;
	public static int slotIndexFirstOutput;
	public static int slotIndexLastOutput;
	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		guiSlots.add(GuiContainerSlot.createInput(slotIndexUVLight = guiSlots.size(), 0, 0, 8, 0)); //mask
		guiSlots.add(GuiContainerSlot.createInput(slotIndexChromeMask = guiSlots.size(), 0, 1, 62, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexPhotoresist = guiSlots.size(), 1, 0, 44, 0));
		guiSlots.add(GuiContainerSlot.createInput(slotIndexWafer = guiSlots.size(), 1, 1, 44, 18));
		guiSlots.add(new GuiContainerSlot(slotIndexHeatSource = guiSlots.size(), SlotType.MISC, -1, -1, 26, 54)); //heat source
		slotIndexFirstOutput = guiSlots.size();

		guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, 0, 0, 116 + 0 * 18, 18 + 0 * 18)); //output
		guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, 2, 0, 116 + 2 * 18, 18 + 0 * 18)); //output
		guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, 2, 2, 116 + 2 * 18, 18 + 2 * 18)); //output

		slotIndexLastOutput = guiSlots.size() - 1;
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		ContactPrinterInventory.config = config;
		config.containerType = PolycraftContainerType.CONTACT_PRINTER;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, ContactPrinterInventory.class));
	}

	public ContactPrinterInventory() {
		super(PolycraftContainerType.CONTACT_PRINTER, config, 84, slotIndexHeatSource, -1, -1);
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
