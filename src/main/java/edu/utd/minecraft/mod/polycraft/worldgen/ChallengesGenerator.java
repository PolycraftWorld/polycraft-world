package edu.utd.minecraft.mod.polycraft.worldgen;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.IWorldGenerator;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.schematic.PolySchematic;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;

public class ChallengesGenerator extends WorldGenerator implements IWorldGenerator {
	
	int [][] chunk1= new int[16][16];
	int [][] chunk2= new int[16][16];
	int [][] chunk3= new int[16][16];
	int [][] chunk4= new int[16][16];
	
	public ChallengesGenerator() {
		super();
	}
	
	
	
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) {
		if (world.provider.dimensionId != 8) 
		{
			return;
		}else if( ((Math.abs(chunkX)%5==0) && (Math.abs(chunkZ)%5==0)))
		{
			
			
			int x = chunkX * 16;
			int z = chunkZ * 16;
			int y = 80;
			short n = 0;
			Schematic sch = new Schematic(new NBTTagList(), n, n, n, new int[] {0}, new int[] {0});
			Schematic sh = sch.get("testout.schematic");
			
			int count=0;
			
			for (int k = 0; k < (int)sh.length; k++) {
				for (int j = 0; j < (int)sh.height; j++) {
					for (int i = 0; i < (int)sh.width; i++)
					{
						if(count==15361)
						{
							System.out.println("too big");
						}
						
						world.setBlock(x + k, y + j , z + i, Block.getBlockById((int)sh.blocks[count]), sh.data[count], 2);
						count++;
						
					}
				}
			}
			for (int k = 0; k < (int)sh.tileentities.tagCount(); k++)
			{
				NBTTagCompound nbt = sh.tileentities.getCompoundTagAt(k);
				if(nbt.getString("id").equals("Chest"))
				{
					TileEntityChest tile = new TileEntityChest();
					for(int i=0;i<nbt.getTagList("Items", 10).tagCount();i++)
					{
						NBTTagCompound nbt2 = nbt.getTagList("Items", 10).getCompoundTagAt(i);
						ItemStack stack = new ItemStack(Item.getItemById(nbt2.getShort("id")));
						tile.setInventorySlotContents(i, stack);
					}
					world.setTileEntity(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"), tile);
				}
			}
			
		}
		return;
		
	}
	
	public void splitSchematic(Schematic schem)
	{
		
	}






	@Override
	public boolean generate(World p_76484_1_, Random p_76484_2_, int p_76484_3_, int p_76484_4_, int p_76484_5_) {
		
		return false;
	}

}
