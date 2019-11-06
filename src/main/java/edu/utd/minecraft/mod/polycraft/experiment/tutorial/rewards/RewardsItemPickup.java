package edu.utd.minecraft.mod.polycraft.experiment.tutorial.rewards;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;

public class RewardsItemPickup extends ExperimentReward{

	private int rewardValue = 1;
	private String itemName;
	private int rewardsRemaining = 1;
	
	@Override
	public void init(ExperimentTutorial exp) {
		//no init needed for this reward type
	}

	@Override
	public int rewardEvent(ExperimentTutorial exp, Event event) {
		if(rewardsRemaining > 0 && event instanceof ItemPickupEvent) {
			if(((ItemPickupEvent)event).pickedUp.getEntityItem().getItem().getUnlocalizedName().equals(this.itemName)) {
				rewardsRemaining--;
				return rewardValue;
			}
		}
		return 0;
	}

}
