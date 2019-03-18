package edu.utd.minecraft.mod.polycraft.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.FileHandler;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.item.ArmorSlot;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;

//TODO http://www.minecraftforge.net/wiki/Event_Reference
//TODO LivingAttackEvent
//TODO LivingDeathEvent
//TODO LivingFallEvent
//TODO LivingHurtEvent
//TODO LivingJumpEvent
//TODO EntityInteractEvent
//TODO EntityItemPickupEvent
//TODO PlayerDropsEvent

//TODO add non-player specific events (such as display world name on startup?)
//TODO need to check the log rollover logic, appears to not be creating a new file on a new day
public class Analytics {
	public static Analytics INSTANCE = new Analytics();

	public static final Logger logger = LogManager.getLogger(PolycraftMod.MODID + "-analytics");
	//public static final Logger logger1 = LogManager.getLogger("Experiment-analytics");
	
	//boolean append = true;
	//FileHandler handler = new FileHandler("default.log", append);

	public static final String DELIMETER_SEGMENT = "\t";
	public static final String DELIMETER_DATA = ",";

	public static enum Category {
		PlayerTickSpatial,
		PlayerTickSwimming,
		PlayerTickHealth,
		PlayerTickItem,
		PlayerTickFood,
		PlayerTickExperience,
		PlayerTickArmor,
		PlayerTickHotbar,
		PlayerTickInventory,
		PlayerChat,
		PlayerInteract,
		PlayerUseItemStart,
		PlayerUseItemStop,
		PlayerUseItemFinish,
		PlayerPickupItem,
		PlayerTossItem,
		PlayerCraftItem,
		PlayerSmeltItem,
		PlayerPolycraftItem,
		PlayerAttackEntity,
		PlayerBreakBlock,
		PlayerSleepInBed,
		PlayerAchievement,
		PlayerExperimentEvent0,
		PlayerExperimentEvent1,
		PlayerExperimentEvent2,
		PlayerExperimentEvent3,
		PlayerExperimentEvent4,
		PlayerExperimentEvent5,
		PlayerExperimentEvent6,
		PlayerExperimentEvent7,
	}

	public static class TickIntervals {
		public int spatial = getValue("spatial", 1);
		public int swimming = getValue("swimming", 1);
		public int health = getValue("health", 5);
		public int item = getValue("item", 10);
		public int food = getValue("food", 60);
		public int experience = getValue("experience", 60);
		public int armor = getValue("armor", 60);
		public int hotbar = getValue("hotbar", 60);
		public int inventory = getValue("inventory", 300);

		private static int getValue(String name, int value) {
			name = "analytics.tick.interval." + name;
			if (System.getProperty(name) == null) {
				PolycraftMod.logger.info("Using default analytics value (" + name + "): " + value);
			}
			else {
				try
				{
					value = Integer.parseInt(System.getProperty(name));
					PolycraftMod.logger.info("Using custom analytics value (" + name + "): " + value);
				} catch (final Exception e) {
					PolycraftMod.logger.error("Invalid analytics setting (" + name + "): " + e.getMessage());
				}
			}
			return PolycraftMod.convertSecondsToGameTicks(value);
		}
	}

	private class PlayerState {
		private int ticksSpatial = 0;
		private int ticksSwimming = 0;
		private int ticksHealth = 0;
		private int ticksItem = 0;
		private int ticksFood = 0;
		private int ticksExperience = 0;
		private int ticksArmor = 0;
		private int ticksHotbar = 0;
		private int ticksInventory = 0;
	}

	private final Map<EntityPlayer, PlayerState> playerStates = Maps.newHashMap();

	private synchronized PlayerState getPlayerState(final EntityPlayer player) {
		PlayerState playerState = playerStates.get(player);
		if (playerState == null) {
			playerState = new PlayerState();
			playerStates.put(player, playerState);
		}
		return playerState;
	}

	public static boolean debug = System.getProperty("analytics.debug") == null ? false : Boolean.parseBoolean(System.getProperty("analytics.debug"));
	private TickIntervals tickIntervals = new TickIntervals();

	private String formatBoolean(final boolean value) {
		return debug ? (value ? "true" : "false") : (value ? "1" : "0");
	}

	private static String formatEnum(final Enum value) {
		return debug ? value.toString() : String.valueOf(value.ordinal());
	}

	private String formatItemStackName(final ItemStack item) {
		return debug ? (item == null ? "n/a" : item.getDisplayName()) : (item == null ? "" : item.getUnlocalizedName());
	}

	private String formatItemStackSize(final ItemStack item) {
		return debug ? (item == null ? "n/a" : String.valueOf(item.stackSize)) : (item == null ? "" : String.valueOf(item.stackSize));
	}

	private String formatItemStackDamage(final ItemStack item) {
		return debug ? (item == null ? "n/a" : String.valueOf(item.getItemDamage())) : (item == null ? "" : String.valueOf(item.getItemDamage()));
	}

	private String formatBlock(final Block block) {
		return debug ? block.getLocalizedName() : block.getUnlocalizedName();
	}

	private String formatEntity(final Entity entity) {
		return debug ? entity.getClass().getSimpleName() : entity.getClass().getName();
	}

	private String formatInventoryName(final IInventory inventory) {
		return debug ? (inventory == null ? "n/a" : inventory.getInventoryName()) : (inventory == null ? "" : inventory.getClass().getName());
	}

	private static final String FORMAT_LOG = "%1$s%3$s%1$s%4$s%1$s%5$d%2$s%6$d%2$s%7$d%1$s%8$s";
	private static final String FORMAT_LOG_DEBUG = " %1$s %3$s %1$s User=%4$s %1$s PosX=%5$d%2$s PosY=%6$d%2$s PosZ=%7$d %1$s %8$s";

	public synchronized static void log(final EntityPlayer player, final Category category, final String data) {
		//TODO JM need to log the world name? player.worldObj.getWorldInfo().getWorldName()
		final Long playerID = Enforcer.whitelist.get(player.getDisplayName().toLowerCase());
		logger.info(String.format(debug ? FORMAT_LOG_DEBUG : FORMAT_LOG,
				DELIMETER_SEGMENT, DELIMETER_DATA,
				formatEnum(category),
				playerID == null ? "-1" : playerID.toString(),
				(int) player.posX, (int) player.posY, (int) player.posZ,
				data.replace(DELIMETER_SEGMENT, " ")));
		
	}
	
//	public synchronized static void log1(final EntityPlayer player, final Category category, final String data) {
//		//TODO JM need to log the world name? player.worldObj.getWorldInfo().getWorldName()
//		final Long playerID = Enforcer.whitelist.get(player.getDisplayName().toLowerCase());
//		logger1.info(String.format(debug ? FORMAT_LOG_DEBUG : FORMAT_LOG,
//				DELIMETER_SEGMENT, DELIMETER_DATA,
//				formatEnum(category),
//				playerID == null ? "-1" : playerID.toString(),
//				(int) player.posX, (int) player.posY, (int) player.posZ,
//				data.replace(DELIMETER_SEGMENT, " ")));
//	}
	
//	public synchronized static void log1(final String playerName2, final Category category, final String data) {
//		//TODO JM need to log the world name? player.worldObj.getWorldInfo().getWorldName()
//		final Long playerID = Enforcer.whitelist.get(playerName2.toLowerCase());
//		logger.info(String.format(debug ? FORMAT_LOG_DEBUG : FORMAT_LOG,
//				DELIMETER_SEGMENT, DELIMETER_DATA,
//				formatEnum(category),
//				playerID == null ? "-1" : playerID.toString(),
//				0,0,0,
//				data.replace(DELIMETER_SEGMENT, " ")));
//	}

	public static final String FORMAT_TICK_SPATIAL = "%2$.2f%1$s%3$.2f%1$s%4$.2f%1$s%5$d%1$s%6$d%1$s%7$d%1$s%8$s%1$s%9$s%1$s%10$s";
	public static final String FORMAT_TICK_SPATIAL_DEBUG = "MotionX=%2$.2f%1$s MotionY=%3$.2f%1$s MotionZ=%4$.2f%1$s RotationPitch=%5$d%1$s RotationYaw=%6$d%1$s RotationYawHead=%7$d%1$s OnGround=%8$s%1$s IsSprinting=%9$s%1$s IsSneaking=%10$s";
	public static final String FORMAT_TICK_SWIMMING = "%d";
	public static final String FORMAT_TICK_SWIMMING_DEBUG = "Air=%d";
	public static final String FORMAT_TICK_HEALTH = "%.1f";
	public static final String FORMAT_TICK_HEALTH_DEBUG = "Health=%.1f";
	public static final String FORMAT_TICK_ITEM = "%2$s%1$s%3$s";
	public static final String FORMAT_TICK_ITEM_DEBUG = "Item=%2$s%1$s Damage=%3$s";
	public static final String FORMAT_TICK_FOOD = "%2$d%1$s%3$.1f";
	public static final String FORMAT_TICK_FOOD_DEBUG = "Food=%2$d%1$s Saturation=%3$.1f";
	public static final String FORMAT_TICK_EXPERIENCE = "%2$d%1$s%3$d";
	public static final String FORMAT_TICK_EXPERIENCE_DEBUG = "Experience=%2$d%1$s Level=%3$d";
	public static final String FORMAT_TICK_ARMOR = "%2$d%1$s%3$s%1$s%4$s";
	public static final String FORMAT_TICK_ARMOR_ITEM = "%2$d%1$s%3$s%1$s%4$s";
	public static final String FORMAT_TICK_ARMOR_DEBUG = "Armor=%2$d%1$s Total=%3$d%1$s %4$s";
	public static final String FORMAT_TICK_ARMOR_ITEM_DEBUG = "Slot=%2$d%1$s Item=%3$s%1$s Damage=%4$s";
	public static final String FORMAT_TICK_HOTBAR = "%2$d%1$s%3$s";
	public static final String FORMAT_TICK_HOTBAR_ITEM = "%2$d%1$s%3$s%1$s%4$s";
	public static final String FORMAT_TICK_HOTBAR_DEBUG = "Total=%2$d%1$s %3$s";
	public static final String FORMAT_TICK_HOTBAR_ITEM_DEBUG = "Slot=%2$d%1$s Item=%3$s%1$s Count=%4$s";
	public static final String FORMAT_TICK_INVENTORY = "%2$d%1$s%3$s";
	public static final String FORMAT_TICK_INVENTORY_ITEM = "%2$d%1$s%3$s%1$s%4$s";
	public static final String FORMAT_TICK_INVENTORY_DEBUG = "Total=%2$d%1$s %3$s";
	public static final String FORMAT_TICK_INVENTORY_ITEM_DEBUG = "Slot=%2$d%1$s Item=%3$s%1$s Count=%4$s";

	@SubscribeEvent
	public synchronized void onPlayerTick(final TickEvent.PlayerTickEvent tick) {
		if (tick.phase == Phase.END) {
			if (tick.player.isEntityAlive()) {
				final EntityPlayer player = tick.player;
				final PlayerState playerState = getPlayerState(player);

				if (tickIntervals.spatial > 0 && playerState.ticksSpatial++ == tickIntervals.spatial) {
					playerState.ticksSpatial = 0;
					log(player, Category.PlayerTickSpatial, String.format(debug ? FORMAT_TICK_SPATIAL_DEBUG : FORMAT_TICK_SPATIAL, DELIMETER_DATA,
							player.motionX, player.motionY, player.motionZ,
							(int) player.rotationPitch, (int) player.rotationYaw, (int) player.rotationYawHead,
							formatBoolean(player.onGround),
							formatBoolean(player.isSprinting()), formatBoolean(player.isSneaking())));
				}

				if (tickIntervals.swimming > 0) {
					if (player.isInWater()) {
						if (playerState.ticksSwimming++ == tickIntervals.swimming) {
							playerState.ticksSwimming = 0;
							log(player, Category.PlayerTickSwimming, String.format(debug ? FORMAT_TICK_SWIMMING_DEBUG : FORMAT_TICK_SWIMMING, player.getAir()));
						}
					}
					else if (playerState.ticksSwimming > 0)
						playerState.ticksSwimming = 0;
				}

				if (tickIntervals.health > 0 && playerState.ticksHealth++ == tickIntervals.health) {
					playerState.ticksHealth = 0;
					log(player, Category.PlayerTickHealth, String.format(debug ? FORMAT_TICK_HEALTH_DEBUG : FORMAT_TICK_HEALTH, player.getHealth()));
				}

				if (tickIntervals.item > 0 && playerState.ticksItem++ == tickIntervals.item) {
					playerState.ticksItem = 0;
					log(player, Category.PlayerTickItem, String.format(debug ? FORMAT_TICK_ITEM_DEBUG : FORMAT_TICK_ITEM, DELIMETER_DATA,
							formatItemStackName(player.getCurrentEquippedItem()),
							formatItemStackDamage(player.getCurrentEquippedItem())));
				}

				if (tickIntervals.food > 0 && playerState.ticksFood++ == tickIntervals.food) {
					playerState.ticksFood = 0;
					log(player, Category.PlayerTickFood, String.format(debug ? FORMAT_TICK_FOOD_DEBUG : FORMAT_TICK_FOOD, DELIMETER_DATA,
							player.getFoodStats().getFoodLevel(),
							player.getFoodStats().getSaturationLevel()));
				}

				if (tickIntervals.experience > 0 && playerState.ticksExperience++ == tickIntervals.experience) {
					playerState.ticksExperience = 0;
					log(player, Category.PlayerTickExperience, String.format(debug ? FORMAT_TICK_EXPERIENCE_DEBUG : FORMAT_TICK_EXPERIENCE, DELIMETER_DATA,
							player.experienceTotal,
							player.experienceLevel));
				}

				if (tickIntervals.armor > 0 && playerState.ticksArmor++ == tickIntervals.armor) {
					playerState.ticksArmor = 0;
					final StringBuilder items = new StringBuilder();
					int count = 0;
					for (final ArmorSlot slot : ArmorSlot.values()) {
						final ItemStack item = player.getCurrentArmor(slot.getInventoryArmorSlot());
						if (item != null) {
							if (count++ > 0)
								items.append(DELIMETER_DATA);
							items.append(String.format(debug ? FORMAT_TICK_ARMOR_ITEM_DEBUG : FORMAT_TICK_ARMOR_ITEM, DELIMETER_DATA,
									slot.ordinal(), formatItemStackName(item), formatItemStackDamage(item)));
						}
					}
					log(player, Category.PlayerTickArmor, String.format(debug ? FORMAT_TICK_ARMOR_DEBUG : FORMAT_TICK_ARMOR, DELIMETER_DATA,
							player.getTotalArmorValue(), count, items.toString()));
				}

				if (tickIntervals.hotbar > 0 && playerState.ticksHotbar++ == tickIntervals.hotbar) {
					playerState.ticksHotbar = 0;
					final StringBuilder items = new StringBuilder();
					int count = 0;
					for (int i = 0; i < 9; i++) {
						final ItemStack item = player.inventory.getStackInSlot(i);
						if (item != null) {
							if (count++ > 0)
								items.append(DELIMETER_DATA);
							items.append(String.format(debug ? FORMAT_TICK_HOTBAR_ITEM_DEBUG : FORMAT_TICK_HOTBAR_ITEM, DELIMETER_DATA,
									i, formatItemStackName(item), formatItemStackSize(item)));
						}
					}
					log(player, Category.PlayerTickHotbar, String.format(debug ? FORMAT_TICK_HOTBAR_DEBUG : FORMAT_TICK_HOTBAR, DELIMETER_DATA, count, items.toString()));
				}

				if (tickIntervals.inventory > 0 && playerState.ticksInventory++ == tickIntervals.inventory) {
					playerState.ticksInventory = 0;
					final StringBuilder items = new StringBuilder();
					int count = 0;
					for (int i = 0; i < 27; i++) {
						final ItemStack item = player.inventory.getStackInSlot(i + 9);
						if (item != null) {
							if (count++ > 0)
								items.append(DELIMETER_DATA);
							items.append(String.format(debug ? FORMAT_TICK_INVENTORY_ITEM_DEBUG : FORMAT_TICK_INVENTORY_ITEM, DELIMETER_DATA,
									i, formatItemStackName(item), formatItemStackSize(item)));
						}
					}
					log(player, Category.PlayerTickInventory, String.format(debug ? FORMAT_TICK_INVENTORY_DEBUG : FORMAT_TICK_INVENTORY, DELIMETER_DATA, count, items.toString()));
				}
			}
		}
	}

	public static final String FORMAT_SERVER_CHAT = "%s";
	public static final String FORMAT_SERVER_CHAT_DEBUG = "Message=%s";

	@SubscribeEvent
	public synchronized void onServerChat(final ServerChatEvent event) {
		log(event.player, Category.PlayerChat, String.format(debug ? FORMAT_SERVER_CHAT_DEBUG : FORMAT_SERVER_CHAT, event.message));
	}

	public static final String FORMAT_INTERACT = "%2$s%1$s%3$d%1$s%4$d%1$s%5$d%1$s%6$d%1$s%7$s%1$s%8$s%1$s%9$d%1$s%10$s";
	public static final String FORMAT_INTERACT_DEBUG = "Action=%2$s%1$s X=%3$d%1$s Y=%4$d%1$s Z=%5$d%1$s Face=%6$d%1$s Result=%7$s%1$s Block=%8$s%1$s Metadata=%9$d%1$s Item=%10$s";

	@SubscribeEvent
	public synchronized void onPlayerInteract(final PlayerInteractEvent event) {
		if(!(formatItemStackName(event.entityPlayer.getCurrentEquippedItem()).equals("item.1hw")||formatItemStackName(event.entityPlayer.getCurrentEquippedItem()).equals("item.1hw")))
		{if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
			log(event.entityPlayer, Category.PlayerInteract, String.format(debug ? FORMAT_INTERACT_DEBUG : FORMAT_INTERACT, DELIMETER_DATA,
					formatEnum(event.action), event.x, event.y, event.z,
					event.face, formatEnum(event.getResult()),
					formatBlock(event.world.getBlock(event.x, event.y, event.z)),
					event.world.getBlockMetadata(event.x, event.y, event.z),
					formatItemStackName(event.entityPlayer.getCurrentEquippedItem())));
		}
		else
		{
			if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
				log(event.entityPlayer, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT2_DEBUG : FORMAT_ON_EXPERIMENT_EVENT2, DELIMETER_DATA,
						2,formatEnum(event.action), event.x, event.y, event.z,
						event.face, formatEnum(event.getResult()),
						formatBlock(event.world.getBlock(event.x, event.y, event.z)),
						event.world.getBlockMetadata(event.x, event.y, event.z),
						formatItemStackName(event.entityPlayer.getCurrentEquippedItem())));
		}
			
	}

	public static final String FORMAT_USE_ITEM = "%2$s%1$s%3$s%1$s%4$d";
	public static final String FORMAT_USE_ITEM_DEBUG = "Item=%2$s%1$s Damage=%3$s%1$s Duration=%4$d";
	public static final String FORMAT_USE_ITEM_FINISH = FORMAT_USE_ITEM + "%1$s%5$s%1$s%6$s";
	public static final String FORMAT_USE_ITEM_FINISH_DEBUG = FORMAT_USE_ITEM_DEBUG + "%1$s ResultItem=%5$s%1$s Damage=%6$s";

	@SubscribeEvent
	public synchronized void onPlayerUseItemStart(final PlayerUseItemEvent.Start event) {
		log(event.entityPlayer, Category.PlayerUseItemStart, String.format(debug ? FORMAT_USE_ITEM_DEBUG : FORMAT_USE_ITEM, DELIMETER_DATA,
				formatItemStackName(event.item),
				formatItemStackDamage(event.item),
				event.duration));
	}

	@SubscribeEvent
	public synchronized void onPlayerUseItemStop(final PlayerUseItemEvent.Stop event) {
		log(event.entityPlayer, Category.PlayerUseItemStop, String.format(debug ? FORMAT_USE_ITEM_DEBUG : FORMAT_USE_ITEM, DELIMETER_DATA,
				formatItemStackName(event.item),
				formatItemStackDamage(event.item),
				event.duration));
	}

	@SubscribeEvent
	public synchronized void onPlayerUseItemFinish(final PlayerUseItemEvent.Finish event) {
		log(event.entityPlayer, Category.PlayerUseItemFinish, String.format(debug ? FORMAT_USE_ITEM_FINISH_DEBUG : FORMAT_USE_ITEM_FINISH, DELIMETER_DATA,
				formatItemStackName(event.item),
				formatItemStackDamage(event.item),
				event.duration,
				formatItemStackName(event.result),
				formatItemStackDamage(event.result)));
	}

	public static final String FORMAT_PICKUP_ITEM = "%2$s%1$s%3$s";
	public static final String FORMAT_PICKUP_ITEM_DEBUG = "Item=%2$s%1$s Damage=%3$s";

	@SubscribeEvent
	public synchronized void onItemPickup(final ItemPickupEvent event) {
		log(event.player, Category.PlayerPickupItem, String.format(debug ? FORMAT_PICKUP_ITEM_DEBUG : FORMAT_PICKUP_ITEM, DELIMETER_DATA,
				formatItemStackName(event.pickedUp.getEntityItem()),
				formatItemStackDamage(event.pickedUp.getEntityItem())));
	}

	public static final String FORMAT_TOSS_ITEM = "%2$s%1$s%3$s%1$s%4$s";
	public static final String FORMAT_TOSS_ITEM_DEBUG = "Item=%2$s%1$s Damage=%3$s%1$s Count=%4$s";

	@SubscribeEvent
	public synchronized void onItemToss(final ItemTossEvent event) {
		log(event.player, Category.PlayerTossItem, String.format(debug ? FORMAT_TOSS_ITEM_DEBUG : FORMAT_TOSS_ITEM, DELIMETER_DATA,
				formatItemStackName(event.entityItem.getEntityItem()),
				formatItemStackDamage(event.entityItem.getEntityItem()),
				formatItemStackSize(event.entityItem.getEntityItem())));
	}

	public static final String FORMAT_CRAFT_ITEM = "%2$s%1$s%3$s%1$s%4$s";
	public static final String FORMAT_CRAFT_ITEM_DEBUG = "Item=%2$s%1$s Count=%3$s%1$s Inventory=%4$s";

	@SubscribeEvent
	public synchronized void onItemCrafted(final ItemCraftedEvent event) {
		log(event.player, Category.PlayerCraftItem, String.format(debug ? FORMAT_CRAFT_ITEM_DEBUG : FORMAT_CRAFT_ITEM, DELIMETER_DATA,
				formatItemStackName(event.crafting),
				formatItemStackSize(event.crafting),
				formatInventoryName(event.craftMatrix)));
	}

	public static final String FORMAT_SMELT_ITEM = "%2$s%1$s%3$s";
	public static final String FORMAT_SMELT_ITEM_DEBUG = "Item=%2$s%1$s Count=%3$s";

	@SubscribeEvent
	public synchronized void onItemSmelted(final ItemSmeltedEvent event) {
		log(event.player, Category.PlayerSmeltItem, String.format(debug ? FORMAT_SMELT_ITEM_DEBUG : FORMAT_SMELT_ITEM, DELIMETER_DATA,
				formatItemStackName(event.smelting), formatItemStackSize(event.smelting)));
	}

	public static final String FORMAT_POLYCRAFT_ITEM = "%2$s%1$s%3$s%1$s%4$s";
	public static final String FORMAT_POLYCRAFT_ITEM_DEBUG = "Item=%2$s%1$s Count=%3$s%1$s Inventory=%4$s";

	public synchronized void onItemPolycrafted(final EntityPlayer player, final ItemStack item, final PolycraftInventory inventory) {
		log(player, Category.PlayerPolycraftItem, String.format(debug ? FORMAT_POLYCRAFT_ITEM_DEBUG : FORMAT_POLYCRAFT_ITEM, DELIMETER_DATA,
				formatItemStackName(item),
				formatItemStackSize(item),
				formatInventoryName(inventory)));
	}

	public static final String FORMAT_ATTACK_ENTITY = "%2$s%1$s%3$s%1$s%4$d%1$s%5$d%1$s%6$d";
	public static final String FORMAT_ATTACK_ENTITY_DEBUG = "Item=%2$s%1$s Target=%3$s%1$s X=%4$d%1$s Y=%5$d%1$s Z=%6$d";

	public static final String FORMAT_ON_EXPERIMENT_EVENT4 = "%2$d%1$s%3$d%1$s%4$s%1$s%5$s%1$s%6$d%1$s%7$d%1$s%8$d";
	public static final String FORMAT_ON_EXPERIMENT_EVENT4_DEBUG = "ID=%2$d%1$s Exp_ID=%3$d%1$s Item=%4$s%1$s Target=%5$s%1$s X=%6$d%1$s Y=%7$d%1$s Z=%8$d";
	
	@SubscribeEvent
	public synchronized void onAttackEntity(final AttackEntityEvent event) {
		
		if(event.target instanceof EntityPlayer ||event.target instanceof EntityPlayerMP)
		{
			EntityPlayer playerName= (EntityPlayer)event.target;
			log(event.entityPlayer, Category.PlayerAttackEntity, String.format(debug ? FORMAT_ATTACK_ENTITY_DEBUG : FORMAT_ATTACK_ENTITY, DELIMETER_DATA,
					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), Enforcer.whitelist.get(((EntityPlayer)event.target).getDisplayName().toLowerCase()).toString(),
					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));
			
			List<Integer> running_experiments = ExperimentManager.getRunningExperiments();
			  
			for (Integer experiment_instance : running_experiments) {  
				if(ExperimentManager.getExperiment(experiment_instance).isPlayerInExperiment(event.entityPlayer.getDisplayName())){
			log(event.entityPlayer, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT4_DEBUG : FORMAT_ON_EXPERIMENT_EVENT4, DELIMETER_DATA,4,experiment_instance,
					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), Enforcer.whitelist.get(((EntityPlayer)event.target).getDisplayName().toLowerCase()).toString(),
					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));
			log((EntityPlayer)event.target, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT4_DEBUG : FORMAT_ON_EXPERIMENT_EVENT4, DELIMETER_DATA,5,experiment_instance,
					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()),Enforcer.whitelist.get(event.entityPlayer.getDisplayName().toLowerCase()).toString(), 
					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));
				  }
				  }
//			log1(event.entityPlayer, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT4_DEBUG : FORMAT_ON_EXPERIMENT_EVENT4, DELIMETER_DATA,4,
//					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), Enforcer.whitelist.get(((EntityPlayer)event.target).getDisplayName().toLowerCase()).toString(),
//					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));
//			log1((EntityPlayer)event.target, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT4_DEBUG : FORMAT_ON_EXPERIMENT_EVENT4, DELIMETER_DATA,5,
//					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()),Enforcer.whitelist.get(event.entityPlayer.getDisplayName().toLowerCase()).toString(), 
//					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));
		}
		else
			log(event.entityPlayer, Category.PlayerAttackEntity, String.format(debug ? FORMAT_ATTACK_ENTITY_DEBUG : FORMAT_ATTACK_ENTITY, DELIMETER_DATA,
					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), formatEntity(event.target),
					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));
		List<Integer> running_experiments = ExperimentManager.getRunningExperiments();
		  
		for (Integer experiment_instance : running_experiments) {  
			if(ExperimentManager.getExperiment(experiment_instance).isPlayerInExperiment(event.entityPlayer.getDisplayName())){
				log(event.entityPlayer, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT4_DEBUG : FORMAT_ON_EXPERIMENT_EVENT4, DELIMETER_DATA,4,experiment_instance,
						formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), formatEntity(event.target),
						(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));
			  }
			  }
		

//			log1(event.entityPlayer, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT4_DEBUG : FORMAT_ON_EXPERIMENT_EVENT4, DELIMETER_DATA,4,
//				formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), formatEntity(event.target),
//				(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));
	}

	public static final String FORMAT_BREAK_BLOCK = "%2$s%1$s%3$d%1$s%4$d%1$s%5$d%1$s%6$s%1$s%7$d%1$s%8$d";
	public static final String FORMAT_BREAK_BLOCK_DEBUG = "Item=%2$s%1$s X=%3$d%1$s Y=%4$d%1$s Z=%5$d%1$s Block=%6$s%1$s Metadata=%7$d%1$s ExpToDrop=%8$d";

	@SubscribeEvent
	public synchronized void onBlockBreakEvent(final BreakEvent event) {
		log(event.getPlayer(), Category.PlayerBreakBlock, String.format(debug ? FORMAT_BREAK_BLOCK_DEBUG : FORMAT_BREAK_BLOCK, DELIMETER_DATA,
				formatItemStackName(event.getPlayer().getCurrentEquippedItem()),
				event.x, event.y, event.z,
				formatBlock(event.block), event.blockMetadata, event.getExpToDrop()));
	}

	public static final String FORMAT_SLEEP_IN_BED = "%2$d%1$s%3$d%1$s%4$d%1$s%5$s";
	public static final String FORMAT_SLEEP_IN_BED_DEBUG = "X=%2$d%1$s Y=%3$d%1$s Z=%4$d%1$s Result=%5$s";

	@SubscribeEvent
	public synchronized void onPlayerSleepInBed(final PlayerSleepInBedEvent event) {
		log(event.entityPlayer, Category.PlayerSleepInBed, String.format(debug ? FORMAT_SLEEP_IN_BED_DEBUG : FORMAT_SLEEP_IN_BED, DELIMETER_DATA,
				event.x, event.y, event.z, formatEnum(event.getResult())));
	}

	public static final String FORMAT_ACHIEVEMENT = "%s";
	public static final String FORMAT_ACHIEVEMENT_DEBUG = "Achievement=%s";

	@SubscribeEvent
	public synchronized void onAchievement(final AchievementEvent event) {
		log(event.entityPlayer, Category.PlayerAchievement, String.format(debug ? FORMAT_ACHIEVEMENT_DEBUG : FORMAT_ACHIEVEMENT,
				debug ? event.achievement.getDescription() : event.achievement.statId));
	}
	
	//This is used to log winner id//
	public static final String FORMAT_ON_EXPERIMENT_EVENT0 = "%2$d%1$s%3$d%1$s%4$d%1$s%5$s";
	public static final String FORMAT_ON_EXPERIMENT_EVENT0_DEBUG = "ID=%2$d%1$s Experiment_ID=%3$d%1$s Experiment_Type=%4$d%1$s Winner_ID=%5$s";

	@SubscribeEvent
	public synchronized static void onExperimentEvent0(final PlayerExperimentEvent0 event) {
		log(event.player, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT0_DEBUG : FORMAT_ON_EXPERIMENT_EVENT0, DELIMETER_DATA, 0, event.id1,event.maxteams1,Enforcer.whitelist.get(event.player.getDisplayName().toLowerCase()).toString()));
//		log1(event.player, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT0_DEBUG : FORMAT_ON_EXPERIMENT_EVENT0, DELIMETER_DATA, 0, event.id1,event.maxteams1,Enforcer.whitelist.get(event.player.getDisplayName().toLowerCase()).toString()));
	}
	
	
	//This is used to log score every second
	public static final String FORMAT_ON_EXPERIMENT_EVENT1 = "%2$d%1$s%3$d%1$s%4$d%1$s%5$.1f";
	public static final String FORMAT_ON_EXPERIMENT_EVENT1_DEBUG = "ID=%2$d%1$s Experiment_ID=%3$d%1$s Experiment_Type=%4$d%1$s Score=%5$.1f";

	@SubscribeEvent
	public synchronized static void onExperimentEvent1(final PlayerExperimentEvent1 event) {
		log(event.player, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT1_DEBUG : FORMAT_ON_EXPERIMENT_EVENT1, DELIMETER_DATA, 1, event.id1,event.maxteams1,event.score));
//		log1(event.player, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT1_DEBUG : FORMAT_ON_EXPERIMENT_EVENT1, DELIMETER_DATA, 1, event.id1,event.maxteams1,event.score));
	}
	
	//This is used to check if player is knocked back
//	public static final String FORMAT_ON_EXPERIMENT_EVENT2 = "%2$d%1$s%3$s%1$s%4$d%1$s%5$d%1$s%6$d%1$s%7$d%1$s%8$s%1$s%9$s%1$s%10$d%1$s%11$s";
//	public static final String FORMAT_ON_EXPERIMENT_EVENT2_DEBUG = "ID=%2$d%1$s Action=%3$s%1$s X=%4$d%1$s Y=%5$d%1$s Z=%6$d%1$s Face=%7$d%1$s Result=%8$s%1$s Block=%9$s%1$s Metadata=%10$d%1$s Item=%11$s";

	public static final String FORMAT_ON_EXPERIMENT_EVENT2 = "%2$d%1$s%3$d%1$s%4$s%1$s%5$s";
	public static final String FORMAT_ON_EXPERIMENT_EVENT2_DEBUG = "ID=%2$d%1$s Exp_id=%3$d%1$s List_of_ids=%4$s%1$s Item=%5$d";
	
	
	//public synchronized void onExperimentEvent2(final PlayerInteractEvent event) {
//		//if(formatItemStackName(event.entityPlayer.getCurrentEquippedItem()).equals("item.1hw")||formatItemStackName(event.entityPlayer.getCurrentEquippedItem()).equals("item.1hv")) 
//		//if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
//			//log(event.entityPlayer, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT2_DEBUG : FORMAT_ON_EXPERIMENT_EVENT2, DELIMETER_DATA,
//					2,formatEnum(event.action), event.x, event.y, event.z,
//					event.face, formatEnum(event.getResult()),
//					formatBlock(event.world.getBlock(event.x, event.y, event.z)),
//					event.world.getBlockMetadata(event.x, event.y, event.z),
//					formatItemStackName(event.entityPlayer.getCurrentEquippedItem())));
//		//()
//		float explosionSize = 5.0F;
//		double posX=event.x;
//		double posY=event.y;
//		double posZ=event.z;
//		
//        int i = MathHelper.floor_double(posX - (double)explosionSize - 1.0D);
//        int j = MathHelper.floor_double(posX + (double)explosionSize + 1.0D);
//        int k = MathHelper.floor_double(posY - (double)explosionSize - 1.0D);
//        int i2 = MathHelper.floor_double(posY + (double)explosionSize + 1.0D);
//        int l = MathHelper.floor_double(posZ - (double)explosionSize - 1.0D);
//        int j2 = MathHelper.floor_double(posZ + (double)explosionSize + 1.0D);
//        final String SEPARATOR = ",";
//        List list1 = new ArrayList();
//        List list2 = new ArrayList();
//        
//		List list = event.world.getEntitiesWithinAABB(Entity.class,AxisAlignedBB.getBoundingBox((double)i, (double)k, (double)l, (double)j, (double)i2, (double)j2));
//		list.forEach(entity->{
//			if(entity instanceof EntityPlayer) {
//				EntityPlayerMP entityPlayer = ((EntityPlayerMP)entity);
//				//System.out.println(entityPlayer.getDisplayName());
//				list1.add(Enforcer.whitelist.get(entityPlayer.getDisplayName().toLowerCase()).toString());
//				list2.add(entityPlayer.getDisplayName());
//			}else {
//				System.out.println(entity);
//				/////
//				//Here's where direction of animal knockback happens
//			}
//			
//		});
//		StringBuilder csvBuilder = new StringBuilder();
//		
//		  for(Object entity1 : list1){
//		    csvBuilder.append(entity1.toString());
//		    csvBuilder.append(SEPARATOR);
//		  }
//				
//		  String csv = csvBuilder.toString();
//		  //System.out.println(csv);
//		 
//				
//		  //Remove last comma
//		  if(csv.length()>0)
//		  csv = csv.substring(0, csv.length() - SEPARATOR.length());
		 
		  //log(event.entityPlayer, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT2_DEBUG : FORMAT_ON_EXPERIMENT_EVENT2, DELIMETER_DATA, 2,csv,event.entityPlayer.getCurrentEquippedItem().getDisplayName()));
		  //for(Object entity1 : list2){
		  //log(getPlayer(entity1.toString()), Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT2_DEBUG : FORMAT_ON_EXPERIMENT_EVENT2, DELIMETER_DATA, 3, Enforcer.whitelist.get(event.entityPlayer.getDisplayName().toLowerCase()).toString(),event.entityPlayer.getCurrentEquippedItem().getDisplayName()));			  
		 // }
//	}
	
//	//This is used to check if player is knocking someone back
//	public static final String FORMAT_ON_EXPERIMENT_EVENT3 = "%2$d%1$s%3$d%1$s%4$d%1$s%5$.1f";
//	public static final String FORMAT_ON_EXPERIMENT_EVENT3_DEBUG = "ID=%2$d%1$s Experiment_ID=%3$d%1$s Experiment_Type=%4$d%1$s Score=%5$.1f";
//
//	@SubscribeEvent
//	public synchronized static void onExperimentEvent3(final PlayerExperimentEvent3 event) {
//		log(event.player, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT3_DEBUG : FORMAT_ON_EXPERIMENT_EVENT3, DELIMETER_DATA, 3, event.id1,event.maxteams1,event.player));
//	}
	
	//This is used to check if player is attacking someone
	public static final String FORMAT_ON_EXPERIMENT_EVENT6 = "%2$d%1$s%3$s%1$s%4$s";
	public static final String FORMAT_ON_EXPERIMENT_EVENT6_DEBUG = "ID=%2$d%1$s Player=%3$s%%1$s PlayerName=%4$s";
	
	@SubscribeEvent
	public synchronized static void onExperimentEvent6(final PlayerExperimentEvent6 event) {
		//EntityPlayer a = null;
		//if(a.getDisplayName().equals(event.playerName2)) {
		log(event.playerName2, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT6_DEBUG : FORMAT_ON_EXPERIMENT_EVENT6, DELIMETER_DATA, 6, event.id,Enforcer.whitelist.get(event.playerName2.getDisplayName().toLowerCase()).toString()));
//		log1(event.playerName2, Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT6_DEBUG : FORMAT_ON_EXPERIMENT_EVENT6, DELIMETER_DATA, 6, event.id,Enforcer.whitelist.get(event.playerName2.getDisplayName().toLowerCase()).toString()));
		//}
		}

	
	public static EntityPlayer getPlayer(String name){

	    ServerConfigurationManager server = MinecraftServer.getServer().getConfigurationManager();
	    ArrayList pl = (ArrayList) server.playerEntityList;
	    ListIterator li = pl.listIterator();

	    while (li.hasNext()){

	        EntityPlayer p = (EntityPlayer) li.next();
	        if(p.getGameProfile().getName().equals(name)){

	            return p;

	        }

	    }
	    return null;

	}
	//This is used to check if player is attacking someone
	public static final String FORMAT_ON_EXPERIMENT_EVENT7 = "%2$d%1$s%3$s%1$s%4$s";
	public static final String FORMAT_ON_EXPERIMENT_EVENT7_DEBUG = "ID=%2$d%1$s Player=%3$s%%1$s PlayerName=%4$s";
		
	@SubscribeEvent
	public synchronized static void onExperimentEvent7(final PlayerExperimentEvent7 event) {
		//EntityPlayer a = null;
		//if(a.getDisplayName().equals(event.playerName2)) {
		log(getPlayer(event.playerName2), Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT7_DEBUG : FORMAT_ON_EXPERIMENT_EVENT7, DELIMETER_DATA, 7, event.id,Enforcer.whitelist.get(event.playerName2.toLowerCase())));
//		log1(getPlayer(event.playerName2), Category.PlayerExperimentEvent0, String.format(debug ? FORMAT_ON_EXPERIMENT_EVENT7_DEBUG : FORMAT_ON_EXPERIMENT_EVENT7, DELIMETER_DATA, 7, event.id,Enforcer.whitelist.get(event.playerName2.toLowerCase())));
		//}
		}
}