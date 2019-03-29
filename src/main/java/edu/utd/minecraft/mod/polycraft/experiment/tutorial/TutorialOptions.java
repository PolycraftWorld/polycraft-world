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

public class TutorialOptions{
	public String name = "";
	public Vec3 pos = Vec3.createVectorHelper(0, 0, 0);
	public Vec3 size = Vec3.createVectorHelper(0, 0, 0);
	public int numTeams = 2, teamSize = 2;
	
	//Gui Parameters
	protected GuiTextField nameField;
	protected GuiPolyNumField xPosField, yPosField, zPosField;

	protected NBTTagCompound nbt = new NBTTagCompound();
	
	public TutorialOptions(){}
	
	public void buildGuiParameters(GuiDevTool guiDevTool, int x_pos, int y_pos) {
		FontRenderer fr = guiDevTool.getFontRenderer();
		
		
	}
	
	public NBTTagCompound save()
	{

		int position1[] = {(int)this.pos.xCoord, (int)this.pos.yCoord, (int)this.pos.zCoord};
		nbt.setIntArray("pos",position1);
		int position2[] = {(int)this.size.xCoord, (int)this.size.yCoord, (int)this.size.zCoord};
		nbt.setIntArray("size",position2);
		nbt.setString("name", this.name);
		nbt.setInteger("numTeams", numTeams);
		nbt.setInteger("teamSize", teamSize);
		return nbt;
	}
	
	public void load(NBTTagCompound nbtFeat)
	{
		int featPos1[]=nbtFeat.getIntArray("pos");
		this.pos=Vec3.createVectorHelper(featPos1[0], featPos1[1], featPos1[2]);
		int featPos2[]=nbtFeat.getIntArray("size");
		this.size=Vec3.createVectorHelper(featPos2[0], featPos2[1], featPos2[2]);
		this.name = nbtFeat.getString("name");
		this.numTeams = nbtFeat.getInteger("numTeams");
		this.teamSize = nbtFeat.getInteger("teamSize");
	}
}
