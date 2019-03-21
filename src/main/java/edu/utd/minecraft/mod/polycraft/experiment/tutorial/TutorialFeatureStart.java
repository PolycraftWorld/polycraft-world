package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.util.ArrayList;

import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

public class TutorialFeatureStart extends TutorialFeature{
	private Vec3 lookDir;
	
	//Gui Parameters
	protected GuiPolyNumField pitchField, yawField;
	
	public TutorialFeatureStart() {}
	
	public TutorialFeatureStart(String name, Vec3 pos, Vec3 lookDir){
		super(name, pos, Color.CYAN);
		this.lookDir = lookDir;
		this.featureType = TutorialFeatureType.START;
	}
	
	@Override
	public void onServerTickUpdate() {
		for(EntityPlayer player: experiment.scoreboard.getPlayersAsEntity()) {
			spawnPlayer((EntityPlayerMP) player);
		}
		isDone = true;
	}
	
	
	/**
	 * Used to dimensionally transport a player into the Experiments dimension (dim. 8)
	 * Player randomly is placed within the experiment zone using Math.random().
	 * TODO: spawn players within their "Team Spawn" Zones.
	 * @param player player to be teleported
	 * @param y height they should be dropped at.
	 */
	private void spawnPlayer(EntityPlayerMP player){
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 0,	
				new PolycraftTeleporter(player.mcServer.worldServerForDimension(0), (int) this.pos.xCoord, (int) this.pos.yCoord, (int) this.pos.zCoord,
						(float) this.lookDir.yCoord, (float) this.lookDir.xCoord));
		
	}

	public Vec3 getLookDir() {
		return lookDir;
	}

	public void setLookDir(Vec3 lookDir) {
		this.lookDir = lookDir;
	}
	
	@Override
	public void buildGuiParameters(GuiDevTool guiDevTool, int x_pos, int y_pos) {
		// TODO Auto-generated method stub
		super.buildGuiParameters(guiDevTool, x_pos, y_pos);
		
		FontRenderer fr = guiDevTool.getFontRenderer();
        
        y_pos += 15;
        
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pitch:"));
        pitchField = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        pitchField.setMaxStringLength(32);
        pitchField.setText(Integer.toString((int)lookDir.xCoord));
        pitchField.setTextColor(16777215);
        pitchField.setVisible(true);
        pitchField.setCanLoseFocus(true);
        pitchField.setFocused(false);
        guiDevTool.textFields.add(pitchField);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Yaw:"));
        yawField = new GuiPolyNumField(fr, x_pos + 110, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        yawField.setMaxStringLength(32);
        yawField.setText(Integer.toString((int)lookDir.yCoord));
        yawField.setTextColor(16777215);
        yawField.setVisible(true);
        yawField.setCanLoseFocus(true);
        yawField.setFocused(false);
        guiDevTool.textFields.add(yawField);
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		int lookDir[] = {(int)this.lookDir.xCoord, (int)this.lookDir.yCoord, (int)this.lookDir.zCoord};
		nbt.setIntArray("lookDir",lookDir);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		int featLookDir[]=nbtFeat.getIntArray("lookDir");
		this.lookDir=Vec3.createVectorHelper(featLookDir[0], featLookDir[1], featLookDir[2]);
	}
	
}
