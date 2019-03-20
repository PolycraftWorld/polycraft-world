package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.util.Vec3;

public class TutorialFeatureGuide extends TutorialFeature {
	private Vec3 pos2;
	
	//GuiFields for Parameters
	protected GuiPolyNumField xPos2Field, yPos2Field, zPos2Field;

	public TutorialFeatureGuide() {}
	
	public TutorialFeatureGuide(String name, Vec3 pos, Vec3 pos2){
		super(name, pos, Color.YELLOW);
		this.pos2 = pos2;
		super.featureType = TutorialFeatureType.GUIDE;
	}

	@Override
	public Vec3 getPos2() {
		return pos2;
	}

	@Override
	public void setPos2(Vec3 pos2) {
		this.pos2 = pos2;
	}
	
	@Override
	public void updateValues() {
		this.pos2.xCoord = Integer.parseInt(xPos2Field.getText());
		this.pos2.yCoord = Integer.parseInt(yPos2Field.getText());
		this.pos2.zCoord = Integer.parseInt(zPos2Field.getText());
		super.updateValues();
	}
	
	@Override
	public void buildGuiParameters(GuiDevTool guiDevTool, int x_pos, int y_pos) {
		// TODO Auto-generated method stub
		super.buildGuiParameters(guiDevTool, x_pos, y_pos);
		FontRenderer fr = guiDevTool.getFontRenderer();
		y_pos += 15;
		
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pos2"));
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +30, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"X:"));
        xPos2Field = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        xPos2Field.setMaxStringLength(32);
        xPos2Field.setText(Integer.toString((int)pos2.xCoord));
        xPos2Field.setTextColor(16777215);
        xPos2Field.setVisible(true);
        xPos2Field.setCanLoseFocus(true);
        xPos2Field.setFocused(false);
        guiDevTool.textFields.add(xPos2Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Y:"));
        yPos2Field = new GuiPolyNumField(fr, x_pos + 95, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        yPos2Field.setMaxStringLength(32);
        yPos2Field.setText(Integer.toString((int)pos2.yCoord));
        yPos2Field.setTextColor(16777215);
        yPos2Field.setVisible(true);
        yPos2Field.setCanLoseFocus(true);
        yPos2Field.setFocused(false);
        guiDevTool.textFields.add(yPos2Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +140, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Z:"));
        zPos2Field = new GuiPolyNumField(fr, x_pos + 150, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        zPos2Field.setMaxStringLength(32);
        zPos2Field.setText(Integer.toString((int)pos2.zCoord));
        zPos2Field.setTextColor(16777215);
        zPos2Field.setVisible(true);
        zPos2Field.setCanLoseFocus(true);
        zPos2Field.setFocused(false);
        guiDevTool.textFields.add(zPos2Field);
	}
	
	public NBTTagCompound save()
	{
		super.save();
		int pos[] = {(int)this.pos2.xCoord, (int)this.pos2.yCoord, (int)this.pos2.zCoord};
		nbt.setIntArray("pos2",pos);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		int featPos2[]=nbtFeat.getIntArray("pos2");
		this.pos2=Vec3.createVectorHelper(featPos2[0], featPos2[1], featPos2[2]);
	}
}
