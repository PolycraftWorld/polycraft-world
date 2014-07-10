package edu.utd.minecraft.mod.polycraft.inventory.heated.industrialoven;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipeManager;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedGui;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventoryState;

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
	
	
	@Override
	protected void finishProcessing() {
		//Set<RecipeComponent> inputs = getMaterials();
		for (int inputNum = 0; inputNum <10; inputNum++)
		{
			Set<RecipeComponent> inputs = Sets.newHashSet();
			inputs.add(new RecipeComponent(inputNum, getStackInSlot(inputNum)));
			
			final PolycraftRecipe recipe = PolycraftMod.recipeManager.findRecipe(PolycraftContainerType.FURNACE, inputs);
			ItemStack[] outputs = returnValidOutputs();
			if (recipe != null) {
				for (final RecipeComponent output : recipe.getOutputs(this)) {
					if (getStackInSlot(output.slot) == null)
						setStackInSlot(output.slot, output.itemStack.copy());
					else
						getStackInSlot(output.slot).stackSize += output.itemStack.stackSize;
	
				}
				final Set<RecipeInput> usedInputs = Sets.newHashSet();
				for (final RecipeComponent input : ImmutableList.copyOf(inputs))
					finishProcessingInput(input.slot.getSlotIndex(), getStackInSlot(input.slot), recipe.getItemstackForInput(input, usedInputs));
			}
			else
			{
				
				outputs[inputNum] = FurnaceRecipes.smelting().getSmeltingResult(getStackInSlot(inputNum));
				
				
			}
		}
	}
	
	
	protected ItemStack[] returnValidOutputs()
	{
		ItemStack[] outputs = new ItemStack[9];
		int filledSlots = 0, inputNum = 0;
		for(GuiContainerSlot iterIndex : guiSlots)
		{
			if (iterIndex.getSlotType() == SlotType.INPUT)
			{
				Item itemInOven = iterIndex.getItem(); 
				if (itemInOven != null)						
				{
					outputs[inputNum] = FurnaceRecipes.smelting().getSmeltingResult(getStackInSlot(inputNum));						
					
					if (outputs[inputNum] == null )
					{					
						Set<RecipeComponent> inputs = Sets.newHashSet();
						inputs.add(new RecipeComponent(inputNum, getStackInSlot(inputNum)));
						PolycraftRecipe rcp = PolycraftMod.recipeManager.findRecipe(PolycraftContainerType.INDUSTRIAL_OVEN, inputs);
						Collection <RecipeComponent> crc = rcp.getOutputs(this);
						if (crc.iterator().hasNext())
						{
							RecipeComponent rc = crc.iterator().next();
							if (rc.slot.getSlotType() == SlotType.OUTPUT)
							{
								if (rc.itemStack != null)
									outputs[inputNum] = rc.itemStack;
								filledSlots++;
							}							
								
						}
					}
					else
						filledSlots++;
				}	
				inputNum++;
			}		
			
		}
		if (filledSlots>0)
			return outputs;
		else
			return null;
	}
	
	
	@Override
	public void updateEntity() {

		for (InventoryBehavior behavior : this.getBehaviors())
			if (behavior.updateEntity(this, this.worldObj))
				return;

		boolean isDirty = false;

		if (isHeated())
			updateState(HeatedInventoryState.HeatSourceTicksRemaining, -1);

		if (!worldObj.isRemote) {
			if (canProcess()) {
				//ItemStack[] outputs = returnValidOutputs();
				//if (outputs != null)
				//{
					if (!isHeated()) {
						final ItemStack heatSourceItemStack = getStackInSlot(slotIndexHeatSource);
						if (heatSourceItemStack != null) {
							setState(HeatedInventoryState.HeatSourceTicksRemaining, setState(HeatedInventoryState.HeatSourceTicksTotal,
									PolycraftMod.convertSecondsToGameTicks(Fuel.getHeatDurationSeconds(heatSourceItemStack.getItem()))));
							setState(HeatedInventoryState.HeatSourceIntensity, Fuel.getHeatIntensity(heatSourceItemStack.getItem()));
							--heatSourceItemStack.stackSize;
							if (heatSourceItemStack.stackSize == 0)
								setInventorySlotContents(slotIndexHeatSource, heatSourceItemStack.getItem().getContainerItem(heatSourceItemStack));
							isDirty = true;
						}
					}
	
					if (isHeated() && isHeatIntensityValid()) {
						if (updateState(HeatedInventoryState.ProcessingTicks, 1) == getTotalProcessingTicksForCurrentInputs()) {
							finishProcessing();
							setState(HeatedInventoryState.ProcessingTicks, 0);
							isDirty = true;
						}
					}
					else
						setState(HeatedInventoryState.ProcessingTicks, 0);
				//}
			}
			else
				setState(HeatedInventoryState.ProcessingTicks, 0);
		}

		if (isDirty)
			markDirty();
	}
	
	
	
	
	
}
