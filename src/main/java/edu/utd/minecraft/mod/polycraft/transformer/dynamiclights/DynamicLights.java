package edu.utd.minecraft.mod.polycraft.transformer.dynamiclights;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.block.Block;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Adapted from AtomicStryker's dynamic lights
 */
public class DynamicLights
{
	private static DynamicLights instance = new DynamicLights();

	private IBlockAccess lastWorld;
	private ConcurrentLinkedQueue<DynamicLightSourceContainer> lastList;

	/**
	 * This Map contains a List of DynamicLightSourceContainer for each World. Since the client can only be in a single World, the other Lists just float idle when unused.
	 */
	public ConcurrentHashMap<World, ConcurrentLinkedQueue<DynamicLightSourceContainer>> worldLightsMap;

	public DynamicLights() {
		worldLightsMap = new ConcurrentHashMap<World, ConcurrentLinkedQueue<DynamicLightSourceContainer>>();
	}

	/**
	 * Exposed method which is called by the transformed World.computeBlockLightValue method instead of Block.blocksList[blockID].getLightValue. Loops active Dynamic Light Sources and if it finds one for the exact coordinates asked, returns
	 * the Light value from that source if higher.
	 * 
	 * @param world
	 *            World queried
	 * @param block
	 *            Block instance of target coords
	 * @param x
	 *            coordinate queried
	 * @param y
	 *            coordinate queried
	 * @param z
	 *            coordinate queried
	 * @return Block.blocksList[blockID].getLightValue or Dynamic Light value, whichever is higher
	 */
	public static int getLightValue(IBlockAccess world, Block block, int x, int y, int z)
	{
		int vanillaValue = block.getLightValue(world, x, y, z);

		if (instance == null || world instanceof WorldServer)
		{
			return vanillaValue;
		}

		if (!world.equals(instance.lastWorld) || instance.lastList == null)
		{
			instance.lastWorld = world;
			instance.lastList = instance.worldLightsMap.get(world);
		}

		int dynamicValue = 0;
		if (instance.lastList != null && !instance.lastList.isEmpty())
		{
			for (DynamicLightSourceContainer light : instance.lastList)
			{
				if (light.getX() == x)
				{
					if (light.getY() == y)
					{
						if (light.getZ() == z)
						{
							dynamicValue = Math.max(dynamicValue, light.getLightSource().getLightLevel());
						}
					}
				}
			}
		}
		return Math.max(vanillaValue, dynamicValue);
	}

	/**
	 * Exposed method to register active Dynamic Light Sources with. Does all the necessary checks, prints errors if any occur, creates new World entries in the worldLightsMap
	 * 
	 * @param lightToAdd
	 *            IDynamicLightSource to register
	 */
	public static void addLightSource(IDynamicLightSource lightToAdd)
	{
		//System.out.println("Calling addLightSource "+lightToAdd+", world "+lightToAdd.getAttachmentEntity().worldObj);
		if (lightToAdd.getAttachmentEntity() != null)
		{
			if (lightToAdd.getAttachmentEntity().isEntityAlive())
			{
				DynamicLightSourceContainer newLightContainer = new DynamicLightSourceContainer(lightToAdd);
				ConcurrentLinkedQueue<DynamicLightSourceContainer> lightList = instance.worldLightsMap.get(lightToAdd.getAttachmentEntity().worldObj);
				if (lightList != null)
				{
					if (!lightList.contains(newLightContainer))
					{
						//System.out.println("Successfully registered Dynamic Light on Entity: "+newLightContainer.getLightSource().getAttachmentEntity()+" in list "+lightList);
						lightList.add(newLightContainer);
					}
					else
					{
						System.out.println("Cannot add Dynamic Light: Attachment Entity is already registered!");
					}
				}
				else
				{
					lightList = new ConcurrentLinkedQueue<DynamicLightSourceContainer>();
					lightList.add(newLightContainer);
					instance.worldLightsMap.put(lightToAdd.getAttachmentEntity().worldObj, lightList);
				}
			}
			else
			{
				System.err.println("Cannot add Dynamic Light: Attachment Entity is dead!");
			}
		}
		else
		{
			System.err.println("Cannot add Dynamic Light: Attachment Entity is null!");
		}
	}

	/**
	 * Exposed method to remove active Dynamic Light sources with. If it fails for whatever reason, it does so quietly.
	 * 
	 * @param lightToRemove
	 *            IDynamicLightSource you want removed.
	 */
	public static void removeLightSource(IDynamicLightSource lightToRemove)
	{
		if (lightToRemove != null && lightToRemove.getAttachmentEntity() != null)
		{
			World world = lightToRemove.getAttachmentEntity().worldObj;
			if (world != null)
			{
				DynamicLightSourceContainer iterContainer = null;
				ConcurrentLinkedQueue<DynamicLightSourceContainer> lightList = instance.worldLightsMap.get(world);
				if (lightList != null)
				{
					Iterator<DynamicLightSourceContainer> iter = lightList.iterator();
					while (iter.hasNext())
					{
						iterContainer = iter.next();
						if (iterContainer.getLightSource().equals(lightToRemove))
						{
							iter.remove();
							break;
						}
					}

					if (iterContainer != null)
					{
						world.updateLightByType(EnumSkyBlock.Block, iterContainer.getX(), iterContainer.getY(), iterContainer.getZ());
					}
				}
			}
		}
	}

	public static void updateLights(final World world) {
		final ConcurrentLinkedQueue<DynamicLightSourceContainer> worldLights = instance.worldLightsMap.get(world);
		if (worldLights != null)
		{
			Iterator<DynamicLightSourceContainer> iter = worldLights.iterator();
			while (iter.hasNext())
			{
				DynamicLightSourceContainer tickedLightContainer = iter.next();
				if (tickedLightContainer.onUpdate())
				{
					iter.remove();
					world.updateLightByType(EnumSkyBlock.Block, tickedLightContainer.getX(), tickedLightContainer.getY(), tickedLightContainer.getZ());
				}
			}
		}
	}
}
