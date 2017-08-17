package edu.utd.minecraft.mod.polycraft.privateproperty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockCollision;
import edu.utd.minecraft.mod.polycraft.block.BlockPipe;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.Exam;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.condenser.CondenserBlock;
import edu.utd.minecraft.mod.polycraft.inventory.courseblock.CHEM2323Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.FloodlightInventory;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.GaslampInventory;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.SpotlightInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.chemicalprocessor.ChemicalProcessorInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.contactprinter.ContactPrinterInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.distillationcolumn.DistillationColumnInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.extruder.ExtruderInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.injectionmolder.InjectionMolderInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.meroxtreatmentunit.MeroxTreatmentUnitInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.steamcracker.SteamCrackerInventory;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.MachiningMillInventory;
import edu.utd.minecraft.mod.polycraft.inventory.maskwriter.MaskWriterInventory;
import edu.utd.minecraft.mod.polycraft.inventory.oilderrick.OilDerrickInventory;
import edu.utd.minecraft.mod.polycraft.inventory.plasticchest.PlasticChestInventory;
import edu.utd.minecraft.mod.polycraft.inventory.pump.FlowRegulatorBlock;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpBlock;
import edu.utd.minecraft.mod.polycraft.inventory.solararray.SolarArrayInventory;
import edu.utd.minecraft.mod.polycraft.inventory.tradinghouse.TradingHouseInventory;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapBlock;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemFreezeRay;
import edu.utd.minecraft.mod.polycraft.item.ItemWaterCannon;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import edu.utd.minecraft.mod.polycraft.trading.ItemStackSwitch;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public abstract class Enforcer {

	public enum DataPacketType {
		Unknown, PrivateProperties, Friends, Broadcast, InventorySync
	}

	protected static boolean updatedMasterForTheDay = false;
	protected static boolean updatedNonMasterForTheDay = false;

	protected static final String chatCommandPrefix = "~";
	protected static final String chatCommandTeleport = "tp";
	protected static final String chatExamCommand = "exam";
	private static final String chatCommandTeleportArgPrivateProperty = "pp";
	private static final String chatCommandTeleportArgUser = "user";
	private static final String chatCommandTeleportArgUTD = "utd";
	private static final String chatExamCommandCHEM2323 = "chem2323";
	private static final double forceExitSpeed = .2;
	private static final int propertyDimension = 0; // you can only own property
													// in the surface dimension
	protected static final int maxPacketSizeBytes = (int) Math.pow(2, 16) - 1;

	protected static final int getPacketsRequired(int bytes) {
		return (int) Math.ceil((double) bytes / (double) maxPacketSizeBytes);
	}

	protected final FMLEventChannel netChannel;
	protected final String netChannelName = "polycraft.enforcer";
	protected String privatePropertiesMasterJson = null;
	protected String privatePropertiesNonMasterJson = null;
	protected String playerItemstackSwitchJson = null;
	protected String broadcastMessage = null;
	protected String whitelistJson = null;
	protected String friendsJson = null;
	protected final Collection<PrivateProperty> privateProperties = Lists
			.newLinkedList();
	protected final Collection<ItemStackSwitch> itemsToSwitch = Lists
			.newLinkedList();
	//protected final Map<String, ItemStackSwitch> itemStackSwitchesByPlayer = Maps
	//		.newHashMap();
	protected final Map<String, List<ItemStackSwitch>> itemStackSwitchesByPlayer = Maps
			.newHashMap();

	protected final Map<String, PrivateProperty> privatePropertiesByChunk = Maps
			.newHashMap();
	protected final Map<String, List<PrivateProperty>> privatePropertiesByOwner = Maps
			.newHashMap();
	// polycraft user ids by minecraft username
	public static Map<String, Long> whitelist = Maps.newHashMap();
	public static Map<String, String> whitelist_uuid = Maps.newHashMap();
	public static Set<String> friends = Sets.newHashSet();
	protected Action actionPrevented = null;
	protected PrivateProperty actionPreventedPrivateProperty = null;
	private final Gson gsonGeneric;

	public static Enforcer getInstance(final World world) {
		if (world.isRemote) {
			return ClientEnforcer.INSTANCE;
		}
		return ServerEnforcer.INSTANCE;
	}

	public Enforcer() {
		netChannel = NetworkRegistry.INSTANCE
				.newEventDrivenChannel(netChannelName);
		netChannel.register(this);
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonGeneric = gsonBuilder.create();
	}

	protected int updatePrivateProperties(final String privatePropertiesJson,
			final boolean master, final boolean serverSide) {

		if (master) {
			this.privatePropertiesMasterJson = privatePropertiesJson;
		} else {
			this.privatePropertiesNonMasterJson = privatePropertiesJson;
		}

		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(PrivateProperty.class,
				new PrivateProperty.Deserializer(master));

		final Gson gson = gsonBuilder.create();
		//this is either the master worlds list or the non-master list
		final Collection<PrivateProperty> newPrivateProperties = gson.fromJson(
				privatePropertiesJson,
				new TypeToken<Collection<PrivateProperty>>() {
				}.getType());

		// java 7 version
		final Collection<PrivateProperty> removePrivateProperties = Lists
				.newLinkedList();
		final Collection<PrivateProperty> dontChangeMasterStatusOfPrivateProperties = Lists
				.newLinkedList();

		//this is the current List of private properties
		for (final PrivateProperty privateProperty : privateProperties) {

			if (master) {
				if (updatedMasterForTheDay && (privateProperty.master == false)) {
					privateProperty.keepMasterWorldSame = true;
					dontChangeMasterStatusOfPrivateProperties.add(privateProperty);
				} else if (privateProperty.master == master) {
					removePrivateProperties.add(privateProperty);
				}
			} else {
				if (updatedNonMasterForTheDay && (privateProperty.master == true)) {
					privateProperty.keepMasterWorldSame = true;
					dontChangeMasterStatusOfPrivateProperties.add(privateProperty);
				} else if ((privateProperty.master == master) && (!(privateProperty.keepMasterWorldSame))) {
					removePrivateProperties.add(privateProperty);
				}
			}

		}

		privateProperties.removeAll(removePrivateProperties);
		// java 8 version
		/*
		 * privateProperties.removeIf(new Predicate<PrivateProperty>() {
		 * 
		 * @Override public boolean test(final PrivateProperty t) { return
		 * t.master == master; } });
		 */
		privatePropertiesByChunk.clear();
		privatePropertiesByOwner.clear();

		if (newPrivateProperties != null) {
			final Collection<PrivateProperty> dontChangePPs = Lists
					.newLinkedList();

			for (final PrivateProperty newPrivateProperty : newPrivateProperties) {
				if (dontChangeMasterStatusOfPrivateProperties != null) {
					for (final PrivateProperty dontChangePrivateProperty : dontChangeMasterStatusOfPrivateProperties) {

						//remove if new has the same value as the previous
						if ((newPrivateProperty.boundBottomRight.x == dontChangePrivateProperty.boundBottomRight.x) &&
								(newPrivateProperty.boundBottomRight.z == dontChangePrivateProperty.boundBottomRight.z) &&
								(newPrivateProperty.boundTopLeft.x == dontChangePrivateProperty.boundTopLeft.x) &&
								(newPrivateProperty.boundTopLeft.z == dontChangePrivateProperty.boundTopLeft.z)) {
							dontChangePPs.add(newPrivateProperty);
						}
					}
				}
			}
			newPrivateProperties.removeAll(dontChangePPs);
			privateProperties.addAll(newPrivateProperties);
			for (final PrivateProperty privateProperty : privateProperties) {
				for (int x = privateProperty.boundTopLeft.x; x <= privateProperty.boundBottomRight.x; x++) {
					// z coords go pos from north to south:
					// http://minecraft.gamepedia.com/coordinates
					for (int z = privateProperty.boundTopLeft.z; z <= privateProperty.boundBottomRight.z; z++) {
						privatePropertiesByChunk.put(getChunkKey(x, z),
								privateProperty);
					}
				}
				List<PrivateProperty> ownerPrivateProperties = privatePropertiesByOwner
						.get(privateProperty.owner);
				if (ownerPrivateProperties == null) {
					ownerPrivateProperties = Lists.newLinkedList();
					privatePropertiesByOwner.put(privateProperty.owner,
							ownerPrivateProperties);
				}
				ownerPrivateProperties.add(privateProperty);
				if (!master)
					privateProperty.keepMasterWorldSame = false;
			}
		}
		if (master)
			updatedMasterForTheDay = true;
		else
			updatedNonMasterForTheDay = true;

		return newPrivateProperties.size();
	}

	protected void updateWhitelist(final String whitelistJson) {
		this.whitelistJson = whitelistJson;
		whitelist = gsonGeneric.fromJson(whitelistJson,
				new TypeToken<Map<String, Long>>() {
				}.getType());
	}

	protected void updateUUIDWhitelist(final String whitelistJson) {
		this.whitelistJson = whitelistJson;
		whitelist_uuid = gsonGeneric.fromJson(whitelistJson,
				new TypeToken<Map<String, String>>() {
				}.getType());
	}

	protected String getFriendPairKey(final Long friend1, final Long friend2) {
		if (friend1 == null || friend2 == null) {
			return "";
		}

		if (friend1 < friend2) {
			return String.format("%d-%d", friend1, friend2);
		}
		return String.format("%d-%d", friend2, friend1);
	}

	protected void updateFriends(final String friendsJson) {
		this.friendsJson = friendsJson;
		final long[][] friendsRaw = gsonGeneric.fromJson(friendsJson,
				new TypeToken<long[][]>() {
				}.getType());
		friends.clear();
		for (final long[] friendPair : friendsRaw) {
			if (friendPair.length == 2) {
				friends.add(getFriendPairKey(friendPair[0], friendPair[1]));
			}
		}
	}

	private static String getChunkKey(final int x, final int z) {
		return String.format("%d,%d", x, z);
	}

	protected PrivateProperty findPrivateProperty(final Entity entity,
			final int chunkX, final int chunkZ) {
		if (entity.dimension == propertyDimension)
			return privatePropertiesByChunk.get(getChunkKey(chunkX, chunkZ));
		return null;
	}

	public PrivateProperty findPrivateProperty(final Entity entity) {
		return findPrivateProperty(entity, entity.chunkCoordX,
				entity.chunkCoordZ);
	}

	public PrivateProperty findPrivatePropertyByBlockCoords(
			final Entity entity, final int x, final int z) {
		if (entity.dimension == propertyDimension) {
			final net.minecraft.world.chunk.Chunk chunk = entity.worldObj
					.getChunkFromBlockCoords(x, z);
			return privatePropertiesByChunk.get(getChunkKey(chunk.xPosition,
					chunk.zPosition));
		}
		return null;
	}

	protected void setActionPrevented(final Action action,
			final PrivateProperty privateProperty) {
		actionPrevented = action;
		actionPreventedPrivateProperty = privateProperty;
	}

	private void possiblyPreventAction(final Event event,
			final EntityPlayer player, final Action action,
			final PrivateProperty privateProperty) {
		// if the player is not in private property, they can do anything
		if (privateProperty != null
				&& !privateProperty.actionEnabled(player, action)) {
			event.setCanceled(true);
			setActionPrevented(action, privateProperty);
		}
	}

	private void possiblyPreventAction(final Event event,
			final EntityPlayer player, final Action action, final int chunkX,
			final int chunkZ) {
		possiblyPreventAction(event, player, action,
				findPrivateProperty(player, chunkX, chunkZ));
	}

	private void possiblyPreventAction(final Event event,
			final EntityPlayer player, final Action action,
			final net.minecraft.world.chunk.Chunk chunk) {
		possiblyPreventAction(event, player, action, chunk.xPosition,
				chunk.zPosition);
	}

	private void possiblyPreventAction(final Event event,
			final EntityPlayer player, final Action action) {
		possiblyPreventAction(event, player, action,
				findPrivateProperty(player));
	}

	public boolean possiblyKillProjectile(final EntityPlayer player,
			final Entity projectile, final MovingObjectPosition position,
			final Action action) {
		final PrivateProperty privateProperty = findPrivatePropertyByBlockCoords(
				projectile, position.blockX, position.blockZ);
		// if the entity is not in private property, they can do anything
		if (privateProperty != null
				&& !privateProperty.actionEnabled(player, action)) {
			projectile.setDead();
			setActionPrevented(action, privateProperty);
			return true;
		}
		return false;
	}

	@SubscribeEvent
	public void onBlockBreak(final BreakEvent event) {
		// TODO what happens if they use dynamite? other ways?
		// TODO why is this not happening on the client?
		possiblyPreventAction(event, event.getPlayer(), Action.DestroyBlock,
				event.world.getChunkFromBlockCoords(event.x, event.z));
	}

	@SubscribeEvent
	public void onAttackEntity(final AttackEntityEvent event) {
		possiblyPreventAction(event, event.entityPlayer, Action.AttackEntity,
				event.target.chunkCoordX, event.target.chunkCoordZ);
	}

	@SubscribeEvent
	public void onFillBucket(final FillBucketEvent event) {
		possiblyPreventAction(event, event.entityPlayer, Action.UseBucket,
				event.world.getChunkFromBlockCoords(event.target.blockX,
						event.target.blockZ));
	}

	@SubscribeEvent
	public void onPlayerTick(final TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			synchronized (privateProperties) {
				final EntityPlayer player = event.player;
				final PrivateProperty privateProperty = findPrivateProperty(player);
				if (privateProperty != null) {

					if (player.ridingEntity != null
							&& !privateProperty.actionEnabled(player,
									Action.MountEntity)) {
						setActionPrevented(Action.MountEntity, privateProperty);
						player.mountEntity(null);
						return;
					}

					if (!privateProperty.actionEnabled(player, Action.Enter)) {
						setActionPrevented(Action.Enter, privateProperty);
						int i = 1;
						// find the first position that is not in the property,
						// and reset the player's location to it
						while (true) {
							if (forcePlayerToExitProperty(player, i, 0,
									privateProperty))
								break;
							if (forcePlayerToExitProperty(player, -i, 0,
									privateProperty))
								break;
							if (forcePlayerToExitProperty(player, 0, i,
									privateProperty))
								break;
							if (forcePlayerToExitProperty(player, 0, -i,
									privateProperty))
								break;
							i++;
						}
						return;
					}
				}
			}
		}
	}

	private boolean forcePlayerToExitProperty(final EntityPlayer player,
			double targetOffsetX, double targetOffsetZ,
			final PrivateProperty privateProperty) {
		if (!player.worldObj.isRemote)
			return true;
		final double x = player.posX + targetOffsetX;
		final double y = player.posY - 2;
		final double z = player.posZ + targetOffsetZ;

		final int xAbs, zAbs;
		if (x > 0)
			xAbs = (int) Math.ceil(x);
		else
			xAbs = (int) Math.floor(x);
		if (z > 0)
			zAbs = (int) Math.ceil(z);
		else
			zAbs = (int) Math.floor(z);

		// if (player.worldObj.isAirBlock(x, y, z) ||
		// (player.worldObj.getBlock(x, y, z) == Blocks.water)) {
		final net.minecraft.world.chunk.Chunk chunk = player.worldObj
				.getChunkFromBlockCoords(xAbs, zAbs);
		final PrivateProperty targetPrivateProperty = findPrivateProperty(
				player, chunk.xPosition, chunk.zPosition);
		if (targetPrivateProperty == null
				|| (targetPrivateProperty != privateProperty && targetPrivateProperty
						.actionEnabled(player, Action.Enter))) {
			// just teleport them out now
			if (targetOffsetX + targetOffsetZ > 2)
				player.setPositionAndUpdate(x, player.worldObj
						.getTopSolidOrLiquidBlock((int) x, (int) z) + 3, z);
			else {
				player.setPositionAndUpdate(x, y, z);
			}
			/*
			 * Old method where they user would be "pushed" out player.motionX =
			 * targetOffsetX > 0 ? forceExitSpeed : targetOffsetX < 0 ?
			 * -forceExitSpeed : 0; player.motionY = 0; player.motionZ =
			 * targetOffsetZ > 0 ? forceExitSpeed : targetOffsetZ < 0 ?
			 * -forceExitSpeed : 0;
			 */
			return true;
			// }
		}
		return false;
	}

	@SubscribeEvent
	public void onPlayerInteract(final PlayerInteractEvent event) {
		switch (event.action) {
		case LEFT_CLICK_BLOCK:
			// TODO why is this not happening on the client?
			possiblyPreventAction(event, event.entityPlayer,
					Action.DestroyBlock,
					event.world.getChunkFromBlockCoords(event.x, event.z));
			break;
		case RIGHT_CLICK_AIR:
			possiblyPreventUseEquippedItem(event);
			break;
		case RIGHT_CLICK_BLOCK:
			final net.minecraft.world.chunk.Chunk blockChunk = event.world
					.getChunkFromBlockCoords(event.x, event.z);
			final Block block = event.world.getBlock(event.x, event.y, event.z);
			if (block instanceof BlockWorkbench) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseCraftingTable, blockChunk);
			} else if (block instanceof BlockContainer) {
				if (block instanceof BlockChest) {
					possiblyPreventAction(event, event.entityPlayer,
							Action.OpenChest, blockChunk);
				} else if (block instanceof BlockFurnace) {
					possiblyPreventAction(event, event.entityPlayer,
							Action.UseFurnace, blockChunk);
				} else if (block instanceof BlockHopper) {
					possiblyPreventAction(event, event.entityPlayer,
							Action.UseHopper, blockChunk);
				} else if (block instanceof BlockDispenser) {
					possiblyPreventAction(event, event.entityPlayer,
							actionPrevented.UseDispenser, blockChunk);
				} else if (block instanceof BlockEnderChest) {
					possiblyPreventAction(event, event.entityPlayer,
							Action.OpenEnderChest, blockChunk);
				} else if (block instanceof PolycraftInventoryBlock) {
					if (block instanceof TreeTapBlock) {
						possiblyPreventAction(event, event.entityPlayer,
								Action.UseTreeTap, blockChunk);
					} else if (block instanceof CondenserBlock) {
						possiblyPreventAction(event, event.entityPlayer,
								Action.UseCondenser, blockChunk);
					} else if (block instanceof PumpBlock) {
						possiblyPreventAction(event, event.entityPlayer,
								Action.UsePump, blockChunk);
					} else if (block instanceof FlowRegulatorBlock) {
						possiblyPreventAction(event, event.entityPlayer,
								Action.UseFlowRegulator, blockChunk);
					} else {
						possiblyPreventAction(event,
								(PolycraftInventoryBlock) block, blockChunk);
					}
				}
			} else if (block instanceof BlockButton) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseButton, blockChunk);
			} else if (block instanceof BlockLever) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseLever, blockChunk);
			} else if (block instanceof BlockPressurePlate) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UsePressurePlate, blockChunk);
			} else if (block instanceof BlockDoor) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseDoor, blockChunk);
			} else if (block instanceof BlockTrapDoor) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseTrapDoor, blockChunk);
			} else if (block instanceof BlockTorch) {
				possiblyPreventAction(event, event.entityPlayer,
						actionPrevented.PlaceTorch, blockChunk);
			} else if (block instanceof BlockSign) {
				possiblyPreventAction(event, event.entityPlayer,
						actionPrevented.PlaceSign, blockChunk);
			} else if (block instanceof BlockFenceGate) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseFenceGate, blockChunk);
			} else if (block instanceof BlockPipe) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UsePipe, blockChunk);
			} else if (block instanceof BlockCollision) {
				final TileEntity tileEntity = BlockCollision
						.findConnectedInventory(event.world, event.x, event.y,
								event.z);
				if (tileEntity != null) {
					final Block pBlock = event.world.getBlock(
							tileEntity.xCoord, tileEntity.yCoord,
							tileEntity.zCoord);
					if (pBlock instanceof PolycraftInventoryBlock) {
						possiblyPreventAction(event,
								(PolycraftInventoryBlock) pBlock, blockChunk);
					}
				}
			}
			//this should not be an else...this should also happen, so you cant place a block on a sign, torch, etc...

			final ItemStack equippedItem = event.entityPlayer
					.getCurrentEquippedItem();
			if (equippedItem != null) {
				if (equippedItem.getItem() instanceof ItemBlock) {
					final Block equippedBlock = ((ItemBlock) equippedItem
							.getItem()).field_150939_a;
					if (equippedBlock instanceof BlockTNT) {
						possiblyPreventAction(event, event.entityPlayer,
								Action.AddBlockTNT, blockChunk);
					} else {
						possiblyPreventAction(event, event.entityPlayer,
								Action.AddBlock, blockChunk);
					}
				} else {
					possiblyPreventUseEquippedItem(event);
				}
			}

			break;
		default:
			break;
		}
	}

	private void possiblyPreventUseEquippedItem(final PlayerInteractEvent event) {
		final ItemStack equippedItemStack = event.entityPlayer
				.getCurrentEquippedItem();
		if (equippedItemStack != null) {
			if (ItemFlameThrower.isEquipped(event.entityPlayer)) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseFlameThrower);
			} else if (ItemFreezeRay.isEquipped(event.entityPlayer)) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseFreezeRay);
			} else if (ItemWaterCannon.isEquipped(event.entityPlayer)) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseWaterCannon);
			} else {
				final Item equippedItem = equippedItemStack.getItem();
				if (equippedItem instanceof ItemMonsterPlacer) {
					possiblyPreventAction(event, event.entityPlayer,
							Action.SpawnEntity);
					preventActionIfOverPopulated(event, event.entityPlayer, Action.SpawnEntity, equippedItem);
				} else if (equippedItem instanceof ItemFlintAndSteel) {
					possiblyPreventAction(event, event.entityPlayer,
							Action.UseFlintAndSteel);
				} else if (equippedItem instanceof ItemSign) {
					possiblyPreventAction(event, event.entityPlayer,
							Action.PlaceSign);
				}
			}
		}
	}

	private void possiblyPreventAction(final PlayerInteractEvent event,
			final PolycraftInventoryBlock polycraftInventoryBlock,
			final net.minecraft.world.chunk.Chunk blockChunk) {
		if (polycraftInventoryBlock != null) {
			if (polycraftInventoryBlock.tileEntityClass == PlasticChestInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.OpenPlasticChest, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == MachiningMillInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseMachiningMill, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == InjectionMolderInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseInjectionMolder, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == ExtruderInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseExtruder, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == DistillationColumnInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseDistillationColumn, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == SteamCrackerInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseSteamCracker, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == MeroxTreatmentUnitInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseMeroxTreatmentUnit, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == ChemicalProcessorInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseChemicalProcessor, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == SpotlightInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseSpotlight, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == FloodlightInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseFloodlight, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == GaslampInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseGaslamp, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == OilDerrickInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseOilDerrick, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == ContactPrinterInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseContactPrinter, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == SolarArrayInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseSolarArray, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == TradingHouseInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseTradingHouse, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == CHEM2323Inventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.AccessCHEM2323, blockChunk);
			} else if (polycraftInventoryBlock.tileEntityClass == MaskWriterInventory.class) {
				possiblyPreventAction(event, event.entityPlayer,
						Action.UseMaskWriter, blockChunk);
			}
		}
	}

	@SubscribeEvent
	public synchronized void onEntityCheckSpawn(final CheckSpawn event) {
		final PrivateProperty privateProperty = this
				.findPrivatePropertyByBlockCoords(event.entity, (int) event.x,
						(int) event.z);
		if (privateProperty != null) {
			if (!privateProperty.actionEnabled(Action.SpawnEntity)) {
				setActionPrevented(Action.SpawnEntity, privateProperty);
				event.setResult(Result.DENY);
			}
		}
		if (event.entity instanceof EntityCow ||
				event.entity instanceof EntityPig ||
				event.entity instanceof EntitySheep ||
				event.entity instanceof EntityMooshroom ||
				event.entity instanceof EntityWolf ||
				event.entity instanceof EntityOcelot ||
				event.entity instanceof EntityHorse ||
				event.entity instanceof EntityChicken) {
			preventActionIfOverPopulated(event);
		}
	}

	private void preventActionIfOverPopulated(final CheckSpawn event) {
		preventOverPopulationHelper(event.world, event.entity, null, event, (double) (event.x), (double) (event.y), (double) (event.z));

	}

	private void preventActionIfOverPopulated(final PlayerInteractEvent event, final EntityPlayer player, final Action action, final Item spawnEgg) {
		Entity entity = EntityList.createEntityByID(spawnEgg.getDamage(player.getCurrentEquippedItem()), event.world);

		preventOverPopulationHelper(event.world, entity, event, null, (double) (event.x), (double) (event.y), (double) (event.z));

	}

	private void preventOverPopulationHelper(World world, Entity entity, Event placeEggEvent, CheckSpawn spawnEvent, double xCoord, double yCoord, double zCoord) {
		List entities = world.getEntitiesWithinAABB(entity.getClass(), AxisAlignedBB.getBoundingBox(
				xCoord - 8.0, yCoord - 8.0, zCoord - 8.0,
				xCoord + 8.0, yCoord + 8.0, zCoord + 8.0));
		if (entities.size() >= 16) {
			if (placeEggEvent != null)
				placeEggEvent.setCanceled(true);
			if (spawnEvent != null)
				spawnEvent.setResult(Result.DENY);
			setActionPrevented(Action.SpawnEntity, null);
		}

	}

	//TODO: Jim and Walter to Discuss
	@SubscribeEvent
	public synchronized void onAllowDespawn(final AllowDespawn event) {
		//only run this once every morning; leave for a few ticks in case of lag
		if (event.world.getWorldTime() % 24000 < 10) {

			if (event.entity instanceof EntityCow ||
					event.entity instanceof EntityPig ||
					event.entity instanceof EntitySheep ||
					event.entity instanceof EntityMooshroom ||
					event.entity instanceof EntityWolf ||
					event.entity instanceof EntityOcelot ||
					event.entity instanceof EntityHorse ||
					event.entity instanceof EntityChicken) {

				List entities = event.world.getEntitiesWithinAABB(event.entity.getClass(), AxisAlignedBB.getBoundingBox(
						event.x - 16.0, event.y - 16.0, event.z - 16.0,
						event.x + 16.0, event.y + 16.0, event.z + 16.0));
				if (entities.size() >= 32) {
					event.setResult(Result.ALLOW);
				}
			}
		}

	}

	@SubscribeEvent
	public synchronized void onServerChatEvent(final ServerChatEvent event) {
		if (event.message.startsWith(chatCommandPrefix)) {
			event.setCanceled(true);
			if (event.player.worldObj.isRemote) {
				return;
			}
			// FIXME analytics?
			final String command = event.message.substring(1);
			// teleport to private property
			if (command.startsWith(chatCommandTeleport)) {
				handleChatCommandTeleport(event.player,
						command.substring(chatCommandTeleport.length() + 1)
								.split(" "));
			} else if (command.startsWith(chatExamCommand)) {
				handleChatExamCommand(event.player,
						command.substring(chatExamCommand.length() + 1)
								.split(" "));
			}

			return;
		}

		if (event.player.worldObj.isRemote)
			return;

		for (int i = 0; i < 36; i++) {
			ItemStack itemStackSend = event.player.inventory.getStackInSlot(i);

			if (itemStackSend != null) {
				if (i < 9) {
					// test if receiving player has walky talky on the hotbar
					if (itemStackSend != null
							&& ((itemStackSend.getUnlocalizedName())
									.equals(CustomObject.registry
											.get("Walky Talky").getItemStack()
											.getUnlocalizedName())))
						((ServerEnforcer) this
								.getInstance(event.player.worldObj))
										.broadcastFromSender(event, itemStackSend);

					// test if receiving player has cell phone on the hotbar
					if (itemStackSend != null
							&& ((itemStackSend.getUnlocalizedName())
									.equals(CustomObject.registry
											.get("Cell Phone").getItemStack()
											.getUnlocalizedName())))
						((ServerEnforcer) this
								.getInstance(event.player.worldObj))
										.broadcastFromSender(event, itemStackSend);

				}

				// test if sending and receiving player have ham radios on same
				// frequency
				if (itemStackSend != null
						&& ((itemStackSend.getUnlocalizedName())
								.equals(CustomObject.registry.get("HAM Radio")
										.getItemStack().getUnlocalizedName())))
					((ServerEnforcer) this.getInstance(event.player.worldObj))
							.broadcastFromSender(event, itemStackSend);

				// test if sending player holding phone broadcast the message
				// send message to a specific user (tell command)
				if (itemStackSend != null
						&& ((itemStackSend.getUnlocalizedName())
								.equals(CustomObject.registry
										.get("Smart Phone").getItemStack()
										.getUnlocalizedName())))
					((ServerEnforcer) this.getInstance(event.player.worldObj))
							.broadcastFromSender(event, itemStackSend);

			}

		}

	}

	public void handleChatExamCommand(final EntityPlayer player,
			final String[] args) {
		if (args.length > 0) {
			// where to add to spawn different exams (test)
			if (chatExamCommandCHEM2323.equalsIgnoreCase(args[0])) {
				int slotIndex = player.inventory.getFirstEmptyStack();

				if (Exam.registry.get("CHEM 2323 Exam 1") != null) {
					ItemStack itemStackExam = Exam.registry.get("CHEM 2323 Exam 1").getItemStack();

					NBTTagCompound examQuestions = new NBTTagCompound();
					NBTTagList questionList = new NBTTagList();

					NBTTagCompound question1 = new NBTTagCompound();
					question1.setShort("number", (short) 1);
					question1.setString("image", "chem2323-chem_2323_exam_1");
					question1.setString("TB1", "flashcard_0");
					question1.setString("TB2", "flashcard_1");
					question1.setString("TB3", "flashcard_2");
					question1.setString("TB4", "flashcard_3");
					question1.setString("TB5", "flashcard_4");
					question1.setString("TB6", "flashcard_5");
					question1.setString("TB7", "flashcard_6");
					question1.setString("TB8", "flashcard_7");
					question1.setString("TB9", "flashcard_8");
					question1.setString("TB10", "flashcard_9");
					question1.setString("TB11", "flashcard_cis");
					question1.setString("TB12", "flashcard_trans");
					questionList.appendTag(question1);

					NBTTagCompound question2 = new NBTTagCompound();
					question2.setShort("number", (short) 2);
					question2.setString("image", "chem2323-chem_2323_exam_1");
					question2.setString("TB1", "flashcard_a");
					question2.setString("TB2", "flashcard_b");
					question2.setString("TB3", "flashcard_c");
					question2.setString("TB4", "flashcard_d");
					question2.setString("TB5", "flashcard_e");
					question2.setString("TB6", "flashcard_f");
					question2.setString("TB7", "flashcard_g");
					question2.setString("TB8", "flashcard_h");
					question2.setString("TB9", "flashcard_i");
					question2.setString("TB10", "flashcard_j");
					question2.setString("TB11", "flashcard_k");
					question2.setString("TB12", "flashcard_l");
					questionList.appendTag(question2);

					examQuestions.setTag("Questions", questionList);
					examQuestions.setInteger("Bookmark", 0);
					itemStackExam.setTagCompound(examQuestions);

					if (slotIndex >= 0) {
						player.inventory.setInventorySlotContents(slotIndex, itemStackExam);
					}
				}

			}
		}
	}

	public void handleChatCommandTeleport(final EntityPlayer player,
			final String[] args) {
		if (args.length > 0) {
			// teleport to UTD
			if (chatCommandTeleportArgUTD.equalsIgnoreCase(args[0])) {
				// only allow if the player is in a private property
				if (findPrivateProperty(player) != null) {
					player.setPositionAndUpdate(1 + .5,
							player.worldObj.getTopSolidOrLiquidBlock(1, 1) + 3,
							1 + .5);
				}
			}
			// only allow if the player is in chunk 0,0 (center of UTD), or if
			// they are in a private property already
			else if ((Math.abs(player.chunkCoordX) <= 5 && Math
					.abs(player.chunkCoordZ) <= 5)
					|| findPrivateProperty(player) != null) {
				boolean valid = false;
				int x = 0, z = 0;
				// teleport to a user
				if (chatCommandTeleportArgUser.equalsIgnoreCase(args[0])) {
					if (args.length > 1) {
						// teleport to a player
						final EntityPlayer targetPlayer = player.worldObj
								.getPlayerEntityByName(args[1]);
						if (targetPlayer != null && targetPlayer != player) {
							final PrivateProperty targetPrivateProperty = findPrivateProperty(targetPlayer);
							valid = targetPrivateProperty != null
									&& targetPrivateProperty.actionEnabled(
											player, Action.Enter);
							x = (int) targetPlayer.posX;
							z = (int) targetPlayer.posZ;
						}
					}
				}
				// teleport to a private property
				else if (chatCommandTeleportArgPrivateProperty
						.equalsIgnoreCase(args[0])) {
					if (args.length < 3) {
						final List<PrivateProperty> ownerPrivateProperties = privatePropertiesByOwner
								.get(player.getDisplayName().toLowerCase());
						if (ownerPrivateProperties != null) {
							int index = 0;
							try {
								index = Integer.parseInt(args[1]);
								if (index < 0
										|| index >= ownerPrivateProperties
												.size())
									return;
							} catch (final Exception e) {
							}

							final PrivateProperty targetPrivateProperty = ownerPrivateProperties
									.get(index);
							int minX = (targetPrivateProperty.boundTopLeft.x * 16) + 1;
							int minZ = (targetPrivateProperty.boundTopLeft.z * 16) + 1;
							int maxX = (targetPrivateProperty.boundBottomRight.x * 16) + 15;
							int maxZ = (targetPrivateProperty.boundBottomRight.z * 16) + 15;
							x = (minX + maxX) / 2;
							z = (minZ + maxZ) / 2;
							valid = true;
						}
					} else {
						try {
							x = Integer.parseInt(args[1]);
							z = Integer.parseInt(args[2]);
							final net.minecraft.world.chunk.Chunk chunk = player.worldObj
									.getChunkFromBlockCoords(x, z);
							final PrivateProperty targetPrivateProperty = findPrivateProperty(
									player, chunk.xPosition, chunk.zPosition);
							valid = targetPrivateProperty != null
									&& targetPrivateProperty.actionEnabled(
											player, Action.Enter);
						} catch (final Exception e) {
						}
					}
				}

				if (valid) {
					player.setPositionAndUpdate(x + .5,
							player.worldObj.getTopSolidOrLiquidBlock(x, z) + 3,
							z + .5);
				}
			}
		}
	}
}
