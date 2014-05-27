package edu.utd.minecraft.mod.polycraft.inventory.heated;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryBehavior;
import edu.utd.minecraft.mod.polycraft.item.ItemMold;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
import edu.utd.minecraft.mod.polycraft.item.PolycraftMoldedItem;

public abstract class MolderInventory extends HeatedInventory {

	private static int slotIndexInput;
	private static int slotIndexFirstStorage;
	private static int slotIndexLastStorage;
	private static int slotIndexMold;
	private static int slotIndexCoolingWater;
	private static int slotIndexHeatSource;
	private static int slotIndexOutput;
	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput = guiSlots.size(), 0, 0, 8, 0));
		guiSlots.add(new GuiContainerSlot(slotIndexMold = guiSlots.size(), SlotType.INPUT, 1, 0, 90, 55)); //mold
		guiSlots.add(new GuiContainerSlot(slotIndexCoolingWater = guiSlots.size(), SlotType.MISC, -1, -1, 110, 55)); //cooling water
		slotIndexFirstStorage = guiSlots.size();
		slotIndexLastStorage = slotIndexFirstStorage + 3;
		for (int i = 0; i <= (slotIndexLastStorage - slotIndexFirstStorage); i++)
			guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.MISC, -1, -1, 26 + i * 18, 18));
		guiSlots.add(new GuiContainerSlot(slotIndexHeatSource = guiSlots.size(), SlotType.MISC, -1, -1, 44, 91)); //heat source
		guiSlots.add(new GuiContainerSlot(slotIndexOutput = guiSlots.size(), SlotType.OUTPUT, 0, 0, 152, 55)); //output
	}

	public MolderInventory(final PolycraftContainerType containerType, final Inventory config) {
		super(containerType, config, slotIndexHeatSource, slotIndexCoolingWater, -1);
		addBehavior(new ConvergeInputsBehavior());
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected HeatedGui getGuiHeated(InventoryPlayer playerInventory) {
		return new HeatedGui(this, playerInventory, new HeatedGui.ProgressDisplayOffsets(45, 85, 131, 55), 203);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		if (super.canInsertItem(slot, item, side)) {
			if (item.getItem() instanceof ItemMold)
				return slot == slotIndexMold;
			return true;
		}
		return false;
	}

	@Override
	public boolean canProcess() {
		if (super.canProcess()) {
			final ItemStack moldItemStack = getStackInSlot(slotIndexMold);
			if (moldItemStack != null) {
				return moldItemStack.getItemDamage() < moldItemStack.getMaxDamage();
			}
		}
		return false;
	}

	@Override
	protected int getTotalProcessingTicksForCurrentInputs() {
		final PolycraftRecipe recipe = PolycraftMod.recipeManager.findRecipe(containerType, getMaterials());
		if (recipe == null) {
			return 0;
		}
		if (recipe.getOutputs(this).size() == 0) {
			return 0;
		}
		RecipeComponent next = recipe.getOutputs(this).iterator().next();
		if (next == null || next.itemStack == null || next.itemStack.getItem() == null) {
			return 0;
		}
		if (next.itemStack.getItem() instanceof PolycraftMoldedItem) {
			final PolycraftMoldedItem moldedItem = (PolycraftMoldedItem) recipe.getOutputs(this).iterator().next().itemStack.getItem();
			return PolycraftMod.convertSecondsToGameTicks(moldedItem.getMoldedItem().craftingDurationSeconds);
		}
		return 0;
	}

	@Override
	protected int getProcessingHeatIntensityForCurrentInputs(final boolean min) {
		final PolymerPellets polymerPellets = ((ItemVessel<PolymerPellets>) getStackInSlot(slotIndexInput).getItem()).cofig;
		return min ? polymerPellets.craftingMinHeatIntensity : polymerPellets.craftingMaxHeatIntensity;
	}

	@Override
	protected void finishProcessingInput(final int slotIndex, final ItemStack actualInput, final ItemStack recipeInput) {
		//leave cooling water intact
		if (slotIndex != slotIndexCoolingWater) {
			if (slotIndex == slotIndexMold) {
				//damage molds
				actualInput.attemptDamageItem(ItemMold.getDamagePerUse(actualInput), random);
				if (actualInput.getItemDamage() == actualInput.getMaxDamage())
					setInventorySlotContents(slotIndex, null);
			}
			else {
				//use up regular inputs (pellets)
				actualInput.stackSize -= recipeInput.stackSize;
				if (actualInput.stackSize <= 0) {
					setInventorySlotContents(slotIndex, null);
				}
			}
		}
	}

	//automatically moves storage slots into the input slot
	private class ConvergeInputsBehavior extends InventoryBehavior<MolderInventory> {

		@Override
		public boolean updateEntity(final MolderInventory inventory, final World world) {
			for (int sourceIndex = slotIndexFirstStorage; sourceIndex <= slotIndexLastStorage; sourceIndex++)
				if (converge(inventory, sourceIndex, slotIndexInput))
					break;
			return true;
		}

		private boolean converge(final MolderInventory inventory, final int sourceIndex, final int targetIndex) {
			final ItemStack sourceInputItemStack = getStackInSlot(sourceIndex);
			if (sourceInputItemStack != null) {
				final ItemStack targetInputItemStack = getStackInSlot(targetIndex);
				if (targetInputItemStack == null || sourceInputItemStack.isItemEqual(targetInputItemStack)) {
					if (sourceInputItemStack.stackSize == 1)
						inventory.setInventorySlotContents(sourceIndex, null);
					else
						sourceInputItemStack.stackSize--;
					if (targetInputItemStack == null)
						inventory.setInventorySlotContents(slotIndexInput, new ItemStack(sourceInputItemStack.getItem()));
					else
						targetInputItemStack.stackSize++;
					inventory.markDirty();
					return true;
				}
			}
			return false;
		}
	}
}
