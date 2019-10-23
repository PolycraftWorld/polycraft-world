package edu.utd.minecraft.mod.polycraft.client.gui.api;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPolyLabel extends Gui{
	private String text;
	private int color;
	private int xStart, yStart;
    private FontRenderer renderer;
    
    public GuiPolyLabel(FontRenderer renderer, int xStart, int yStart, int color, String text) {
    	this.renderer = renderer;
    	this.xStart = xStart;
    	this.yStart = yStart;
    	this.text = text;
    	this.color = color;
    }
    
    /**
     * Renders the label to the screen.
     */
    public void drawLabel() {
    	this.renderer.drawString(text, xStart, yStart, color);
    }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getxStart() {
		return xStart;
	}

	public void setxStart(int xStart) {
		this.xStart = xStart;
	}

	public int getyStart() {
		return yStart;
	}

	public void setyStart(int yStart) {
		this.yStart = yStart;
	}

	public FontRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(FontRenderer renderer) {
		this.renderer = renderer;
	}
    
    

}
