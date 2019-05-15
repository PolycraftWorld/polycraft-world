package edu.utd.minecraft.mod.polycraft.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import net.minecraftforge.fml.common.IWorldGenerator;

public class OreWorldGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		switch (world.provider.getDimensionId()) {
		case -1:
			generateNether(world, random, chunkX * 16, chunkZ * 16);
			break;
		case 0:
			generateSurface(world, random, chunkX * 16, chunkZ * 16);
			break;
		case 1:
			generateEnd(world, random, chunkX * 16, chunkZ * 16);
			break;
		}
	}

	private void generateEnd(World world, Random random, int i, int j) {
	}

	private void generateSurface(World world, Random random, int i, int j) {
		for (final Ore ore : Ore.getByDescendingAbundance()) {
			if (ore.generationBlocksPerVein > 0) {
				final Block oreBlock = PolycraftRegistry.getBlock(ore);

				if (ore.name.equals("OilField"))
				{
					final int firstBlockXCoord = i + random.nextInt(16);
					final int firstBlockZCoord = j + random.nextInt(16);
					IBlockState blockState = oreBlock.getDefaultState();
					//changed for 1.8
					world.setBlockState(new BlockPos(firstBlockXCoord, 4, firstBlockZCoord), oreBlock.getStateFromMeta(8 + random.nextInt(8)), 2);
					world.setBlockState(new BlockPos(firstBlockXCoord, 3, firstBlockZCoord), oreBlock.getDefaultState(), 2);
					world.setBlockState(new BlockPos(firstBlockXCoord, 2, firstBlockZCoord), oreBlock.getDefaultState(), 2);
					world.setBlockState(new BlockPos(firstBlockXCoord, 1, firstBlockZCoord), oreBlock.getDefaultState(), 2);
					// From 1.7.10
//					world.setBlock(firstBlockXCoord, 4, firstBlockZCoord, oreBlock, 8 + random.nextInt(8), 2);
//					world.setBlock(firstBlockXCoord, 3, firstBlockZCoord, oreBlock, 0, 2);
//					world.setBlock(firstBlockXCoord, 2, firstBlockZCoord, oreBlock, 0, 2);
//					world.setBlock(firstBlockXCoord, 1, firstBlockZCoord, oreBlock, 0, 2);
				}
				else
				{

					for (int k = 0; k < ore.generationVeinsPerChunk; k++) {
						final BlockPos firstBlockPos = new BlockPos(i + random.nextInt(16),
							ore.generationStartYMin + random.nextInt(ore.generationStartYMax - ore.generationStartYMin + 1),
							j + random.nextInt(16));
						(new WorldGenMinable(oreBlock.getDefaultState(), ore.generationBlocksPerVein)).generate(world, random, firstBlockPos);
						//System.out.println("Ore: " + ore.name);
					}
				}
			}
		}
	}

	private void generateNether(World world, Random random, int i, int j) {
	}
}
