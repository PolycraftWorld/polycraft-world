package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool.StateEnum;
import edu.utd.minecraft.mod.polycraft.minigame.BoundingBox;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;

public class TutorialFeature implements ITutorialFeature{
	protected String name;
	protected BlockPos pos;
	protected Color color;
	protected TutorialFeatureType featureType;	//used when loading/saving
	protected long completionTime;
	protected boolean isDone = false;		//remove from active features when true
	protected boolean canProceed = false;	//Stop loading new features until true
	protected boolean isDirty = false;		//used to determine if an feature update packet needs to be send to players/server
	
	
	// Rendering variables.
	private float lineWidth = 4; // The width of rendered bounding box lines.
	protected double x1, y1, z1, x2, y2, z2; // The (x, z) coordinates of the northwest and southeast corners.
	private int xRange, yRange, zRange, hRange;
	private float xSpacing, ySpacing, zSpacing;
	
	//Gui Parameters
	@SideOnly(Side.CLIENT)
	protected GuiTextField nameField;
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField xPosField, yPosField, zPosField;
	

	protected NBTTagCompound nbt = new NBTTagCompound();
	
	public enum TutorialFeatureType{
		GENERIC(TutorialFeature.class.getName()),
		GUIDE(TutorialFeatureGuide.class.getName()),
		INSTRUCTION(TutorialFeatureInstruction.class.getName()),
		START(TutorialFeatureStart.class.getName()),
		SCORE(TutorialFeatureScore.class.getName()),
		END(TutorialFeatureEnd.class.getName()),
		GROUP(TutorialFeatureGroup.class.getName());
		public String className;
		
		TutorialFeatureType(String className) {
			this.className = className;
		}
		
		public TutorialFeatureType next() {
		    if (ordinal() == values().length - 1)
		    	return values()[0];
		    return values()[ordinal() + 1];
		}
		
		public TutorialFeatureType previous() {
		    if (ordinal() == 0)
		    	return values()[values().length - 1];
		    return values()[ordinal() - 1];
		}
	}
	
	public TutorialFeature(){}
	
	public TutorialFeature(String name, BlockPos pos, Color c){
		this.name = name;
		this.pos = pos;
		this.color = c;
		this.featureType = TutorialFeatureType.GENERIC;
	}
	
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		pos = pos.add(exp.posOffset.xCoord, exp.posOffset.yCoord, exp.posOffset.zCoord);
		
	}

	@Override
	public void init() {
		x1 = pos.getX();
		x2 = pos.getX() + Integer.signum((int)pos.getX());	//increase value magnitude
		y1 = pos.getY();
		y2 = pos.getY() + 1;	//Shouldn't have to worry if y coord is negative
		z1 = pos.getZ();
		z2 = pos.getZ() + Integer.signum((int)pos.getZ());	//increase value magnitude
		
		xRange = (int) (x2 - x1);
		yRange = (int) (y2 - y1);
		zRange = (int) (z2 - z1);
		hRange = (int) (Math.max(y1, y2) - Math.min(y1, y2));
		xSpacing = (float) 0.5;
		ySpacing = (float) 0.5;
		zSpacing = (float) 0.5;
	}

	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
			if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.fromBounds(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
					Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
				canProceed = true;
				complete(exp);
				
			}
		}
	}
	
	public void complete(ExperimentTutorial exp)
	{
		completionTime=exp.world.getTotalWorldTime();
		isDone = true;
//		for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
//			player.addChatMessage(new ChatComponentText("Time: "+exp.world.getTotalWorldTime()));
//		}
	}
	
	public void complete(Entity entity)
	{
		completionTime=entity.worldObj.getTotalWorldTime();
		isDone = true;
//		EntityPlayer player=(EntityPlayer)entity;
//		player.addChatMessage(new ChatComponentText("Time: "+entity.worldObj.getTotalWorldTime()));
	
	}
	
	@Override
	public void onClientTickUpdate(ExperimentTutorial exp) {
		// TODO Auto-generated method stub
		if(y2 == 0) {
			this.init();
		}
	}

	@Override
	public void end(ExperimentTutorial exp) {
		isDone = true;
	}

	@Override
	public void render(Entity entity) {
		
		if (entity.worldObj.isRemote) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glLineWidth(lineWidth);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F,
					color.getAlpha() / 255F); // Set color to specified color.

			float offset = (entity.ticksExisted % 20) / 20F; // For render animation.

			
			for (int i = 0; i < hRange; i++) {
				float offset3 = i + offset;
				float offset4 = i + 1 - offset;
				double x1o3 = x1 + xSpacing * offset3;
				double x1o4 = x1 + xSpacing * offset4;
				double x2o3 = x2 - xSpacing * offset3;
				double x2o4 = x2 - xSpacing * offset4;
				double z1o3 = z1 + zSpacing * offset3;
				double z1o4 = z1 + zSpacing * offset4;
				double z2o3 = z2 - zSpacing * offset3;
				double z2o4 = z2 - zSpacing * offset4;
				GL11.glVertex3d(x1o4, y2, z1o4);
				GL11.glVertex3d(x2o4, y2, z1o4);
				GL11.glVertex3d(x2o4, y2, z1o4);
				GL11.glVertex3d(x2o4, y2, z2o4);
				GL11.glVertex3d(x2o4, y2, z2o4);
				GL11.glVertex3d(x1o4, y2, z2o4);
				GL11.glVertex3d(x1o4, y2, z2o4);
				GL11.glVertex3d(x1o4, y2, z1o4);

				GL11.glVertex3d(x1o3, y1, z1o3);
				GL11.glVertex3d(x2o3, y1, z1o3);
				GL11.glVertex3d(x2o3, y1, z1o3);
				GL11.glVertex3d(x2o3, y1, z2o3);
				GL11.glVertex3d(x2o3, y1, z2o3);
				GL11.glVertex3d(x1o3, y1, z2o3);
				GL11.glVertex3d(x1o3, y1, z2o3);
				GL11.glVertex3d(x1o3, y1, z1o3);
			}
			

			// Sides of bounding box render.
			for (int i = 0; i < yRange; i++) {
				float height = (float) (y2 - ySpacing * (i + offset));
				GL11.glVertex3d(x1, height, z1);
				GL11.glVertex3d(x2, height, z1);

				GL11.glVertex3d(x2, height, z1);
				GL11.glVertex3d(x2, height, z2);

				GL11.glVertex3d(x2, height, z2);
				GL11.glVertex3d(x1, height, z2);

				GL11.glVertex3d(x1, height, z2);
				GL11.glVertex3d(x1, height, z1);
			}

			GL11.glEnd();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
		
	}
	
	@Override
	public void renderScreen(Entity entity) {
		if (entity.worldObj.isRemote) {
			
		}
	}

	public TutorialFeatureType getFeatureType() {
		return featureType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BlockPos getPos() {
		return pos;
	}
	
	public BlockPos getPos2() {
		return pos;
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}
	
	public void setPos2(BlockPos pos) {
		this.pos = pos;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	
	/*
	 * Called to update values based on edits done in GuiDevTool
	 * Values are pulled from parameter fields
	 */
	public void updateValues() {
		this.name = nameField.getText();
		this.pos = new BlockPos(Integer.parseInt(xPosField.getText())
							,Integer.parseInt(yPosField.getText())
							,Integer.parseInt(zPosField.getText()));
	}
	
	public void buildGuiParameters(GuiExpCreator guiDevTool, int x_pos, int y_pos) {
		FontRenderer fr = guiDevTool.getFontRenderer();
		
		//Name Field
		nameField = new GuiTextField(200,fr, x_pos + 5, y_pos + 30, (int) (guiDevTool.X_WIDTH * .9), 14);
		nameField.setMaxStringLength(32);
		nameField.setText(name);
		nameField.setTextColor(16777215);
		nameField.setVisible(true);
		nameField.setCanLoseFocus(true);
		nameField.setFocused(true);
        guiDevTool.textFields.add(nameField);	//add name field to textfields (This is the list that GuiDevTool uses to render textFields)
        //add some labels for position fields 
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pos"));
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +30, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"X:"));
        //add position text fields
        xPosField = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        xPosField.setMaxStringLength(32);
        xPosField.setText(Integer.toString((int)pos.getX()));
        xPosField.setTextColor(16777215);
        xPosField.setVisible(true);
        xPosField.setCanLoseFocus(true);
        xPosField.setFocused(false);
        guiDevTool.textFields.add(xPosField);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Y:"));
        yPosField = new GuiPolyNumField(fr, x_pos + 95, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        yPosField.setMaxStringLength(32);
        yPosField.setText(Integer.toString((int)pos.getY()));
        yPosField.setTextColor(16777215);
        yPosField.setVisible(true);
        yPosField.setCanLoseFocus(true);
        yPosField.setFocused(false);
        guiDevTool.textFields.add(yPosField);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +140, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Z:"));
        zPosField = new GuiPolyNumField(fr, x_pos + 150, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        zPosField.setMaxStringLength(32);
        zPosField.setText(Integer.toString((int)pos.getZ()));
        zPosField.setTextColor(16777215);
        zPosField.setVisible(true);
        zPosField.setCanLoseFocus(true);
        zPosField.setFocused(false);
        guiDevTool.textFields.add(zPosField);
	}
	
	public NBTTagCompound save()
	{
		nbt = new NBTTagCompound();	//erase current nbt so we don't get duplicates?

		int pos[] = {this.pos.getX(), this.pos.getY(), this.pos.getZ()};
		nbt.setIntArray("pos",pos);
		nbt.setString("name", this.name);
		nbt.setInteger("color", this.color.getRGB());
		nbt.setString("type", featureType.name());
		nbt.setBoolean("canProceed", canProceed);
		nbt.setBoolean("isDone", isDone);
		nbt.setLong("completionTime", completionTime);
		return nbt;
	}
	
	public void load(NBTTagCompound nbtFeat)
	{
		int featPos[]=nbtFeat.getIntArray("pos");
		this.name = nbtFeat.getString("name");
		this.color = new Color(nbtFeat.getInteger("color"));
		this.pos=new BlockPos(featPos[0], featPos[1], featPos[2]);
		this.featureType = TutorialFeatureType.valueOf(nbtFeat.getString("type"));
		this.canProceed = nbtFeat.getBoolean("canProceed");
		this.isDone = nbtFeat.getBoolean("isDone");
		this.completionTime = nbtFeat.getLong("completionTime");
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public boolean canProceed() {
		return canProceed;
	}
}
