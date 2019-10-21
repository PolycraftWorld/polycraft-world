package edu.utd.minecraft.mod.polycraft.aitools;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.client.gui.GuiAITrainingRoom;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonDropDown;
import edu.utd.minecraft.mod.polycraft.client.gui.api.PolycraftGuiScreenBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class AIToolResourceTree extends AIToolResource {
	
	public int treeTypeID;
	public int genType;
	
	public List<List<Boolean>> booleanTable= new ArrayList<List<Boolean>>();
	
	public GuiPolyButtonDropDown treeTypeDropDown;
	public GuiPolyButtonDropDown genTypeDropDown;
	
	public GuiPolyButtonCheckTable locationCheckTable;
	
	public int x,y;

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
		this.booleanTable=new ArrayList<List<Boolean>>();
		
	}
	
	@Override
	public void addButtons(GuiAITrainingRoom gui,int i, int j)
	{
		this.x=i;
		this.y=j;
	
		this.load(nbt);
		this.treeTypeDropDown = new GuiPolyButtonDropDown(15, i, j, 50, 20,TreeType.values()[this.treeTypeID]);
		this.genTypeDropDown = new GuiPolyButtonDropDown(16, i+70, j, 50, 20,GenType.values()[this.genType]);
		if(this.genTypeDropDown.displayString.contains("CLUSTER"))
		{
			addCheckTable(gui, i, j);
		}
		this.remove = new GuiButton(17, i+200, j, 20, 20,"X");
		recDropDownList.add(treeTypeDropDown);
		recDropDownList.add(genTypeDropDown);
		gui.addButton(this.remove);
		gui.addButton(this.treeTypeDropDown);
		gui.addButton(this.genTypeDropDown);
		this.save();
	}
	
	public void addCheckTable(GuiAITrainingRoom gui,int i, int j)
	{
		this.load(nbt);
		this.locationCheckTable = new GuiPolyButtonCheckTable(20,i+125,j-10,gui.AITool.getWidth(),gui.AITool.getLength(),this.booleanTable);
		gui.addBtn(locationCheckTable);
		this.save();
	}
	
	@Override
	public void removeResource(GuiAITrainingRoom gui)
	{
		gui.getButtonList().remove(this.remove);
		gui.getButtonList().remove(this.treeTypeDropDown);
		gui.getButtonList().remove(this.genTypeDropDown);
		if(this.genTypeDropDown.displayString.contains("CLUSTER"))
		{
			gui.getButtonList().remove(this.locationCheckTable);
		}
		gui.recList.remove(this);
	}
	
	
	@Override
	public NBTTagCompound save()
	{
		this.treeTypeID=treeTypeDropDown.getCurrentOpt().ordinal();
		this.genType=genTypeDropDown.getCurrentOpt().ordinal();
		nbt.setInteger("tree", this.treeTypeID);
		nbt.setInteger("gen", this.genType);
		int c=0;
		NBTTagList nbtCheckTable = new NBTTagList();
		this.booleanTable.clear();
		for(List<GuiCheckBox> checkList:this.locationCheckTable.checkTable)
		{
			
			List<Boolean> booleanList= new ArrayList<Boolean>();
			int[] temp= new int[checkList.size()];
			int i=0;
			for(GuiCheckBox checkBox:checkList)
			{
				booleanList.add(checkBox.isChecked());
				temp[i]=checkBox.isChecked() ? 1:0;
				i++;
			}
			this.booleanTable.add(booleanList);
			NBTTagCompound nbtCheckList = new NBTTagCompound();
			nbtCheckList.setIntArray("checkList", temp);
			nbtCheckTable.appendTag(nbtCheckList);
			c++;
		}
		nbt.setTag("checkTable", nbtCheckTable);
		
		
		return nbt;
	}
	
	public void load(NBTTagCompound nbtFeat)
	{
		this.treeTypeID=nbtFeat.getInteger("tree");
		this.genType=nbtFeat.getInteger("gen");
		NBTTagList nbtCheckTable=(NBTTagList) nbtFeat.getTag("checkTable");
		if(nbtCheckTable!=null)
		{
			
			for(int c=0;c<nbtCheckTable.tagCount();c++)
			{
				NBTTagCompound nbtCheckList = (NBTTagCompound) nbtCheckTable.get(c);
				int[] checkList = nbtCheckList.getIntArray("checkList");
				
				for(int i=0;i<checkList.length;i++)
				{
					if(checkList[i]==1)
					{
						booleanTable.get(c).set(i, true);
					}
					else if(checkList[i]==0)
					{
						booleanTable.get(c).set(i, false);
					}
				
				}
			}
		}
		
		
	}

}
