package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.ExpFeatureMessage.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ExpFeatureMessageHandler implements IMessageHandler<ExpFeatureMessage, IMessage>{

	@Override
    public ExpFeatureMessage onMessage(final ExpFeatureMessage message, MessageContext ctx)
    {
        if(ctx.side == Side.SERVER) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(new Runnable()
            {
            	@Override
                public void run()
                {
            		switch(message.type) {
            		case ACTIVE:
            			break;	// this shouldn't happen
            		case All:
            			break;	// this shouldn't happen
            		case SINGLE:
            			for(ExperimentTutorial exp : TutorialManager.INSTANCE.experiments.values()) {
         					if(exp.isPlayerInExperiment(ctx.getServerHandler().playerEntity.getDisplayNameString()))
         						exp.activeFeatures.set(message.featureIndex, message.featureList.get(0));
         				}
            			break;
            		default:
            			break;
            		}
                }
             });
        }else {
        	IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                	switch(message.type) {
					case ACTIVE:
						//TODO: NEED TO VERIFY THAT ALL EXPERIMENT IDs ARE consistant on client side
						TutorialManager.INSTANCE.experiments.get(message.expID).updateActiveFeatures(message.featureList);
						break;
					case All:
						if(!TutorialManager.INSTANCE.experiments.contains(message.expID))
							TutorialManager.INSTANCE.experiments.put(message.expID, new ExperimentTutorial(message.expID, Minecraft.getMinecraft().theWorld, message.featureList));
                		TutorialManager.INSTANCE.clientCurrentExperiment = message.expID;
						break;
					case SINGLE:
						TutorialManager.INSTANCE.experiments.get(message.expID).activeFeatures.set(message.featureIndex, message.featureList.get(0));
						break;
					default:
						break;
                	}
                }
            });
        }
        
       
        return null;
    }

}
