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

	private final int flowTicks;
	private final int flowItemsPerHeatIntensity;
	
	public PumpInventory() {
		this(PolycraftContainerType.PUMP, config);
	}

	protected PumpInventory(final PolycraftContainerType containerType, final Inventory config) {
		super(containerType, config, 84, PumpState.values());
		this.flowTicks = config.params.getInt(0);
		this.flowItemsPerHeatIntensity = config.params.getInt(1);
		this.addBehavior(new AutomaticInputBehavior<HeatedInventory>(false, PolycraftMod.convertSecondsToGameTicks(config.params.getDouble(2))));
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
					flow(flowItemsPerHeatIntensity * getState(PumpState.FuelHeatIntensity));
					updateState(PumpState.FlowTicksRemaining, flowTicks);
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
	
	public void flow(int numItems) {
		final Vec3 coords = Vec3.createVectorHelper(xCoord, yCoord, zCoord);
		final int flowDirection = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		//find the source (going the opposite direction of the flow, starting at the pump)
		final IInventory source = findNetworkInventory(coords, flowDirection, true);
		if (source != null) {
			//find the target (going the direction of the flow, starting at the pump)
			final IInventory target = findNetworkInventory(coords, flowDirection, false);
			if (target != null && target != source) {
				while (numItems > 0) {
					int i = 0;
					for (; i < source.getSizeInventory(); ++i) {
						ItemStack itemstack = source.getStackInSlot(i);
						if (itemstack != null) {
							if (InventoryHelper.transfer(target, source, i, 0)) {
								numItems--;
								//go back out the while loop to ensure we are supposed to send more items,
								//and to keep trying if there are more that we can send from this slot
								break;
							}
						}
					}
					//there are no (more) items to flow, so stop trying
					if (i == source.getSizeInventory())
						break;
				}
			}
		}
	}
	
	private IInventory findNetworkInventory(Vec3 coords, int flowDirection, final boolean searchAgainstflow) {
		final Set<String> seen = new HashSet<String>();
		while (true) {
			coords = PolycraftMod.getAdjacentCoords(coords, flowDirection, searchAgainstflow);
			final String hash = (int)coords.xCoord + "." + (int)coords.yCoord + "." + (int)coords.zCoord;
			//don't allow networks that flow back in on themselves
			if (seen.contains(hash))
				return null;
			seen.add(hash);
			final Block block = getBlockAtVec3(coords);
			if (block instanceof BlockPipe) {
				flowDirection = getBlockMetadataAtVec3(coords);
			}
			//TODO add support for selector
			else {
				//will return null if the block at the coordinates is not an inventory
				return PolycraftMod.getInventoryAt(worldObj, (int)coords.xCoord, (int)coords.yCoord, (int)coords.zCoord);
			}
		}
	}
	
	private Block getBlockAtVec3(final Vec3 vec3) {
		return worldObj.getBlock((int)vec3.xCoord, (int)vec3.yCoord, (int)vec3.zCoord);
	}
	
	private int getBlockMetadataAtVec3(final Vec3 vec3) {
		return worldObj.getBlockMetadata((int)vec3.xCoord, (int)vec3.yCoord, (int)vec3.zCoord);
	}
	
}
