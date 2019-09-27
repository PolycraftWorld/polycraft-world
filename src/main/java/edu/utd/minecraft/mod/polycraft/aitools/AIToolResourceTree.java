package edu.utd.minecraft.mod.polycraft.aitools;

import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonDropDown;
import edu.utd.minecraft.mod.polycraft.client.gui.api.PolycraftGuiScreenBase;
import net.minecraft.nbt.NBTTagCompound;

public class AIToolResourceTree extends AIToolResource {
	
	public int treeTypeID;
	public int genType;
	
	GuiPolyButtonDropDown treeTypeDropDown;
	GuiPolyButtonDropDown genTypeDropDown;
	
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
	
	public void addResource(PolycraftGuiScreenBase gui,int i, int j)
	{
		treeTypeDropDown = new GuiPolyButtonDropDown(15, i, j, 60, 20,TreeType.OAK);
		genTypeDropDown = new GuiPolyButtonDropDown(16, i, j, 60, 20,GenType.CLUSTER);
		gui.addButton(treeTypeDropDown);
		gui.addButton(genTypeDropDown);
	}
	
	public void removeResource(PolycraftGuiScreenBase gui)
	{
		gui.removeButton(treeTypeDropDown);
		gui.removeButton(genTypeDropDown);
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
