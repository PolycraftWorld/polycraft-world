package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockBouncy;
import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.block.BlockPasswordDoor;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeGenerator;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.handler.GuiHandler;
import edu.utd.minecraft.mod.polycraft.handler.RespawnHandler;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.CannonInventory;
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
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigameManager;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.trading.InventorySwap;
import edu.utd.minecraft.mod.polycraft.trading.ItemStackSwitch;
import edu.utd.minecraft.mod.polycraft.util.DynamicValue;
import edu.utd.minecraft.mod.polycraft.util.FuelHandler;
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
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;

public abstract class CommonProxy {

	protected static final float baseJumpMovementFactor = 0.02F;
	protected static final int baseFullAir = 300;
	private static final long jetPackSoundFrequencyTicks = 10;
	private static final String scubaTankSoundName = PolycraftMod.getAssetNameString("scubatank.breathe");
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
	private static final int netMessageCannon = 3; // message number
	
	private static final int netMessageMinigame = 4; // message number
																// 3

	private FMLEventChannel netChannel;

	public void preInit() {
		// TODO: Only enable on debug mode
		//DynamicValue.start();
		PolycraftRegistry.registerFromResources();
		GameRegistry.registerFuelHandler(FuelHandler.instance);
		RecipeGenerator.generateRecipes();
	}

	public void init() {
		//netChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(netChannelName);
		//netChannel.register(this);
		//GameRegistry.registerWorldGenerator(new OreWorldGenerator(), PolycraftMod.oreWorldGeneratorWeight);
		// GameRegistry.registerWorldGenerator(new StructureTest(), 1000);
		//GameRegistry.registerWorldGenerator(new ResearchAssistantLabGenerator(), 1);
		//GameRegistry.registerWorldGenerator(new ChallengesGenerator(), 1);
		NetworkRegistry.INSTANCE.registerGuiHandler(PolycraftMod.instance, new GuiHandler());
		
//		ChallengeHouseDim.init();
//		PolycraftMinigameManager.init();
	}

	public void postInit() {
		MinecraftForge.TERRAIN_GEN_BUS.register(new BiomeInitializer());
		MinecraftForge.EVENT_BUS.register(OilPopulate.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);
//		FMLCommonHandler.instance().bus().register(this);
//		FMLCommonHandler.instance().bus().register(RespawnHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(RespawnHandler.INSTANCE);
	}
	
	public void sendMessageToServerCannon(final int x ,final int y, final int z, final double velocity, final double theta, final double mass, final double phi) {
		sendMessageToServer(netMessageCannon, x, y, z, velocity, theta, mass, phi);
	}
	
	public void sendMessageToServerMinigame(final int minigameid)
	{
		sendMessageToServer(netMessageMinigame, minigameid);
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
				new FMLProxyPacket(new PacketBuffer(Unpooled.buffer().writeInt(type).writeInt(value).copy()), netChannelName));
	}
	
	private void sendMessageToServer(final int type, final int x ,final int y, final int z, final double velocity, final double theta, final double mass,final double phi) {
		netChannel.sendToServer(
				new FMLProxyPacket(new PacketBuffer(Unpooled.buffer().writeInt(type).writeInt(x).writeInt(y).writeInt(z).writeDouble(velocity).writeDouble(theta).writeDouble(mass).copy().writeDouble(phi)), netChannelName));
	}
	
	

	@SubscribeEvent
	public synchronized void onServerPacket(final FMLNetworkEvent.ServerCustomPacketEvent event) {
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
			player.worldObj.setBlockState(player.getPosition(), Blocks.lava.getDefaultState(),3);
			break;
		case netMessageCannon:
			EntityPlayer player1 = ((NetHandlerPlayServer) event.handler).playerEntity;
			int x=payload.readInt();
			int y=payload.readInt();
			int z=payload.readInt();
			double velocity=payload.readDouble();
			double theta=payload.readDouble();
			double mass=payload.readDouble();
			double phi= payload.readDouble();
			CannonInventory cannon = (CannonInventory) player1.worldObj.getTileEntity(new BlockPos(x, y, z));
			cannon.velocity=velocity;
			cannon.theta=theta;
			cannon.mass=mass;
			cannon.phi=phi;
			cannon.shouldRenderInPass(0);
			break;
		case netMessageMinigame:
			
			break;
		default:
			break;
		}
	}
	
	//Client sided function
	public void freeze(EntityPlayer player, boolean flag, int freezeType) {
		
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
		return entity.worldObj.getBlockState(entity.getPosition().down(1)).getBlock();
	}

	protected static Block getBlockUnderNorthOfEntity(final Entity entity) {
		return entity.worldObj.getBlockState(entity.getPosition().north(1)).getBlock();
	}

	protected static Block getBlockUnderSouthOfEntity(final Entity entity) {
		return entity.worldObj.getBlockState(entity.getPosition().south(1)).getBlock();
	}

	protected static Block getBlockUnderEastOfEntity(final Entity entity) {
		return entity.worldObj.getBlockState(entity.getPosition().east(1)).getBlock();
	}

	protected static Block getBlockUnderWestOfEntity(final Entity entity) {
		return entity.worldObj.getBlockState(entity.getPosition().west(1)).getBlock();
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
				//onPlayerTickServerPhaseShifter(tick.player, playerState);
				
				//Experiment Servers DO NOT allow for players to sync their inventories
				if(System.getProperty("isExperimentServer") != null) {
					ExperimentManager.INSTANCE.onPlayerTick(tick);
					tick.player.worldObj.getGameRules().setOrCreateGameRule("doDaylightCycle", "False");	
				}else {
				onPlayerTickServerSyncInventory(tick.player, playerState);
				}
				
			}
		}
		if(PolycraftMinigameManager.INSTANCE!=null && PolycraftMinigameManager.INSTANCE.shouldUpdatePackets())
		{
			PolycraftMinigameManager.INSTANCE.onPlayerTick(tick);
		}
		
		
		//KillWall.onPlayerTick(tick);
		//RaceGame.INSTANCE.onPlayerTick(tick);
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
		if (tick.phase == TickEvent.Phase.END) {
			if(System.getProperty("isExperimentServer") != null) {
				ExperimentManager.INSTANCE.onServerTickUpdate(tick);
				ServerScoreboard.INSTANCE.onServerTick(tick);
			}
			BlockLight.processPendingUpdates(16);
			if(PolycraftMinigameManager.INSTANCE!=null && PolycraftMinigameManager.INSTANCE.shouldUpdatePackets())
			{
				PolycraftMinigameManager.INSTANCE.onServerTick(tick);
			}
			
			TutorialManager.INSTANCE.onServerTickUpdate(tick);
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
	
	public void openDoorGui(BlockPasswordDoor block, EntityPlayer player, BlockPos blockPos)
	{
		
	}
	
	public void openConsentGui(EntityPlayer player, int x, int y, int z)
	{
		
	}
	
	public void openDevToolGui(EntityPlayer player) {
		
	}
	
	public void openAIToolGui(EntityPlayer player) {
		
	}
	
	public void openTutorialGui(EntityPlayer player) {
	
	}

	public void openHalftimeGui(EntityPlayer player) {
				
	}
	public void closeHalftimeGui(EntityPlayer player) {
		
	}
	public void toggleTutorialRender() {
		// TODO Auto-generated method stub
	}

	public void openExperimentManagerGui(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

}
