package edu.utd.minecraft.mod.polycraft.privateproperty.network.aitool;

import java.util.Random;

import com.google.common.base.Predicate;

import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GenerateMessageHandler implements IMessageHandler<GenerateMessage, IMessage>{

	@Override
    public IMessage onMessage(final GenerateMessage message, MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
        mainThread.addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
            	int x=(int)player.posX-(message.width/2);
            	int y=(int)player.posY-1;
            	int z=(int)player.posZ-(message.length/2);
            	for(int i=0;i<message.width;i++)
            	{
            		for(int j=0;j<message.length;j++)
            		{
            			player.worldObj.setBlockState(new BlockPos(x+i,y,z+j), Block.getBlockById(message.block).getDefaultState());
            			Random rand= new Random();
            			if(rand.nextInt(Math.min(message.width, message.length))==0 && message.treeTypes.size()!=0)
            			{
//            				BlockPlanks.EnumType treetype = new BlockPlanks.EnumType(, String p_i46388_4_, MapColor p_i46388_5_)
//            				player.worldObj.setBlockState(new BlockPos(x+i,y+1,z+j), (IBlockState) new BlockState(this, new IProperty[] {TYPE, STAGE}));
//            				BlockSapling sap =((BlockSapling)(player.worldObj.getBlockState(new BlockPos(x+i,y+1,z+j)).getBlock()));
//            				IBlockState sapState = Blocks.sapling.getDefaultState();
//
//            				
//            				sap.generateTree(player.worldObj, new BlockPos(x+i,y+1,z+j), Blocks.sapling.getDefaultState(), rand);
            				
            				
            				IBlockState iblockstate;
            				IBlockState iblockstate1;
            				WorldGenerator worldgenerator;
            				BlockNewLeaf leaf = new BlockNewLeaf();
            				
            				switch(message.treeTypes.get(rand.nextInt(message.treeTypes.size())))
            				{
            					case 0://oak
            						iblockstate = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK);
            				        iblockstate1 = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
            				        worldgenerator = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockstate, iblockstate1, false);
            				        worldgenerator.generate(player.worldObj, rand, new BlockPos(x+i,y+1,z+j));
            						break;
            					case 1://spruce
        							iblockstate = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
        				            iblockstate1 = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
        				            worldgenerator = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockstate, iblockstate1, false);
        				            worldgenerator.generate(player.worldObj, rand, new BlockPos(x+i,y+1,z+j));
            						break;
            					case 2://birch
        							iblockstate = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH);
        				            iblockstate1 = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
        				            worldgenerator = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockstate, iblockstate1, false);
        				            worldgenerator.generate(player.worldObj, rand, new BlockPos(x+i,y+1,z+j));
            						break;
            					case 3://jungle
        							iblockstate = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
        				            iblockstate1 = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
        				            worldgenerator = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockstate, iblockstate1, false);
        				            worldgenerator.generate(player.worldObj, rand, new BlockPos(x+i,y+1,z+j));
            						break;
            					case 4://acacia
        							iblockstate = Blocks.log.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA);
        				            iblockstate1 = leaf.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
        				            worldgenerator = new WorldGenSavannaTree(true);
        				            worldgenerator.generate(player.worldObj, rand, new BlockPos(x+i,y+1,z+j));
            						break;
            					case 5://darkoak
        							iblockstate = Blocks.log.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        				            iblockstate1 = leaf.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
        				            worldgenerator =  new WorldGenCanopyTree(true);
        				            worldgenerator.generate(player.worldObj, rand, new BlockPos(x+i,y+1,z+j));
            						break;
            				}

            				
            			}
            			if(message.walls)
                    	{
            				if(i==0 || i==(message.width-1) || j==0 || j==(message.length-1))
            				{
	                    		for(int c=1;c<=message.height;c++)
	                    		{
	                    			player.worldObj.setBlockState(new BlockPos(x+i,y+c,z+j), Blocks.stone.getDefaultState());
	                    		}
            				}
                    	}
            		}
            		
            	}
            	
            	
            }
        });
        return null;
    }

}
