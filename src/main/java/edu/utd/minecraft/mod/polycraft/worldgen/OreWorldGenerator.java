package edu.utd.minecraft.mod.polycraft.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
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
		for (Ore ore : Ore.registry.values()) {
			Block oreBlock = PolycraftMod.blocks.get(ore.gameName);
			for (int k = 0; k < ore.generationVeinsPerChunk; k++) {
				int firstBlockXCoord = i + random.nextInt(16);
				int firstBlockYCoord = ore.generationStartYMin + random.nextInt(ore.generationStartYMax - ore.generationStartYMin);
				int firstBlockZCoord = j + random.nextInt(16);
				(new WorldGenMinable(oreBlock, ore.generationBlocksPerVein)).generate(world, random, firstBlockXCoord, firstBlockYCoord, firstBlockZCoord);
			}
		}
	}

	private void generateNether(World world, Random random, int i, int j) {
	}
}
