package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.nio.charset.StandardCharsets;

import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool.StateEnum;
import edu.utd.minecraft.mod.polycraft.util.Format;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialOptions{
	public String name = "";
	public BlockPos pos = new BlockPos(0, 0, 0);
	public BlockPos pos2 = new BlockPos(0, 0, 0);
	public int numTeams = 1, teamSize = 1;
	public boolean hideGui = false;
	public boolean outputScreen = false;
	public long seed = 0;
	
	//Gui Parameters
	@SideOnly(Side.CLIENT)
	protected GuiTextField nameField;
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField xPosField, yPosField, zPosField, xPos2Field, yPos2Field, zPos2Field, seedField;
	@SideOnly(Side.CLIENT)
	protected GuiPolyButtonCycle<GuiPolyButtonCycle.Toggle> btnHideGui, btnScreen;

	protected NBTTagCompound nbt = new NBTTagCompound();
	
	public TutorialOptions(){}
	
	public void buildGuiParameters(GuiExpCreator guiDevTool, int x_pos, int y_pos) {
		FontRenderer fr = guiDevTool.getFontRenderer();
		
//		//Name Field
//		nameField = new GuiTextField(200,fr, x_pos + 5, y_pos + 30, (int) (guiDevTool.X_WIDTH * .9), 14);
//		nameField.setMaxStringLength(32);
//		nameField.setText(name);
//		nameField.setTextColor(16777215);
//		nameField.setVisible(true);
//		nameField.setCanLoseFocus(true);
//		nameField.setFocused(true);
//        guiDevTool.textFields.add(nameField);	//add name field to textfields (This is the list that GuiDevTool uses to render textFields)
		
		y_pos += 2;
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
        
        y_pos += 15;
		
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pos2"));
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +30, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"X:"));
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
        
        y_pos += 20;
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"HideGUI: "));
        btnHideGui = new GuiPolyButtonCycle<GuiPolyButtonCycle.Toggle>(
        		guiDevTool.buttonCount + 1, x_pos + 50, y_pos + 45, (int) (guiDevTool.X_WIDTH * .6), 20, 
        		"Type",  GuiPolyButtonCycle.Toggle.fromBool(hideGui));
        guiDevTool.addBtn(btnHideGui);
        
        y_pos += 20;
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Screen: "));
        btnScreen = new GuiPolyButtonCycle<GuiPolyButtonCycle.Toggle>(
        		guiDevTool.buttonCount + 1, x_pos + 50, y_pos + 45, (int) (guiDevTool.X_WIDTH * .6), 20, 
        		"Type",  GuiPolyButtonCycle.Toggle.fromBool(outputScreen));
        guiDevTool.addBtn(btnScreen);
        
        y_pos += 20;
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Seed: "));
        seedField = new GuiPolyNumField(fr, x_pos + 50, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        seedField.setMaxStringLength(32);
        seedField.setText(Long.toString(seed));
        seedField.setTextColor(16777215);
        seedField.setVisible(true);
        seedField.setCanLoseFocus(true);
        seedField.setFocused(false);
        guiDevTool.textFields.add(seedField);
	}
	
	public void updateValues() {
		this.pos=new BlockPos(xPosField.getNum(), yPosField.getNum(), zPosField.getNum());
		this.pos2=new BlockPos(xPos2Field.getNum(), yPos2Field.getNum(), zPos2Field.getNum());
		this.seed = seedField.getNum();
		this.hideGui = GuiPolyButtonCycle.Toggle.toBool(btnHideGui.getCurrentOption());
		this.outputScreen = GuiPolyButtonCycle.Toggle.toBool(btnScreen.getCurrentOption());
	}
	
	public NBTTagCompound save()
	{

		int position1[] = {(int)this.pos.getX(), (int)this.pos.getY(), (int)this.pos.getZ()};
		nbt.setIntArray("pos",position1);
		int position2[] = {(int)this.pos2.getX(), (int)this.pos2.getY(), (int)this.pos2.getZ()};
		nbt.setIntArray("size",position2);
		nbt.setString("name", this.name);
		nbt.setInteger("numTeams", numTeams);
		nbt.setInteger("teamSize", teamSize);
		nbt.setLong("seed", seed);
		nbt.setBoolean("hideGUI", hideGui);
		nbt.setBoolean("outputScreen", outputScreen);
		return nbt;
	}
	
	public void load(NBTTagCompound nbtFeat)
	{
		int featPos1[]=nbtFeat.getIntArray("pos");
		this.pos=new BlockPos(featPos1[0], featPos1[1], featPos1[2]);
		int featPos2[]=nbtFeat.getIntArray("size");
		this.pos2=new BlockPos(featPos2[0], featPos2[1], featPos2[2]);
		this.name = nbtFeat.getString("name");
		this.numTeams = nbtFeat.getInteger("numTeams");
		this.teamSize = nbtFeat.getInteger("teamSize");
		this.seed = nbtFeat.getLong("seed");
		this.hideGui = nbtFeat.getBoolean("hideGUI");
		this.outputScreen = nbtFeat.getBoolean("outputScreen");
	}
	
    public void fromBytes(ByteBuf buf)
    {
    	name = StandardCharsets.UTF_8.decode(buf.readBytes(buf.readInt()).nioBuffer()).toString();
    	pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    	pos2 = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    	numTeams = buf.readInt();
    	teamSize = buf.readInt();
    	seed = buf.readLong();
    	hideGui = buf.readBoolean();
    	outputScreen = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf)
    {
    	buf.writeInt(name.getBytes().length);
    	buf.writeBytes(name.getBytes());
    	buf.writeInt(pos.getX());
    	buf.writeInt(pos.getY());
    	buf.writeInt(pos.getZ());
    	buf.writeInt(pos2.getX());
    	buf.writeInt(pos2.getY());
    	buf.writeInt(pos2.getZ());
    	buf.writeInt(numTeams);
    	buf.writeInt(teamSize);
    	buf.writeLong(seed);
    	buf.writeBoolean(hideGui);
    	buf.writeBoolean(outputScreen);
    	
    }
}
