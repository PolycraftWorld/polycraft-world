package edu.utd.minecraft.mod.polycraft.inventory.pump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockPipe;
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.ElementVessel;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.GameIdentifiedConfig;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryHelper;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.StatefulInventory;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselUpcycler;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;

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
		PolycraftInventory.register(new PumpBlock(config, PumpInventory.class));
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

	private static final int maxTicksPerEpoch = (int) Math.pow(2, 15);

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

						setState(PumpState.FuelTicksRemaining, fuelTicksTotal % maxTicksPerEpoch);
						setState(PumpState.FuelIndex, fuel.index);
						setState(PumpState.FuelTicksTotal, fuelTicksTotal % maxTicksPerEpoch);
						setState(PumpState.FuelTicksRemainingEpochs, fuelTicksTotal / maxTicksPerEpoch);
						setState(PumpState.FuelTicksTotalEpochs, fuelTicksTotal / maxTicksPerEpoch);
						setState(PumpState.FuelHeatIntensity, fuel.heatIntensity);
					}
				}
			}

			if (getState(PumpState.FuelTicksRemaining) > 0) {
				if (getState(PumpState.FlowTicksRemaining) == 0)
				{
					getFlowNetwork().flow(flowItemsPerHeatIntensity * getState(PumpState.FuelHeatIntensity)); //TODO: game balancing for flow rate here
					updateState(PumpState.FlowTicksRemaining, flowTickHeatIntensityRatio / getState(PumpState.FuelHeatIntensity)); //TODO: game balancing for flow rate here
				}
				else
					updateState(PumpState.FlowTicksRemaining, -1);
				updateState(PumpState.FuelTicksRemaining, -1);
				markDirty();
			}
		}
	}

	public ContainerSlot getNextFuelSlot() {
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
			public ItemStack itemStackInFlowRegulator; //this is set when the target is regulated to a certain direction

			public Terminal(final Vec3 coords, final IInventory inventory, final int distanceFromPump)
			{
				this.coords = coords;
				this.inventory = inventory;
				this.distanceFromPump = distanceFromPump;
			}
		}

		public class ExplicitTerminal extends Terminal
		{

			public ExplicitTerminal(Vec3 coords, IInventory inventory, int distanceFromPump) {
				super(coords, inventory, distanceFromPump);

			}
		}

		public class FuelTerminal extends ExplicitTerminal
		{

			public FuelTerminal(Vec3 coords, IInventory inventory, int distanceFromPump) {
				super(coords, inventory, distanceFromPump);
				// TODO Auto-generated constructor stub
			}

		}

		public class InputTerminal extends ExplicitTerminal
		{

			public final int offsetIndex;

			public InputTerminal(Vec3 coords, IInventory inventory, int distanceFromPump, int offsetIndex) {
				super(coords, inventory, distanceFromPump);
				this.offsetIndex = offsetIndex;

			}

		}

		public final Vec3 pumpCoords;
		public final int pumpFlowDirection;
		public final Set<String> coordsUsed = new HashSet<String>();
		public Terminal source;
		public Terminal defaultTarget;
		public Map<Item, ArrayList<Terminal>> regulatedTargets;
		public boolean pumpShutOffValve;

		public FlowNetwork(final Vec3 pumpCoords)
		{
			this.pumpCoords = pumpCoords;
			pumpFlowDirection = worldObj.getBlockMetadata((int) pumpCoords.xCoord, (int) pumpCoords.yCoord, (int) pumpCoords.zCoord);
			coordsUsed.add(getHashVec3(pumpCoords));
			//find the source (going the opposite direction of the flow, starting at the pump)

			source = findNetworkSource(pumpCoords, pumpFlowDirection);
			if (source != null) {
				//find the targets (going the direction of the flow, starting at the pump)
				regulatedTargets = new HashMap<Item, ArrayList<Terminal>>();
				defaultTarget = findNetworkTargetInventories(pumpCoords, pumpFlowDirection, false, 0);
			}

		}

		public int flow(int numItems)
		{
			int itemsFlowed = 0;
			if (isValid()) {

				while (numItems > 0) {
					int i = 0;
					boolean breakTransfer = false;
					boolean waitForUpcycling = false;
					for (; i < source.inventory.getSizeInventory(); ++i) {
						ItemStack regulatedItemStack = source.inventory.getStackInSlot(i); //number of items in the source			
						if (regulatedItemStack != null) {
							ArrayList<Terminal> targets = regulatedTargets.get(regulatedItemStack.getItem());
							if (targets == null) { //check to see if we can find a larger version of this vessel		
								ItemVessel largerVessel = getLargerVessel(regulatedItemStack.getItem());
								targets = regulatedTargets.get(largerVessel);
								if (targets == null) { //check to see if we can find a yet larger version of this vessel	
									largerVessel = getLargerVessel(largerVessel);
									targets = regulatedTargets.get(largerVessel);
								}

								if (targets != null) {
									//this means we have found a larger vessel and want to hold smaller vessels upstream
									waitForUpcycling = true;
									continue;
								}

								else {
									if ((defaultTarget instanceof ExplicitTerminal)) {
										if (InventoryHelper.transferExplicit(((ExplicitTerminal) defaultTarget), source.inventory, i)) {
											numItems--;
											itemsFlowed++;
											break;
										}
									}
									else {
										if (InventoryHelper.transfer(defaultTarget.inventory, source.inventory, i, 0, 1)) {
											numItems--;
											itemsFlowed++;
											break;
										}
									}
								}
							}
							else
							{
								Iterator<Terminal> targetIter = targets.iterator();
								breakTransfer = false;
								while (targetIter.hasNext())
								{
									Terminal target = targetIter.next();
									if (target.itemStackInFlowRegulator != null) {
										if ((numItems >= target.itemStackInFlowRegulator.stackSize) && (regulatedItemStack.stackSize >= target.itemStackInFlowRegulator.stackSize)) {
											if ((target instanceof ExplicitTerminal)) {
												if (InventoryHelper.transferExplicit(((ExplicitTerminal) target), source.inventory, i)) {
													numItems -= target.itemStackInFlowRegulator.stackSize;
													itemsFlowed += target.itemStackInFlowRegulator.stackSize;
													breakTransfer = true;
												}
											}
											else {
												if (InventoryHelper.transfer(((Terminal) target).inventory, source.inventory, i, 0, target.itemStackInFlowRegulator.stackSize)) {
													numItems -= target.itemStackInFlowRegulator.stackSize;
													itemsFlowed += target.itemStackInFlowRegulator.stackSize;
													breakTransfer = true;
												}
											}

										}
									}
								}
								if (breakTransfer)
									break;
							}
						}
					}
					if (i == source.inventory.getSizeInventory())
						break;
				}
			}
			return itemsFlowed;
		}

		public ItemVessel getLargerVessel(Item item)
		{
			if (item instanceof ItemVessel)
			{
				ItemVessel regulatedVessel = (ItemVessel) item;
				ItemVessel largerVessel = null;

				if (regulatedVessel.config.vesselType.largerType != null) {
					GameIdentifiedConfig largerConfig = null;
					if (regulatedVessel.config instanceof ElementVessel)
						largerConfig = ElementVessel.registry.find(((ElementVessel) regulatedVessel.config).source, regulatedVessel.config.vesselType.largerType);
					else if (regulatedVessel.config instanceof CompoundVessel)
						largerConfig = CompoundVessel.registry.find(((CompoundVessel) regulatedVessel.config).source, regulatedVessel.config.vesselType.largerType);
					else if (regulatedVessel.config instanceof PolymerPellets)
						largerConfig = PolymerPellets.registry.find(((PolymerPellets) regulatedVessel.config).source, regulatedVessel.config.vesselType.largerType);
					if (largerConfig != null)
						return (ItemVessel) PolycraftRegistry.getItem(largerConfig);

				}
			}
			return null;
		}

		//										}
		//
		//										else if (InventoryHelper.transferExplicit(((ExplicitTerminal) target), source.inventory, i)) {
		//											numItems--; //this may be misleading
		//											itemsFlowed++; //TODO: this may be misleading now 12.20.15 WV
		//											//go back out the while loop to ensure we are supposed to send more items,
		//											//and to keep trying if there are more that we can send from this slot
		//	
		//										}
		//	
		//									}
		//									else if (numItems >= regulatedItemStack.stackSize) //TODO finish this line
		//									{
		//										if (InventoryHelper.transfer(((Terminal)target).inventory, source.inventory, i, 0, regulatedItemStack.stackSize)) {
		//											//else if ((defaultTransfer) && (InventoryHelper.transfer(target.inventory, source.inventory, i, 0))) {
		//											numItems -= regulatedItemStack.stackSize;
		//											itemsFlowed += regulatedItemStack.stackSize;
		//											//go back out the while loop to ensure we are supposed to send more items,
		//											//and to keep trying if there are more that we can send from this slot												
		//										}
		//									}
		//
		//							}
		//
		//						}
		//						break;
		//
		//						//}
		//					}
		//
		//				}
		//				//there are no (more) items to flow, so stop trying
		//				if (i == source.inventory.getSizeInventory())
		//					break;
		//			}
		//
		//			}
		//
		//				else
		//				{
		//
		//					while (numItems > 0) {
		//						int i = 0;
		//						for (; i < source.inventory.getSizeInventory(); ++i) {
		//							ItemStack itemstack = source.inventory.getStackInSlot(i);
		//							if (itemstack != null) {
		//								boolean defaultTransfer = false;
		//								Terminal target = regulatedTargets.get(itemstack.getItem());
		//
		//								if (target == null)
		//								{
		//									if (itemstack.getItem() instanceof ItemVessel)
		//									{
		//										ItemVessel regulatedVessel = (ItemVessel) itemstack.getItem();
		//										ItemVessel smallerVessel = null;
		//
		//										if (regulatedVessel.config.vesselType.smallerType != null) {
		//											GameIdentifiedConfig smallerConfig = null;
		//											if (regulatedVessel.config instanceof ElementVessel)
		//												smallerConfig = ElementVessel.registry.find(((ElementVessel) regulatedVessel.config).source, regulatedVessel.config.vesselType.smallerType);
		//											if (regulatedVessel.config instanceof CompoundVessel)
		//												smallerConfig = CompoundVessel.registry.find(((CompoundVessel) regulatedVessel.config).source, regulatedVessel.config.vesselType.smallerType);
		//											else if (regulatedVessel.config instanceof PolymerPellets)
		//												smallerConfig = PolymerPellets.registry.find(((PolymerPellets) regulatedVessel.config).source, regulatedVessel.config.vesselType.smallerType);
		//											if (smallerConfig != null)
		//											{
		//												smallerVessel = (ItemVessel) PolycraftRegistry.getItem(smallerConfig);
		//												target = regulatedTargets.get(smallerVessel);
		//											}
		//										}
		//									}
		//
		//									if (target == null) // if it still is null 
		//									{
		//										target = defaultTarget;
		//										defaultTransfer = true;
		//									}
		//
		//								}
		//								if ((target instanceof ExplicitTerminal))
		//								{
		//									ItemStack regulatedItemStack = ((ExplicitTerminal) target).itemStack;
		//									if (regulatedItemStack != null)
		//									{
		//										if (numItems >= regulatedItemStack.stackSize)
		//										{
		//											if (InventoryHelper.transferExplicit(((ExplicitTerminal) target), source.inventory, i)) {
		//												numItems -= regulatedItemStack.stackSize;
		//												itemsFlowed += regulatedItemStack.stackSize;
		//												//go back out the while loop to ensure we are supposed to send more items,
		//												//and to keep trying if there are more that we can send from this slot
		//												break;
		//											}
		//
		//										}
		//									}
		//									//else if (InventoryHelper.transfer(target.inventory, source.inventory, i, 0)) {
		//									else if (InventoryHelper.transferExplicit(((ExplicitTerminal) target), source.inventory, i)) {
		//										numItems--; //this may be misleading
		//										itemsFlowed++; //TODO: this may be misleading now 12.20.15 WV
		//										//go back out the while loop to ensure we are supposed to send more items,
		//										//and to keep trying if there are more that we can send from this slot
		//										break;
		//									}
		//
		//								}
		//
		//								else if (InventoryHelper.transfer(target.inventory, source.inventory, i, 0, 1)) {
		//									//else if ((defaultTransfer) && (InventoryHelper.transfer(target.inventory, source.inventory, i, 0))) {
		//									numItems--;
		//									itemsFlowed++;
		//									//go back out the while loop to ensure we are supposed to send more items,
		//									//and to keep trying if there are more that we can send from this slot
		//									break;
		//								}
		//							}
		//						}
		//						//there are no (more) items to flow, so stop trying
		//						if (i == source.inventory.getSizeInventory())
		//							break;
		//					}
		//				}
		//			}
		//
		//			return itemsFlowed;
		//
		//		}

		public String getHashVec3(final Vec3 vec3) {
			return (int) vec3.xCoord + "." + (int) vec3.yCoord + "." + (int) vec3.zCoord;
		}

		public boolean isValid()
		{
			if (worldObj.isBlockIndirectlyGettingPowered((int) pumpCoords.xCoord, (int) pumpCoords.yCoord, (int) pumpCoords.zCoord))
			{
				pumpShutOffValve = true;
				return false;
			}
			else
				pumpShutOffValve = false;

			return source != null && defaultTarget != null && regulatedTargets != null;
		}

		private Terminal findNetworkSource(Vec3 coords, int flowDirection) {
			int distanceFromPump = 0;
			while (true) {
				coords = PolycraftMod.getAdjacentCoords(coords, flowDirection, true);
				final String hash = getHashVec3(coords);
				//don't allow networks that flow back in on themselves
				if (coordsUsed.contains(hash))
					return null;
				coordsUsed.add(hash);
				final Block block = getBlockAtVec3(coords);
				if (block instanceof BlockPipe)
					flowDirection = getBlockMetadataAtVec3(coords);
				else
				{
					IInventory sourceInventory = getInventoryAtVec3(coords);
					if (sourceInventory != null)
					{
						//TODO: Jim - check Walter's modification of your code to ensure functionality not broken
						if (sourceInventory instanceof PolycraftInventory)
						{
							PolycraftInventory pi = ((PolycraftInventory) sourceInventory);
							PolycraftInventoryBlock pib = (PolycraftInventoryBlock) pi.getWorldObj().getBlock(pi.xCoord, pi.yCoord, pi.zCoord);
							Vec3 input = pib.getBlockCoords(pi.xCoord, pi.yCoord, pi.zCoord, pi.getWorldObj().getBlockMetadata(pi.xCoord, pi.yCoord, pi.zCoord), pib.config.outputBlockOffset);

							if ((input.xCoord == coords.xCoord) && (input.yCoord == coords.yCoord) && (input.zCoord == coords.zCoord))
							{
								return new Terminal(Vec3.createVectorHelper(pi.xCoord, pi.yCoord, pi.zCoord), sourceInventory, distanceFromPump);
							}
							return null;

						}

						return new Terminal(coords, sourceInventory, distanceFromPump);

					}
					return null;
				}
				distanceFromPump++;
			}
		}

		private IInventory getInventoryAtVec3(final Vec3 vec3) {
			return PolycraftMod.getInventoryAt(worldObj, (int) vec3.xCoord, (int) vec3.yCoord, (int) vec3.zCoord);
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
			//flowDistributor = false;
			while (regulatedTargets != null) {
				coords = PolycraftMod.getAdjacentCoords(coords, flowDirection, false);
				final String hash = getHashVec3(coords);

				final Block block = getBlockAtVec3(coords);
				if (block instanceof BlockPipe) {
					//don't allow networks that flow back in onto other pipes
					if (coordsUsed.contains(hash))
						return null;
					coordsUsed.add(hash);
					flowDirection = getBlockMetadataAtVec3(coords);
				}
				//only check for regulators if we are not on a regulator path already
				else if (!regulatorPath && block instanceof FlowRegulatorBlock) {
					//don't allow networks that flow back into flow regulators
					if (coordsUsed.contains(hash))
						return null;
					coordsUsed.add(hash);
					flowDirection = getBlockMetadataAtVec3(coords);
					ForgeDirection forgeFlowDirection = ForgeDirection.values()[flowDirection];
					final IInventory regulatorInventory = getInventoryAtVec3(coords);
					//regulator order will be left, right, bottom, top given a starting orientation
					//for example, if the starting orienation is facing SOUTH, then the order will be:
					//WEST, EAST, BOTTOM, TOP
					for (int i = 0; i < regulatorInventory.getSizeInventory(); i++)
					{
						ArrayList terminalsList = null;
						//Item distributeAll = new Item();
						final ItemStack regulatorItemStack = regulatorInventory.getStackInSlot(i);
						if (regulatorItemStack != null) {

							//if we have already regulated this item, the regulator becomes a distribution hub
							if (regulatedTargets.containsKey(regulatorItemStack.getItem())) {
								terminalsList = regulatedTargets.get(regulatorItemStack.getItem());
								if (terminalsList == null)
									terminalsList = new ArrayList();
							}
							else
								terminalsList = new ArrayList();

							final Terminal regulatedTarget = findNetworkTargetInventories(coords, REGULATED_DIRECTIONS[flowDirection][i].ordinal(), true, distanceFromPump);
							if (regulatedTarget != null)
							{
								regulatedTarget.itemStackInFlowRegulator = regulatorItemStack;
								terminalsList.add(regulatedTarget);
								regulatedTargets.put(regulatorItemStack.getItem(), terminalsList);

							}
						}
					}
				}
				else {
					final IInventory inventory = getInventoryAtVec3(coords);
					if (inventory != null)
					{
						//TODO: Jim - check Walter's modification of your code to ensure functionality not broken
						if (inventory instanceof PolycraftInventory)
						{
							PolycraftInventory pi = ((PolycraftInventory) inventory);
							PolycraftInventoryBlock pib = (PolycraftInventoryBlock) pi.getWorldObj().getBlock(pi.xCoord, pi.yCoord, pi.zCoord);

							if (pib.config.fuelBlockOffset != null)
							{
								Vec3 input = pib.getBlockCoords(pi.xCoord, pi.yCoord, pi.zCoord, pi.getWorldObj().getBlockMetadata(pi.xCoord, pi.yCoord, pi.zCoord), pib.config.fuelBlockOffset);

								if ((input.xCoord == coords.xCoord) && (input.yCoord == coords.yCoord) && (input.zCoord == coords.zCoord))
								{
									//need to return a class that contains the slot as well
									return new FuelTerminal(Vec3.createVectorHelper(pi.xCoord, pi.yCoord, pi.zCoord), inventory, regulatorPath ? distanceFromPump + regulatorDistanceFromPump : distanceFromPump);
								}
							}
							if (pib.config.inputBlockOffset != null)
							{
								//iterate through the five slots: can this be more generic?
								for (int offsetIndex = 0; offsetIndex < pib.config.inputBlockOffset.size(); offsetIndex++)
								{
									Vec3 input = pib.getBlockCoords(pi.xCoord, pi.yCoord, pi.zCoord, pi.getWorldObj().getBlockMetadata(pi.xCoord, pi.yCoord, pi.zCoord), pib.config.inputBlockOffset.get(offsetIndex));

									if ((input.xCoord == coords.xCoord) && (input.yCoord == coords.yCoord) && (input.zCoord == coords.zCoord))
									{
										//need to return a class that contains the slot as well
										return new InputTerminal(Vec3.createVectorHelper(pi.xCoord, pi.yCoord, pi.zCoord), inventory, regulatorPath ? distanceFromPump + regulatorDistanceFromPump : distanceFromPump, offsetIndex);
									}
								}
								//if none of the slots match we don't have a proper input: should this happen?
								return null;
							}
						}
						return new Terminal(coords, inventory, regulatorPath ? distanceFromPump + regulatorDistanceFromPump : distanceFromPump);

					}
					return null;
				}
				distanceFromPump++;
			}
			return null;
		}

		private Block getBlockAtVec3(final Vec3 vec3) {
			return worldObj.getBlock((int) vec3.xCoord, (int) vec3.yCoord, (int) vec3.zCoord);
		}

		private int getBlockMetadataAtVec3(final Vec3 vec3) {
			return worldObj.getBlockMetadata((int) vec3.xCoord, (int) vec3.yCoord, (int) vec3.zCoord);
		}
	}
}
