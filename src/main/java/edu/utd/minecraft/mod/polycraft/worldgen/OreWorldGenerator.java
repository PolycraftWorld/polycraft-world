package edu.utd.minecraft.mod.polycraft.worldgen;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.Ore;

public class OreWorldGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		switch (world.provider.dimensionId) {
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
					world.setBlock(firstBlockXCoord, 4, firstBlockZCoord, oreBlock, 8 + random.nextInt(8), 2);
					world.setBlock(firstBlockXCoord, 3, firstBlockZCoord, oreBlock, 0, 2);
					world.setBlock(firstBlockXCoord, 2, firstBlockZCoord, oreBlock, 0, 2);
					world.setBlock(firstBlockXCoord, 1, firstBlockZCoord, oreBlock, 0, 2);
				}
				else
				{

					for (int k = 0; k < ore.generationVeinsPerChunk; k++) {
						final int firstBlockXCoord = i + random.nextInt(16);
						final int firstBlockYCoord = ore.generationStartYMin + random.nextInt(ore.generationStartYMax - ore.generationStartYMin + 1);
						final int firstBlockZCoord = j + random.nextInt(16);
						(new WorldGenMinable(oreBlock, ore.generationBlocksPerVein)).generate(world, random, firstBlockXCoord, firstBlockYCoord, firstBlockZCoord);
						//System.out.println("Ore: " + ore.name);
					}
				}
			}
		}
	}

	private void generateNether(World world, Random random, int i, int j) {
	}
}
