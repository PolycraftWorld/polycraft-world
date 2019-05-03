package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureInstruction.InstructionType;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

public class TutorialFeatureScore extends TutorialFeature{
	//Working variables
	List<Long> times=new ArrayList<Long>();
	protected int score = 0;

	public TutorialFeatureScore() {}
	
	public TutorialFeatureScore(String name, Vec3 pos){
		super(name, pos, Color.WHITE);
		super.featureType = TutorialFeatureType.SCORE;
	}

	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
		canProceed = true;
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		
		this.times=getTrialTimes(exp);
		long sum=0;
		long trials=times.size();
		
		
		for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
			
			for(Long time: times)
			{
				//player.addChatMessage(new ChatComponentText("Times: "+time+" sec"));
				sum+=time;
			}
			if(trials==0)
				trials=1;
			long avg = sum/trials;
			long avg2=avg;
			if(avg<30)
			{
				avg2=30;
			}
			
			
			long score = 88+30-avg2;
			
			for (TutorialFeature feature : exp.features) {
			    if(feature instanceof TutorialFeatureInstruction)
			    {
			    	TutorialFeatureInstruction feat=(TutorialFeatureInstruction)feature;
			    	if(feat.getType()==InstructionType.JUMP_SPRINT && feat.failCount<2)
			    	{
			    		score+=12;
			    	}
			    }
			}
			
			if(score<0)
			{
				score=0;
			}
			player.addChatMessage(new ChatComponentText("Average time per Trial: "+avg+" sec"));
			player.addChatMessage(new ChatComponentText("Score: "+score+" out of 100"));
			//Send score to Website
			ServerEnforcer.INSTANCE.updateSkillLevel(player.getDisplayName(), (int) score);
		}
		
		complete(exp);
	}
	
	public List<Long>  getTrialTimes(ExperimentTutorial exp)
	{

		TutorialFeatureGroup TrialStart= null;
		TutorialFeatureGroup TrialEnd= null;
		List<Long> TrialTimes=new ArrayList<Long>();
		
		for (TutorialFeature feature : exp.features) {
		    if(feature instanceof TutorialFeatureGroup)
		    {
		    	TutorialFeatureGroup group = (TutorialFeatureGroup)feature;
		    	if(TrialStart==null)
		    	{
			    	if(group.getType()==TutorialFeatureGroup.GroupType.START)
			    	{
			    		TrialStart=group;
			    	}
		    	}
		    	else
		    	{
		    		if(group.getType()==TutorialFeatureGroup.GroupType.END)
			    	{
		    			TrialEnd=group;
			    		long TrialTime = (TrialEnd.completionTime-TrialStart.completionTime)/20;
			    		TrialTimes.add(TrialTime);
			    		TrialStart= null;
			    		TrialEnd= null;
			    	}
		    	}
		    }
		}
		
		return TrialTimes;
		
		
	}
	
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		nbt.setInteger("score", score);
		return nbt;
	}
	
	@Override
	public void end(ExperimentTutorial exp) {
		super.end(exp);
//		for(String playerName: exp.getPlayersInExperiment())
//			ServerEnforcer.INSTANCE.updateSkillLevel(playerName, 100);
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		this.score=nbtFeat.getInteger("score");
	}
}
