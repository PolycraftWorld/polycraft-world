package edu.utd.minecraft.mod.polycraft.inventory.heated.distillationcolumn;

import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedGui;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;

public class DistillationColumnInventory extends HeatedInventory {

	private static Random random = new Random();

	private static int slotIndexInput;
	private static int slotIndexCoolingWater;
	private static int slotIndexHeatingWater;
	private static int slotIndexHeatSource;
	private static int slotIndexFirstOutput;
	private static int slotIndexLastOutput;
	public final static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		guiSlots.add(GuiContainerSlot.createInput(slotIndexInput = guiSlots.size(), 0, 0, 26, 0));
		guiSlots.add(new GuiContainerSlot(slotIndexCoolingWater = guiSlots.size(), SlotType.INPUT, 1, 0, 62, 18)); //cooling water
		guiSlots.add(new GuiContainerSlot(slotIndexHeatingWater = guiSlots.size(), SlotType.INPUT, 2, 0, 62, 54)); //heating water
		guiSlots.add(new GuiContainerSlot(slotIndexHeatSource = guiSlots.size(), SlotType.MISC, -1, -1, 26, 54)); //heat source
		slotIndexFirstOutput = guiSlots.size();
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				guiSlots.add(new GuiContainerSlot(guiSlots.size(), SlotType.OUTPUT, x, y, 116 + x * 18, 18 + y * 18)); //output
		slotIndexLastOutput = guiSlots.size() - 1;
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		DistillationColumnInventory.config = config;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, DistillationColumnInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	public DistillationColumnInventory() {
		super(PolycraftContainerType.DISTILLATION_COLUMN, config, slotIndexHeatSource, 84);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected HeatedGui getGuiHeated(InventoryPlayer playerInventory) {
		return new HeatedGui(this, playerInventory, new HeatedGui.ProgressDisplayOffsets(26, 49, 89, 36), 166);
	}

	@Override
	protected int getTotalProcessingTicksForCurrentInputs() {
		if (!canProcess())
			return 0;
		return 200; //TODO load dynamically from somewhere?
	}

	@Override
	protected int getProcessingHeatIntensityForCurrentInputs(final boolean min) {
		if (!canProcess())
			return 0;
		return 1; //TODO load dynamically from somewhere?
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
				//leave cooling and heating water intact
				if (input.slot.getSlotIndex() != slotIndexCoolingWater && input.slot.getSlotIndex() != slotIndexHeatingWater) {
					final ItemStack itemStack = getStackInSlot(input.slot);
					itemStack.stackSize -= recipe.getItemstackForInput(input, usedInputs).stackSize;
					if (itemStack.stackSize <= 0) {
						clearSlotContents(input.slot);
					}
				}
			}
		}
	}
}
