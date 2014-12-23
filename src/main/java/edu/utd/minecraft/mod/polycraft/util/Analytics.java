package edu.utd.minecraft.mod.polycraft.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Analytics {
	public static final Logger logger = LogManager.getLogger(PolycraftMod.MODID + "-analytics");

	private static enum Category {
		Spatial,
		Status,
		Interact,
		UseItemStart,
		AttackEntity,
		BreakBlock,
		ServerChat,
	}
	
	private class PlayerState {
		private int ticksSpatial = 0;
		private int ticksStatus = 0;
	}

	private boolean debug;
	private int tickIntervalSpatial = PolycraftMod.convertSecondsToGameTicks(10);
	private int tickIntervalStatus = PolycraftMod.convertSecondsToGameTicks(60);
	
	private final Map<EntityPlayer, PlayerState> playerStates = Maps.newHashMap();

	private synchronized PlayerState getPlayerState(final EntityPlayer player) {
		PlayerState playerState = playerStates.get(player);
		if (playerState == null) {
			playerState = new PlayerState();
			playerStates.put(player, playerState);
		}
		return playerState;
	}
	
	/**
	 * @param debug Use true to show more verbose messages, use false in production
	 * @param intervalSpatial number of seconds to wait between logging spatial data, default is 10
	 * @param intervalHealth number of seconds to wait between logging health data, default is 60
	 */
	public Analytics(final boolean debug, final Integer intervalSpatial, final Integer intervalStatus) {
		this.debug = debug;
		if (intervalSpatial != null)
			tickIntervalSpatial = PolycraftMod.convertSecondsToGameTicks(intervalSpatial);
		if (intervalStatus != null)
			tickIntervalStatus = PolycraftMod.convertSecondsToGameTicks(intervalStatus);
	}

	private String formatBoolean(final boolean value) {
		return debug ? (value ? "true" : "false") : (value ? "1" : "0");
	}
	
	private String formatEnum(final Enum value) {
		return debug ? value.toString() : String.valueOf(value.ordinal());
	}
	
	private String formatItemStack(final ItemStack item) {
		return debug ? (item == null ? "none" : item.getDisplayName()) : (item == null ? "" : item.getUnlocalizedName());
	}
	
	private String formatBlock(final Block block) {
		return debug ? block.getLocalizedName() : block.getUnlocalizedName();
	}
	
	private String formatEntity(final Entity entity) {
		return debug ? entity.getClass().getName() : entity.getClass().getSimpleName();
	}

	private static final String FORMAT_LOG = "[%s][%d,%d,%d][%s][%s]";
	private static final String FORMAT_LOG_DEBUG = "[User=%s][PosX=%d, PosY=%d, PosZ=%d][Category=%s][%s]";
	
	private synchronized void log(final EntityPlayer player, final Category category, final String format, final Object...params) {
		//TODO JM need to log the world name? player.worldObj.getWorldInfo().getWorldName()
		logger.info(String.format(debug ? FORMAT_LOG_DEBUG : FORMAT_LOG,
				player.getDisplayName(), (int)player.posX, (int)player.posY, (int)player.posZ,
				formatEnum(category), String.format(format, params)));
	}

	private static final String FORMAT_TICK_SPATIAL = "%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%s,%s,%s,%s";
	private static final String FORMAT_TICK_SPATIAL_DEBUG = "MotionX=%.2f, MotionY=%.2f, MotionZ=%.2f, RotationPitch=%.2f, RotationYaw=%.2f, RotationYawHead=%.2f, IsSneaking=%s, IsSprinting=%s, OnGround=%s, InWater=%s";
	private static final String FORMAT_TICK_STATUS = "%.2f,%d,%d,%d,%d,%s,%s,%s";
	private static final String FORMAT_TICK_STATUS_DEBUG = "Health=%.2f, Armor=%d, Air=%d, ExperienceTotal=%d, ExperienceLevel=%d, Burning=%s, Blocking=%s, Wet=%s";
	
	@SubscribeEvent
	public synchronized void onPlayerTick(final TickEvent.PlayerTickEvent tick) {
		if (tick.phase == Phase.END) {
			if (tick.player.isEntityAlive()) {
				final EntityPlayer player = tick.player;
				final PlayerState playerState = getPlayerState(player);
				if (playerState.ticksSpatial++ == tickIntervalSpatial) {
					playerState.ticksSpatial = 0;
					log(player, Category.Spatial, debug ? FORMAT_TICK_SPATIAL_DEBUG : FORMAT_TICK_SPATIAL,
							player.motionX, player.motionY, player.motionZ,
							player.rotationPitch, player.rotationYaw, player.rotationYawHead,
							formatBoolean(player.isSneaking()), formatBoolean(player.isSprinting()),
							formatBoolean(player.onGround), formatBoolean(player.isInWater()));
				}
				if (playerState.ticksStatus++ == tickIntervalStatus) {
					playerState.ticksStatus = 0;
					log(player, Category.Status, debug ? FORMAT_TICK_STATUS_DEBUG : FORMAT_TICK_STATUS,
							player.getHealth(), player.getTotalArmorValue(), player.getAir(),
							player.experienceTotal, player.experienceLevel,
							formatBoolean(player.isBurning()), formatBoolean(player.isBlocking()), formatBoolean(player.isWet()));
				}
			}
		}
	}

	private static final String FORMAT_INTERACT = "%s,%d,%d,%d,%d,%s,%s,%d,%s";
	private static final String FORMAT_INTERACT_DEBUG = "Action=%s, X=%d, Y=%d, Z=%d, Face=%d, Result=%s, Block=%s, Metadata=%d, Item=%s";
	
	@SubscribeEvent
	public synchronized void onPlayerInteract(final PlayerInteractEvent event) {
		if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
			log(event.entityPlayer, Category.Interact, debug ? FORMAT_INTERACT_DEBUG : FORMAT_INTERACT,
					formatEnum(event.action), event.x, event.y, event.z,
					event.face, formatEnum(event.getResult()),
					formatBlock(event.world.getBlock(event.x, event.y, event.z)),
					event.world.getBlockMetadata(event.x, event.y, event.z),
					formatItemStack(event.entityPlayer.getCurrentEquippedItem()));
	}
	
	private static final String FORMAT_USE_ITEM = "%s,%d,%d";
	private static final String FORMAT_USE_ITEM_DEBUG = "Item=%s, Damage=%d, Duration=%d";
	
	@SubscribeEvent
	public synchronized void onPlayerUseItem(final PlayerUseItemEvent.Start event) {
		log(event.entityPlayer, Category.UseItemStart, debug ? FORMAT_USE_ITEM_DEBUG : FORMAT_USE_ITEM,
				formatItemStack(event.item), event.item.getItemDamage(), event.duration);
	}
	
	private static final String FORMAT_ATTACK_ENTITY = "%s,%s,%d,%d,%d";
	private static final String FORMAT_ATTACK_ENTITY_DEBUG = "Item=%s, Target=%s, TargetX=%d, TargetY=%d, TargetZ=%d";
	
	@SubscribeEvent
	public synchronized void onAttackEntity(final AttackEntityEvent event) {
		log(event.entityPlayer, Category.AttackEntity, debug ? FORMAT_ATTACK_ENTITY_DEBUG : FORMAT_ATTACK_ENTITY,
				formatItemStack(event.entityPlayer.getCurrentEquippedItem()), formatEntity(event.target),
				(int)event.target.posX, (int)event.target.posY, (int)event.target.posZ);
	}
	
	private static final String FORMAT_BREAK_BLOCK = "%s,%d,%d,%d,%s,%d,%d";
	private static final String FORMAT_BREAK_BLOCK_DEBUG = "Item=%s, X=%d, Y=%d, Z=%d, Block=%s, Metadata=%d, ExpToDrop=%d";
	
	@SubscribeEvent
	public synchronized void onBlockBreakEvent(final BreakEvent event) {
		log(event.getPlayer(), Category.BreakBlock, debug ? FORMAT_BREAK_BLOCK_DEBUG : FORMAT_BREAK_BLOCK,
				formatItemStack(event.getPlayer().getCurrentEquippedItem()),
				event.x, event.y, event.z,
				formatBlock(event.block), event.blockMetadata, event.getExpToDrop());
	}
	
	private static final String FORMAT_SERVER_CHAT = "%s";
	private static final String FORMAT_SERVER_CHAT_DEBUG = "Message=%s";
	
	@SubscribeEvent
	public synchronized void onServerChatEvent(final ServerChatEvent event) {
		log(event.player, Category.ServerChat, debug ? FORMAT_SERVER_CHAT_DEBUG : FORMAT_SERVER_CHAT, event.message);
	}
	
	//TODO http://www.minecraftforge.net/wiki/Event_Reference
	//TODO ItemTossEvent
	//TODO LivingAttackEvent
	//TODO LivingDeathEvent
	//TODO LivingFallEvent
	//TODO LivingHurtEvent
	//TODO LivingJumpEvent
	//TODO AchievementEvent
	//TODO EntityInteractEvent
	//TODO EntityItemPickupEvent
	//TODO PlayerDropsEvent
	//TODO PlayerSleepInBedEvent
	
	public static void main(final String...args) throws Exception {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		final File analyticsFile = new File(args[0]);
		final String analyticsFileDate = new Timestamp(analyticsFile.lastModified()).toString().substring(0, 10);
		final BufferedReader br = new BufferedReader(new FileReader(analyticsFile));
		String line;
		while ((line = br.readLine()) != null) {
			final String[] lineSegments = line.replaceAll("\\[", "").split("\\]");
			int lineSegment = 0;
			final Date date = dateFormat.parse(analyticsFileDate + " " + lineSegments[lineSegment++]);
			final String user = lineSegments[lineSegment++];
			final String[] position = lineSegments[lineSegment++].split(",");
			final int posX = Integer.parseInt(position[0]);
			final int posY = Integer.parseInt(position[1]);
			final int posZ = Integer.parseInt(position[2]);
			final Category category = Category.values()[Integer.parseInt(lineSegments[lineSegment++])];
			final String[] data = lineSegments[lineSegment++].split(",");
			//System.out.printf("Date=%s, User=%s, PosX=%d, PosY=%d, PosZ=%d, Category=%s, Data=%d\n", date.toString(), user, posX, posY, posZ, category.toString(), data.length);
			if (category == Category.Spatial) {
				final double motionX = Double.parseDouble(data[0]);
				final double motionY = Double.parseDouble(data[1]);
				final double motionZ = Double.parseDouble(data[2]);
				System.out.printf("{%d,%d,%d},\n", posX, posY, posZ);
				//System.out.printf("{{%d,%d,%d},{%.2f,%.2f,%.2f}},\n", posX, posY, posZ, motionX, motionY, motionZ);
			}
		}
		br.close();
	}
}
