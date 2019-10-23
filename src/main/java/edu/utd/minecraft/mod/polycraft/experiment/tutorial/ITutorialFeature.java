package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import net.minecraft.entity.Entity;

public interface ITutorialFeature{
	
	//pre-initialization function runs before the tutorial begins to setup this room
	public void preInit(ExperimentTutorial exp);
	
	//Initialization runs just before the player enters the room or as they enter (this would be when the player reaches the last checkpoint before this stage begins)
	public abstract void init();

	//onServerTickUpdate is called once for every server tick by the tutorial manager
	public abstract void onServerTickUpdate(ExperimentTutorial exp);
	
	//onPlayerTickUpdate is called once for every Client tick by the tutorial manager
	public abstract void onClientTickUpdate(ExperimentTutorial exp);
	
	//end will run as the last function in case there is any cleanup that needs to be done before the user can progress through the rest of the stages
	public abstract void end(ExperimentTutorial exp);
	
	//used to check if this feature should continue to run
	public abstract boolean isDone();
	
	//used to check if the next feature can be added to active features
	public abstract boolean canProceed();
	
	//render function
	public abstract void render(Entity entity);
	
	//render on Screen function
	public abstract void renderScreen(Entity entity);

}
