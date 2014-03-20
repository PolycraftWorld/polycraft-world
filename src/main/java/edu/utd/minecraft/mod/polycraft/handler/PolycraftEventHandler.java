package edu.utd.minecraft.mod.polycraft.handler;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;

public class PolycraftEventHandler {

	private final Random random = new Random();
	private final float baseWalkSpeed = 0.1f;
	private final float baseJumpMovementFactor = 0.02F;
	private final float baseFlySpeed = 0.05f;
	private boolean previousAllowRunning = false;
	private boolean previousIsFlying = false;
	private int ticksSinceLastJetPackDamage = 0;
	private int ticksSinceLastJetPackNoise = 0;

	@SubscribeEvent
	public void onLivingUpdateEvent(final LivingUpdateEvent event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			final EntityPlayer player = (EntityPlayer) event.entity;

			boolean allowRunning = false;
			final ItemStack boots = player.getCurrentArmor(0);
			if (boots != null && boots.getItem() instanceof ItemRunningShoes)
				allowRunning = boots.getItemDamage() < boots.getMaxDamage();
			if (allowRunning != previousAllowRunning)
			{
				if (allowRunning)
				{
					final float walkSpeedBuff = ((ItemRunningShoes) boots.getItem()).getWalkSpeedBuff();
					player.capabilities.setPlayerWalkSpeed(baseWalkSpeed * (1 + walkSpeedBuff));
					player.jumpMovementFactor = baseJumpMovementFactor * (1 + walkSpeedBuff);
				}
				else
				{
					player.capabilities.setPlayerWalkSpeed(baseWalkSpeed);
					player.jumpMovementFactor = baseJumpMovementFactor;
				}
				previousAllowRunning = allowRunning;
			}

			boolean allowFlying = false;
			final ItemStack chestplate = player.getCurrentArmor(2);
			if (chestplate != null && chestplate.getItem() instanceof ItemJetPack)
				allowFlying = chestplate.getItemDamage() < chestplate.getMaxDamage();

			if (player.capabilities.allowFlying != allowFlying)
			{
				if (allowFlying)
					player.capabilities.setFlySpeed(baseFlySpeed * (1 + ((ItemJetPack) chestplate.getItem()).getFlySpeedBuff()));
				else
				{
					player.capabilities.setFlySpeed(baseFlySpeed);
					player.capabilities.isFlying = false; // TODO doesn't hurt player if falling from max item damage (does if the player un-equips the item...)
				}
				ticksSinceLastJetPackDamage = 0;
				ticksSinceLastJetPackNoise = 0;
				player.capabilities.allowFlying = allowFlying;
			}

			if (player.capabilities.isFlying)
			{
				ticksSinceLastJetPackDamage++;
				if (ticksSinceLastJetPackDamage % 20 == 0)
				{
					ticksSinceLastJetPackDamage = 0;
					chestplate.attemptDamageItem(1, random);
				}

				event.entity.worldObj.spawnParticle("hugeexplosion", player.posX, player.posY - 10, player.posZ, -player.motionX, -player.motionY, -player.motionZ);

				// TODO these noises don't play continuously that well
				ticksSinceLastJetPackNoise++;
				if (ticksSinceLastJetPackNoise % 200 == 0)
				{
					ticksSinceLastJetPackNoise = 0;
					event.entity.worldObj.playSoundAtEntity(player, PolycraftMod.MODID + ":jetpack.fly", 1f, 1f);
				}
				if (!previousIsFlying)
					event.entity.worldObj.playSoundAtEntity(player, PolycraftMod.MODID + ":jetpack.fly", 1f, 1f);
			}
			previousIsFlying = player.capabilities.isFlying;
		}
	}
}
