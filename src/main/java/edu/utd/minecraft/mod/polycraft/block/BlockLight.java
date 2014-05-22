package edu.utd.minecraft.mod.polycraft.block;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.vecmath.Point3i;

import net.minecraft.block.BlockAir;
import net.minecraft.world.World;

import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class BlockLight extends BlockAir {

	private static class WorldState {
		private final World world;
		private final Map<Point3i, Integer> votes = Maps.newHashMap();
		private final Map<Point3i, Boolean> pendingUpdates = Maps.newLinkedHashMap();

		private WorldState(final World world) {
			this.world = world;
		}
	}

	private static final Map<World, WorldState> stateByWorld = Maps.newHashMap();
	private static boolean worldStatesDirty = false;

	public static void vote(final World world, final int x, final int y, final int z, final boolean enabled) {
		synchronized (stateByWorld) {
			WorldState worldState = stateByWorld.get(world);
			if (worldState == null)
				stateByWorld.put(world, worldState = new WorldState(world));
			final Point3i key = new Point3i(x, y, z);
			final Integer value = worldState.votes.get(key);
			if (value == null) {
				if (!enabled)
					throw new Error("Too many disabled votes for: " + key);
				worldState.votes.put(key, 1);
				worldState.pendingUpdates.put(key, true);
			}
			else {
				if (enabled)
					worldState.votes.put(key, value + 1);
				else {
					if (value > 1)
						worldState.votes.put(key, value - 1);
					else {
						worldState.votes.remove(key);
						worldState.pendingUpdates.put(key, false);
					}
				}
			}
			worldStatesDirty = true;
		}
	}

	private static final int pendingUpdatesToProcessPerCall = 10;

	public static void processPendingUpdates() {
		synchronized (stateByWorld) {
			if (worldStatesDirty) {
				for (final WorldState worldState : stateByWorld.values()) {
					if (worldState.pendingUpdates.size() > 0) {
						final Iterator<Entry<Point3i, Boolean>> pendingUpdatesIterator = worldState.pendingUpdates.entrySet().iterator();
						for (int i = 0; i < pendingUpdatesToProcessPerCall && pendingUpdatesIterator.hasNext(); i++) {
							final Entry<Point3i, Boolean> entry = pendingUpdatesIterator.next();
							final Point3i point = entry.getKey();
							if (entry.getValue()) {
								if (worldState.world.isAirBlock(point.x, point.y, point.z))
									worldState.world.setBlock(point.x, point.y, point.z, PolycraftMod.blockLight);
							}
							else {
								if (worldState.world.getBlock(point.x, point.y, point.z) instanceof BlockLight)
									worldState.world.setBlockToAir(point.x, point.y, point.z);
							}
							pendingUpdatesIterator.remove();
						}
					}
				}
			}
		}
	}

	public BlockLight(final float level) {
		super();
		this.setLightLevel(level);
	}
}
