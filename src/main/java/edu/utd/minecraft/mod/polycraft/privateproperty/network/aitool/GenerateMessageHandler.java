package edu.utd.minecraft.mod.polycraft.privateproperty.network.aitool;

import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
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
            			player.worldObj.setBlockState(new BlockPos(x+i,y,z+j), Blocks.stone.getDefaultState());
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
