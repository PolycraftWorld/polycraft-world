package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class TutorialFeatureCake extends TutorialFeature{

	public TutorialFeatureCake() {}
	
	public TutorialFeatureCake(String name, BlockPos pos){
		super(name, pos, Color.PINK);
		super.featureType = TutorialFeatureType.CAKE;
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
			if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.fromBounds(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
					Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
				if(player.inventory.getFirstEmptyStack() != -1) {
					player.inventory.addItemStackToInventory(new ItemStack(Items.cake, 1));
					canProceed = true;
					complete(exp);
				}
			}
		}
	}
	
}
