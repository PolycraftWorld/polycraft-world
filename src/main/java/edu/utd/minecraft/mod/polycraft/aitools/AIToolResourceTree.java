package edu.utd.minecraft.mod.polycraft.aitools;

import edu.utd.minecraft.mod.polycraft.client.gui.GuiAITrainingRoom;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonDropDown;
import edu.utd.minecraft.mod.polycraft.client.gui.api.PolycraftGuiScreenBase;
import net.minecraft.nbt.NBTTagCompound;

public class AIToolResourceTree extends AIToolResource {
	
	public int treeTypeID;
	public int genType;
	
	public GuiPolyButtonDropDown treeTypeDropDown;
	public GuiPolyButtonDropDown genTypeDropDown;
	
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
		

		
	}
	
	@Override
	public void addButtons(GuiAITrainingRoom gui,int i, int j)
	{
		treeTypeDropDown = new GuiPolyButtonDropDown(15, i, j, 50, 20,TreeType.OAK);
		genTypeDropDown = new GuiPolyButtonDropDown(16, i+70, j, 50, 20,GenType.CLUSTER);
		gui.addButton(treeTypeDropDown);
		gui.addButton(genTypeDropDown);
	}
	
	public void removeResource(GuiAITrainingRoom gui)
	{
		gui.recList.remove(this);
	}
	
	
	public NBTTagCompound save()
	{
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
