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
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPolyTextField extends GuiTextField{
	
	private String toolTip = "";
    protected HoverChecker tooltipHoverChecker;
	
	public GuiPolyTextField(FontRenderer p_i1032_1_, int p_i1032_2_, int p_i1032_3_, int p_i1032_4_, int p_i1032_5_) {
		super(101, p_i1032_1_, p_i1032_2_, p_i1032_3_, p_i1032_4_, p_i1032_5_);
	}
	
	@Override
	public void drawTextBox() {
		super.drawTextBox();
		if (this.tooltipHoverChecker == null)
            this.tooltipHoverChecker = new HoverChecker(yPosition, yPosition + height, xPosition, xPosition + width - 8, 800);
        else
            this.tooltipHoverChecker.updateBounds(yPosition, yPosition + height, xPosition, xPosition + width - 8);
	}

}
