package edu.utd.minecraft.mod.polycraft.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
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
import edu.utd.minecraft.mod.polycraft.block.BlockBouncy;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeGenerator;
import edu.utd.minecraft.mod.polycraft.handler.GuiHandler;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
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
	private static final String scubaTankSoundName = PolycraftMod.getAssetName("scubatank.breathe");
	private static final long scubaTankSoundFrequencyTicks = 30;
	private static final String flameThrowerSoundName = PolycraftMod.getAssetName("flamethrower.ignite");
	private static final long flameThrowerSoundFrequencyTicks = 10;
	private static final String netChannelName = PolycraftMod.MODID;
	private static final int netMessageTypeJetPackIsFlying = 0;

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

	protected void sendMessageToServerJetPackIsFlying(final boolean jetPackIsFlying) {
		sendMessageToServer(netMessageTypeJetPackIsFlying, jetPackIsFlying ? 1 : 0);
	}

	private void sendMessageToServer(final int type, final int value) {
		netChannel.sendToServer(new FMLProxyPacket(Unpooled.buffer().writeInt(type).writeInt(value).copy(), netChannelName));
	}

	@SubscribeEvent
	public synchronized void onServerPacket(final ServerCustomPacketEvent event) {
		final PlayerState playerState = getPlayerState(((NetHandlerPlayServer) event.handler).playerEntity);
		final ByteBuf payload = event.packet.payload();
		switch (payload.readInt()) {
		case netMessageTypeJetPackIsFlying:
			playerState.jetPackIsFlying = (payload.readInt() == 1);
			break;
		default:
			break;
		}
	}

	protected static final Random random = new Random();

	private class PlayerState {
		private boolean jetPackIsFlying = false;
		private long jetPackLastSoundTicks = 0;
		private long flameThrowerLastSoundTicks = 0;
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

	protected static Block getBlockUnderEntity(final Entity entity) {
		return entity.worldObj.getBlock((int) Math.floor(entity.posX), (int) Math.floor(entity.posY - entity.getYOffset()) - 1, (int) Math.floor(entity.posZ));
	}

	protected static boolean isEntityOnBouncyBlock(final Entity entity) {
		return getBlockUnderEntity(entity) instanceof BlockBouncy;
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
			else {
				final boolean entityOnBouncyBlock = isEntityOnBouncyBlock(player);
				if (ItemPogoStick.isEquipped(player)) {
					if (entityOnBouncyBlock || event.distance < ItemPogoStick.getEquippedItem(player).config.maxFallNoDamageHeight)
						event.distance = 0;
					else
						event.distance *= PolycraftMod.itemPogoStickMaxFallExcedeDamageReduction;
					ItemPogoStick.damage(player, random);
				}
				else if (entityOnBouncyBlock) {
					event.distance = 0;
				}
			}
		}
	}

	@SubscribeEvent
	public synchronized void onPlayerTick(final TickEvent.PlayerTickEvent tick) {
		if (tick.phase == Phase.END && tick.side == Side.SERVER) {
			if (tick.player.isEntityAlive()) {
				final PlayerState playerState = getPlayerState(tick.player);
				onPlayerTickServerJetPack(tick.player, playerState);
				onPlayerTickServerFlameThrower(tick.player, playerState);
				onPlayerTickServerRunningShoes(tick.player);
				onPlayerTickServerScubaFins(tick.player);
				onPlayerTickServerScubaTank(tick.player, playerState);
				onPlayerTickServerPhaseShifter(tick.player, playerState);
			}
		}
	}

	private void onPlayerTickServerJetPack(final EntityPlayer player, final PlayerState playerState) {
		final boolean jetPackIgnited = ItemJetPack.allowsFlying(player) && !player.onGround && playerState.jetPackIsFlying;
		if (jetPackIgnited) {
			if (playerState.jetPackLastSoundTicks++ > jetPackSoundFrequencyTicks) {
				playerState.jetPackLastSoundTicks = 0;
				player.worldObj.playSoundAtEntity(player, jetPackSoundName, 1f, 1f);
			}
			ItemJetPack.burnFuel(player);
			ItemJetPack.dealExhaustDamage(player, player.worldObj);
			player.fallDistance = 0;
		}

		ItemJetPack.setIgnited(player, jetPackIgnited);
	}

	private void onPlayerTickServerFlameThrower(final EntityPlayer player, final PlayerState playerState) {
		final boolean flameThrowerIgnited = ItemFlameThrower.allowsFiring(player) && player.isUsingItem();
		if (flameThrowerIgnited) {
			if (playerState.flameThrowerLastSoundTicks++ > flameThrowerSoundFrequencyTicks) {
				playerState.flameThrowerLastSoundTicks = 0;
				player.worldObj.playSoundAtEntity(player, flameThrowerSoundName, 1f, 1f);
			}
			ItemFlameThrower.burnFuel(player);
			ItemFlameThrower.dealFlameDamage(player, player.worldObj);
		}
		ItemFlameThrower.setIgnited(player, flameThrowerIgnited);
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
			if (playerState.scubaBreatheLastSoundTicks++ > this.scubaTankSoundFrequencyTicks) {
				player.worldObj.playSoundAtEntity(player, scubaTankSoundName, 1f, 1f);
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
			playerState.phaseShifterEquipped = phaseShifterEnabled ? ItemPhaseShifter.getEquippedItem(player) : null;
		}
		if (phaseShifterEnabled) {
			if (player.isUsingItem())
				ItemPhaseShifter.createBoundary(playerState.phaseShifterEquipped, player, player.worldObj);
			player.fallDistance = 0;
		}
	}
}
