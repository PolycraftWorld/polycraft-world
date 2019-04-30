package edu.utd.minecraft.mod.polycraft.client.gui.experiment;

import java.awt.Color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment1PlayerCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentFlatCTB;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

public class ExperimentDef{
	protected String name;
	protected int teamCount;
	protected int playersPerTeam;
	protected String expClass;
	protected Vec3 pos;
	protected Color color;
	protected ExperimentType expType;	//used when loading/saving
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
	
	
	public enum ExperimentType{
		CTB_FLAT_1_PLAYER(Experiment1PlayerCTB.class.getName()),
		CTB_STOOP(ExperimentCTB.class.getName()),
		CTB_FLAT(ExperimentFlatCTB.class.getName());
		
		public String className;
		
		ExperimentType(String className) {
			this.className = className;
		}
		
		public ExperimentType next() {
		    if (ordinal() == values().length - 1)
		    	return values()[0];
		    return values()[ordinal() + 1];
		}
		
		public ExperimentType previous() {
		    if (ordinal() == 0)
		    	return values()[values().length - 1];
		    return values()[ordinal() - 1];
		}
	}
	
	public ExperimentDef(){}
	
	
	// addExp type schem name teams playersPerTeam
	public ExperimentDef(String expClass, String name, int teamCount, int playersPerTeam ) {
		this.expClass = expClass;
		this.name = name;
		this.teamCount = teamCount;
		this.playersPerTeam = playersPerTeam;
	}

	public ExperimentType getExpType() {
		return expType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vec3 getPos() {
		return pos;
	}
	
	public Vec3 getPos2() {
		return pos;
	}

	public void setPos(Vec3 pos) {
		this.pos = pos;
	}
	
	public void setPos2(Vec3 pos) {
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
		this.pos.xCoord = Integer.parseInt(xPosField.getText());
		this.pos.yCoord = Integer.parseInt(yPosField.getText());
		this.pos.zCoord = Integer.parseInt(zPosField.getText());
	}
	
	public void buildGuiParameters(GuiExperimentManager guiExpManager , int x_pos, int y_pos) {
		FontRenderer fr = guiExpManager.getFontRenderer();
		
		//Name Field
		nameField = new GuiTextField(fr, x_pos + 5, y_pos + 30, (int) (guiExpManager.X_WIDTH * .9), 14);
		nameField.setMaxStringLength(32);
		nameField.setText(name);
		nameField.setTextColor(16777215);
		nameField.setVisible(true);
		nameField.setCanLoseFocus(true);
		nameField.setFocused(true);
        guiExpManager.textFields.add(nameField);	//add name field to textfields (This is the list that GuiDevTool uses to render textFields)
        //add some labels for position fields 
        guiExpManager.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pos"));
        guiExpManager.labels.add(new GuiPolyLabel(fr, x_pos +30, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"X:"));
        //add position text fields
        xPosField = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiExpManager.X_WIDTH * .2), 10);
        xPosField.setMaxStringLength(32);
        xPosField.setText(Integer.toString((int)pos.xCoord));
        xPosField.setTextColor(16777215);
        xPosField.setVisible(true);
        xPosField.setCanLoseFocus(true);
        xPosField.setFocused(false);
        guiExpManager.textFields.add(xPosField);
        guiExpManager.labels.add(new GuiPolyLabel(fr, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Y:"));
        yPosField = new GuiPolyNumField(fr, x_pos + 95, y_pos + 49, (int) (guiExpManager.X_WIDTH * .2), 10);
        yPosField.setMaxStringLength(32);
        yPosField.setText(Integer.toString((int)pos.yCoord));
        yPosField.setTextColor(16777215);
        yPosField.setVisible(true);
        yPosField.setCanLoseFocus(true);
        yPosField.setFocused(false);
        guiExpManager.textFields.add(yPosField);
        guiExpManager.labels.add(new GuiPolyLabel(fr, x_pos +140, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Z:"));
        zPosField = new GuiPolyNumField(fr, x_pos + 150, y_pos + 49, (int) (guiExpManager.X_WIDTH * .2), 10);
        zPosField.setMaxStringLength(32);
        zPosField.setText(Integer.toString((int)pos.zCoord));
        zPosField.setTextColor(16777215);
        zPosField.setVisible(true);
        zPosField.setCanLoseFocus(true);
        zPosField.setFocused(false);
        guiExpManager.textFields.add(zPosField);
	}
	
	public NBTTagCompound save()
	{
		nbt = new NBTTagCompound();	//erase current nbt so we don't get duplicates?

		int pos[] = {(int)this.pos.xCoord, (int)this.pos.yCoord, (int)this.pos.zCoord};
		nbt.setIntArray("pos",pos);
		nbt.setString("name", this.name);
		nbt.setInteger("color", this.color.getRGB());
		nbt.setString("type", expType.name());
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
		this.pos=Vec3.createVectorHelper(featPos[0], featPos[1], featPos[2]);
		this.expType = ExperimentType.valueOf(nbtFeat.getString("type"));
		this.canProceed = nbtFeat.getBoolean("canProceed");
		this.isDone = nbtFeat.getBoolean("isDone");
		this.completionTime = nbtFeat.getLong("completionTime");
	}

}
