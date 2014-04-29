package edu.utd.minecraft.mod.polycraft.inventory.heated.injectionmolder;

import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.CraftingBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedGui;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;
import edu.utd.minecraft.mod.polycraft.item.ItemMold;
import edu.utd.minecraft.mod.polycraft.item.ItemMoldedItem;
import edu.utd.minecraft.mod.polycraft.item.ItemPellets;

public class InjectionMolderInventory extends HeatedInventory {

	private static Random random = new Random();

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
		guiSlots.add(new GuiContainerSlot(slotIndexCoolingWater = guiSlots.size(), SlotType.INPUT, 2, 0, 110, 55)); //cooling water
		slotIndexFirstStorage = guiSlots.size();
		slotIndexLastStorage = slotIndexFirstStorage + 3;
		for (int i = 0; i <= (slotIndexLastStorage - slotIndexFirstStorage); i++)
			guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.MISC, -1, -1, 26 + i * 18, 18));
		guiSlots.add(new GuiContainerSlot(slotIndexHeatSource = guiSlots.size(), SlotType.MISC, -1, -1, 44, 91)); //heat source
		guiSlots.add(new GuiContainerSlot(slotIndexOutput = guiSlots.size(), SlotType.OUTPUT, 0, 0, 152, 55)); //output
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		InjectionMolderInventory.config = config;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, InjectionMolderInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	public InjectionMolderInventory() {
		super(PolycraftContainerType.INJECTION_MOLDER, config, slotIndexHeatSource);
		addBehavior(new ConvergeInputsBehavior());
	}

	@Override
	protected HeatedGui getGuiHeated(InventoryPlayer playerInventory) {
		return new HeatedGui(this, playerInventory, new HeatedGui.ProgressDisplayOffsets(45, 85, 131, 55), 203);
	}

	@Override
	public boolean canProcess() {
		if (super.canProcess()) {
			final ItemStack moldItemStack = getStackInSlot(slotIndexMold);
			return moldItemStack.getItemDamage() < moldItemStack.getMaxDamage();
		}
		return false;
	}

	@Override
	protected int getTotalProcessingTicksForCurrentInputs() {
		final PolycraftRecipe recipe = PolycraftMod.recipeManager.findRecipe(containerType, getMaterials());
		if (recipe == null)
			return 0;
		final ItemMoldedItem moldedItem = (ItemMoldedItem) recipe.getOutputs().iterator().next().itemStack.getItem();
		return PolycraftMod.convertSecondsToGameTicks(moldedItem.moldedItem.craftingDurationSeconds);
	}

	@Override
	protected int getProcessingHeatIntensityForCurrentInputs(final boolean min) {
		if (!canProcess())
			return 0;
		final PolymerPellets polymerPellets = ((ItemPellets) getStackInSlot(slotIndexInput).getItem()).polymerPellets;
		return min ? polymerPellets.craftingMinHeatIntensity : polymerPellets.craftingMaxHeatIntensity;
	}

	@Override
	protected void finishProcessing() {
		Set<RecipeComponent> inputs = getMaterials();
		final PolycraftRecipe recipe = PolycraftMod.recipeManager.findRecipe(containerType, inputs);
		if (recipe != null) {
			for (final RecipeComponent output : recipe.getOutputs()) {
				if (getStackInSlot(output.slot) == null) {
					setStackInSlot(output.slot, output.itemStack.copy());
				} else {
					getStackInSlot(output.slot).stackSize += output.itemStack.stackSize;
				}
			}
			final Set<RecipeInput> usedInputs = Sets.newHashSet();
			for (final RecipeComponent input : ImmutableList.copyOf(inputs)) {
				//leave cooling water intact
				if (input.slot.getSlotIndex() != slotIndexCoolingWater) {
					final ItemStack itemStack = getStackInSlot(input.slot);
					if (input.slot.getSlotIndex() == slotIndexMold) {
						//damage molds
						itemStack.attemptDamageItem(ItemMold.getDamagePerUse(itemStack), random);
					}
					else {
						//use up regular inputs (pellets)
						itemStack.stackSize -= recipe.getItemstackForInput(input, usedInputs).stackSize;
						if (itemStack.stackSize <= 0) {
							clearSlotContents(input.slot);
						}
					}
				}
			}
		}
	}

	//automatically moves inputs into the target (center) input
	private class ConvergeInputsBehavior extends CraftingBehavior {
		@Override
		public boolean updateEntity(final PolycraftInventory inventory, final World world) {
			if (!worldObj.isRemote) {
				final ItemStack targetInputItemStack = getStackInSlot(slotIndexInput);
				if (targetInputItemStack == null || targetInputItemStack.stackSize < targetInputItemStack.getMaxStackSize()) {
					for (int i = slotIndexFirstStorage; i <= slotIndexLastStorage; i++) {
						if (converge(inventory, i, targetInputItemStack))
							return true;
					}
				}
			}
			return true;
		}

		private boolean converge(final PolycraftInventory inventory, int sourceIndex, final ItemStack targetInputItemStack) {
			final ItemStack sourceInputItemStack = getStackInSlot(sourceIndex);
			if (sourceInputItemStack != null) {
				if (targetInputItemStack == null || sourceInputItemStack.isItemEqual(targetInputItemStack)) {
					if (sourceInputItemStack.stackSize == 1)
						setInventorySlotContents(sourceIndex, null);
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
