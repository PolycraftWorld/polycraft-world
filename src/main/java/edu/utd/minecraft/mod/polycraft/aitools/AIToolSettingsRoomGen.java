package edu.utd.minecraft.mod.polycraft.aitools;

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

public class AIToolSettingsRoomGen{

	public int width;
	public int length;
	public int height;
	public int blockTypeID;
	public boolean walls;
	

	protected NBTTagCompound nbt = new NBTTagCompound();
	
	public AIToolSettingsRoomGen(){}
	
	
	public NBTTagCompound save()
	{

		nbt.setInteger("width", this.width);
		nbt.setInteger("length", this.length);
		nbt.setInteger("height", this.height);
		nbt.setInteger("block", this.blockTypeID);
		nbt.setBoolean("wall", this.walls);
		return nbt;
	}
	
	public void load(NBTTagCompound nbtFeat)
	{
		this.width=nbtFeat.getInteger("width");
		this.length=nbtFeat.getInteger("length");
		this.height=nbtFeat.getInteger("height");
		this.blockTypeID=nbtFeat.getInteger("block");
		this.walls=nbtFeat.getBoolean("wall");
	}
}
