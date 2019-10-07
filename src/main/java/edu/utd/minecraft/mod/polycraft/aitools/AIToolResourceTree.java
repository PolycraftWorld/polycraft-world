package edu.utd.minecraft.mod.polycraft.aitools;

import java.util.List;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.client.gui.GuiAITrainingRoom;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonDropDown;
import edu.utd.minecraft.mod.polycraft.client.gui.api.PolycraftGuiScreenBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;

public class AIToolResourceTree extends AIToolResource {
	
	public int treeTypeID;
	public int genType;
	
	public GuiPolyButtonDropDown treeTypeDropDown;
	public GuiPolyButtonDropDown genTypeDropDown;

	//public GuiButton remove;
	
	public enum TreeType{
		OAK,
		SPRUCE,
		BIRCH,
		JUNGLE,
		ACACIA,
		DARK_OAK
	};
	
	public enum GenType{
		CLUSTER,
		CLUSTERS,
		RANDOM
	};

	protected NBTTagCompound nbt = new NBTTagCompound();
	
	public AIToolResourceTree(){
		
		this.treeTypeID=TreeType.OAK.ordinal();
		this.genType=GenType.CLUSTER.ordinal();
		
	}
	
	@Override
	public void addButtons(GuiAITrainingRoom gui,int i, int j)
	{
		this.load(nbt);
		this.treeTypeDropDown = new GuiPolyButtonDropDown(15, i, j, 50, 20,TreeType.values()[this.treeTypeID]);
		this.genTypeDropDown = new GuiPolyButtonDropDown(16, i+70, j, 50, 20,GenType.values()[this.genType]);
		this.remove = new GuiButton(17, i+160, j, 20, 20,"X");
		recDropDownList.add(treeTypeDropDown);
		recDropDownList.add(genTypeDropDown);
		gui.addButton(this.remove);
		gui.addButton(this.treeTypeDropDown);
		gui.addButton(this.genTypeDropDown);
		this.save();
	}
	
	@Override
	public void removeResource(GuiAITrainingRoom gui)
	{
		gui.getButtonList().remove(this.remove);
		gui.getButtonList().remove(this.treeTypeDropDown);
		gui.getButtonList().remove(this.genTypeDropDown);
		gui.recList.remove(this);
	}
	
	
	@Override
	public NBTTagCompound save()
	{
		this.treeTypeID=treeTypeDropDown.getCurrentOpt().ordinal();
		this.genType=genTypeDropDown.getCurrentOpt().ordinal();
		nbt.setInteger("tree", this.treeTypeID);
		nbt.setInteger("gen", this.genType);
		return nbt;
	}
	
	public void load(NBTTagCompound nbtFeat)
	{
		this.treeTypeID=nbtFeat.getInteger("tree");
		this.genType=nbtFeat.getInteger("gen");
	}

}
