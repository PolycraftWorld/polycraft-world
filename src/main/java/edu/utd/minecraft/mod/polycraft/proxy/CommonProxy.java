package edu.utd.minecraft.mod.polycraft.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Map;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeGenerator;
import edu.utd.minecraft.mod.polycraft.handler.GuiHandler;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.util.DynamicValue;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeInitializer;
import edu.utd.minecraft.mod.polycraft.worldgen.OilPopulate;
import edu.utd.minecraft.mod.polycraft.worldgen.OreWorldGenerator;

public abstract class CommonProxy {

	private static final String jetPackSoundName = PolycraftMod.getAssetName("jetpack.fly");
	private static final long jetPackSoundIntervalMillis = 10;
	private static final String netChannelName = PolycraftMod.MODID;
	private static final int netMessageTypeIsFlying = 0;

	private FMLEventChannel netChannel;

	public void preInit() {
		// TODO: Only enable on debug mode
		DynamicValue.start();
		PolycraftRegistry.registerFromResources();
	}

	public void init() {
		netChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(netChannelName);
		netChannel.register(this);
		RecipeGenerator.generateRecipes();
		GameRegistry.registerWorldGenerator(new OreWorldGenerator(), PolycraftMod.oreWorldGeneratorWeight);
		NetworkRegistry.INSTANCE.registerGuiHandler(PolycraftMod.instance, new GuiHandler());
	}

	public void postInit() {
		MinecraftForge.TERRAIN_GEN_BUS.register(new BiomeInitializer());
		MinecraftForge.EVENT_BUS.register(OilPopulate.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	protected void sendMessageToServerIsFlying(final boolean isFlying) {
		sendMessageToServer(netMessageTypeIsFlying, isFlying ? 1 : 0);
	}

	private void sendMessageToServer(final int type, final int value) {
		netChannel.sendToServer(new FMLProxyPacket(Unpooled.buffer().writeInt(type).writeInt(value).copy(), netChannelName));
	}

	@SubscribeEvent
	public synchronized void onServerPacket(final ServerCustomPacketEvent event) {
		final PlayerState playerState = getPlayerState(((NetHandlerPlayServer) event.handler).playerEntity);
		final ByteBuf payload = event.packet.payload();
		switch (payload.readInt()) {
		case netMessageTypeIsFlying:
			playerState.isFlying = (payload.readInt() == 1);
			break;
		default:
			break;
		}
	}

	protected static final Random random = new Random();

	private class PlayerState {
		private boolean isFlying = false;
		private long jetPackLastSoundTicks = 0;
		public boolean jetPackIsFlying = false;
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

	protected static boolean isPlayerEntity(final Entity entity) {
		return entity instanceof EntityPlayer;
	}

	protected static boolean isEntityOnClient(final Entity entity) {
		return entity.worldObj.isRemote;
	}

	protected static boolean isEntityOnServer(final Entity entity) {
		return !isEntityOnClient(entity);
	}

	protected static void setPlayerVelocityForward(final EntityPlayer player, final float velocity) {
		final double playerRotationRadians = Math.toRadians(player.rotationYaw + 90);
		player.motionX = velocity * Math.cos(playerRotationRadians);
		player.motionZ = velocity * Math.sin(playerRotationRadians);
	}

	@SubscribeEvent
	public synchronized void onEntityLivingDeath(final LivingDeathEvent event) {
		if (isPlayerEntity(event.entity))
			playerStates.remove(event.entity);
	}

	@SubscribeEvent
	public synchronized void onPlayerFlyableFallEvent(final PlayerFlyableFallEvent event) {
		if (isEntityOnServer(event.entity)) {
			if (!ItemParachute.isEquipped(event.entityPlayer)) {
				if (ItemJetPack.isEquipped(event.entityPlayer)) {
					final float fallDamage = event.distance - 3.0f;
					if (fallDamage > 0)
						event.entityPlayer.attackEntityFrom(DamageSource.fall, fallDamage);
				}
			}
		}
	}

	@SubscribeEvent
	public synchronized void onLivingFallEvent(final LivingFallEvent event) {
		if (isEntityOnServer(event.entity) && isPlayerEntity(event.entity)) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			if (ItemParachute.isEquipped(player)) {
				event.distance = 0;
			}
			else if (ItemPogoStick.isEquipped(player)) {
				if (event.distance > ItemPogoStick.getEquippedItem(player).config.maxFallNoDamageHeight)
					event.distance *= PolycraftMod.itemPogoStickMaxFallExcedeDamageReduction;
				else
					event.distance = 0;
				ItemPogoStick.damage(player, random);
			}
		}
	}

	@SubscribeEvent
	public synchronized void onPlayerTick(final TickEvent.PlayerTickEvent tick) {
		if (tick.phase == Phase.END && tick.side == Side.SERVER) {
			if (tick.player.isEntityAlive()) {
				final PlayerState playerState = getPlayerState(tick.player);
				onPlayerTickServerJetPack(tick.player, playerState);
				onPlayerTickServerRunningShoes(tick.player);
				onPlayerTickServerScubaFins(tick.player);
			}
		}
	}

	private void onPlayerTickServerJetPack(final EntityPlayer player, final PlayerState playerState) {
		final boolean jetPackIsFlying = ItemJetPack.allowsFlying(player) && !player.onGround && playerState.isFlying;
		if (jetPackIsFlying) {
			if (playerState.jetPackLastSoundTicks++ > jetPackSoundIntervalMillis) {
				playerState.jetPackLastSoundTicks = 0;
				player.worldObj.playSoundAtEntity(player, jetPackSoundName, 1f, 1f);
			}
			ItemJetPack.burnFuel(player);
			ItemJetPack.dealExhaustDamage(player, player.worldObj);
		}
		else
			playerState.jetPackLastSoundTicks = 0;

		if (playerState.jetPackIsFlying != jetPackIsFlying) {
			playerState.jetPackIsFlying = jetPackIsFlying;
			ItemJetPack.setIgnited(player, jetPackIsFlying);
		}
	}

	private void onPlayerTickServerRunningShoes(final EntityPlayer player) {
		if (ItemRunningShoes.allowsRunning(player))
			ItemRunningShoes.damageIfMovingOnGround(player, random);
	}

	private void onPlayerTickServerScubaFins(final EntityPlayer player) {
		if (ItemScubaFins.allowsFastSwimming(player))
			ItemScubaFins.damageIfMoving(player, random);
	}
}
