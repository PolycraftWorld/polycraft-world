package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.util.ArrayList;

import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialFeatureStart extends TutorialFeature{
	private BlockPos lookDir;	//xCoord == pitch; yCoord = yaw
	
	//working parameters
	private boolean spawnedInServer = false, spawnedInClient = false;
	private int dim = 0;
	
	//Gui Parameters
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField pitchField, yawField;
	
	public TutorialFeatureStart() {}
	
	public TutorialFeatureStart(String name, BlockPos pos, BlockPos lookDir){
		super(name, pos, Color.CYAN);
		this.lookDir = lookDir;
		this.featureType = TutorialFeatureType.START;
	}
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
		dim = exp.dim;

		System.out.println("Feature Start pos:" + pos.getX() + "," + pos.getY() + "," + pos.getZ());
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		if(!spawnedInServer) {
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				spawnPlayer((EntityPlayerMP) player, exp);
				System.out.println("Feature Start pos:" + pos.getX() + "," + pos.getY() + "," + pos.getZ());
			}
			spawnedInServer = true;
		}
			
		if(spawnedInClient) {
			canProceed = true;
			this.complete(exp);
		}
	}
	
	@Override
	public void onPlayerTickUpdate(ExperimentTutorial exp) {
		if(!spawnedInClient) {
			Minecraft.getMinecraft().getRenderViewEntity().rotationPitch = (float) this.lookDir.getX();
			Minecraft.getMinecraft().getRenderViewEntity().rotationYaw = (float) this.lookDir.getY();
			spawnedInClient = true;
			isDirty = true;
		}
	}
	
	
	/**
	 * Used to dimensionally transport a player into the Experiments dimension (dim. 8)
	 * Player randomly is placed within the experiment zone using Math.random().
	 * TODO: spawn players within their "Team Spawn" Zones.
	 * @param player player to be teleported
	 * param y height they should be dropped at.
	 */
	private void spawnPlayer(EntityPlayerMP player, ExperimentTutorial exp){
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player, dim,	
				new PolycraftTeleporter(player.mcServer.worldServerForDimension(dim), (int) this.pos.getX(), (int) this.pos.getY(), (int) this.pos.getZ(),
						(float) this.lookDir.getY(), (float) this.lookDir.getX()));
		
	}

	public BlockPos getLookDir() {
		return lookDir;
	}

	public void setLookDir(BlockPos lookDir) {
		this.lookDir = lookDir;
	}
	
	
	@Override
	public void render(Entity entity) {
		TutorialRender.renderLoadingScreen(entity);
	}
	
	@Override
	public void buildGuiParameters(GuiExpCreator guiDevTool, int x_pos, int y_pos) {
		// TODO Auto-generated method stub
		super.buildGuiParameters(guiDevTool, x_pos, y_pos);
		
		FontRenderer fr = guiDevTool.getFontRenderer();
        
        y_pos += 15;
        
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pitch:"));
        pitchField = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        pitchField.setMaxStringLength(32);
        pitchField.setText(Integer.toString((int)lookDir.getX()));
        pitchField.setTextColor(16777215);
        pitchField.setVisible(true);
        pitchField.setCanLoseFocus(true);
        pitchField.setFocused(false);
        guiDevTool.textFields.add(pitchField);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Yaw:"));
        yawField = new GuiPolyNumField(fr, x_pos + 110, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        yawField.setMaxStringLength(32);
        yawField.setText(Integer.toString((int)lookDir.getY()));
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
		int lookDir[] = {(int)this.lookDir.getX(), (int)this.lookDir.getY(), (int)this.lookDir.getZ()};
		nbt.setIntArray("lookDir",lookDir);
		nbt.setInteger("dim", dim);
		nbt.setBoolean("spawnedInServer", spawnedInServer);
		nbt.setBoolean("spawnedInClient", spawnedInClient);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		int featLookDir[]=nbtFeat.getIntArray("lookDir");
		this.lookDir=new BlockPos(featLookDir[0], featLookDir[1], featLookDir[2]);
		this.dim = nbtFeat.getInteger("dim");
		this.spawnedInServer = nbtFeat.getBoolean("spawnedInServer");
		this.spawnedInClient = nbtFeat.getBoolean("spawnedInClient");
	}
	
}
