package edu.utd.minecraft.mod.polycraft.inventory.pump;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.block.LabelTexture;
import edu.utd.minecraft.mod.polycraft.block.BlockLight.Point3i;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.StatefulInventory;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.AutomaticInputBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselUpcycler;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;

public class PumpInventory extends StatefulInventory<PumpState> implements ISidedInventory {

	private static int[] accessibleSlots = new int[9];
	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		for (int i = 0; i < accessibleSlots.length; i++) {
			guiSlots.add(GuiContainerSlot.createInput(i, i, 0, 8, 2));
			accessibleSlots[i] = i;
		}
	}

	private static Inventory config;

	public static void register(final Inventory config) {
		PumpInventory.config = config;
		config.containerType = PolycraftContainerType.PUMP;
		PolycraftInventory.register(new PumpBlock(config, PumpInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	private final float flowTicksHeatIntensityRatio;
	private FlowNetwork flowNetwork = null;
	
	public PumpInventory() {
		this(PolycraftContainerType.PUMP, config);
	}

	protected PumpInventory(final PolycraftContainerType containerType, final Inventory config) {
		super(containerType, config, 84, PumpState.values());
		this.flowTicksHeatIntensityRatio = config.params.getFloat(0);
		this.addBehavior(new AutomaticInputBehavior<HeatedInventory>(false, PolycraftMod.convertSecondsToGameTicks(config.params.getDouble(1))));
		this.addBehavior(new VesselUpcycler());
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return accessibleSlots;
	}

	@Override
	public boolean canInsertItem(int var1, ItemStack var2, int var3) {
		return Fuel.getFuel(var2.getItem()) != null;
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PumpGui(this, playerInventory);
	}
	
	private static final int maxTicksPerEpoch = (int) Math.pow(2,15);

	@Override
	public synchronized void updateEntity() {
		super.updateEntity();
		if (worldObj != null && !worldObj.isRemote) {
			if (getState(PumpState.FuelTicksRemaining) == 0) {
				
				if (getState(PumpState.FuelTicksRemainingEpochs) > 0) //decrement tickEpoch
				{
					setState(PumpState.FuelTicksRemaining, maxTicksPerEpoch);	
					updateState(PumpState.FuelTicksRemainingEpochs, -1);					
				}
				
				else //the pump should go off or take next fuel
				{
					final ContainerSlot fuelSlot = getNextFuelSlot();
					if (fuelSlot != null) {
						final ItemStack fuelStack = getStackInSlot(fuelSlot);
						fuelStack.stackSize--;
						if (fuelStack.stackSize == 0)
							clearSlotContents(fuelSlot);
	
						final Fuel fuel = Fuel.getFuel(fuelStack.getItem());
						final int fuelTicksTotal = PolycraftMod.convertSecondsToGameTicks(Fuel.getHeatDurationSeconds(fuelStack.getItem()));
						
						setState(PumpState.FuelTicksRemaining, fuelTicksTotal%maxTicksPerEpoch);					
						setState(PumpState.FuelIndex, fuel.index);
						setState(PumpState.FuelTicksTotal, fuelTicksTotal%maxTicksPerEpoch);
						setState(PumpState.FuelTicksRemainingEpochs, fuelTicksTotal / maxTicksPerEpoch);
						setState(PumpState.FuelTicksTotalEpochs, fuelTicksTotal / maxTicksPerEpoch);
						setState(PumpState.FuelHeatIntensity, fuel.heatIntensity);
					}
				}
			}

			if (getState(PumpState.FuelTicksRemaining) > 0) {
				if (getState(PumpState.FlowTicksRemaining) == 0)
				{
					if (flowNetwork == null || !flowNetwork.isIntact())
						flowNetwork = new FlowNetwork();
					flowNetwork.flow();
					updateState(PumpState.FlowTicksRemaining, (int)(flowTicksHeatIntensityRatio / getState(PumpState.FuelHeatIntensity)));
				}
				else
					updateState(PumpState.FlowTicksRemaining, -1);
				updateState(PumpState.FuelTicksRemaining, -1);
				markDirty();
			}
		}
	}

	private ContainerSlot getNextFuelSlot() {
		for (final ContainerSlot inputSlot : getInputSlots()) {
			final ItemStack fuelStack = getStackInSlot(inputSlot);
			if (fuelStack != null && Fuel.getHeatDurationSeconds(fuelStack.getItem()) > 0)
				return inputSlot;
		}
		return null;
	}
	
	private Pair<Vec3, Block> getAdjacentValidPipeNetworkBlock()
	{
		return null;
	}

	private class FlowNetwork
	{
		public final Map<Vec3, Block> blocks;
		//TODO not sure if these are really ISidedInventory
		public ISidedInventory source = null;
		public ISidedInventory defaultTarget = null;
		public Map<Item, ISidedInventory> selectedTargets = null;
		//TODO improvement keep track of how far the targets are from the source and use it for flow rate
		
		public FlowNetwork()
		{
			blocks = new HashMap<Vec3, Block>();
			final Vec3 pumpCoords = Vec3.createVectorHelper(xCoord, yCoord, zCoord);
			final int pumpFlowDirection = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

			final World world = getWorldObj();
			int pipeFlowDirection = pumpFlowDirection;
			Vec3 nextSourceCoords = PolycraftMod.getAdjacentCoords(pumpCoords, pipeFlowDirection, true);
			while (source == null)
			{
				final Block sourceBlock = worldObj.getBlock((int)nextSourceCoords.xCoord, (int)nextSourceCoords.yCoord, (int)nextSourceCoords.zCoord);
				if (sourceBlock instanceof BlockPolymer) //TODO change to BlockPipe
				{
					blocks.put(nextSourceCoords, sourceBlock);
					//TODO make this respect turning pipes using getMetaData from the block, updating pipeFlowDirection
					//TODO make sure the previous pipe direction agrees with the new pipe direction
					pipeFlowDirection = worldObj.getBlockMetadata((int)nextSourceCoords.xCoord, (int)nextSourceCoords.yCoord, (int)nextSourceCoords.zCoord);
					nextSourceCoords = PolycraftMod.getAdjacentCoords(nextSourceCoords, pipeFlowDirection, true);
				}
				else if (sourceBlock instanceof PolycraftInventoryBlock)
				{
					//TODO only of the pipe direction agrees?
					source = (ISidedInventory) sourceBlock;
					break;
				}
				else
				{
					break;
				}
			}
			
			if (source != null)
			{
				Vec3 nextTargetCoords = PolycraftMod.getAdjacentCoords(pumpCoords, pipeFlowDirection, false);
				while (defaultTarget == null)
				{
					//invalid to have anything other than a polycraft inventory after having gone through the selected side of a selector
					//when we come to a selector, go ahead and navigate all the way to try to find the end target, and if its not found, bomb out 
					//if an additional selector is found with the same item as a previous selector, invalid network
					break;
				}
			}
		}
		
		public boolean isIntact()
		{
			final World world = getWorldObj();
			for (final Entry<Vec3, Block> pipeNetworkBlockEntry : blocks.entrySet())
			{
				final Vec3 pipeNetworkPoint = pipeNetworkBlockEntry.getKey();
				if (world.getBlock((int)pipeNetworkPoint.xCoord, (int)pipeNetworkPoint.yCoord, (int)pipeNetworkPoint.zCoord) != pipeNetworkBlockEntry.getValue())
				{
					return false;
				}
			}
			return true;
		}
		
		public void flow()
		{
			if (source != null && defaultTarget != null)
			{
				//TODO need to redefine the flow interval to be a constant number of ticks, and have the number of items per flow variable on heat intensity
				//TODO go through the source inputs for number of items to flow, and send them either to the default target, or the appropriate selected target (unless full)
			}
		}
	}
	
}
