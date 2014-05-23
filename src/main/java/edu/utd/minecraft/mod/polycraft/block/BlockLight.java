package edu.utd.minecraft.mod.polycraft.block;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.vecmath.Point3i;

import net.minecraft.block.BlockAir;
import net.minecraft.world.World;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class BlockLight extends BlockAir {

	//TODO enable other shapes like directional cones and spheres?
	public static class Source {

		private final World world;
		private final Point3i origin;
		private final int size;
		private final int direction;
		private final Map<Point3i, Boolean> points = Maps.newLinkedHashMap(); //TODO true means lit, false mean occluded

		public Source(final World world, final int originX, final int originY, final int originZ, final int size) {
			this(world, originX, originY, originZ, size, -1);
		}

		public Source(final World world, final int originX, final int originY, final int originZ, final int size, final int direction) {
			this.world = world;
			this.origin = new Point3i(originX, originY, originZ);
			this.size = size;
			this.direction = direction;
			points.put(origin, true);
			//omni-directional
			if (direction == -1) {
				for (int s = 1; s <= size; s++) {
					final int offsetX = origin.x - s;
					final int offsetY = origin.y;// - s;
					final int offsetZ = origin.z - s;
					final int length = (s * 2) + 1;
					final int spacing = length > 3 ? 4 : 2;
					for (int l = 0; l < length; l += spacing) {
						int y = 0;
						//TODO enabled 3rd dimension?
						//for (int y = 0; y < length; y += spacing) {
						points.put(new Point3i(offsetX + l, offsetY + y, offsetZ), true);
						points.put(new Point3i(offsetX + l, offsetY + y, offsetZ + length - 1), true);
						if (l > 0 && l < (length - 1)) {
							points.put(new Point3i(offsetX, offsetY + y, offsetZ + l), true);
							points.put(new Point3i(offsetX + length - 1, offsetY + y, offsetZ + l), true);
						}
						//}
					}
				}
			}
			//directional
			else {
				for (int i = 1; i <= size; i++) {
					switch (direction) {
					case LabelTexture.SIDE_BOTTOM:
						points.put(new Point3i(origin.x, origin.y - i, origin.z), true);
						break;
					case LabelTexture.SIDE_TOP:
						points.put(new Point3i(origin.x, origin.y + i, origin.z), true);
						break;
					case LabelTexture.SIDE_BACK:
						points.put(new Point3i(origin.x, origin.y, origin.z - i), true);
						break;
					case LabelTexture.SIDE_FRONT:
						points.put(new Point3i(origin.x, origin.y, origin.z + i), true);
						break;
					case LabelTexture.SIDE_LEFT:
						points.put(new Point3i(origin.x - i, origin.y, origin.z), true);
						break;
					case LabelTexture.SIDE_RIGHT:
						points.put(new Point3i(origin.x + i, origin.y, origin.z), true);
						break;
					}
				}
			}
		}
	}

	private static class WorldState {
		private final World world;
		private final Set<Source> sources = Sets.newLinkedHashSet();
		private final Map<Point3i, Integer> litPointVotes = Maps.newHashMap();
		private final Map<Point3i, Boolean> pendingUpdates = Maps.newLinkedHashMap();

		private WorldState(final World world) {
			this.world = world;
		}

		private boolean addSource(final Source source) {
			synchronized (pendingUpdates) {
				sources.add(source);
				return updateLights(source, true);
			}
		}

		private boolean removeSource(final Source source) {
			synchronized (pendingUpdates) {
				sources.remove(source);
				return updateLights(source, false);
			}
		}

		public boolean updateLights(final Source source, final boolean enabled) {
			boolean pendingUpdatedChanged = false;
			for (final Entry<Point3i, Boolean> pointEntry : source.points.entrySet()) {
				if (pointEntry.getValue()) { //if this point is not occluded
					final Point3i point = pointEntry.getKey();
					final Integer value = litPointVotes.get(point);
					if (value == null) {
						if (!enabled)
							throw new Error("Too many disabled votes for: " + point);
						litPointVotes.put(point, 1);
						pendingUpdates.put(point, true);
						pendingUpdatedChanged = true;
					}
					else {
						if (enabled)
							litPointVotes.put(point, value + 1);
						else {
							if (value > 1)
								litPointVotes.put(point, value - 1);
							else {
								litPointVotes.remove(point);
								pendingUpdates.put(point, false);
								pendingUpdatedChanged = true;
							}
						}
					}
				}
			}
			return pendingUpdatedChanged;
		}
	}

	private static final Map<World, WorldState> stateByWorld = Maps.newLinkedHashMap();
	private static boolean pendingUpdatesAvailable = false;

	private static WorldState getWorldState(final World world) {
		WorldState worldState = stateByWorld.get(world);
		if (worldState == null)
			stateByWorld.put(world, worldState = new WorldState(world));
		return worldState;
	}

	public static Source addSource(final World world, final Source source) {
		synchronized (stateByWorld) {
			pendingUpdatesAvailable = getWorldState(world).addSource(source);
		}
		return source;
	}

	public static Source removeSource(final World world, final Source source) {
		synchronized (stateByWorld) {
			pendingUpdatesAvailable = getWorldState(world).removeSource(source);
		}
		return source;
	}

	private static final int pendingUpdatesToProcessPerCall = 5;

	public static void processPendingUpdates() {
		synchronized (stateByWorld) {
			if (pendingUpdatesAvailable) {
				pendingUpdatesAvailable = false;
				for (final WorldState worldState : stateByWorld.values()) {
					if (worldState.pendingUpdates.size() > 0) {
						final Iterator<Entry<Point3i, Boolean>> pendingUpdates = worldState.pendingUpdates.entrySet().iterator();
						for (int i = 0; pendingUpdates.hasNext(); i++) {
							if (i < pendingUpdatesToProcessPerCall) {
								final Entry<Point3i, Boolean> entry = pendingUpdates.next();
								final Point3i point = entry.getKey();
								if (entry.getValue()) {
									if (worldState.world.isAirBlock(point.x, point.y, point.z))
										worldState.world.setBlock(point.x, point.y, point.z, PolycraftMod.blockLight);
								}
								else {
									if (worldState.world.getBlock(point.x, point.y, point.z) instanceof BlockLight)
										worldState.world.setBlockToAir(point.x, point.y, point.z);
								}
								pendingUpdates.remove();
							}
							else {
								pendingUpdatesAvailable = true;
								break;
							}
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
