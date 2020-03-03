package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
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
            		case All:
            			break;	// this shouldn't happen
            		case SINGLE:
            			for(ExperimentTutorial exp : TutorialManager.INSTANCE.experiments.values()) {
         					if(exp.isPlayerInExperiment(ctx.getServerHandler().playerEntity.getDisplayNameString())) {
         						//Instead of inserting a new feature, lets update the old feature using the save output of the new one
         						for(TutorialFeature feature: exp.getFeatures()) {
         							// find the correct feature to update
         							if(feature.getUUID().compareTo(message.featureList.get(0).getUUID()) == 0) {
         								feature.load(message.featureList.get(0).save());
                 						break; 	// Once we find the match, exit the for loop.  
         							}
         						}
         						break;	// we found the experiment the player is in and updated the feature. Exit loop
         					}
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
					case All:	// This is for starting a new experiment.  Add all exp features to client side
						for(ExperimentTutorial exp: TutorialManager.INSTANCE.experiments.values()) {
							if(exp.currentState != ExperimentTutorial.State.Done && exp.id != message.expID)	
								exp.stop(); // first stop any experiments this update packet doesn't relate to
						}
						if(!TutorialManager.INSTANCE.experiments.containsKey(message.expID))
							TutorialManager.INSTANCE.experiments.put(message.expID, new ExperimentTutorial(message.expID, Minecraft.getMinecraft().theWorld, message.featureList));
                		TutorialManager.INSTANCE.clientCurrentExperiment = message.expID;
						break;
					case SINGLE:
						ExperimentTutorial exp = TutorialManager.INSTANCE.experiments.get(message.expID);
 						// Update the old feature using the save output of the new one
 						for(TutorialFeature feature: exp.getFeatures()) {
 							// find the correct feature to update
 							if(feature.getUUID().compareTo(message.featureList.get(0).getUUID()) == 0) {
 								feature.load(message.featureList.get(0).save());
         						break; 	// Once we find the match, exit the for loop.  
 							}
 						}
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
