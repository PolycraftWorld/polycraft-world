package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool.StateEnum;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.util.Vec3;

public class TutorialFeature implements ITutorialFeature{
	private String name;
	private Vec3 pos;
	private Color color;
	protected TutorialFeatureType featureType;
	
	//Gui Parameters
	protected GuiTextField nameField;
	protected GuiPolyNumField xPosField, yPosField, zPosField;

	protected NBTTagCompound nbt = new NBTTagCompound();
	
	public enum TutorialFeatureType{
		GENERIC(TutorialFeature.class.getName()),
		GUIDE(TutorialFeatureGuide.class.getName()),
		INSTRUCTION(TutorialFeatureInstruction.class.getName()),
		START(TutorialFeatureStart.class.getName());
		
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
	
	public TutorialFeature(String name, Vec3 pos, Color c){
		this.name = name;
		this.pos = pos;
		this.color = c;
		this.featureType = TutorialFeatureType.GENERIC;
	}
	
	
	@Override
	public void preInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServerTickUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerTickUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(Entity entity) {
		// TODO Auto-generated method stub
		
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
	
	public void buildGuiParameters(GuiDevTool guiDevTool, int x_pos, int y_pos) {
		FontRenderer fr = guiDevTool.getFontRenderer();
		
		//Name Field
		nameField = new GuiTextField(fr, x_pos + 5, y_pos + 30, (int) (guiDevTool.X_WIDTH * .9), 14);
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
        xPosField.setText(Integer.toString((int)pos.xCoord));
        xPosField.setTextColor(16777215);
        xPosField.setVisible(true);
        xPosField.setCanLoseFocus(true);
        xPosField.setFocused(false);
        guiDevTool.textFields.add(xPosField);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Y:"));
        yPosField = new GuiPolyNumField(fr, x_pos + 95, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        yPosField.setMaxStringLength(32);
        yPosField.setText(Integer.toString((int)pos.yCoord));
        yPosField.setTextColor(16777215);
        yPosField.setVisible(true);
        yPosField.setCanLoseFocus(true);
        yPosField.setFocused(false);
        guiDevTool.textFields.add(yPosField);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +140, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Z:"));
        zPosField = new GuiPolyNumField(fr, x_pos + 150, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        zPosField.setMaxStringLength(32);
        zPosField.setText(Integer.toString((int)pos.zCoord));
        zPosField.setTextColor(16777215);
        zPosField.setVisible(true);
        zPosField.setCanLoseFocus(true);
        zPosField.setFocused(false);
        guiDevTool.textFields.add(zPosField);
	}
	
	public NBTTagCompound save()
	{

		int pos[] = {(int)this.pos.xCoord, (int)this.pos.yCoord, (int)this.pos.zCoord};
		nbt.setIntArray("pos",pos);
		nbt.setString("name", this.name);
		nbt.setInteger("color", this.color.getRGB());
		nbt.setString("type", featureType.name());
		return nbt;
	}
	
	public void load(NBTTagCompound nbtFeat)
	{
		int featPos[]=nbtFeat.getIntArray("pos");
		this.name = nbtFeat.getString("name");
		this.color = new Color(nbtFeat.getInteger("color"));
		this.pos=Vec3.createVectorHelper(featPos[0], featPos[1], featPos[2]);
		this.featureType = TutorialFeatureType.valueOf(nbtFeat.getString("type"));
	}
}
