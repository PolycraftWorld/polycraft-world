package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.util.Format;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class TutorialFeatureEnd extends TutorialFeature{

	public TutorialFeatureEnd() {}
	public int countDown;
	
	public TutorialFeatureEnd(String name, BlockPos pos){
		super(name, pos, Color.YELLOW);
		super.featureType = TutorialFeatureType.END;
		this.countDown=50;
	}
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
		this.countDown=50;
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
			this.countDown+=-1;
			if(this.countDown%20==0)
			{
				this.isDirty=true;
			}
			if(this.countDown<=5)
			{
				//TP player to UTD
				sendPlayerToUTD((EntityPlayerMP) player, exp);
				complete(exp);
				//save Player/Client hasCompletedTutorial=true;
				ServerEnforcer.INSTANCE.UpdateClientTutorialCompleted((EntityPlayerMP)player);
			}
		}
	}
	
	private void sendPlayerToUTD(EntityPlayerMP player, ExperimentTutorial exp){
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 0,	
				new PolycraftTeleporter(player.mcServer.worldServerForDimension(0), 
				(int) 0, (int) 100, (int) 0,
				(float) 0, (float) 0));
	}
	
	@Override
	public void render(Entity entity) {
		TutorialRender.instance.renderTutorialDrawStringWithScale("\u00A76YOU WON!",100,35,2F);
		TutorialRender.instance.renderTutorialDrawString("\u00A7eYou will be teleported in \u00A76"+this.countDown/20+"\u00A7e seconds!",150,100);
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		nbt.setInteger("countdown", this.countDown);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		this.countDown=nbtFeat.getInteger("countdown");
	}
}
