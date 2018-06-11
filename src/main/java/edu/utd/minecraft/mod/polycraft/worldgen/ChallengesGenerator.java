package edu.utd.minecraft.mod.polycraft.worldgen;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.IWorldGenerator;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;

public class ChallengesGenerator extends WorldGenerator implements IWorldGenerator {

	public ChallengesGenerator() {
		super();
	}
	
	
	
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) {
		if (world.provider.dimensionId != 8 || !(Math.abs(chunkX)%4==0) || !(Math.abs(chunkZ)%4==0))//TODO change generation distance
			return;
		int x = chunkX * 16;
		int z = chunkZ * 16;
		int y = 80;
		ResourceLocation testSchematic = new ResourceLocation(PolycraftMod.getAssetName("schematics/test.schematic"));
		short n = 0;
		Schematic sch = new Schematic(new NBTTagList(), n, n, n, new byte[] {0}, new byte[] {0});
		Schematic sh = sch.get("test.schematic");
		
		int count=0;
		for (int k = 0; k < (int)sh.height; k++) {
			for (int j = 0; j < (int)sh.length; j++) {
				for (int i = 0; i < (int)sh.width; i++)
				{
					world.setBlock(x + i, y + k , z + j, Block.getBlockById((int)sh.blocks[count]), sh.data[count], 2);
					count++;
				}
			}
		}
		
	}






	@Override
	public boolean generate(World p_76484_1_, Random p_76484_2_, int p_76484_3_, int p_76484_4_, int p_76484_5_) {
		
		return false;
	}

}
