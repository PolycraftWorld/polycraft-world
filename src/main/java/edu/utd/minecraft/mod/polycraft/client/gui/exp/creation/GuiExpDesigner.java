package edu.utd.minecraft.mod.polycraft.client.gui.exp.creation;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonDropDown;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

public class GuiExpDesigner extends GuiScreen{
	
	EntityPlayer player;
	HashMap<Integer, GuiBox> guiBoxes;
	int selectedBox;
	Mode currentMode;
	boolean menuUp = false;
	GuiPolyButtonDropDown<MenuButtons> menu = null;
	int nextID;
	int lastMouseX = 0, lastMouseY = 0;
	float scale = 1, offsetX = 0, offsetY = 0, scaleFactor = 15;
	
	public enum Mode{
		Place,
		Link
	}
	
	public enum MenuButtons{
		Feature,
		Guide,
		Instruction,
		Start,
		Score,
		End,
		Group
	}

	/**
     * Simpler constructor. Not sure if we care about the x,y,z of the player.
     * @param player the player who called up this screen
     */
    public GuiExpDesigner(EntityPlayer player) {
        this.player = player;
        this.guiBoxes = new HashMap<Integer, GuiBox>();
        selectedBox = -1;
        currentMode = Mode.Place;
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	GL11.glTranslatef( 0, 0, 5);
    	this.drawGradientRect(0, 0, this.width, this.height, 0xFFAAAAAA, 0xFF252525);
    	super.drawScreen(mouseX, mouseY, partialTicks);
    	GL11.glScalef(scale, scale, 1);
    	GL11.glTranslatef(offsetX, offsetY, 0);
    	for(GuiBox box: guiBoxes.values()) {
    		box.drawBox();
    	}
    	GL11.glTranslatef(-offsetX, -offsetY, 0);
    	GL11.glScalef(1/scale, 1/scale, 1);
    	
    	GL11.glTranslatef( 0, 0, -5);
    }
    
    /**
	 * Handles mouse wheel scrolling.
	 */
	public void handleMouseInput() {
		
		int i = Mouse.getEventDWheel();
		if (i != 0) {
			float preScaleX = (float) (((width/2) + offsetX)*Math.sqrt(scale));
			float preScaleY = (239 - (Mouse.getY() / 2))*scale;
	    	//offsetY = (239 - (Mouse.getY() / 2))/scale;
			scale += Math.signum(i) / scaleFactor;
			if (this.scale < 0.1F) {
				this.scale = 0.1F;
			} else if (this.scale > 2.0F) {
				this.scale = 2.0F;
			}
			offsetX += (preScaleX - ((width/2) + offsetX)*Math.sqrt(scale));
			//offsetX = (float) (preScaleX + Math.sqrt(scale)*(offsetX - preScaleX));
			//player.addChatComponentMessage(new ChatComponentText("Mouse X,Y,offsetX: " + (Mouse.getX() / 2) + "," + (239 - (Mouse.getY() / 2)) + "," + offsetX));
			player.addChatComponentMessage(new ChatComponentText("Mouse Scale,offsetX: " + scale + "," + offsetX));
		}
		try {
			super.handleMouseInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if(timeSinceLastClick > 50) {
			offsetX -= (this.lastMouseX - mouseX)/scale;
			offsetY -= (this.lastMouseY - mouseY)/scale;
		}else {
			this.lastMouseX = mouseX;
			this.lastMouseY = mouseY;
		}
			
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
	
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
    	player.addChatComponentMessage(new ChatComponentText("Mouse X,Y: " + mouseX + "," + mouseY));
    	int index = -1;
    	int mX = (int) (mouseX/scale);
    	int mY = (int) (mouseY/scale);
    	
    	loop: for(int x: guiBoxes.keySet()) {
    		if(mX > guiBoxes.get(x).x && mX < guiBoxes.get(x).x + 40
    				&& mY > guiBoxes.get(x).y && mY < guiBoxes.get(x).y + 40) {
    			index = x;
    			break loop;
    		}
    	}
    	
    	if(mouseButton == 1 && index == -1) {
    		this.buttonList.clear();
    		menu = new GuiPolyButtonDropDown<GuiExpDesigner.MenuButtons>(1, mouseX, mouseY, 50, 100, MenuButtons.Feature);
    		for(GuiButton button: menu.getButtons()) {
    			this.buttonList.add(button);
    		}
//    		if(currentMode == Mode.Place)
//    			currentMode = Mode.Link;
//    		else
//    			currentMode = Mode.Place;
//    		selectedBox = -1;	//set selected back to -1 to prevent null pointer
//    		offsetX = 0;
//	    	offsetY = 0;
    	}
    	
    	switch(currentMode) {
    	case Place:
    		if(mouseButton == 0)
				guiBoxes.put(nextID++, new GuiBox(mX, mY, "test"));
			else if(mouseButton == 1)
				guiBoxes.remove(index);
    		break;
    	case Link:
    		if(mouseButton == 0) {
        		if(selectedBox == -1) {
        			selectedBox = index;
        		}else if(index != -1) {
        			guiBoxes.get(selectedBox).setLink(index);
        			selectedBox = -1;	//set selected back to -1 to prevent null pointer
        		}
    		}
    		break;
		default:
    		break;	
    	}
    }
    
    private class GuiBox{
    	int x, y;
    	String text;
    	int linkedTo;
    	
    	public GuiBox(int x, int y, String text) {
    		this.x = x;
    		this.y = y;
    		this.text = text;
    		this.linkedTo = -1;
    	}
    	
    	public void drawBox() {
    		drawRect(x, y, x + 10, y + 10, 0xFFFFFFFF);
    		if(linkedTo != -1) {
    			if(GuiExpDesigner.this.guiBoxes.get(linkedTo) != null)
    				drawLine(this.x, this.y, GuiExpDesigner.this.guiBoxes.get(linkedTo).x, GuiExpDesigner.this.guiBoxes.get(linkedTo).y);
    			else
    				linkedTo = -1;
    		}
    	}
    	
    	public void setLink(int link) {
    		linkedTo = link;
    	}
    	
    	public void drawLine(int x1, int y1, int x2, int y2) {
    		int xMin = Math.min(x1, x2), xMax = Math.max(x1, x2), yMin = Math.min(y1, y2), yMax = Math.max(y1, y2);
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.shadeModel(7425);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            GL11.glLineWidth(5);
            worldrenderer.pos(x1+5, y1+5, -1).color(200, 0, 0, 255).endVertex();
            worldrenderer.pos(x2+5, y2+5, -1).color(200, 0, 0, 255).endVertex();
            tessellator.draw();
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
    	}
    }
    
    private static class GuiLine{
    	int x1, y1, x2, y2;
    	String text;
    	
    	public GuiLine(int x1, int y1, int x2, int y2) {
    		this.x1 = x1;
    		this.y1 = y1;
    		this.x2 = x2;
    		this.y2 = y2;
    	}
    	
    	public void drawLine() {
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.shadeModel(7425);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos((double)x1, (double)y1, (double)0).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos((double)x2, (double)y2, (double)0).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
    	}
    }
	
}
