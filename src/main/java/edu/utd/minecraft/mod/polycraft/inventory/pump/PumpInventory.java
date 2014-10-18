package edu.utd.minecraft.mod.polycraft.inventory.pump;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.block.BlockPipe;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.block.LabelTexture;
import edu.utd.minecraft.mod.polycraft.block.BlockLight.Point3i;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryHelper;
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

	private final int flowTickHeatIntensityRatio;
	private final int flowItemsPerHeatIntensity;
	
	public PumpInventory() {
		this(PolycraftContainerType.PUMP, config);
	}

	protected PumpInventory(final PolycraftContainerType containerType, final Inventory config) {
		super(containerType, config, 128, PumpState.values());
		this.flowTickHeatIntensityRatio = config.params.getInt(0);
		this.flowItemsPerHeatIntensity = config.params.getInt(1);
		//this.addBehavior(new AutomaticInputBehavior<HeatedInventory>(false, PolycraftMod.convertSecondsToGameTicks(config.params.getDouble(2))));
		this.addBehavior(new VesselUpcycler());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PumpGui(this, playerInventory);
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
					getFlowNetwork().flow(flowItemsPerHeatIntensity * getState(PumpState.FuelHeatIntensity));   //TODO: game balancing for flow rate here
					updateState(PumpState.FlowTicksRemaining, flowTickHeatIntensityRatio/getState(PumpState.FuelHeatIntensity)); //TODO: game balancing for flow rate here
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
	
	public FlowNetwork getFlowNetwork()
	{
		return new FlowNetwork(Vec3.createVectorHelper(xCoord, yCoord, zCoord));
	}

	public class FlowNetwork
	{
		public class Terminal
		{
			public final Vec3 coords;
			public final IInventory inventory;
			public final int distanceFromPump;
			
			public Terminal(final Vec3 coords, final IInventory inventory, final int distanceFromPump)
			{
				this.coords = coords;
				this.inventory = inventory;
				this.distanceFromPump = distanceFromPump;
			}
		}

		public final Vec3 pumpCoords;
		public final int pumpFlowDirection;
		public final Set<String> coordsUsed = new HashSet<String>();
		public Terminal source;
		public Terminal defaultTarget;
		public Map<Item, Terminal> regulatedTargets;
		
		public FlowNetwork(final Vec3 pumpCoords)
		{
			this.pumpCoords = pumpCoords;
			pumpFlowDirection = worldObj.getBlockMetadata((int)pumpCoords.xCoord, (int)pumpCoords.yCoord, (int)pumpCoords.zCoord);
			coordsUsed.add(getHashVec3(pumpCoords));
			//find the source (going the opposite direction of the flow, starting at the pump)
			source = findNetworkSource(pumpCoords, pumpFlowDirection);
			if (source != null) {
				//find the targets (going the direction of the flow, starting at the pump)
				regulatedTargets = new HashMap<Item, Terminal>();
				defaultTarget = findNetworkTargetInventories(pumpCoords, pumpFlowDirection, false, 0);
			}
		}
		
		public int flow(int numItems)
		{
			int itemsFlowed = 0;
			if (isValid()) {
				while (numItems > 0) {
					int i = 0;
					for (; i < source.inventory.getSizeInventory(); ++i) {
						ItemStack itemstack = source.inventory.getStackInSlot(i);
						if (itemstack != null) {
							Terminal target = regulatedTargets.get(itemstack.getItem());
							if (target == null)
								target = defaultTarget;
							if (InventoryHelper.transfer(target.inventory, source.inventory, i, 0)) {
								numItems--;
								itemsFlowed++;
								//go back out the while loop to ensure we are supposed to send more items,
								//and to keep trying if there are more that we can send from this slot
								break;
							}
						}
					}
					//there are no (more) items to flow, so stop trying
					if (i == source.inventory.getSizeInventory())
						break;
				}
			}
			return itemsFlowed;
		}
		
		public String getHashVec3(final Vec3 vec3) {
			return (int)vec3.xCoord + "." + (int)vec3.yCoord + "." + (int)vec3.zCoord;
		}
		
		public boolean isValid()
		{
			return source != null && defaultTarget != null && regulatedTargets != null;
		}

		private Terminal findNetworkSource(Vec3 coords, int flowDirection) {
			int distanceFromPump = 0;
			while (true) {
				coords = PolycraftMod.getAdjacentCoords(coords, flowDirection, true);
				final String hash = (int)coords.xCoord + "." + (int)coords.yCoord + "." + (int)coords.zCoord;
				//don't allow networks that flow back in on themselves
				if (coordsUsed.contains(hash))
					return null;
				coordsUsed.add(getHashVec3(coords));
				final Block block = getBlockAtVec3(coords);
				if (block instanceof BlockPipe)
					flowDirection = getBlockMetadataAtVec3(coords);
				else
				{
					IInventory sourceInventory = getInventoryAtVec3(coords);
					if (sourceInventory != null)
						return new Terminal(coords, sourceInventory, distanceFromPump);
					return null;
				}
				distanceFromPump++;
			}
		}
		
		private IInventory getInventoryAtVec3(final Vec3 vec3) {
			return PolycraftMod.getInventoryAt(worldObj, (int)vec3.xCoord, (int)vec3.yCoord, (int)vec3.zCoord);
		}

	    private final ForgeDirection[][] REGULATED_DIRECTIONS = {
	        { ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST }, //DOWN
	        { ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST }, //UP
	    	{ ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.UP, ForgeDirection.DOWN }, //NORTH
	    	{ ForgeDirection.WEST, ForgeDirection.EAST, ForgeDirection.UP, ForgeDirection.DOWN }, //SOUTH
	    	{ ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.UP, ForgeDirection.DOWN }, //WEST
	    	{ ForgeDirection.SOUTH, ForgeDirection.NORTH, ForgeDirection.UP, ForgeDirection.DOWN }, //EAST
	    };
	    
		private Terminal findNetworkTargetInventories(Vec3 coords, int flowDirection, final boolean regulatorPath, final int regulatorDistanceFromPump) {
			int distanceFromPump = 0;
			while (regulatedTargets != null) {
				coords = PolycraftMod.getAdjacentCoords(coords, flowDirection, false);
				final String hash = getHashVec3(coords);
				//don't allow networks that flow back in on themselves
				if (coordsUsed.contains(hash))
					return null;
				coordsUsed.add(hash);
				final Block block = getBlockAtVec3(coords);
				if (block instanceof BlockPipe) {
					flowDirection = getBlockMetadataAtVec3(coords);
				}
				//only check for regulators if we are not on a regulator path already
				else if (!regulatorPath && block instanceof FlowRegulatorBlock) {
					flowDirection = getBlockMetadataAtVec3(coords);
					ForgeDirection forgeFlowDirection = ForgeDirection.values()[flowDirection];
					final IInventory regulatorInventory = getInventoryAtVec3(coords);
					//regulator order will be left, right, bottom, top given a starting orientation
					//for example, if the starting orienation is facing SOUTH, then the order will be:
					//WEST, EAST, BOTTOM, TOP
					for (int i = 0; i < regulatorInventory.getSizeInventory(); i++)
					{
						final ItemStack regulatorItemStack = regulatorInventory.getStackInSlot(i);
						if (regulatorItemStack != null) {
							//if we have already regulated this item, this is an invalid network
							if (regulatedTargets.containsKey(regulatorItemStack.getItem())) {
								regulatedTargets = null;
								return null;
							}
							final Terminal regulatedTarget = findNetworkTargetInventories(coords, REGULATED_DIRECTIONS[flowDirection][i].ordinal(), true, distanceFromPump);
							if (regulatedTarget != null)
								regulatedTargets.put(regulatorItemStack.getItem(), regulatedTarget);
						}
					}
				}
				else {
					final IInventory inventory = getInventoryAtVec3(coords);
					if (inventory != null)
						return new Terminal(coords, inventory, regulatorPath ? distanceFromPump + regulatorDistanceFromPump : distanceFromPump);
					return null;
				}
				distanceFromPump++;
			}
			return null;
		}
		
		private Block getBlockAtVec3(final Vec3 vec3) {
			return worldObj.getBlock((int)vec3.xCoord, (int)vec3.yCoord, (int)vec3.zCoord);
		}
		
		private int getBlockMetadataAtVec3(final Vec3 vec3) {
			return worldObj.getBlockMetadata((int)vec3.xCoord, (int)vec3.yCoord, (int)vec3.zCoord);
		}
	}
}
