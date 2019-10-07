package edu.utd.minecraft.mod.polycraft.privateproperty.network.aitool;

import java.util.Random;

import com.google.common.base.Predicate;

import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
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
            			if(rand.nextInt(Math.min(message.width, message.length))==0)
            			{
//            				BlockPlanks.EnumType treetype = new BlockPlanks.EnumType(, String p_i46388_4_, MapColor p_i46388_5_)
//            				player.worldObj.setBlockState(new BlockPos(x+i,y+1,z+j), (IBlockState) new BlockState(this, new IProperty[] {TYPE, STAGE}));
//            				BlockSapling sap =((BlockSapling)(player.worldObj.getBlockState(new BlockPos(x+i,y+1,z+j)).getBlock()));
//            				IBlockState sapState = Blocks.sapling.getDefaultState();
//
//            				
//            				sap.generateTree(player.worldObj, new BlockPos(x+i,y+1,z+j), Blocks.sapling.getDefaultState(), rand);
            			    PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.<BlockPlanks.EnumType>create("variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>()
            			    {
            			        public boolean apply(BlockPlanks.EnumType p_apply_1_)
            			        {
            			            return p_apply_1_.getMetadata() >= 4;
            			        }
            			    });
            			    PropertyEnum<BlockLog.EnumAxis> LOG_AXIS = PropertyEnum.<BlockLog.EnumAxis>create("axis", BlockLog.EnumAxis.class);
            				switch(message.treeTypes.get(0))
            				{
            					case 0://oak
            						for(int c=1;c<=3;c++)
            							player.worldObj.setBlockState(new BlockPos(x+i,y+c,z+j), Blocks.log.getDefaultState());
            							break;
            					case 1://spruce
            						for(int c=1;c<=3;c++)
            							player.worldObj.setBlockState(new BlockPos(x+i,y+c,z+j), Blocks.log.getDefaultState());
            						break;
            					case 2://birch
            						for(int c=1;c<=3;c++)
            							player.worldObj.setBlockState(new BlockPos(x+i,y+c,z+j), Blocks.log.getDefaultState());
            						break;
            					case 3://jungle
            						for(int c=1;c<=3;c++)
            							player.worldObj.setBlockState(new BlockPos(x+i,y+c,z+j), Blocks.log.getDefaultState());
            						break;
            					case 4://acacia
            						for(int c=1;c<=3;c++)
            							player.worldObj.setBlockState(new BlockPos(x+i,y+c,z+j), Blocks.log.getDefaultState());
            						break;
            					case 5://darkoak
            						for(int c=1;c<=3;c++)
            							player.worldObj.setBlockState(new BlockPos(x+i,y+c,z+j), Blocks.log.getDefaultState());
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
