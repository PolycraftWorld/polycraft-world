package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import edu.utd.minecraft.mod.polycraft.experiment.feature.ExperimentFeature;
import net.minecraft.world.World;

public abstract class TutorialStageFeature extends ExperimentFeature{

	public TutorialStageFeature(Integer x, Integer y, Integer z) {
		super(x, y, z);
		// TODO Auto-generated constructor stub
	}
	
	//pre-initialization function runs before the tutorial begins to setup this room
	public abstract void preInit();
	
	//Initialization runs just before the player enters the room or as they enter (this would be when the player reaches the last checkpoint before this stage begins)
	public abstract void init();

	//onServerTickUpdate is called once for every server tick by the tutorial manager
	public abstract void onServerTickUpdate();
	
	//onPlayerTickUpdate is called once for every Client tick by the tutorial manager
	public abstract void onPlayerTickUpdate();
	
	//end will run as the last function in case there is any cleanup that needs to be done before the user can progress through the rest of the stages
	public abstract void end();
	
	
	@Override
	public void build(World world) {
		// TODO Auto-generated method stub
		
	}

}
