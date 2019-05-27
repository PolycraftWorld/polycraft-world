package edu.utd.minecraft.mod.polycraft.worldgen;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class OilPopulate {

	public static final OilPopulate INSTANCE = new OilPopulate();
	public static final EventType EVENT_TYPE = EnumHelper.addEnum(EventType.class, "BUILDCRAFT_OIL", new Class[0], new Object[0]);
	private static final byte LARGE_WELL_HEIGHT = 16;
	private static final byte MEDIUM_WELL_HEIGHT = 6;
	public final Set<Integer> excessiveBiomes = new HashSet<Integer>();
	public final Set<Integer> surfaceDepositBiomes = new HashSet<Integer>();
	public final Set<Integer> excludedBiomes = new HashSet<Integer>();
	public final Set<Integer> aboveGroundBiomes = new HashSet<Integer>();

	private enum GenType {

		LARGE, MEDIUM, LAKE, NONE
	};

	private OilPopulate() {
		aboveGroundBiomes.add(BiomeGenBase.ocean.biomeID);
		aboveGroundBiomes.add(BiomeGenBase.desert.biomeID);
		aboveGroundBiomes.add(BiomeGenBase.icePlains.biomeID);

		surfaceDepositBiomes.add(BiomeGenBase.desert.biomeID);

		excludedBiomes.add(BiomeGenBase.sky.biomeID);
		excludedBiomes.add(BiomeGenBase.hell.biomeID);
	}

	@SubscribeEvent
	public void populate(PopulateChunkEvent.Pre event) {
		boolean doGen = TerrainGen.populate(event.chunkProvider, event.world, event.rand, event.chunkX, event.chunkX, event.hasVillageGenerated, EVENT_TYPE);

		if (!doGen) {
			event.setResult(Result.ALLOW);
			return;
		}

		//generateOil(event.world, event.rand, event.chunkX, event.chunkZ);
	}

	public void generateOil(World world, Random rand, int chunkX, int chunkZ) {

		// shift to world coordinates
		int x = chunkX * 16 + 8 + rand.nextInt(16);
		int z = chunkZ * 16 + 8 + rand.nextInt(16);

		BiomeGenBase biome = world.getBiomeGenForCoords(new BlockPos(x, 0, z));

		// Do not generate oil in the End or Nether
		if (excludedBiomes.contains(biome.biomeID)) {
			return;
		}

		boolean surfaceDepositBiome = surfaceDepositBiomes.contains(biome.biomeID);

		double bonus = surfaceDepositBiome ? 3.0 : 1.0;
		bonus *= PolycraftMod.oilWellScalar;
		if (excessiveBiomes.contains(biome.biomeID)) {
			bonus *= 30.0;
		}
		GenType type = GenType.NONE;
		if (rand.nextDouble() <= 0.0004 * bonus) {// 0.04%
			type = GenType.LARGE;
		} else if (rand.nextDouble() <= 0.001 * bonus) {// 0.1%
			type = GenType.MEDIUM;
		} else if (surfaceDepositBiome && rand.nextDouble() <= 0.02 * bonus) {// 2%
			type = GenType.LAKE;
		}

		if (type == GenType.NONE) {
			return;
		}

		// Find ground level
		int groundLevel = getTopBlock(world, x, z);
		if (groundLevel < 5) {
			return;
		}

		double deviation = surfaceDeviation(world, x, groundLevel, z, 8);
		if (deviation > 0.45) {
			return;
		}

		// Generate a Well
		if (type == GenType.LARGE || type == GenType.MEDIUM) {
			int wellX = x;
			int wellZ = z;

			int wellHeight = MEDIUM_WELL_HEIGHT;
			if (type == GenType.LARGE) {
				wellHeight = LARGE_WELL_HEIGHT;
			}
			int maxHeight = aboveGroundBiomes.contains(biome.biomeID) ? groundLevel + wellHeight : groundLevel - 5;
			if (maxHeight >= world.getActualHeight() - 1) {
				return;
			}

			// Generate a spherical cave deposit
			int wellY = 20 + rand.nextInt(10);

			int radius;
			if (type == GenType.LARGE) {
				radius = 8 + rand.nextInt(9);
			} else {
				radius = 4 + rand.nextInt(4);
			}

			int radiusSq = radius * radius;

			for (int poolX = -radius; poolX <= radius; poolX++) {
				for (int poolY = -radius; poolY <= radius; poolY++) {
					for (int poolZ = -radius; poolZ <= radius; poolZ++) {
						int distance = poolX * poolX + poolY * poolY + poolZ * poolZ;

						if (distance <= radiusSq) {
							world.setBlockState(new BlockPos(poolX + wellX, poolY + wellY, poolZ + wellZ), PolycraftMod.blockOil.getDefaultState(),distance == radiusSq ? 3 : 2);
						}
					}
				}
			}

			// Generate Lake around Spout
			int lakeRadius;
			if (type == GenType.LARGE) {
				lakeRadius = 25 + rand.nextInt(20);
				//				if (BuildCraftCore.debugMode) {
				//					lakeRadius += 40;
				//				}
			} else {
				lakeRadius = 5 + rand.nextInt(10);
			}
			generateSurfaceDeposit(world, rand, biome, new BlockPos(wellX, groundLevel, wellZ), lakeRadius);

			boolean makeSpring = false; //TODO type == GenType.LARGE && BuildCraftEnergy.spawnOilSprings && BuildCraftCore.springBlock != null && (BuildCraftCore.debugMode || rand.nextDouble() <= 0.25);

			// Generate Spout
			int baseY;
			if (makeSpring) {
				baseY = 0;
			} else {
				baseY = wellY;
			}

			//TODO add springs back in at some point?
			//if (makeSpring && world.getBlock(wellX, baseY, wellZ) == Blocks.bedrock) {
			//	world.setBlock(wellX, baseY, wellZ, BuildCraftCore.springBlock, 1, 3);
			//}
			for (int y = baseY + 1; y <= maxHeight; ++y) {
				world.setBlockState(new BlockPos(wellX, y, wellZ), PolycraftMod.blockOil.getDefaultState());
			}

			if (type == GenType.LARGE) {
				for (int y = wellY; y <= maxHeight - wellHeight / 2; ++y) {
					world.setBlockState(new BlockPos(wellX + 1, y, wellZ), PolycraftMod.blockOil.getDefaultState());
					world.setBlockState(new BlockPos(wellX - 1, y, wellZ), PolycraftMod.blockOil.getDefaultState());
					world.setBlockState(new BlockPos(wellX, y, wellZ + 1), PolycraftMod.blockOil.getDefaultState());
					world.setBlockState(new BlockPos(wellX, y, wellZ - 1), PolycraftMod.blockOil.getDefaultState());
				}
			}

		} else if (type == GenType.LAKE) {
			// Generate a surface oil lake
			int lakeX = x;
			int lakeZ = z;
			int lakeY = groundLevel;

			Block block = world.getBlockState(new BlockPos(lakeX, lakeY, lakeZ)).getBlock();
			if (block == biome.topBlock) {
				generateSurfaceDeposit(world, rand, biome, new BlockPos(lakeX, lakeY, lakeZ), 5 + rand.nextInt(10));
			}
		}
	}

	public void generateSurfaceDeposit(World world, Random rand, BlockPos blockPos, int radius) {
		BiomeGenBase biome = world.getBiomeGenForCoords(blockPos);
		generateSurfaceDeposit(world, rand, biome, blockPos, radius);
	}

	private void generateSurfaceDeposit(World world, Random rand, BiomeGenBase biome, BlockPos blockPos, int radius) {
		int depth = rand.nextDouble() < 0.5 ? 1 : 2;
		int x = blockPos.getX();
		int y = blockPos.getY();
		int z = blockPos.getZ();
		// Center
		setOilColumnForLake(world, biome, x, y, z, depth, 2);

		// Generate tendrils, from the center outward
		for (int w = 1; w <= radius; ++w) {
			float proba = (float) (radius - w + 4) / (float) (radius + 4);

			setOilWithProba(world, biome, rand, proba, x, y, z + w, depth);
			setOilWithProba(world, biome, rand, proba, x, y, z - w, depth);
			setOilWithProba(world, biome, rand, proba, x + w, y, z, depth);
			setOilWithProba(world, biome, rand, proba, x - w, y, z, depth);

			for (int i = 1; i <= w; ++i) {
				setOilWithProba(world, biome, rand, proba, x + i, y, z + w, depth);
				setOilWithProba(world, biome, rand, proba, x + i, y, z - w, depth);
				setOilWithProba(world, biome, rand, proba, x + w, y, z + i, depth);
				setOilWithProba(world, biome, rand, proba, x - w, y, z + i, depth);

				setOilWithProba(world, biome, rand, proba, x - i, y, z + w, depth);
				setOilWithProba(world, biome, rand, proba, x - i, y, z - w, depth);
				setOilWithProba(world, biome, rand, proba, x + w, y, z - i, depth);
				setOilWithProba(world, biome, rand, proba, x - w, y, z - i, depth);
			}
		}

		// Fill in holes
		for (int dx = x - radius; dx <= x + radius; ++dx) {
			for (int dz = z - radius; dz <= z + radius; ++dz) {
				if (isOil(world, dx, y, dz)) {
					continue;
				}
				if (isOilSurrounded(world, dx, y, dz)) {
					setOilColumnForLake(world, biome, dx, y, dz, depth, 2);
				}
			}
		}
	}

	private boolean isReplaceableFluid(World world, int x, int y, int z) {
		Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
		return (block instanceof BlockFluidBase || block instanceof IFluidBlock) && block.getMaterial() != Material.lava;
	}

	private boolean isOil(World world, int x, int y, int z) {
		Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
		return (block == PolycraftMod.blockOil);
	}

	private boolean isReplaceableForLake(World world, BiomeGenBase biome, int x, int y, int z) {
		Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();

		if (block == null) {
			return true;
		}

		if (block == biome.fillerBlock || block == biome.topBlock) {
			return true;
		}

		if (!block.getMaterial().blocksMovement()) {
			return true;
		}

		// TODO: The code below doesn't seem to have been replaced by something
		// in 1.7.2 - to update or remove.
		//if (block.isGenMineableReplaceable(world, x, y, z, Blocks.stone)) {
		//	return true;
		//}

		if (block instanceof BlockFlower) {
			return true;
		}

		if (!block.isOpaqueCube()) {
			return true;
		}

		return false;
	}

	private boolean isOilAdjacent(World world, int x, int y, int z) {
		return isOil(world, x + 1, y, z)
				|| isOil(world, x - 1, y, z)
				|| isOil(world, x, y, z + 1)
				|| isOil(world, x, y, z - 1);
	}

	private boolean isOilSurrounded(World world, int x, int y, int z) {
		return isOil(world, x + 1, y, z)
				&& isOil(world, x - 1, y, z)
				&& isOil(world, x, y, z + 1)
				&& isOil(world, x, y, z - 1);
	}

	private void setOilWithProba(World world, BiomeGenBase biome, Random rand, float proba, int x, int y, int z, int depth) {
		if (rand.nextFloat() <= proba && world.getBlockState(new BlockPos(x, y - depth - 1, z)).getBlock() != null) {
			if (isOilAdjacent(world, x, y, z)) {
				setOilColumnForLake(world, biome, x, y, z, depth, 3);
			}
		}
	}

	private void setOilColumnForLake(World world, BiomeGenBase biome, int x, int y, int z, int depth, int update) {
		if (isReplaceableForLake(world, biome, x, y + 1, z)) {
			if (!world.isAirBlock(new BlockPos(x, y + 2, z))) {
				return;
			}
			if (isReplaceableFluid(world, x, y, z) || world.isSideSolid(new BlockPos(x, y - 1, z), EnumFacing.UP)) {
				world.setBlockState(new BlockPos(x, y, z), PolycraftMod.blockOil.getDefaultState(), update);
			} else {
				return;
			}
			if (!world.isAirBlock(new BlockPos(x, y + 1, z))) {
				world.setBlockState(new BlockPos(x, y + 1, z), Blocks.air.getDefaultState(), update);
			}

			for (int d = 1; d <= depth - 1; d++) {
				if (isReplaceableFluid(world, x, y - d, z) || !world.isSideSolid(new BlockPos(x, y - d - 1, z), EnumFacing.UP)) {
					return;
				}
				world.setBlockState(new BlockPos(x, y - d, z), PolycraftMod.blockOil.getDefaultState(),2);
			}
		}
	}

	private int getTopBlock(World world, int x, int z) {
		Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
		int y = chunk.getTopFilledSegment() + 15;

		int trimmedX = x & 15;
		int trimmedZ = z & 15;

		for (; y > 0; --y) {
			Block block = chunk.getBlock(trimmedX, y, trimmedZ);

			if (block == null) {
				continue;
			}

			if (block instanceof BlockFluidBase) {
				return y;
			}

			if (block instanceof IFluidBlock) {
				return y;
			}

			if (!block.getMaterial().blocksMovement()) {
				continue;
			}

			if (block instanceof BlockFlower) {
				continue;
			}

			return y - 1;
		}

		return -1;
	}

	private double surfaceDeviation(World world, int x, int y, int z, int radius) {
		int diameter = radius * 2;
		double centralTendancy = y;
		double deviation = 0;
		for (int i = 0; i < diameter; i++) {
			for (int k = 0; k < diameter; k++) {
				deviation += getTopBlock(world, x - radius + i, z - radius + k) - centralTendancy;
			}
		}
		return Math.abs(deviation / centralTendancy);
	}
}
