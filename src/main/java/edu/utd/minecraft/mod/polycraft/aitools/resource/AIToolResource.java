package edu.utd.minecraft.mod.polycraft.aitools.resource;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.client.gui.GuiAITrainingRoom;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonDropDown;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;

public class AIToolResource {
	
	public GuiButton remove;
	public List<GuiPolyButtonDropDown> recDropDownList = Lists.<GuiPolyButtonDropDown>newArrayList();
	
	
	public void addButtons(GuiAITrainingRoom gui,int i, int j)
	{
		
	}

	public void removeResource(GuiAITrainingRoom gui) {
		// TODO Auto-generated method stub
		
	}

	public NBTTagCompound save() {
		return null;
		// TODO Auto-generated method stub
		
	}

}
