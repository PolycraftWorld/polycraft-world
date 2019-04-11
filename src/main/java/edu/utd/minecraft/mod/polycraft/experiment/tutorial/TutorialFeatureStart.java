package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

public class TutorialFeatureStart extends TutorialFeature{
	private Vec3 lookDir;	//xCoord == pitch; yCoord = yaw
	
	//working parameters
	private boolean spawnedInServer = false, spawnedInClient = false;
	private int dim = 8;
	
	//Gui Parameters
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField pitchField, yawField;
	
	public TutorialFeatureStart() {}
	
	public TutorialFeatureStart(String name, Vec3 pos, Vec3 lookDir){
		super(name, pos, Color.CYAN);
		this.lookDir = lookDir;
		this.featureType = TutorialFeatureType.START;
	}
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
		dim = exp.dim;

		System.out.println("Feature Start pos:" + pos.xCoord + "," + pos.yCoord + "," + pos.zCoord);
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		if(!spawnedInServer) {
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				spawnPlayer((EntityPlayerMP) player, exp);
				System.out.println("Feature Start pos:" + pos.xCoord + "," + pos.yCoord + "," + pos.zCoord);
			}
			spawnedInServer = true;
		}
			
		if(spawnedInClient) {
			canProceed = true;
			isDone = true;
		}
	}
	
	@Override
	public void onPlayerTickUpdate(ExperimentTutorial exp) {
		if(!spawnedInClient) {
			Minecraft.getMinecraft().renderViewEntity.rotationPitch = (float) this.lookDir.xCoord;
			Minecraft.getMinecraft().renderViewEntity.rotationYaw = (float) this.lookDir.yCoord;
			spawnedInClient = true;
			isDirty = true;
		}
	}
	
	
	/**
	 * Used to dimensionally transport a player into the Experiments dimension (dim. 8)
	 * Player randomly is placed within the experiment zone using Math.random().
	 * TODO: spawn players within their "Team Spawn" Zones.
	 * @param player player to be teleported
	 * @param y height they should be dropped at.
	 */
	private void spawnPlayer(EntityPlayerMP player, ExperimentTutorial exp){
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player, dim,	
				new PolycraftTeleporter(player.mcServer.worldServerForDimension(dim), (int) this.pos.xCoord, (int) this.pos.yCoord, (int) this.pos.zCoord,
						(float) this.lookDir.yCoord, (float) this.lookDir.xCoord));
		
	}

	public Vec3 getLookDir() {
		return lookDir;
	}

	public void setLookDir(Vec3 lookDir) {
		this.lookDir = lookDir;
	}
	
	
	@Override
	public void render(Entity entity) {
		TutorialRender.renderLoadingScreen(entity);
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
		nbt.setBoolean("spawnedInServer", spawnedInServer);
		nbt.setBoolean("spawnedInClient", spawnedInClient);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		int featLookDir[]=nbtFeat.getIntArray("lookDir");
		this.lookDir=Vec3.createVectorHelper(featLookDir[0], featLookDir[1], featLookDir[2]);
		this.spawnedInServer = nbtFeat.getBoolean("spawnedInServer");
		this.spawnedInClient = nbtFeat.getBoolean("spawnedInClient");
	}
	
}
