package edu.utd.minecraft.mod.polycraft.client.gui.api;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.command.CommandBase;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPolyItemNameField extends GuiTextField{
	
	private String itemGuess = "";

	public GuiPolyItemNameField(FontRenderer p_i1032_1_, int p_i1032_2_, int p_i1032_3_, int p_i1032_4_, int p_i1032_5_) {
		super(100, p_i1032_1_, p_i1032_2_, p_i1032_3_, p_i1032_4_, p_i1032_5_);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean textboxKeyTyped(char c, int p) {
		super.textboxKeyTyped(c, p);
		String[] tempText = this.getText().split(" ");
		
		List<String> itemList = CommandBase.getListOfStringsMatchingLastWord(tempText, Item.itemRegistry.getKeys());
		if(itemList.size() > 0) {
			int maxLength = Math.max(itemList.get(0).length(), getText().length());
			for(String item: itemList) {
				if(item.length() <= maxLength && item.length() >= getText().length()) {
					itemGuess = item;
					if(item.length() == getText().length())
						break;
				}
			}
		}else {
			itemGuess = "NO ITEM FOUND";
		}
		
		
		return false;
	}
	
	
	@Override
	public void drawTextBox() {
		super.drawTextBox();
		if (this.getVisible() && getText().length() > 0)
        {
            int color = 7368816;
            int xPos = this.xPosition + 4 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(getText());
            int yPos = getEnableBackgroundDrawing() ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
            
            if (itemGuess.length() > 0)
            {
                Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(itemGuess.replace(getText(), ""), (float)xPos, (float)yPos, color);
            }

        }
	}

}
