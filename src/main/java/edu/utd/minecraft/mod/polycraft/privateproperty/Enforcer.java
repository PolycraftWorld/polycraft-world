package edu.utd.minecraft.mod.polycraft.privateproperty;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.condenser.CondenserBlock;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.FueledLampInventory;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.SpotlightInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.chemicalprocessor.ChemicalProcessorInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.distillationcolumn.DistillationColumnInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.extruder.ExtruderInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.injectionmolder.InjectionMolderInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.meroxtreatmentunit.MeroxTreatmentUnitInventory;
import edu.utd.minecraft.mod.polycraft.inventory.heated.steamcracker.SteamCrackerInventory;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.MachiningMillInventory;
import edu.utd.minecraft.mod.polycraft.inventory.oilderrick.OilDerrickBlock;
import edu.utd.minecraft.mod.polycraft.inventory.plasticchest.PlasticChestInventory;
import edu.utd.minecraft.mod.polycraft.inventory.pump.FlowRegulatorBlock;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpBlock;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapBlock;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemFreezeRay;
import edu.utd.minecraft.mod.polycraft.item.ItemWaterCannon;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;

public abstract class Enforcer {
	private static final String chatCommandPrefix = "~";
	private static final String chatCommandTeleport = "tp";
	private static final String chatCommandTeleportArgPrivateProperty = "pp";
	private static final String chatCommandTeleportArgUTD = "utd";
	private static final double forceExitSpeed = .2;
	private static final int propertyDimension = 0; //you can only own property in the surface dimension
	protected static final int maxPacketSizeBytes = (int)Math.pow(2, 16) - 1;
	protected static final int getPacketsRequired(int bytes) {
		return (int) Math.ceil((double)bytes / (double)maxPacketSizeBytes);
	}
	protected final FMLEventChannel netChannel;
	protected final String netChannelName = "private.properties";
	private final Gson gson;
	protected String privatePropertiesJson = null;
	protected String whitelistJson = null;
	protected final Collection<PrivateProperty> privateProperties = Lists.newLinkedList();
	protected final Map<String, PrivateProperty> privatePropertiesByChunk = Maps.newHashMap();
	protected final Map<String, List<PrivateProperty>> privatePropertiesByOwner = Maps.newHashMap();
	protected String[] whitelist = new String[] {};
	protected Action actionPrevented = null;
	protected PrivateProperty actionPreventedPrivateProperty = null;

	public Enforcer() {
		netChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(netChannelName);
		netChannel.register(this);
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(PrivateProperty.class, new PrivateProperty.Deserializer());
		gson = gsonBuilder.create();
	}

	protected void updatePrivateProperties(final String privatePropertiesJson) {
		this.privatePropertiesJson = privatePropertiesJson;
		final Collection<PrivateProperty> newPrivateProperties = gson.fromJson(privatePropertiesJson, new TypeToken<Collection<PrivateProperty>>() {
		}.getType());
		privateProperties.clear();
		privatePropertiesByChunk.clear();
		privatePropertiesByOwner.clear();
		if (newPrivateProperties != null) {
			privateProperties.addAll(newPrivateProperties);
			for (final PrivateProperty privateProperty : privateProperties) {
				for (int x = privateProperty.boundTopLeft.x; x <= privateProperty.boundBottomRight.x; x++) {
					//z coords go pos from north to south: http://minecraft.gamepedia.com/coordinates
					for (int z = privateProperty.boundTopLeft.z; z <= privateProperty.boundBottomRight.z; z++) {
						privatePropertiesByChunk.put(getChunkKey(x, z), privateProperty);
					}
				}
				List<PrivateProperty> ownerPrivateProperties = privatePropertiesByOwner.get(privateProperty.owner);
				if (ownerPrivateProperties == null) {
					ownerPrivateProperties = Lists.newLinkedList();
					privatePropertiesByOwner.put(privateProperty.owner, ownerPrivateProperties);
				}
				ownerPrivateProperties.add(privateProperty);
			}
		}
	}

	protected void updateWhitelist(final String whitelistJson) {
		this.whitelistJson = whitelistJson;
		whitelist = gson.fromJson(whitelistJson, new TypeToken<String[]>() {
		}.getType());
	}

	private static String getChunkKey(final int x, final int z) {
		return String.format("%d,%d", x, z);
	}

	protected PrivateProperty findPrivateProperty(final Entity entity, final int chunkX, final int chunkZ) {
		if (entity.dimension == propertyDimension)
			return privatePropertiesByChunk.get(getChunkKey(chunkX, chunkZ));
		return null;
	}

	protected PrivateProperty findPrivateProperty(final Entity entity) {
		return findPrivateProperty(entity, entity.chunkCoordX, entity.chunkCoordZ);
	}

	protected void setActionPrevented(final Action action, final PrivateProperty privateProperty) {
		actionPrevented = action;
		actionPreventedPrivateProperty = privateProperty;
	}

	private void possiblyPreventAction(final Event event, final EntityPlayer player, final Action action, final PrivateProperty privateProperty) {
		//if the player is not in private property, they can do anything
		if (privateProperty != null && !privateProperty.actionEnabled(player, action)) {
			event.setCanceled(true);
			setActionPrevented(action, privateProperty);
		}
	}

	private void possiblyPreventAction(final Event event, final EntityPlayer player, final Action action, final int chunkX, final int chunkZ) {
		possiblyPreventAction(event, player, action, findPrivateProperty(player, chunkX, chunkZ));
	}

	private void possiblyPreventAction(final Event event, final EntityPlayer player, final Action action, final net.minecraft.world.chunk.Chunk chunk) {
		possiblyPreventAction(event, player, action, chunk.xPosition, chunk.zPosition);
	}

	private void possiblyPreventAction(final Event event, final EntityPlayer player, final Action action) {
		possiblyPreventAction(event, player, action, findPrivateProperty(player));
	}

	@SubscribeEvent
	public void onBlockBreak(final BreakEvent event) {
		//TODO what happens if they use dynamite? other ways?
		//TODO why is this not happening on the client?
		possiblyPreventAction(event, event.getPlayer(), Action.DestroyBlock, event.world.getChunkFromBlockCoords(event.x, event.z));
	}

	@SubscribeEvent
	public void onAttackEntity(final AttackEntityEvent event) {
		possiblyPreventAction(event, event.entityPlayer, Action.AttackEntity, event.target.chunkCoordX, event.target.chunkCoordZ);
	}

	@SubscribeEvent
	public void onFillBucket(final FillBucketEvent event) {
		possiblyPreventAction(event, event.entityPlayer, Action.UseBucket, event.world.getChunkFromBlockCoords(event.target.blockX, event.target.blockZ));
	}

	@SubscribeEvent
	public void onPlayerTick(final TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			synchronized (privateProperties) {
				final EntityPlayer player = event.player;
				final PrivateProperty privateProperty = findPrivateProperty(player);
				if (privateProperty != null) {
					if (!privateProperty.actionEnabled(player, Action.Enter)) {
						setActionPrevented(Action.Enter, privateProperty);
						int i = 1;
						//find the first position that is not in the property, and reset the player's location to it
						while (true) {
							if (forcePlayerToExitProperty(player, i, 0, privateProperty))
								break;
							if (forcePlayerToExitProperty(player, -i, 0, privateProperty))
								break;
							if (forcePlayerToExitProperty(player, 0, i, privateProperty))
								break;
							if (forcePlayerToExitProperty(player, 0, -i, privateProperty))
								break;
							i++;
						}
					}
				}
			}
		}
	}

	private boolean forcePlayerToExitProperty(final EntityPlayer player, int targetOffsetX, double targetOffsetZ, final PrivateProperty privateProperty) {
		final int x = (int) (player.posX + targetOffsetX);
		final int y = (int) player.posY;
		final int z = (int) (player.posZ + targetOffsetZ);
		if (player.worldObj.isAirBlock(x, y, z)) {
			final net.minecraft.world.chunk.Chunk chunk = player.worldObj.getChunkFromBlockCoords(x, z);
			final PrivateProperty targetPrivateProperty = findPrivateProperty(player, chunk.xPosition, chunk.zPosition);
			if (targetPrivateProperty == null || (targetPrivateProperty != privateProperty && targetPrivateProperty.actionEnabled(player, Action.Enter))) {
				player.motionX = targetOffsetX > 0 ? forceExitSpeed : targetOffsetX < 0 ? -forceExitSpeed : 0;
				player.motionY = 0;
				player.motionZ = targetOffsetZ > 0 ? forceExitSpeed : targetOffsetZ < 0 ? -forceExitSpeed : 0;
				return true;
			}
		}
		return false;
	}

	@SubscribeEvent
	public void onPlayerInteract(final PlayerInteractEvent event) {
		switch (event.action) {
		case LEFT_CLICK_BLOCK:
			//TODO why is this not happening on the client?
			possiblyPreventAction(event, event.entityPlayer, Action.DestroyBlock, event.world.getChunkFromBlockCoords(event.x, event.z));
			break;
		case RIGHT_CLICK_AIR:
			if (event.entityPlayer.getCurrentEquippedItem() != null) {
				if (ItemFlameThrower.isEquipped(event.entityPlayer)) {
					possiblyPreventAction(event, event.entityPlayer, Action.UseFlameThrower);
				}
				else if (ItemFreezeRay.isEquipped(event.entityPlayer)) {
					possiblyPreventAction(event, event.entityPlayer, Action.UseFreezeRay);
				}
				else if (ItemWaterCannon.isEquipped(event.entityPlayer)) {
					possiblyPreventAction(event, event.entityPlayer, Action.UseWaterCannon);
				}
			}
			break;
		case RIGHT_CLICK_BLOCK:
			final net.minecraft.world.chunk.Chunk blockChunk = event.world.getChunkFromBlockCoords(event.x, event.z);
			final Block block = event.world.getBlock(event.x, event.y, event.z);
			if (block instanceof BlockWorkbench) {
				possiblyPreventAction(event, event.entityPlayer, Action.UseCraftingTable, blockChunk);
			}
			else if (block instanceof BlockFurnace) {
				possiblyPreventAction(event, event.entityPlayer, Action.UseFurnace, blockChunk);
			}
			else if (block instanceof BlockContainer) {
				if (block instanceof BlockChest) {
					possiblyPreventAction(event, event.entityPlayer, Action.OpenChest, blockChunk);
				}
				else if (block instanceof BlockEnderChest) {
					possiblyPreventAction(event, event.entityPlayer, Action.OpenEnderChest, blockChunk);
				}
				else if (block instanceof PolycraftInventoryBlock) {
					if (block instanceof TreeTapBlock) {
						possiblyPreventAction(event, event.entityPlayer, Action.UseTreeTap, blockChunk);
					}
					else if (block instanceof CondenserBlock) {
						possiblyPreventAction(event, event.entityPlayer, Action.UseCondenser, blockChunk);
					}
					else if (block instanceof PumpBlock) {
						possiblyPreventAction(event, event.entityPlayer, Action.UsePump, blockChunk);
					}
					else if (block instanceof FlowRegulatorBlock) {
						possiblyPreventAction(event, event.entityPlayer, Action.UseFlowRegulator, blockChunk);
					}
					else if (block instanceof CondenserBlock) {
						possiblyPreventAction(event, event.entityPlayer, Action.UseCondenser, blockChunk);
					}
					else if (block instanceof OilDerrickBlock) {
						possiblyPreventAction(event, event.entityPlayer, Action.UseOilDerrick, blockChunk);
					}
					else {
						final PolycraftInventoryBlock polycraftInventoryBlock = (PolycraftInventoryBlock) block;
						if (polycraftInventoryBlock.tileEntityClass == PlasticChestInventory.class) {
							possiblyPreventAction(event, event.entityPlayer, Action.OpenPlasticChest, blockChunk);
						}
						else if (polycraftInventoryBlock.tileEntityClass == MachiningMillInventory.class) {
							possiblyPreventAction(event, event.entityPlayer, Action.UseMachiningMill, blockChunk);
						}
						else if (polycraftInventoryBlock.tileEntityClass == InjectionMolderInventory.class) {
							possiblyPreventAction(event, event.entityPlayer, Action.UseInjectionMolder, blockChunk);
						}
						else if (polycraftInventoryBlock.tileEntityClass == ExtruderInventory.class) {
							possiblyPreventAction(event, event.entityPlayer, Action.UseExtruder, blockChunk);
						}
						else if (polycraftInventoryBlock.tileEntityClass == DistillationColumnInventory.class) {
							possiblyPreventAction(event, event.entityPlayer, Action.UseDistillationColumn, blockChunk);
						}
						else if (polycraftInventoryBlock.tileEntityClass == SteamCrackerInventory.class) {
							possiblyPreventAction(event, event.entityPlayer, Action.UseSteamCracker, blockChunk);
						}
						else if (polycraftInventoryBlock.tileEntityClass == MeroxTreatmentUnitInventory.class) {
							possiblyPreventAction(event, event.entityPlayer, Action.UseMeroxTreatmentUnit, blockChunk);
						}
						else if (polycraftInventoryBlock.tileEntityClass == ChemicalProcessorInventory.class) {
							possiblyPreventAction(event, event.entityPlayer, Action.UseChemicalProcessor, blockChunk);
						}
						else if (polycraftInventoryBlock.tileEntityClass == SpotlightInventory.class) {
							possiblyPreventAction(event, event.entityPlayer, Action.UseSpotlight, blockChunk);
						}
						else if (polycraftInventoryBlock.tileEntityClass == FueledLampInventory.class) {
							possiblyPreventAction(event, event.entityPlayer, Action.UseFueledLamp, blockChunk);
						}
					}
				}
			}
			else if (block instanceof BlockButton) {
				possiblyPreventAction(event, event.entityPlayer, Action.UseButton, blockChunk);
			}
			else if (block instanceof BlockLever) {
				possiblyPreventAction(event, event.entityPlayer, Action.UseLever, blockChunk);
			}
			else if (block instanceof BlockPressurePlate) {
				possiblyPreventAction(event, event.entityPlayer, Action.UsePressurePlate, blockChunk);
			}
			else {
				final ItemStack equippedItem = event.entityPlayer.getCurrentEquippedItem();
				if (equippedItem != null) {
					if (equippedItem.getItem() instanceof ItemBlock) {
						possiblyPreventAction(event, event.entityPlayer, Action.AddBlock, blockChunk);
					}
					else if (equippedItem.getItem() instanceof ItemFlameThrower) {
						possiblyPreventAction(event, event.entityPlayer, Action.UseFlameThrower);
					}
				}
			}
			break;
		default:
			break;
		}
	}

	@SubscribeEvent
	public synchronized void onCommandEvent(final ServerChatEvent event) {
		if (event.message.startsWith(chatCommandPrefix)) {
			event.setCanceled(true);
			if (event.player.worldObj.isRemote) {
				return;
			}
			//FIXME analytics?
			final String command = event.message.substring(1);
			//teleport to private property
			if (command.startsWith(chatCommandTeleport)) {
				handleChatCommandTeleport(event.player, command.substring(chatCommandTeleport.length() + 1).split(" "));
			}
		}
	}

	public void handleChatCommandTeleport(final EntityPlayer player, final String[] args) {
		if (args.length > 0) {
			//teleport to UTD
			if (chatCommandTeleportArgUTD.equalsIgnoreCase(args[0])) {
				//only allow if the player is in a private property
				if (findPrivateProperty(player) != null) {
					player.setPositionAndUpdate(1, player.worldObj.getTopSolidOrLiquidBlock(1, 1) + 3, 1);
				}
			}
			//teleport to a private property
			else if (chatCommandTeleportArgPrivateProperty.equalsIgnoreCase(args[0])) {
				//only allow if the player is in chunk 0,0 (center of UTD), or if they are in a private property already
				if ((Math.abs(player.chunkCoordX) <= 5 && Math.abs(player.chunkCoordZ) <= 5) || findPrivateProperty(player) != null) {
					boolean valid = false;
					int x = 0, z = 0;
					if (args.length < 3) {
						final List<PrivateProperty> ownerPrivateProperties = privatePropertiesByOwner.get(player.getDisplayName());
						if (ownerPrivateProperties != null) {
							int index = 0;
							try {
								index = Integer.parseInt(args[1]);
								if (index < 0 || index >= ownerPrivateProperties.size())
									return;
							} catch (final Exception e) {
							}

							final PrivateProperty targetPrivateProperty = ownerPrivateProperties.get(index);
							final PrivateProperty.Chunk tpChunk = targetPrivateProperty.boundTopLeft;
							x = tpChunk.x * 16;
							z = tpChunk.z * 16;
							valid = true;
						}
					}
					else {
						try {
							x = Integer.parseInt(args[1]);
							z = Integer.parseInt(args[2]);
							final net.minecraft.world.chunk.Chunk chunk = player.worldObj.getChunkFromBlockCoords(x, z);
							final PrivateProperty targetPrivateProperty = findPrivateProperty(player, chunk.xPosition, chunk.zPosition);
							valid = targetPrivateProperty != null && targetPrivateProperty.actionEnabled(player, Action.Enter);
						} catch (final Exception e) {
						}
					}
					if (valid) {
						player.setPositionAndUpdate(x, player.worldObj.getTopSolidOrLiquidBlock(x, z) + 3, z);
					}
				}
			}
		}
	}
}
