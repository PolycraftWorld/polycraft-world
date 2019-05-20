package edu.utd.minecraft.mod.polycraft.handler;

import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;

/**
 * Class holding code for adding loaded entities to reloaded chunks.
 * 
 * @author Chris
 */
public class ResyncHandler {

	public static final ResyncHandler INSTANCE = new ResyncHandler(); // Singleton.
	private final Minecraft client;
	private boolean resync = false; // True if the client player has just respawned.

	private ResyncHandler() {
		client = FMLClientHandler.instance().getClient(); // Get client from FML.
	}

	/**
	 * Add loaded entities back to chunks if needed since respawning forces a
	 * complete chunk reload.
	 */
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		World zaWarudo = client.theWorld;
		if (event.phase == Phase.END && resync && zaWarudo != null) {
			List worldEntities = zaWarudo.loadedEntityList;
			boolean resyncNeeded = false;
			for (int i = 0; i < worldEntities.size(); i++) {
				Entity entity = (Entity) worldEntities.get(i);
				// Go through each entity and check the chunk at its position.
				if (entity instanceof EntityLivingBase && !entity.isDead) {
					if (zaWarudo.getChunkProvider().chunkExists(entity.chunkCoordX, entity.chunkCoordZ)) {
						Chunk chunk = zaWarudo.getChunkFromChunkCoords(entity.chunkCoordX, entity.chunkCoordZ);
						if (chunk instanceof EmptyChunk) {
							resyncNeeded = true; // Chunk has not been reloaded yet. Try again next tick.
						} else {
							// Check if chunk already has this entity.
							boolean add = chunk.hasEntities;
							for (int j = 0; add && j < chunk.entityLists.length; j++) {
								List chunkEntities = chunk.entityLists[j];
								for (int k = 0; add && k < chunkEntities.size(); k++)
									if (((Entity) chunkEntities.get(k)) == entity)
										add = false; // Don't add entity if already in chunk.
							}
							if (add) // Add entity to chunk if not already in chunk.
								chunk.addEntity(entity);
						}
					} else // If the chunk does not exist somehow.
						resyncNeeded = true;
				}
			}
			resync = resyncNeeded; // Indicate if we need to perform resync next tick.
		}
	}

	/**
	 * Indicate if we need to perform entity resync on client next tick.
	 */
	public void setResync() {
		resync = true;
	}
}
