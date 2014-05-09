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
import edu.utd.minecraft.mod.polycraft.item.ItemPhaseShifter;
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.util.DynamicValue;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeInitializer;
import edu.utd.minecraft.mod.polycraft.worldgen.OilPopulate;
import edu.utd.minecraft.mod.polycraft.worldgen.OreWorldGenerator;

public abstract class CommonProxy {

	protected static final float baseJumpMovementFactor = 0.02F;
	protected static final float baseWalkSpeed = 0.1f;
	protected static final float baseSwimSpeed = 0.05f;
	protected static final float baseFlySpeed = 0.05f;
	protected static final int baseFullAir = 300;
	private static final String jetPackSoundName = PolycraftMod.getAssetName("jetpack.fly");
	private static final long jetPackSoundFrequencyTicks = 10;
	private static final String scubaBreatheSoundName = PolycraftMod.getAssetName("scubatank.breathe");
	private static final long scubaBreatheSoundFrequencyTicks = 30;
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
		private ItemPhaseShifter phaseShifterEquipped = null;
		private long scubaBreatheLastSoundTicks = 0;
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
				onPlayerTickServerScubaTank(tick.player, playerState);
				onPlayerTickServerPhaseShifter(tick.player, playerState);
			}
		}
	}

	private void onPlayerTickServerJetPack(final EntityPlayer player, final PlayerState playerState) {
		final boolean jetPackIsFlying = ItemJetPack.allowsFlying(player) && !player.onGround && playerState.isFlying;
		if (jetPackIsFlying) {
			if (playerState.jetPackLastSoundTicks++ > jetPackSoundFrequencyTicks) {
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

	private void onPlayerTickServerScubaTank(final EntityPlayer player, final PlayerState playerState) {
		if (ItemScubaTank.allowsWaterBreathing(player) && player.getAir() < baseFullAir) {
			ItemScubaTank.consumeAir(player);
			player.setAir(baseFullAir);
			if (playerState.scubaBreatheLastSoundTicks++ > this.scubaBreatheSoundFrequencyTicks) {
				player.worldObj.playSoundAtEntity(player, scubaBreatheSoundName, 1f, 1f);
				playerState.scubaBreatheLastSoundTicks = 0;
			}
		}
	}

	private void onPlayerTickServerPhaseShifter(final EntityPlayer player, final PlayerState playerState) {
		final boolean phaseShifterEnabled = ItemPhaseShifter.isEquipped(player);
		if ((playerState.phaseShifterEquipped != null) != phaseShifterEnabled) {
			player.noClip = phaseShifterEnabled;
			player.capabilities.disableDamage = phaseShifterEnabled;
			player.setInvisible(phaseShifterEnabled);
			if (!phaseShifterEnabled)
				ItemPhaseShifter.createBoundary(playerState.phaseShifterEquipped, player, player.worldObj);
			playerState.phaseShifterEquipped = phaseShifterEnabled ? ItemPhaseShifter.getEquippedItem(player) : null;
		}
	}
}
