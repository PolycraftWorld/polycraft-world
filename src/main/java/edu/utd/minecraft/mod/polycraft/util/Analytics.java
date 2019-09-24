package edu.utd.minecraft.mod.polycraft.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.item.ArmorSlot;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.util.Analytics.Category;

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
	
	static List<Integer> list_of_registered_experiments=new ArrayList<Integer>();
	static List<String> list_of_registered_experiments_with_time=new ArrayList<String>();
	public static HashMap<Integer,String>  Map_of_registered_experiments_with_time= new HashMap<Integer,String>();
	
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
		PlayerExperimentEvent
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


	public static String formatItemStackName(final ItemStack item) {
		return debug ? (item == null ? "n/a" : item.getDisplayName()) : (item == null ? "" : item.getUnlocalizedName());
	}

	private static String formatItemStackSize(final ItemStack item) {
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
		return debug ? (inventory == null ? "n/a" : inventory.getName()) : (inventory == null ? "" : inventory.getClass().getName());
	}

	private static final String FORMAT_LOG = "%1$s%3$s%1$s%4$s%1$s%5$d%2$s%6$d%2$s%7$d%1$s%8$s";
	private static final String FORMAT_LOG_DEBUG = " %1$s %3$s %1$s User=%4$s %1$s PosX=%5$d%2$s PosY=%6$d%2$s PosZ=%7$d %1$s %8$s";

	
	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	static LocalDateTime now = LocalDateTime.now();
	
	public synchronized static void log(final EntityPlayer player, final Category category, final String data) {
		//TODO JM need to log the world name? player.worldObj.getWorldInfo().getWorldName()
		final Long playerID = Enforcer.whitelist.get(player.getDisplayNameString().toLowerCase());
		logger.info(String.format(debug ? FORMAT_LOG_DEBUG : FORMAT_LOG,
				DELIMETER_SEGMENT, DELIMETER_DATA,
				formatEnum(category),
				playerID == null ? "-1" : playerID.toString(),
				(int) player.posX, (int) player.posY, (int) player.posZ,
				data.replace(DELIMETER_SEGMENT, " ")));
		
	}
    	
	/**
	 * Used to return exact log data from logger but returns as string to be written to experiment log file.
	 */
	public synchronized static String log1(final EntityPlayer player, final Category category, final String data) {
		//TODO JM need to log the world name? player.worldObj.getWorldInfo().getWorldName()
		final Long playerID = Enforcer.whitelist.get(player.getDisplayNameString().toLowerCase());
		return String.format(debug ? FORMAT_LOG_DEBUG : FORMAT_LOG,
				DELIMETER_SEGMENT, DELIMETER_DATA,
				formatEnum(category),
				player.getDisplayName(),
				(int) player.posX, (int) player.posY, (int) player.posZ,
				data.replace(DELIMETER_SEGMENT, " "));		
	}
	
	/**
	 *  Used to record AI scores and team scores in polycraft-analytics logger based log file.
	 */
	public synchronized static void log2(final String teamname, final Category category, final String data) {
		//TODO JM need to log the world name? player.worldObj.getWorldInfo().getWorldName()
		logger.info(String.format(debug ? FORMAT_LOG_DEBUG : FORMAT_LOG,
				DELIMETER_SEGMENT, DELIMETER_DATA,
				formatEnum(category),
				teamname,
				0, 0, 0,
				data.replace(DELIMETER_SEGMENT, " ")));
	}
	
	/**
	 * Used to record AI scores and team scores in experiment log files. 
	 */
	public synchronized static String log3(final String teamname, final Category category, final String data) {
		//TODO JM need to log the world name? player.worldObj.getWorldInfo().getWorldName()
		return String.format(debug ? FORMAT_LOG_DEBUG : FORMAT_LOG,
				DELIMETER_SEGMENT, DELIMETER_DATA,
				formatEnum(category),
				teamname,
				0, 0, 0,
				data.replace(DELIMETER_SEGMENT, " "));
	}

	/**
	 * Used to write to log all the events.
	 */
	public static void Write_to_log(EntityPlayer player,int Exp_id, String log_data)
	{
		LocalDateTime myDateObj = LocalDateTime.now(ZoneOffset.UTC); 
		DateTimeFormatter myFormatObj1 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		String formattedDate1 = myDateObj.format(myFormatObj1); 
		FileWriter writer = null;
		try {
			writer = new FileWriter(Map_of_registered_experiments_with_time.get(Exp_id),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer.write(formattedDate1+log_data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Used to write to log all the events along with experiment id.
	 */
	public static void Write_to_log_with_Exp_ID(EntityPlayer player, String log_data)
	{
		List<Integer> running_experiments = ExperimentManager.getRunningExperiments();
		LocalDateTime myDateObj = LocalDateTime.now(ZoneOffset.UTC); 
		DateTimeFormatter myFormatObj1 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		String formattedDate1 = myDateObj.format(myFormatObj1); 
		for (Integer experiment_instance : running_experiments) {  
			if(ExperimentManager.getExperiment(experiment_instance).isPlayerInExperiment(player.getDisplayNameString())){
				FileWriter writer = null;
				try {
					writer = new FileWriter(Map_of_registered_experiments_with_time.get(experiment_instance),true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					writer.write(formattedDate1+log_data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Used to log AI scores and Team scores to experiment log files.
	 */
	private static void Write_to_log_AI(String teamname,int Exp_id, String log_data) 
	{
		// TODO Auto-generated method stub
		LocalDateTime myDateObj = LocalDateTime.now(ZoneOffset.UTC); 
		DateTimeFormatter myFormatObj1 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		String formattedDate1 = myDateObj.format(myFormatObj1); 

		FileWriter writer = null;
		try {
			//File file=new File(Map_of_registered_experiments_with_time.get(event.id));
			writer = new FileWriter(Map_of_registered_experiments_with_time.get(Exp_id),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer.write(formattedDate1+log_data);
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  try {
		writer.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}

	/**
	 * Get the player from player name.
	 */
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
	
	/**
	 * Get experiment id from player if the player is in the current running experiment.
	 */
	public static Integer get_exp_ID(EntityPlayer player) {
		// TODO Auto-generated method stub
		List<Integer> running_experiments = ExperimentManager.getRunningExperiments();
		for (Integer experiment_instance : running_experiments) {  
			if(ExperimentManager.getExperiment(experiment_instance).isPlayerInExperiment(player.getDisplayNameString())){
			return experiment_instance;
			}
		}
		return 0;
	}
	
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
					log(player, Category.PlayerTickSpatial, String.format(debug ? FORMAT_TICK_SPATIAL_DEBUG : FORMAT_TICK_SPATIAL, DELIMETER_DATA,player.motionX, player.motionY, player.motionZ,(int) player.rotationPitch, (int) player.rotationYaw, (int) player.rotationYawHead,formatBoolean(player.onGround),formatBoolean(player.isSprinting()), formatBoolean(player.isSneaking())));
					Write_to_log_with_Exp_ID(player,log1(player, Category.PlayerTickSpatial, String.format(debug ? FORMAT_TICK_SPATIAL_DEBUG : FORMAT_TICK_SPATIAL, DELIMETER_DATA,player.motionX, player.motionY, player.motionZ,(int) player.rotationPitch, (int) player.rotationYaw, (int) player.rotationYawHead,formatBoolean(player.onGround),formatBoolean(player.isSprinting()), formatBoolean(player.isSneaking())))+System.getProperty("line.separator"));
				}

				if (tickIntervals.swimming > 0) {
					if (player.isInWater()) {
						if (playerState.ticksSwimming++ == tickIntervals.swimming) {
							playerState.ticksSwimming = 0;
							log(player, Category.PlayerTickSwimming, String.format(debug ? FORMAT_TICK_SWIMMING_DEBUG : FORMAT_TICK_SWIMMING, player.getAir()));
							Write_to_log_with_Exp_ID(player,log1(player, Category.PlayerTickSwimming, String.format(debug ? FORMAT_TICK_SWIMMING_DEBUG : FORMAT_TICK_SWIMMING, player.getAir()))+System.getProperty("line.separator"));
						}
					}
					else if (playerState.ticksSwimming > 0)
						playerState.ticksSwimming = 0;
				}

				if (tickIntervals.health > 0 && playerState.ticksHealth++ == tickIntervals.health) {
					playerState.ticksHealth = 0;
					log(player, Category.PlayerTickHealth, String.format(debug ? FORMAT_TICK_HEALTH_DEBUG : FORMAT_TICK_HEALTH, player.getHealth()));
					Write_to_log_with_Exp_ID(player,log1(player, Category.PlayerTickHealth, String.format(debug ? FORMAT_TICK_HEALTH_DEBUG : FORMAT_TICK_HEALTH, player.getHealth()))+System.getProperty("line.separator"));
				}

				if (tickIntervals.item > 0 && playerState.ticksItem++ == tickIntervals.item) {
					playerState.ticksItem = 0;
					log(player, Category.PlayerTickItem, String.format(debug ? FORMAT_TICK_ITEM_DEBUG : FORMAT_TICK_ITEM, DELIMETER_DATA,formatItemStackName(player.getCurrentEquippedItem()),formatItemStackDamage(player.getCurrentEquippedItem())));
					Write_to_log_with_Exp_ID(player,log1(player, Category.PlayerTickItem, String.format(debug ? FORMAT_TICK_ITEM_DEBUG : FORMAT_TICK_ITEM, DELIMETER_DATA,formatItemStackName(player.getCurrentEquippedItem()),formatItemStackDamage(player.getCurrentEquippedItem())))+System.getProperty("line.separator"));
				}

				if (tickIntervals.food > 0 && playerState.ticksFood++ == tickIntervals.food) {
					playerState.ticksFood = 0;
					log(player, Category.PlayerTickFood, String.format(debug ? FORMAT_TICK_FOOD_DEBUG : FORMAT_TICK_FOOD, DELIMETER_DATA,player.getFoodStats().getFoodLevel(),player.getFoodStats().getSaturationLevel()));
					Write_to_log_with_Exp_ID(player,log1(player, Category.PlayerTickFood, String.format(debug ? FORMAT_TICK_FOOD_DEBUG : FORMAT_TICK_FOOD, DELIMETER_DATA,player.getFoodStats().getFoodLevel(),player.getFoodStats().getSaturationLevel()))+System.getProperty("line.separator"));
				}

				if (tickIntervals.experience > 0 && playerState.ticksExperience++ == tickIntervals.experience) {
					playerState.ticksExperience = 0;
					log(player, Category.PlayerTickExperience, String.format(debug ? FORMAT_TICK_EXPERIENCE_DEBUG : FORMAT_TICK_EXPERIENCE, DELIMETER_DATA,player.experienceTotal,player.experienceLevel));
					Write_to_log_with_Exp_ID(player,log1(player, Category.PlayerTickExperience, String.format(debug ? FORMAT_TICK_EXPERIENCE_DEBUG : FORMAT_TICK_EXPERIENCE, DELIMETER_DATA,player.experienceTotal,player.experienceLevel))+System.getProperty("line.separator"));
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
					log(player, Category.PlayerTickArmor, String.format(debug ? FORMAT_TICK_ARMOR_DEBUG : FORMAT_TICK_ARMOR, DELIMETER_DATA,player.getTotalArmorValue(), count, items.toString()));
					Write_to_log_with_Exp_ID(player,log1(player, Category.PlayerTickArmor, String.format(debug ? FORMAT_TICK_ARMOR_DEBUG : FORMAT_TICK_ARMOR, DELIMETER_DATA,player.getTotalArmorValue(), count, items.toString()))+System.getProperty("line.separator"));
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
					Write_to_log_with_Exp_ID(player,log1(player, Category.PlayerTickHotbar, String.format(debug ? FORMAT_TICK_HOTBAR_DEBUG : FORMAT_TICK_HOTBAR, DELIMETER_DATA, count, items.toString()))+System.getProperty("line.separator"));
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
					Write_to_log_with_Exp_ID(player,log1(player, Category.PlayerTickInventory, String.format(debug ? FORMAT_TICK_INVENTORY_DEBUG : FORMAT_TICK_INVENTORY, DELIMETER_DATA, count, items.toString()))+System.getProperty("line.separator"));
				}
			}
		}
	}

	public static final String FORMAT_SERVER_CHAT = "%s";
	public static final String FORMAT_SERVER_CHAT_DEBUG = "Message=%s";

	@SubscribeEvent
	public synchronized void onServerChat(final ServerChatEvent event) {
		log(event.player, Category.PlayerChat, String.format(debug ? FORMAT_SERVER_CHAT_DEBUG : FORMAT_SERVER_CHAT, event.message));
		Write_to_log_with_Exp_ID(event.player,log1(event.player, Category.PlayerChat, String.format(debug ? FORMAT_SERVER_CHAT_DEBUG : FORMAT_SERVER_CHAT, event.message))+System.getProperty("line.separator"));
	}

	public static final String FORMAT_INTERACT = "%2$s%1$s%3$d%1$s%4$d%1$s%5$d%1$s%6$d%1$s%7$s%1$s%8$s%1$s%9$d%1$s%10$s";
	public static final String FORMAT_INTERACT_DEBUG = "Action=%2$s%1$s X=%3$d%1$s Y=%4$d%1$s Z=%5$d%1$s Face=%6$d%1$s Result=%7$s%1$s Block=%8$s%1$s Metadata=%9$d%1$s Item=%10$s";
	
	public static final String FORMAT_INTERACT1 = "%2$s%1$s%3$d%1$s%4$d%1$s%5$d%1$s%6$d%1$s%7$s%1$s%8$s%1$s%9$d%1$s%10$s";
	public static final String FORMAT_INTERACT1_DEBUG = "Action=%2$s%1$s X=%3$d%1$s Y=%4$d%1$s Z=%5$d%1$s Face=%6$d%1$s Result=%7$s%1$s Block=%8$s%1$s Metadata=%9$d%1$s Item=%10$s";

	@SubscribeEvent
	public synchronized void onPlayerInteract(final PlayerInteractEvent event) {
		if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
			log(event.entityPlayer, Category.PlayerInteract, String.format(debug ? FORMAT_INTERACT_DEBUG : FORMAT_INTERACT, DELIMETER_DATA,formatEnum(event.action), event.pos.getX(), event.pos.getY(), event.pos.getZ(),event.face, formatEnum(event.getResult()),formatBlock(event.world.getBlockState(event.pos).getBlock()),event.world.getBlockState(event.pos).getBlock().getMetaFromState(event.world.getBlockState(event.pos)),formatItemStackName(event.entityPlayer.getCurrentEquippedItem())));
			Write_to_log_with_Exp_ID(event.entityPlayer,log1(event.entityPlayer, Category.PlayerInteract, String.format(debug ? FORMAT_INTERACT_DEBUG : FORMAT_INTERACT, DELIMETER_DATA,formatEnum(event.action), event.pos.getX(), event.pos.getY(), event.pos.getY(),event.face, formatEnum(event.getResult()),formatBlock(event.world.getBlockState(event.pos).getBlock()),event.world.getBlockState(event.pos).getBlock().getMetaFromState(event.world.getBlockState(event.pos)),formatItemStackName(event.entityPlayer.getCurrentEquippedItem())))+System.getProperty("line.separator"));
		}
	}

	public static final String FORMAT_USE_ITEM = "%2$s%1$s%3$s%1$s%4$d";
	public static final String FORMAT_USE_ITEM_DEBUG = "Item=%2$s%1$s Damage=%3$s%1$s Duration=%4$d";
	public static final String FORMAT_USE_ITEM_FINISH = FORMAT_USE_ITEM + "%1$s%5$s%1$s%6$s";
	public static final String FORMAT_USE_ITEM_FINISH_DEBUG = FORMAT_USE_ITEM_DEBUG + "%1$s ResultItem=%5$s%1$s Damage=%6$s";

	@SubscribeEvent
	public synchronized void onPlayerUseItemStart(final PlayerUseItemEvent.Start event) {
		log(event.entityPlayer, Category.PlayerUseItemStart, String.format(debug ? FORMAT_USE_ITEM_DEBUG : FORMAT_USE_ITEM, DELIMETER_DATA,formatItemStackName(event.item),formatItemStackDamage(event.item),event.duration));
		Write_to_log_with_Exp_ID(event.entityPlayer,log1(event.entityPlayer, Category.PlayerUseItemStart, String.format(debug ? FORMAT_USE_ITEM_DEBUG : FORMAT_USE_ITEM, DELIMETER_DATA,formatItemStackName(event.item),formatItemStackDamage(event.item),event.duration))+System.getProperty("line.separator"));
	}

	@SubscribeEvent
	public synchronized void onPlayerUseItemStop(final PlayerUseItemEvent.Stop event) {
		log(event.entityPlayer, Category.PlayerUseItemStop, String.format(debug ? FORMAT_USE_ITEM_DEBUG : FORMAT_USE_ITEM, DELIMETER_DATA,formatItemStackName(event.item),formatItemStackDamage(event.item),event.duration));
		Write_to_log_with_Exp_ID(event.entityPlayer,log1(event.entityPlayer, Category.PlayerUseItemStop, String.format(debug ? FORMAT_USE_ITEM_DEBUG : FORMAT_USE_ITEM, DELIMETER_DATA,formatItemStackName(event.item),formatItemStackDamage(event.item),event.duration))+System.getProperty("line.separator"));
	}

	@SubscribeEvent
	public synchronized void onPlayerUseItemFinish(final PlayerUseItemEvent.Finish event) {
		log(event.entityPlayer, Category.PlayerUseItemFinish, String.format(debug ? FORMAT_USE_ITEM_FINISH_DEBUG : FORMAT_USE_ITEM_FINISH, DELIMETER_DATA,formatItemStackName(event.item),formatItemStackDamage(event.item),event.duration,formatItemStackName(event.result),formatItemStackDamage(event.result)));
		Write_to_log_with_Exp_ID(event.entityPlayer,log1(event.entityPlayer, Category.PlayerUseItemFinish, String.format(debug ? FORMAT_USE_ITEM_FINISH_DEBUG : FORMAT_USE_ITEM_FINISH, DELIMETER_DATA,formatItemStackName(event.item),formatItemStackDamage(event.item),event.duration,formatItemStackName(event.result),formatItemStackDamage(event.result)))+System.getProperty("line.separator"));
	}

	public static final String FORMAT_PICKUP_ITEM = "%2$s%1$s%3$s";
	public static final String FORMAT_PICKUP_ITEM_DEBUG = "Item=%2$s%1$s Damage=%3$s";

	@SubscribeEvent
	public synchronized void onItemPickup(final ItemPickupEvent event) {
		log(event.player, Category.PlayerPickupItem, String.format(debug ? FORMAT_PICKUP_ITEM_DEBUG : FORMAT_PICKUP_ITEM, DELIMETER_DATA,formatItemStackName(event.pickedUp.getEntityItem()),formatItemStackDamage(event.pickedUp.getEntityItem())));
		Write_to_log_with_Exp_ID(event.player,log1(event.player, Category.PlayerPickupItem, String.format(debug ? FORMAT_PICKUP_ITEM_DEBUG : FORMAT_PICKUP_ITEM, DELIMETER_DATA,formatItemStackName(event.pickedUp.getEntityItem()),formatItemStackDamage(event.pickedUp.getEntityItem())))+System.getProperty("line.separator"));
	}

	public static final String FORMAT_TOSS_ITEM = "%2$s%1$s%3$s%1$s%4$s";
	public static final String FORMAT_TOSS_ITEM_DEBUG = "Item=%2$s%1$s Damage=%3$s%1$s Count=%4$s";

	@SubscribeEvent
	public synchronized void onItemToss(final ItemTossEvent event) {
		log(event.player, Category.PlayerTossItem, String.format(debug ? FORMAT_TOSS_ITEM_DEBUG : FORMAT_TOSS_ITEM, DELIMETER_DATA,formatItemStackName(event.entityItem.getEntityItem()),formatItemStackDamage(event.entityItem.getEntityItem()),formatItemStackSize(event.entityItem.getEntityItem())));
		Write_to_log_with_Exp_ID(event.player,log1(event.player, Category.PlayerTossItem, String.format(debug ? FORMAT_TOSS_ITEM_DEBUG : FORMAT_TOSS_ITEM, DELIMETER_DATA,formatItemStackName(event.entityItem.getEntityItem()),formatItemStackDamage(event.entityItem.getEntityItem()),formatItemStackSize(event.entityItem.getEntityItem())))+System.getProperty("line.separator"));
	}

	public static final String FORMAT_CRAFT_ITEM = "%2$s%1$s%3$s%1$s%4$s";
	public static final String FORMAT_CRAFT_ITEM_DEBUG = "Item=%2$s%1$s Count=%3$s%1$s Inventory=%4$s";

	@SubscribeEvent
	public synchronized void onItemCrafted(final ItemCraftedEvent event) {
		log(event.player, Category.PlayerCraftItem, String.format(debug ? FORMAT_CRAFT_ITEM_DEBUG : FORMAT_CRAFT_ITEM, DELIMETER_DATA,formatItemStackName(event.crafting),formatItemStackSize(event.crafting),formatInventoryName(event.craftMatrix)));
		Write_to_log_with_Exp_ID(event.player,log1(event.player, Category.PlayerCraftItem, String.format(debug ? FORMAT_CRAFT_ITEM_DEBUG : FORMAT_CRAFT_ITEM, DELIMETER_DATA,formatItemStackName(event.crafting),formatItemStackSize(event.crafting),formatInventoryName(event.craftMatrix)))+System.getProperty("line.separator"));
	}

	public static final String FORMAT_SMELT_ITEM = "%2$s%1$s%3$s";
	public static final String FORMAT_SMELT_ITEM_DEBUG = "Item=%2$s%1$s Count=%3$s";

	@SubscribeEvent
	public synchronized void onItemSmelted(final ItemSmeltedEvent event) {
		log(event.player, Category.PlayerSmeltItem, String.format(debug ? FORMAT_SMELT_ITEM_DEBUG : FORMAT_SMELT_ITEM, DELIMETER_DATA,formatItemStackName(event.smelting), formatItemStackSize(event.smelting)));
		Write_to_log_with_Exp_ID(event.player,log1(event.player, Category.PlayerSmeltItem, String.format(debug ? FORMAT_SMELT_ITEM_DEBUG : FORMAT_SMELT_ITEM, DELIMETER_DATA,formatItemStackName(event.smelting), formatItemStackSize(event.smelting)))+System.getProperty("line.separator"));
	}

	public static final String FORMAT_POLYCRAFT_ITEM = "%2$s%1$s%3$s%1$s%4$s";
	public static final String FORMAT_POLYCRAFT_ITEM_DEBUG = "Item=%2$s%1$s Count=%3$s%1$s Inventory=%4$s";

	public synchronized void onItemPolycrafted(final EntityPlayer player, final ItemStack item, final PolycraftInventory inventory) {
		log(player, Category.PlayerPolycraftItem, String.format(debug ? FORMAT_POLYCRAFT_ITEM_DEBUG : FORMAT_POLYCRAFT_ITEM, DELIMETER_DATA,formatItemStackName(item),formatItemStackSize(item),formatInventoryName(inventory)));
		Write_to_log_with_Exp_ID(player,log1(player, Category.PlayerPolycraftItem, String.format(debug ? FORMAT_POLYCRAFT_ITEM_DEBUG : FORMAT_POLYCRAFT_ITEM, DELIMETER_DATA,formatItemStackName(item),formatItemStackSize(item),formatInventoryName(inventory)))+System.getProperty("line.separator"));
	}

	public static final String FORMAT_ATTACK_ENTITY = "%2$s%1$s%3$s%1$s%4$d%1$s%5$d%1$s%6$d";
	public static final String FORMAT_ATTACK_ENTITY_DEBUG = "Item=%2$s%1$s Target=%3$s%1$s X=%4$d%1$s Y=%5$d%1$s Z=%6$d";

	public static final String FORMAT_ON_EXPERIMENT_ATTACK_ENTITY = "%2$d%1$s%3$d%1$s%4$s%1$s%5$s%1$s%6$d%1$s%7$d%1$s%8$d";
	public static final String FORMAT_ON_EXPERIMENT_ATTACK_ENTITY_DEBUG = "ID=%2$d%1$s Exp_ID=%3$d%1$s Item=%4$s%1$s Target=%5$s%1$s X=%6$d%1$s Y=%7$d%1$s Z=%8$d";
	
	/**
	 * 23-4 is to get the player info who got attacked in experiment whereas 23-5 is the player info who attacked.
	 * (If entity got attacked only 23-4 is recorded not 23-5). Both are recorded with the equipped item used to attack.
	 */
	@SubscribeEvent
	public synchronized void onAttackEntity(final AttackEntityEvent event) {
		if(event.target instanceof EntityPlayer ||event.target instanceof EntityPlayerMP)
		{
			EntityPlayer playerName= (EntityPlayer)event.target;
			log(event.entityPlayer, Category.PlayerAttackEntity, String.format(debug ? FORMAT_ATTACK_ENTITY_DEBUG : FORMAT_ATTACK_ENTITY, DELIMETER_DATA,
					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), Enforcer.whitelist.get(((EntityPlayer)event.target).getDisplayNameString().toLowerCase()).toString(),
					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));			
			log(event.entityPlayer, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_EXPERIMENT_ATTACK_ENTITY_DEBUG : FORMAT_ON_EXPERIMENT_ATTACK_ENTITY, DELIMETER_DATA,4,get_exp_ID(event.entityPlayer),
					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), Enforcer.whitelist.get(((EntityPlayer)event.target).getDisplayNameString().toLowerCase()).toString(),
					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));
			log((EntityPlayer)event.target, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_EXPERIMENT_ATTACK_ENTITY_DEBUG : FORMAT_ON_EXPERIMENT_ATTACK_ENTITY, DELIMETER_DATA,5,get_exp_ID(event.entityPlayer),
					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()),Enforcer.whitelist.get(event.entityPlayer.getDisplayNameString().toLowerCase()).toString(), 
					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));
			
			Write_to_log_with_Exp_ID(event.entityPlayer,log1(event.entityPlayer, Category.PlayerAttackEntity, String.format(debug ? FORMAT_ATTACK_ENTITY_DEBUG : FORMAT_ATTACK_ENTITY, DELIMETER_DATA,
					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), Enforcer.whitelist.get(((EntityPlayer)event.target).getDisplayNameString().toLowerCase()).toString(),
					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ))+System.getProperty("line.separator"));
			Write_to_log_with_Exp_ID(event.entityPlayer,log1(event.entityPlayer, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_EXPERIMENT_ATTACK_ENTITY_DEBUG : FORMAT_ON_EXPERIMENT_ATTACK_ENTITY, DELIMETER_DATA,4,get_exp_ID(event.entityPlayer),
					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), Enforcer.whitelist.get(((EntityPlayer)event.target).getDisplayNameString().toLowerCase()).toString(),
					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ))+System.getProperty("line.separator"));
			Write_to_log_with_Exp_ID(event.entityPlayer,log1((EntityPlayer)event.target, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_EXPERIMENT_ATTACK_ENTITY_DEBUG : FORMAT_ON_EXPERIMENT_ATTACK_ENTITY, DELIMETER_DATA,5,get_exp_ID(event.entityPlayer),
					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()),Enforcer.whitelist.get(event.entityPlayer.getDisplayNameString().toLowerCase()).toString(), 
					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ))+System.getProperty("line.separator"));
		}
		else
		{
			log(event.entityPlayer, Category.PlayerAttackEntity, String.format(debug ? FORMAT_ATTACK_ENTITY_DEBUG : FORMAT_ATTACK_ENTITY, DELIMETER_DATA,
					formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), event.target.getClass().getSimpleName(),
					(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));
			log(event.entityPlayer, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_EXPERIMENT_ATTACK_ENTITY_DEBUG : FORMAT_ON_EXPERIMENT_ATTACK_ENTITY, DELIMETER_DATA,4,get_exp_ID(event.entityPlayer),
						formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), event.target.getClass().getSimpleName(),
						(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ));
			Write_to_log_with_Exp_ID(event.entityPlayer,log1(event.entityPlayer, Category.PlayerAttackEntity, String.format(debug ? FORMAT_ATTACK_ENTITY_DEBUG : FORMAT_ATTACK_ENTITY, DELIMETER_DATA,
						formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), event.target.getClass().getSimpleName(),
						(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ))+System.getProperty("line.separator"));
			Write_to_log_with_Exp_ID(event.entityPlayer,log1(event.entityPlayer, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_EXPERIMENT_ATTACK_ENTITY_DEBUG : FORMAT_ON_EXPERIMENT_ATTACK_ENTITY, DELIMETER_DATA,4,get_exp_ID(event.entityPlayer),
						formatItemStackName(event.entityPlayer.getCurrentEquippedItem()), event.target.getClass().getSimpleName(),
						(int) event.target.posX, (int) event.target.posY, (int) event.target.posZ))+System.getProperty("line.separator"));
		}
	}

	public static final String FORMAT_BREAK_BLOCK = "%2$s%1$s%3$d%1$s%4$d%1$s%5$d%1$s%6$s%1$s%7$d%1$s%8$d";
	public static final String FORMAT_BREAK_BLOCK_DEBUG = "Item=%2$s%1$s X=%3$d%1$s Y=%4$d%1$s Z=%5$d%1$s Block=%6$s%1$s Metadata=%7$d%1$s ExpToDrop=%8$d";

	@SubscribeEvent
	public synchronized void onBlockBreakEvent(final BreakEvent event) {
		log(event.getPlayer(), Category.PlayerBreakBlock, String.format(debug ? FORMAT_BREAK_BLOCK_DEBUG : FORMAT_BREAK_BLOCK, DELIMETER_DATA,formatItemStackName(event.getPlayer().getCurrentEquippedItem()),event.pos.getX(), event.pos.getY(), event.pos.getZ(),formatBlock(event.state.getBlock()), event.state.getBlock().getMetaFromState(event.state), event.getExpToDrop()));
		Write_to_log_with_Exp_ID(event.getPlayer(),log1(event.getPlayer(), Category.PlayerBreakBlock, String.format(debug ? FORMAT_BREAK_BLOCK_DEBUG : FORMAT_BREAK_BLOCK, DELIMETER_DATA,formatItemStackName(event.getPlayer().getCurrentEquippedItem()),event.pos.getX(), event.pos.getY(), event.pos.getZ(),formatBlock(event.state.getBlock()), event.state.getBlock().getMetaFromState(event.state), event.getExpToDrop()))+System.getProperty("line.separator"));
	}

	public static final String FORMAT_SLEEP_IN_BED = "%2$d%1$s%3$d%1$s%4$d%1$s%5$s";
	public static final String FORMAT_SLEEP_IN_BED_DEBUG = "X=%2$d%1$s Y=%3$d%1$s Z=%4$d%1$s Result=%5$s";

	@SubscribeEvent
	public synchronized void onPlayerSleepInBed(final PlayerSleepInBedEvent event) {
		log(event.entityPlayer, Category.PlayerSleepInBed, String.format(debug ? FORMAT_SLEEP_IN_BED_DEBUG : FORMAT_SLEEP_IN_BED, DELIMETER_DATA,event.pos.getX(), event.pos.getY(), event.pos.getZ(), formatEnum(event.getResult())));
		Write_to_log_with_Exp_ID(event.entityPlayer,log1(event.entityPlayer, Category.PlayerSleepInBed, String.format(debug ? FORMAT_SLEEP_IN_BED_DEBUG : FORMAT_SLEEP_IN_BED, DELIMETER_DATA,event.pos.getX(), event.pos.getY(), event.pos.getZ(), formatEnum(event.getResult())))+System.getProperty("line.separator"));
	}

	public static final String FORMAT_ACHIEVEMENT = "%s";
	public static final String FORMAT_ACHIEVEMENT_DEBUG = "Achievement=%s";

	@SubscribeEvent
	public synchronized void onAchievement(final AchievementEvent event) {
		log(event.entityPlayer, Category.PlayerAchievement, String.format(debug ? FORMAT_ACHIEVEMENT_DEBUG : FORMAT_ACHIEVEMENT,debug ? event.achievement.getDescription() : event.achievement.statId));
		Write_to_log_with_Exp_ID(event.entityPlayer,log1(event.entityPlayer, Category.PlayerAchievement, String.format(debug ? FORMAT_ACHIEVEMENT_DEBUG : FORMAT_ACHIEVEMENT,debug ? event.achievement.getDescription() : event.achievement.statId))+System.getProperty("line.separator"));
	}
	
	public static final String FORMAT_ON_TEAMWON = "%2$d%1$s%3$d%1$s%4$s";
	public static final String FORMAT_ON_TEAMWON_DEBUG = "ID=%2$d%1$s Experiment_ID=%3$d%1$s Winner_ID=%4$s";

	/**
	 * 23-0 Logs winner team name
	 */
	@SubscribeEvent
	public synchronized static void onTeamWon(final TeamWonEvent event) {
		if(event.playername=="Animals") {
			log(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_TEAMWON_DEBUG : FORMAT_ON_TEAMWON, DELIMETER_DATA, 0, event.id1,"AI"));
			Write_to_log(event.player,event.id1,log1(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_TEAMWON_DEBUG : FORMAT_ON_TEAMWON, DELIMETER_DATA, 0, event.id1,"AI"))+System.getProperty("line.separator"));
		}
		else {
		log(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_TEAMWON_DEBUG : FORMAT_ON_TEAMWON, DELIMETER_DATA, 0, event.id1,Enforcer.whitelist.get(event.player.getDisplayNameString().toLowerCase()).toString()));
		Write_to_log(event.player,event.id1,log1(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_TEAMWON_DEBUG : FORMAT_ON_TEAMWON, DELIMETER_DATA, 0, event.id1,Enforcer.whitelist.get(event.player.getDisplayNameString().toLowerCase()).toString()))+System.getProperty("line.separator"));
		}
	}
	
	public static final String FORMAT_ON_SCOREEVENT = "%2$d%1$s%3$d%1$s%4$.1f";
	public static final String FORMAT_ON_SCOREEVENT_DEBUG = "ID=%2$d%1$s Experiment_ID=%3$d%1$s Score=%5$.1f";

	/**
	 * 23-1 Logs team wise score every second 
	 */
	@SubscribeEvent
	public synchronized static void onScoreEvent(final ScoreEvent event) {
		log(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_SCOREEVENT_DEBUG : FORMAT_ON_SCOREEVENT, DELIMETER_DATA, 1, event.id1,event.score));
		Write_to_log(event.player,event.id1,log1(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_SCOREEVENT_DEBUG : FORMAT_ON_SCOREEVENT, DELIMETER_DATA, 1, event.id1,event.score))+System.getProperty("line.separator"));
	}

	public static final String FORMAT_ON_KNOCKBACK_EVENT = "%2$d%1$s%3$d%1$s%4$s%1$s%5$s";
	public static final String FORMAT_ON_KNOCKBACK_EVENT_DEBUG = "ID=%2$d%1$s Exp_id=%3$d%1$s List_of_ids=%4$s%1$s Item=%5$d";
	
	/**23-2 logs the list of entities/players who got knocked back when KBB/FKBB is used and the equipped item is also recorded.
	 * 
	 */
	@SubscribeEvent
	public synchronized static void onKnockBackEvent(final PlayerKnockBackEvent event) {
		log(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_KNOCKBACK_EVENT_DEBUG : FORMAT_ON_KNOCKBACK_EVENT, DELIMETER_DATA, 2,get_exp_ID(event.player),event.knocked_list,formatItemStackName(event.player.getCurrentEquippedItem())));
		Write_to_log_with_Exp_ID(event.player,log1(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_KNOCKBACK_EVENT_DEBUG : FORMAT_ON_KNOCKBACK_EVENT, DELIMETER_DATA, 2,get_exp_ID(event.player),event.knocked_list,formatItemStackName(event.player.getCurrentEquippedItem())))+System.getProperty("line.separator"));
		}
	
	/**
	 * 23-2 logs the player details who got knocked other players/entities when KBB/FKBB is used and the equipped item is also recorded.
	 */
	@SubscribeEvent
	public synchronized static void onKnockedBackEvent(final PlayerKnockedBackEvent event) {
		log(getPlayer(event.entity1), Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_KNOCKBACK_EVENT_DEBUG : FORMAT_ON_KNOCKBACK_EVENT, DELIMETER_DATA, 3,get_exp_ID(event.player), Enforcer.whitelist.get(event.player.getDisplayNameString().toLowerCase()).toString(),formatItemStackName(event.player.getCurrentEquippedItem())));
		Write_to_log_with_Exp_ID(event.player,log1(getPlayer(event.entity1), Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_KNOCKBACK_EVENT_DEBUG : FORMAT_ON_KNOCKBACK_EVENT, DELIMETER_DATA, 3,get_exp_ID(event.player), Enforcer.whitelist.get(event.player.getDisplayNameString().toLowerCase()).toString(),formatItemStackName(event.player.getCurrentEquippedItem())))+System.getProperty("line.separator"));
		}
	
	public static final String FORMAT_ON_PLAYER_REGISTER_EVENT = "%2$d%1$s%3$s%1$s%4$s";
	public static final String FORMAT_ON_PLAYER_REGISTER_EVENT_DEBUG = "ID=%2$d%1$s Player=%3$s%%1$s PlayerName=%4$s";
	
	/**
	 * 23-6 creates log file and logs the player ID along with experiment ID.
	 */
	@SubscribeEvent
	public synchronized static void onPlayerRegisterEvent(final PlayerRegisterEvent event) {		
		int i=0;
		log(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_PLAYER_REGISTER_EVENT_DEBUG : FORMAT_ON_PLAYER_REGISTER_EVENT, DELIMETER_DATA, 6, event.id,Enforcer.whitelist.get(event.player.getDisplayNameString().toLowerCase()).toString()));

		LocalDateTime myDateObj = LocalDateTime.now(ZoneOffset.UTC); 
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
		DateTimeFormatter myFormatObj1 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

		String formattedDate = myDateObj.format(myFormatObj); 
		String formattedDate1 = myDateObj.format(myFormatObj1); 
		if(list_of_registered_experiments.contains(event.id)) {
			FileWriter writer = null;
			try {
				//File file=new File(Map_of_registered_experiments_with_time.get(event.id));
				writer = new FileWriter(Map_of_registered_experiments_with_time.get(event.id),true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      try {
				writer.write(formattedDate1+log1(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_PLAYER_REGISTER_EVENT_DEBUG : FORMAT_ON_PLAYER_REGISTER_EVENT, DELIMETER_DATA, 6, event.id,Enforcer.whitelist.get(event.player.getDisplayNameString().toLowerCase()).toString()))+System.getProperty("line.separator"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			list_of_registered_experiments.add(event.id);
			File directory = new File("logs/Experiment_logs");
		    if (! directory.exists())
		    	directory.mkdir();
			String a="logs/Experiment_logs/Experiment "+event.id+" "+formattedDate+".log";
			File file = new File(a);
			list_of_registered_experiments_with_time.add(a);
			Map_of_registered_experiments_with_time.put(event.id,a);
			try {
				if (file.createNewFile())
				{
				} else {
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileWriter writer = null;
			try {
				writer = new FileWriter(file,true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      try {
				writer.write(formattedDate1+log1(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_PLAYER_REGISTER_EVENT_DEBUG : FORMAT_ON_PLAYER_REGISTER_EVENT, DELIMETER_DATA, 6, event.id,Enforcer.whitelist.get(event.player.getDisplayNameString().toLowerCase()).toString()))+System.getProperty("line.separator"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}	
		}

	public static final String FORMAT_ON_PLAYEREXIT_EVENT = "%2$d%1$s%3$s%1$s%4$s";
	public static final String FORMAT_ON_PLAYEREXIT_EVENT_DEBUG = "ID=%2$d%1$s Player=%3$s%%1$s PlayerName=%4$s";

	/**
	 * 23-7 logs which player left the experiment or disconnected from the server.
	 */
	@SubscribeEvent
	public synchronized static void onPlayerExitEvent(final PlayerExitEvent event) {
		log(getPlayer(event.playerName2), Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_PLAYEREXIT_EVENT_DEBUG : FORMAT_ON_PLAYEREXIT_EVENT, DELIMETER_DATA, 7, event.id,Enforcer.whitelist.get(event.playerName2.toLowerCase())));
		Write_to_log(getPlayer(event.playerName2),event.id,log1(getPlayer(event.playerName2), Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_PLAYEREXIT_EVENT_DEBUG : FORMAT_ON_PLAYEREXIT_EVENT, DELIMETER_DATA, 7, event.id,Enforcer.whitelist.get(event.playerName2.toLowerCase())))+System.getProperty("line.separator"));
		}

	public static final String FORMAT_ON_HALFTIMEGUI_EVENT = "%2$d%1$s%3$d%1$s%4$s";
	public static final String FORMAT_ON_HALFTIMEGUI_EVENT_DEBUG = "ID=%2$d%1$s Exp_ID=%3$d%1$s HalfTimeAnswers=%4$s";
	
	static List<Integer> running_experiments;
	
	/**
	 * 23-8 records half time GUI answers for respective players.
	 */
	@SubscribeEvent
	public synchronized static void onHalfTimeGUIEvent(final PlayerHalfTimeGUIEvent event) {
		log(getPlayer(event.playername), Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_HALFTIMEGUI_EVENT_DEBUG : FORMAT_ON_HALFTIMEGUI_EVENT, DELIMETER_DATA, 8,get_exp_ID(getPlayer(event.playername)), event.Halftime_GUI_answers));
		Write_to_log_with_Exp_ID(getPlayer(event.playername),log1(getPlayer(event.playername), Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_HALFTIMEGUI_EVENT_DEBUG : FORMAT_ON_HALFTIMEGUI_EVENT, DELIMETER_DATA, 8, get_exp_ID(getPlayer(event.playername)),event.Halftime_GUI_answers))+System.getProperty("line.separator"));
	}

	public static final String FORMAT_ON_PLAYERTEAM_EVENT = "%2$d%1$s%3$s%1$s%4$s%1$s%5$s";
	public static final String FORMAT_ON_PLAYERTEAM_EVENT_DEBUG = "ID=%2$d%1$s TeamName=%3$s%%1$s TeamName=%4$s%%1$s Player=%5$s";
	
	/**
	 * 23-9 Records all player IDs to their respective team names just after player registers for the experiment.
	 */
	@SubscribeEvent
	public synchronized static void onPlayerTeamEvent(final PlayerTeamEvent event) {
		// TODO Auto-generated method stub
		log(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_PLAYERTEAM_EVENT_DEBUG : FORMAT_ON_PLAYERTEAM_EVENT, DELIMETER_DATA, 9, event.id,event.TeamName,Enforcer.whitelist.get(event.player.getDisplayNameString().toLowerCase()).toString()));
		Write_to_log(event.player,event.id,log1(event.player, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_PLAYERTEAM_EVENT_DEBUG : FORMAT_ON_PLAYERTEAM_EVENT, DELIMETER_DATA, 9, event.id,event.TeamName,Enforcer.whitelist.get(event.player.getDisplayNameString().toLowerCase()).toString()))+System.getProperty("line.separator"));
	}

	/**
	 * 23-1 addition records AI scores with AI as player id and its score.
	 */
	@SubscribeEvent
	public synchronized static void onAIScoreEvent(final PlayerAIScoreEvent event) {
		log2(event.teamname, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_SCOREEVENT_DEBUG : FORMAT_ON_SCOREEVENT, DELIMETER_DATA, 1, event.id1,event.score));
		Write_to_log_AI(event.teamname,event.id1,log3(event.teamname, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_SCOREEVENT_DEBUG : FORMAT_ON_SCOREEVENT, DELIMETER_DATA, 1, event.id1,event.score))+System.getProperty("line.separator"));
	}
	
	/**
	 * 23-1 addition records Team Scores every second
	 */
	@SubscribeEvent
	public synchronized static void onTeamScoreEvent(final PlayerTeamScoreEvent event) {
		log2(event.teamname, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_SCOREEVENT_DEBUG : FORMAT_ON_SCOREEVENT, DELIMETER_DATA, 1, event.id,event.score));
		Write_to_log_AI(event.teamname,event.id,log3(event.teamname, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_SCOREEVENT_DEBUG : FORMAT_ON_SCOREEVENT, DELIMETER_DATA, 1, event.id,event.score))+System.getProperty("line.separator"));
	}
	
	public static final String FORMAT_ON_BASESTATUSCHANGE_EVENT = "%2$d%1$s%3$s%1$s%4$s%1$s%5$s";
	public static final String FORMAT_ON_BASESTATUSCHANGE_EVENT_DEBUG = "ID=%2$d%1$s Initial_state=%3$s%%1$s Final_state=%4$s%%1$s Entities_in_base=%5$s";
	
	/**
	 * 23-10 logs when there is a state change in bases.
	 */
	public synchronized static void onBaseStatusChangeEvent(final BaseStatusChangeEvent event) {
		//log(getPlayer(event.playerName2), Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_BASESTATUSCHANGE_EVENT_DEBUG : FORMAT_ON_BASESTATUSCHANGE_EVENT, DELIMETER_DATA, 7, event.id,Enforcer.whitelist.get(event.playerName2.toLowerCase())));
		Write_to_log_with_Exp_ID(event.entityPlayer,log1(event.entityPlayer, Category.PlayerExperimentEvent, String.format(debug ? FORMAT_ON_BASESTATUSCHANGE_EVENT_DEBUG : FORMAT_ON_BASESTATUSCHANGE_EVENT, DELIMETER_DATA, 10, event.initial_state,event.final_state,event.entities_in_base))+System.getProperty("line.separator"));
		}
	
	/**
	 * 18 records Tree Tap log
	 */
	public synchronized static void onShiftClickEvent(final ShiftClickEvent event) {
		log(event.entityPlayer, Category.PlayerPolycraftItem, String.format(debug ? FORMAT_POLYCRAFT_ITEM_DEBUG : FORMAT_POLYCRAFT_ITEM, DELIMETER_DATA,formatItemStackName(event.itemstack1),formatItemStackSize(event.itemstack1),event.containerType));
		Write_to_log_with_Exp_ID(event.entityPlayer,log1(event.entityPlayer, Category.PlayerPolycraftItem, String.format(debug ? FORMAT_POLYCRAFT_ITEM_DEBUG : FORMAT_POLYCRAFT_ITEM, DELIMETER_DATA,formatItemStackName(event.itemstack1),formatItemStackSize(event.itemstack1),event.containerType))+System.getProperty("line.separator"));
	}
}