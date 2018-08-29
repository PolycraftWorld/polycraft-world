package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

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
import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.block.BlockPasswordDoor;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeGenerator;
import edu.utd.minecraft.mod.polycraft.handler.GuiHandler;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemFreezeRay;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemPhaseShifter;
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.item.ItemWaterCannon;
import edu.utd.minecraft.mod.polycraft.minigame.KillWall;
import edu.utd.minecraft.mod.polycraft.trading.InventorySwap;
import edu.utd.minecraft.mod.polycraft.trading.ItemStackSwitch;
import edu.utd.minecraft.mod.polycraft.util.DynamicValue;
import edu.utd.minecraft.mod.polycraft.worldgen.BiomeInitializer;
import edu.utd.minecraft.mod.polycraft.worldgen.ChallengeHouseDim;
import edu.utd.minecraft.mod.polycraft.worldgen.ChallengesGenerator;
import edu.utd.minecraft.mod.polycraft.worldgen.OilPopulate;
import edu.utd.minecraft.mod.polycraft.worldgen.OreWorldGenerator;
import edu.utd.minecraft.mod.polycraft.worldgen.ResearchAssistantLabGenerator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;

public abstract class CommonProxy {

	protected static final float baseJumpMovementFactor = 0.02F;
	protected static final int baseFullAir = 300;
	private static final long jetPackSoundFrequencyTicks = 10;
	private static final String scubaTankSoundName = PolycraftMod.getAssetName("scubatank.breathe");
	private static final long scubaTankSoundFrequencyTicks = 30;
	private static final int flameSoundID = 1009;
	private static final long flameThrowerSoundFrequencyTicks = 10;
	private static final String netChannelName = PolycraftMod.MODID;
	private static final int netMessageTypeJetPackIsFlying = 0; // message
																// number 0
	private static final int netMessageClientWantsToSync = 1; // message number
																// 1
	private static final int netMessageClientFailedDoorPass = 2; // message number
																// 2

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
		// GameRegistry.registerWorldGenerator(new StructureTest(), 1000);
		GameRegistry.registerWorldGenerator(new ResearchAssistantLabGenerator(), 1);
		GameRegistry.registerWorldGenerator(new ChallengesGenerator(), 1);
		NetworkRegistry.INSTANCE.registerGuiHandler(PolycraftMod.instance, new GuiHandler());
		
		ChallengeHouseDim.init();
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

	protected void sendMessageToServerClientWantsToSync(final boolean clientWantsToSync) {
		sendMessageToServer(netMessageClientWantsToSync, clientWantsToSync ? 1 : 0);
	}
	
	public void sendMessageToServerClientFailedDoorPass(final boolean ClientFailedPass) {
		sendMessageToServer(netMessageClientFailedDoorPass, ClientFailedPass ? 1 : 0);
	}

	private void sendMessageToServer(final int type, final int value) {
		netChannel.sendToServer(
				new FMLProxyPacket(Unpooled.buffer().writeInt(type).writeInt(value).copy(), netChannelName));
	}

	@SubscribeEvent
	public synchronized void onServerPacket(final ServerCustomPacketEvent event) {
		final PlayerState playerState = getPlayerState(((NetHandlerPlayServer) event.handler).playerEntity);
		final ByteBuf payload = event.packet.payload();
		switch (payload.readInt()) {
		case netMessageTypeJetPackIsFlying:
			playerState.jetPackIsFlying = (payload.readInt() == 1);
			break;
		case netMessageClientWantsToSync:
			playerState.choseToSyncInventory = (payload.readInt() == 1);
			break;
		case netMessageClientFailedDoorPass:
			EntityPlayer player = ((NetHandlerPlayServer) event.handler).playerEntity;
			player.worldObj.setBlock((int)player.posX, (int)player.posY, (int)player.posZ, Blocks.lava, 0, 3);
		default:
			break;
		}
	}

	protected static final Random random = new Random();

	private class PlayerState {
		private boolean jetPackIsFlying = false;
		private boolean choseToSyncInventory = false;
		private long jetPackLastSoundTicks = 0;
		private long flameThrowerLastSoundTicks = 0;
		private ItemPhaseShifter phaseShifterEquipped = null;
		private long scubaBreatheLastSoundTicks = 0;
		private long freezeRayFrozenTicks = 0;
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
		return entity.worldObj.getBlock((int) Math.floor(entity.posX),
				(int) Math.floor(entity.posY - entity.getYOffset()) - 1, (int) Math.floor(entity.posZ));
	}

	protected static Block getBlockUnderNorthOfEntity(final Entity entity) {
		return entity.worldObj.getBlock((int) Math.floor(entity.posX),
				(int) Math.floor(entity.posY - entity.getYOffset()) - 1, (int) Math.floor(entity.posZ - 1));
	}

	protected static Block getBlockUnderSouthOfEntity(final Entity entity) {
		return entity.worldObj.getBlock((int) Math.floor(entity.posX),
				(int) Math.floor(entity.posY - entity.getYOffset()) - 1, (int) Math.floor(entity.posZ + 1));
	}

	protected static Block getBlockUnderEastOfEntity(final Entity entity) {
		return entity.worldObj.getBlock((int) Math.floor(entity.posX + 1),
				(int) Math.floor(entity.posY - entity.getYOffset()) - 1, (int) Math.floor(entity.posZ));
	}

	protected static Block getBlockUnderWestOfEntity(final Entity entity) {
		return entity.worldObj.getBlock((int) Math.floor(entity.posX - 1),
				(int) Math.floor(entity.posY - entity.getYOffset()) - 1, (int) Math.floor(entity.posZ));
	}

	protected static boolean isEntityOnBouncyBlock(final Entity entity) {
		if (getBlockUnderEntity(entity) instanceof BlockBouncy)
			return true;
		else if ((getBlockUnderNorthOfEntity(entity) instanceof BlockBouncy)
				|| (getBlockUnderNorthOfEntity(entity) instanceof BlockBed))
			return true;
		else if ((getBlockUnderSouthOfEntity(entity) instanceof BlockBouncy)
				|| (getBlockUnderSouthOfEntity(entity) instanceof BlockBed))
			return true;
		else if ((getBlockUnderEastOfEntity(entity) instanceof BlockBouncy)
				|| (getBlockUnderEastOfEntity(entity) instanceof BlockBed))
			return true;
		else if ((getBlockUnderWestOfEntity(entity) instanceof BlockBouncy)
				|| (getBlockUnderWestOfEntity(entity) instanceof BlockBed))
			return true;
		else
			return false;
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
			} else {
				final boolean entityOnBouncyBlock = isEntityOnBouncyBlock(player);
				if (ItemPogoStick.isEquipped(player)) {
					if (entityOnBouncyBlock
							|| event.distance < ItemPogoStick.getEquippedItem(player).config.maxFallNoDamageHeight)
						event.distance = 0;
					else
						event.distance *= PolycraftMod.itemPogoStickMaxFallExcedeDamageReduction;
					ItemPogoStick.damage(player, random);
				} else if (entityOnBouncyBlock) {
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
				onPlayerTickServerFreezeRay(tick.player, playerState);
				onPlayerTickServerWaterCannon(tick.player, playerState);
				onPlayerTickServerRunningShoes(tick.player);
				onPlayerTickServerScubaFins(tick.player);
				onPlayerTickServerScubaTank(tick.player, playerState);
				onPlayerTickServerPhaseShifter(tick.player, playerState);
				onPlayerTickServerSyncInventory(tick.player, playerState);
				onPlayerTickServerKillWall(tick.player);
				onPlayerTickServerKillWallShrink(tick.player);
				
			}
		}
	}
	
	private void onPlayerTickServerKillWall(final EntityPlayer player) {
		
		if (KillWall.isInKillWall(player))
		{
			
			if(player.ticksExisted%20==0 && !player.worldObj.isRemote)
			{
				((EntityPlayer) player).addChatComponentMessage(new ChatComponentText("Past Kill Wall"));
				player.setHealth(player.getHealth()-2);
			}
		}
	
	}
	
	private void onPlayerTickServerKillWallShrink(final EntityPlayer player) {
		if(!player.worldObj.isRemote)
		{
			KillWall.shrinkKillWall();
		}
	}

	private boolean onPlayerTickServerSyncInventory(final EntityPlayer player, final PlayerState playerState) {
		final boolean clientWantsToSync = playerState.choseToSyncInventory;
		// and make sure they are in PP

		if (clientWantsToSync) {
			InventorySwap is = new InventorySwap(player);

			playerState.choseToSyncInventory = false;

			int stackSize = 0;
			int damage = 0;
			int invSlot;
			int id = 0;
			ItemStack itemstack;
			String enchantments = "";
			NBTTagList enchantmentList;

			// pull the list into memory from the server

			for (invSlot = player.inventory
					.getHotbarSize(); invSlot < player.inventory.mainInventory.length; ++invSlot) {
				itemstack = player.inventory.mainInventory[invSlot];

				is.pushItemToPortal(new ItemStackSwitch(player, itemstack));

			}

			if (is.swapPlayerInventoryWithPortal(player)) {

				// make sure it went through

				Iterator it = is.itemsToPull.iterator();
				for (invSlot = player.inventory
						.getHotbarSize(); invSlot < player.inventory.mainInventory.length; ++invSlot) {
					itemstack = player.inventory.mainInventory[invSlot];

					if (it.hasNext())
						player.inventory.setInventorySlotContents(invSlot, ((ItemStackSwitch) (it.next())).itemStack);
					else
						player.inventory.setInventorySlotContents(invSlot, null);
				}
				return true;
			} else {
				// send message to say that the sync failed
			}

		}
		return false;

	}

	private void onPlayerTickServerJetPack(final EntityPlayer player, final PlayerState playerState) {
		final boolean jetPackIgnited = ItemJetPack.allowsFlying(player) && !player.onGround
				&& playerState.jetPackIsFlying;
		if (jetPackIgnited) {
			if (playerState.jetPackLastSoundTicks++ > jetPackSoundFrequencyTicks) {
				playerState.jetPackLastSoundTicks = 0;
				// TODO this causes performance problems, find a better way to
				// have jet pack sounds
				// player.worldObj.playAuxSFXAtEntity((EntityPlayer)null,
				// flameSoundID, (int)player.posX, (int)player.posY,
				// (int)player.posZ, 0);
			}
			ItemJetPack.burnFuel(player);
			ItemJetPack.dealExhaustDamage(player, player.worldObj);
			player.fallDistance = 0;
		}

		ItemJetPack.setIgnited(player, jetPackIgnited);
	}

	@SubscribeEvent
	public synchronized void onServerTick(final TickEvent.ServerTickEvent tick) {
		if (tick.phase == Phase.END) {
			BlockLight.processPendingUpdates(16);
		}
	}

	private void onPlayerTickServerFlameThrower(final EntityPlayer player, final PlayerState playerState) {
		final boolean flameThrowerActivated = ItemFlameThrower.allowsActivation(player) && player.isUsingItem();
		if (flameThrowerActivated) {
			if (playerState.flameThrowerLastSoundTicks++ > flameThrowerSoundFrequencyTicks) {
				playerState.flameThrowerLastSoundTicks = 0;
				// TODO this causes performance problems, find a better way to
				// have jet pack sounds
				// player.worldObj.playAuxSFXAtEntity((EntityPlayer)null,
				// flameSoundID, (int)player.posX, (int)player.posY,
				// (int)player.posZ, 0);
			}
			ItemFlameThrower.burnFuel(player);
			ItemFlameThrower.getEquippedItem(player).spawnProjectiles(player, player.worldObj, random);
		}
		ItemFlameThrower.setActivated(player, flameThrowerActivated);
	}

	private void onPlayerTickServerFreezeRay(final EntityPlayer player, final PlayerState playerState) {
		final boolean activated = ItemFreezeRay.allowsActivation(player) && player.isUsingItem();
		if (activated) {
			ItemFreezeRay.burnFuel(player);
			ItemFreezeRay.getEquippedItem(player).spawnProjectiles(player, player.worldObj, random);
		}
		ItemFreezeRay.setActivated(player, activated);
	}

	private void onPlayerTickServerWaterCannon(final EntityPlayer player, final PlayerState playerState) {
		final boolean activated = ItemWaterCannon.allowsActivation(player) && player.isUsingItem();
		if (activated) {
			ItemWaterCannon.burnFuel(player);
			ItemWaterCannon.getEquippedItem(player).spawnProjectiles(player, player.worldObj, random);
		}
		ItemWaterCannon.setActivated(player, activated);
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

	public void registerRenderers() {

	}
	
	public void openDoorGui(BlockPasswordDoor block, EntityPlayer player, int x, int y, int z)
	{
		
	}
}
