package edu.utd.minecraft.mod.polycraft.handler;

import java.util.List;

import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * Server side code to notify clients that entities need to be resynced.
 * 
 * @author Chris
 */
public class RespawnHandler {

	public static final RespawnHandler INSTANCE = new RespawnHandler(); // Singleton.

	private RespawnHandler() {
	}

	/**
	 * Triggered server side, send player client a notification that they need to
	 * possibly transfer nearby entities to new loaded chunks.
	 */
	@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		EntityPlayer player = event.player;
		World world = player.worldObj;
		if (world.isRemote) // Just in case this is triggered on a client thread.
			return;
		// Need to resync all nearby mobs to client, so send player resync packet.
		ServerEnforcer.INSTANCE.sendRespawnSync((EntityPlayerMP) player);
	}
}
