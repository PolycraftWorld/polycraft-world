package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.util.ArrayList;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.aitools.BotAPI;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonCycle;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialFeatureStart extends TutorialFeature{
	private BlockPos lookDir, pos2;	//lookDir xCoord == pitch; yCoord = yaw
	boolean spawnRand;
	
	//working parameters
	private boolean spawnedInServer = false, spawnedInClient = false;
	private int dim = 0;
	private boolean isAI = false;
	
	//Gui Parameters
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField pitchField, yawField, xPos2Field, yPos2Field, zPos2Field;
	@SideOnly(Side.CLIENT)
	protected GuiPolyButtonCycle<GuiPolyButtonCycle.Toggle> toggleRandomSpawn;
	
	public TutorialFeatureStart() {}
	
	public TutorialFeatureStart(String name, BlockPos pos, BlockPos lookDir){
		super(name, pos, Color.CYAN);
		this.lookDir = lookDir;
		this.pos2 = pos;
		this.spawnRand = false;
		this.featureType = TutorialFeatureType.START;
	}
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
		dim = exp.dim;
		isAI = BotAPI.apiRunning.get();
		System.out.println("Feature Start pos:" + pos.getX() + "," + pos.getY() + "," + pos.getZ());
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		if(!spawnedInServer) {
			for(EntityPlayerMP player: exp.scoreboard.getPlayersAsEntity()) {
				spawnPlayer(player, exp);
				player.inventory.clear(); 	//clear player inventory when spawning into experiment
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
	public void onClientTickUpdate(ExperimentTutorial exp) {
		if(!spawnedInClient) {
			Minecraft.getMinecraft().thePlayer.setPositionAndRotation(((int)this.pos.getX()) + 0.5, ((int)this.pos.getY()) + 0.5, ((int)this.pos.getZ()) + 0.5,
					(float) this.lookDir.getY(), (float) this.lookDir.getX());
			spawnedInClient = true;
			isDirty = true;
			//TODO: if using an AI bot, send response telling AI client, loading complete
			
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
		if(player.dimension != dim) {
			player.setPositionAndRotation(((int)this.pos.getX()) + 0.5, ((int)this.pos.getY()) + 0.5, ((int)this.pos.getZ()) + 0.5,
					(float) this.lookDir.getY(), (float) this.lookDir.getX());
			player.mcServer.getConfigurationManager().transferPlayerToDimension(player, dim,	
				new PolycraftTeleporter(player.mcServer.worldServerForDimension(dim), (int) this.pos.getX(), (int) this.pos.getY(), (int) this.pos.getZ(),
						(float) this.lookDir.getY(), (float) this.lookDir.getX()));
		}
		else
			player.setPositionAndRotation(((int)this.pos.getX()) + 0.5, ((int)this.pos.getY()) + 0.5, ((int)this.pos.getZ()) + 0.5,
						(float) this.lookDir.getY(), (float) this.lookDir.getX());
	}

	public BlockPos getLookDir() {
		return lookDir;
	}

	public void setLookDir(BlockPos lookDir) {
		this.lookDir = lookDir;
	}
	
	
	@Override
	public void render(Entity entity) {
		//TutorialRender.renderLoadingScreen(entity);
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
        
        y_pos += 15;
        toggleRandomSpawn = new GuiPolyButtonCycle<GuiPolyButtonCycle.Toggle>(
        		guiDevTool.buttonCount++, x_pos + 10, y_pos + 45, (int) (guiDevTool.X_WIDTH * .9), 14, 
        		"Random Spawn",  GuiPolyButtonCycle.Toggle.fromBool(spawnRand));
        guiDevTool.addBtn(toggleRandomSpawn);
        y_pos += 15;
        //Add random spawn area setting
        //add some labels for position fields 
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pos"));
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +30, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"X:"));
        //add position text fields
        xPos2Field = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        xPos2Field.setMaxStringLength(32);
        xPos2Field.setText(Integer.toString((int)pos2.getX()));
        xPos2Field.setTextColor(16777215);
        xPos2Field.setVisible(true);
        xPos2Field.setCanLoseFocus(true);
        xPos2Field.setFocused(false);
        guiDevTool.textFields.add(xPos2Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Y:"));
        yPos2Field = new GuiPolyNumField(fr, x_pos + 95, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        yPos2Field.setMaxStringLength(32);
        yPos2Field.setText(Integer.toString((int)pos2.getY()));
        yPos2Field.setTextColor(16777215);
        yPos2Field.setVisible(true);
        yPos2Field.setCanLoseFocus(true);
        yPos2Field.setFocused(false);
        guiDevTool.textFields.add(yPos2Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +140, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Z:"));
        zPos2Field = new GuiPolyNumField(fr, x_pos + 150, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        zPos2Field.setMaxStringLength(32);
        zPos2Field.setText(Integer.toString((int)pos2.getZ()));
        zPos2Field.setTextColor(16777215);
        zPos2Field.setVisible(true);
        zPos2Field.setCanLoseFocus(true);
        zPos2Field.setFocused(false);
        guiDevTool.textFields.add(zPos2Field);
        
	}
	
	@Override
	public void updateValues() {
		this.pos2 = new BlockPos(Integer.parseInt(xPos2Field.getText())
				,Integer.parseInt(yPos2Field.getText())
				,Integer.parseInt(zPos2Field.getText()));
		spawnRand = GuiPolyButtonCycle.Toggle.toBool(toggleRandomSpawn.getCurrentOption());
		super.updateValues();
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		int lookDir[] = {(int)this.lookDir.getX(), (int)this.lookDir.getY(), (int)this.lookDir.getZ()};
		nbt.setIntArray("lookDir",lookDir);
		int pos2[] = {(int)this.pos2.getX(), (int)this.pos2.getY(), (int)this.pos2.getZ()};
		nbt.setIntArray("pos2",pos2);
		nbt.setInteger("dim", dim);
		nbt.setBoolean("spawnedInServer", spawnedInServer);
		nbt.setBoolean("spawnedInClient", spawnedInClient);
		nbt.setBoolean("spawnRand", spawnRand);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		int featLookDir[]=nbtFeat.getIntArray("lookDir");
		this.lookDir=new BlockPos(featLookDir[0], featLookDir[1], featLookDir[2]);
		int featPos2[]=nbtFeat.getIntArray("pos2");
		this.pos2=new BlockPos(featPos2[0], featPos2[1], featPos2[2]);
		this.dim = nbtFeat.getInteger("dim");
		this.spawnedInServer = spawnedInServer? spawnedInServer : nbtFeat.getBoolean("spawnedInServer");
		this.spawnedInClient = nbtFeat.getBoolean("spawnedInClient");
		this.spawnRand = nbtFeat.getBoolean("spawnRand");
	}
	
	@Override
	public JsonObject saveJson()
	{
		super.saveJson();
		jobj.add("pos2", blockPosToJsonArray(pos2));
		jobj.add("lookDir", blockPosToJsonArray(lookDir));
		jobj.addProperty("dim", dim);
		jobj.addProperty("spawnedInServer", spawnedInServer);
		jobj.addProperty("spawnedInClient", spawnedInClient);
		jobj.addProperty("spawnRand", spawnRand);
		return jobj;
	}
	
	@Override
	public void loadJson(JsonObject featJson)
	{
		super.loadJson(featJson);
		this.pos2 = blockPosFromJsonArray(featJson.get("pos2").getAsJsonArray());
		this.lookDir = blockPosFromJsonArray(featJson.get("lookDir").getAsJsonArray());
		this.dim = featJson.get("dim").getAsInt();
		this.spawnedInServer = featJson.get("spawnedInServer").getAsBoolean();
		this.spawnedInClient = featJson.get("spawnedInClient").getAsBoolean();
		this.spawnRand = featJson.get("spawnRand").getAsBoolean();
	}
	
}
