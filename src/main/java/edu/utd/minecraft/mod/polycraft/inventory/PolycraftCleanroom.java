package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockCollision;
import edu.utd.minecraft.mod.polycraft.block.BlockCompressed;
import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.block.BlockPipe;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerSlab;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerStairs;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerWall;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpInventory;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpState;

public abstract class PolycraftCleanroom {

	private static int cleanroomMaxX = 64;
	private static int cleanroomMaxY = 12;
	private static int cleanroomMaxZ = 64;
	private static int cleanroomMinXYZ = 6;

	public static boolean isLocationClean(World worldObj, int x, int y, int z) {

		int cleanroomWidth = 0;
		int cleanroomLength = 0;
		int cleanroomHeight = 0;
		boolean foundPump = false;
		Vec3 pumpCoords = null;

		Block block = worldObj.getBlock(x, y, z);

		Vec3 nwCoords = findNWBottomCornerOfCleanroom(worldObj, block, x, y, z);

		if (nwCoords == null)
			return false;

		x = (int) nwCoords.xCoord;
		y = (int) nwCoords.yCoord;
		z = (int) nwCoords.zCoord;

		if (worldObj.getBlock(x, y, z) != PolycraftRegistry.getBlock("Block (PP)"))
			return false;

		for (int i = 1; i < cleanroomMaxX; i++)
		{
			if (!isCleanroomFloorBlock(block = worldObj.getBlock(x + i, y, z + 1)))
			{
				cleanroomWidth = i + 1;
				break;
			}
		}

		for (int j = 0; j < cleanroomMaxY; j++)
		{
			if (!isCleanroomWallBlock(block = worldObj.getBlock(x, y + j, z)))
			{
				cleanroomHeight = j;
				break;
			}
		}

		for (int k = 1; k < cleanroomMaxZ; k++)
		{
			if (!isCleanroomFloorBlock(block = worldObj.getBlock(x + 1, y, z + k)))
			{
				cleanroomLength = k + 1;
				break;
			}
		}

		if (cleanroomWidth < cleanroomMinXYZ)
			return false;
		if (cleanroomHeight < cleanroomMinXYZ)
			return false;
		if (cleanroomLength < cleanroomMinXYZ)
			return false;

		//NORTH Wall
		for (int i = 0; i < cleanroomWidth; i++)
		{
			for (int j = 0; j < cleanroomHeight; j++)
			{
				if (!isCleanroomWallBlock(block = worldObj.getBlock(x + i, y + j, z)))
					return false;
				if (!foundPump && isPump(block))
				{
					if (foundPump = isPumpPurifying(worldObj, Vec3.createVectorHelper(x + i, y + j, z)))
						pumpCoords = Vec3.createVectorHelper(x + i, y + j, z);

				}
			}
		}

		//EAST Wall
		for (int k = 0; k < cleanroomLength; k++)
		{
			for (int j = 0; j < cleanroomHeight; j++)
			{
				if (!isCleanroomWallBlock(block = worldObj.getBlock(x + cleanroomWidth - 1, y + j, z + k)))
					return false;
				if (!foundPump && isPump(block))
				{
					if (foundPump = isPumpPurifying(worldObj, Vec3.createVectorHelper(x + cleanroomWidth - 1, y + j, z + k)))
						pumpCoords = Vec3.createVectorHelper(x + cleanroomWidth - 1, y + j, z + k);
				}
			}
		}

		//SOUTH Wall
		for (int i = 0; i < cleanroomWidth; i++)
		{
			for (int j = 0; j < cleanroomHeight; j++)
			{
				if (!isCleanroomWallBlock(block = worldObj.getBlock(x + i, y + j, z + cleanroomLength - 1)))
					return false;
				if (!foundPump && isPump(block))
				{
					if (foundPump = isPumpPurifying(worldObj, Vec3.createVectorHelper(x + i, y + j, z + cleanroomLength - 1)))
						pumpCoords = Vec3.createVectorHelper(x + i, y + j, z + cleanroomLength - 1);
				}
			}
		}

		//WEST Wall
		for (int k = 0; k < cleanroomLength; k++)
		{
			for (int j = 0; j < cleanroomHeight; j++)
			{
				if (!isCleanroomWallBlock(block = worldObj.getBlock(x, y + j, z + k)))
					return false;
				if (!foundPump && isPump(block))
				{
					if (foundPump = isPumpPurifying(worldObj, Vec3.createVectorHelper(x, y + j, z + k)))
						pumpCoords = Vec3.createVectorHelper(x, y + j, z + k);

				}
			}
		}

		if (pumpCoords == null) //no working pump - not clean
		{
			return false;
		}

		//Ceiling
		for (int k = 0; k < cleanroomLength; k++)
		{
			for (int i = 0; i < cleanroomWidth; i++)
			{
				if (!isCleanroomCeilingBlock(worldObj.getBlock(x + i, y + cleanroomHeight - 1, z + k)))
					return false;
			}
		}

		//Floor
		for (int k = 1; k < cleanroomLength - 1; k++)
		{
			for (int i = 1; i < cleanroomWidth - 1; i++)
			{
				if (!isCleanroomFloorBlock(worldObj.getBlock(x + i, y, z + k)))
					return false;
			}
		}

		//Internal
		for (int k = 1; k < cleanroomLength - 1; k++)
		{
			for (int j = 1; j < cleanroomHeight - 1; j++)
			{
				for (int i = 1; i < cleanroomWidth - 1; i++)
				{
					if (!isCleanroomBlock(worldObj.getBlock(x + i, y + j, z + k)))
						return false;
				}
			}
		}

		return true;
	}

	private static boolean isPumpPurifying(World worldObj, Vec3 pumpCoords)
	{

		//Is pump working
		if (pumpCoords != null)
		{
			int pumpFlowDirection = worldObj.getBlockMetadata((int) pumpCoords.xCoord, (int) pumpCoords.yCoord, (int) pumpCoords.zCoord);
			//TODO: Walter - test to see if HEPA Filter is next to Pump

			IInventory inventory = PolycraftMod.getInventoryAt(worldObj, (int) pumpCoords.xCoord, (int) pumpCoords.yCoord, (int) pumpCoords.zCoord);
			if (!(inventory instanceof PumpInventory))
				return false;
			if (!((((PumpInventory) inventory).getState(PumpState.FuelTicksRemaining) > 0) || (((PumpInventory) inventory).getState(PumpState.FuelTicksRemaining) > 0)))
			{
				if (((PumpInventory) inventory).getNextFuelSlot() == null)
					return false; //if no fuel left (ticks or epochs) and nothing in the next slot
			}
			return true;
		}
		return false;

	}

	private static boolean isPump(Block block) {
		if ((block == PolycraftRegistry.getBlock("Pump")))
			return true;
		return false;
	}

	private static boolean isCleanroomWallBlock(Block block) {

		if ((block == PolycraftRegistry.getBlock("Block (PP)")) ||
				(block == Blocks.iron_door) ||
				(block == Blocks.stained_glass) ||
				(block == Blocks.stained_glass_pane) ||
				(block instanceof BlockPipe) ||
				(block == PolycraftRegistry.getBlock("Pump")))
			return true;

		return false;
	}

	private static boolean isCleanroomCeilingBlock(Block block) {

		if ((block == PolycraftRegistry.getBlock("Block (PP)")) ||
				(block instanceof BlockPipe) ||
				(block == Blocks.stained_glass))
			return true;

		return false;
	}

	private static boolean isCleanroomFloorBlock(Block block) {

		if (block == PolycraftRegistry.getBlock("Block (PVC)") ||
				(block instanceof BlockPipe))
			return true;

		return false;
	}

	private static boolean isCleanroomBlock(Block block) {

		if ((block instanceof PolycraftInventoryBlock) ||
				(block instanceof BlockCollision) ||
				(block == Blocks.air) ||
				(block instanceof BlockLight) ||
				(block instanceof BlockCompressed) ||
				(block instanceof BlockPolymer) ||
				(block instanceof BlockPolymerSlab) ||
				(block instanceof BlockPolymerStairs) ||
				(block instanceof BlockPolymerWall) ||
				(block instanceof BlockPipe) ||
				(block == Blocks.iron_door) ||
				(block == Blocks.iron_block) ||
				(block == Blocks.brewing_stand) ||
				(block == Blocks.crafting_table) ||
				(block == Blocks.diamond_block) ||
				(block == Blocks.dispenser) ||
				(block == Blocks.emerald_block) ||
				(block == Blocks.enchanting_table) ||
				(block == Blocks.ender_chest) ||
				(block == Blocks.gold_block) ||
				(block == Blocks.sticky_piston) ||
				(block == Blocks.piston) ||
				(block == Blocks.piston_extension) ||
				(block == Blocks.piston_head) ||
				(block == Blocks.hopper) ||
				(block == Blocks.anvil) ||
				(block == Blocks.beacon) ||
				(block == Blocks.lapis_block) ||
				(block == Blocks.light_weighted_pressure_plate) ||
				(block == Blocks.heavy_weighted_pressure_plate) ||
				(block == Blocks.obsidian) ||
				(block == Blocks.powered_comparator) ||
				(block == Blocks.redstone_block) ||
				(block == Blocks.redstone_lamp) ||
				(block == Blocks.redstone_torch) ||
				(block == Blocks.redstone_wire) ||
				(block == Blocks.stained_glass) ||
				(block == Blocks.stained_glass_pane) ||
				(block == Blocks.unlit_redstone_torch) ||
				(block == Blocks.unpowered_comparator) ||
				(block == Blocks.unpowered_repeater) ||
				(block == Blocks.wall_sign) ||
				(block == Blocks.water) ||
				(block == Blocks.chest) ||
				(block == Blocks.quartz_block))
		{
			return true;
		}
		return false;
	}

	private static Vec3 findNWBottomCornerOfCleanroom(World worldObj, Block block, int x, int y, int z) {

		int counter = 0;
		while (!isCleanroomFloorBlock(block))
		{
			block = worldObj.getBlock(x, --y, z);
			if (y == 3)
				return null;
			if (counter++ >= cleanroomMaxY)
				return null;
		}

		counter = 0;
		while (isCleanroomFloorBlock(block))
		{
			block = worldObj.getBlock(--x, y, z);
			if (counter++ >= cleanroomMaxX)
				return null;
		}

		block = worldObj.getBlock(++x, y, z);
		counter = 0;
		while (isCleanroomFloorBlock(block))
		{
			block = worldObj.getBlock(x, y, --z);
			if (counter++ >= cleanroomMaxZ)
				return null;
		}
		x--;
		return Vec3.createVectorHelper(x, y, z);
	}

}
