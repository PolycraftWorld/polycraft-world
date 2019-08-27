package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool.StateEnum;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class TutorialOptions{
	public String name = "";
	public BlockPos pos = new BlockPos(0, 0, 0);
	public BlockPos size = new BlockPos(0, 0, 0);
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

		int position1[] = {(int)this.pos.getX(), (int)this.pos.getY(), (int)this.pos.getZ()};
		nbt.setIntArray("pos",position1);
		int position2[] = {(int)this.size.getX(), (int)this.size.getY(), (int)this.size.getZ()};
		nbt.setIntArray("size",position2);
		nbt.setString("name", this.name);
		nbt.setInteger("numTeams", numTeams);
		nbt.setInteger("teamSize", teamSize);
		return nbt;
	}
	
	public void load(NBTTagCompound nbtFeat)
	{
		int featPos1[]=nbtFeat.getIntArray("pos");
		this.pos=new BlockPos(featPos1[0], featPos1[1], featPos1[2]);
		int featPos2[]=nbtFeat.getIntArray("size");
		this.size=new BlockPos(featPos2[0], featPos2[1], featPos2[2]);
		this.name = nbtFeat.getString("name");
		this.numTeams = nbtFeat.getInteger("numTeams");
		this.teamSize = nbtFeat.getInteger("teamSize");
	}
}
