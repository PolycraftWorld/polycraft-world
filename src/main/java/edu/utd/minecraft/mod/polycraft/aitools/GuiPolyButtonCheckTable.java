package edu.utd.minecraft.mod.polycraft.aitools;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonDropDown;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiPolyButtonCheckTable extends GuiButton {

	
	public List<List<GuiCheckBox>> checkTable;
	
	public GuiPolyButtonCheckTable(int buttonId, int x, int y, int widthIn, int heightIn, List<List<Boolean>> booleanTable) {
		super(buttonId, x, y, widthIn, heightIn, "");
		checkTable = new ArrayList<List<GuiCheckBox>>(this.width); 
		for(int i=0;i<this.width;i++)
		{
			List<GuiCheckBox> checkList = Lists.<GuiCheckBox>newArrayList();
			boolean test=false;
			if(booleanTable.size()>i)
			{
				test=true;
			}
			else
			{
				booleanTable.add(new ArrayList<Boolean>());
			}
			for(int j=0;j<this.height;j++)
			{
				if(test)
				{
					if(booleanTable.get(i).size()>j)
					{
						checkList.add( new GuiCheckBox(20, x+(i*11), y+(j*11), "", booleanTable.get(i).get(j)));
					}
					else
					{
						checkList.add( new GuiCheckBox(20, x+(i*11), y+(j*11), "", false));
						booleanTable.get(i).add(false);
					}
				}
				else
				{
					checkList.add( new GuiCheckBox(20, x+(i*11), y+(j*11), "", false));
					booleanTable.get(i).add(false);
				}
			}
			checkTable.add(checkList);
		}
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
		for(List<GuiCheckBox> checkList: this.checkTable)
		{
			for(GuiCheckBox checkBox: checkList)
			{
				checkBox.drawButton(mc, mouseX, mouseY);
			}
		}
    }
	
	@Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
    	for(List<GuiCheckBox> checkList: this.checkTable)
		{
			for(GuiCheckBox checkBox: checkList)
			{
				if(checkBox.mousePressed(mc, mouseX, mouseY))
				{
					return true;
				}
			}
		}
        return false;
    }


}
