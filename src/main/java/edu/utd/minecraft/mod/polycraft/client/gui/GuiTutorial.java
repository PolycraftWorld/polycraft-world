package edu.utd.minecraft.mod.polycraft.client.gui;

import java.io.File;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.util.ThreadDownloadGUIImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;

public class GuiTutorial extends GuiScreen {

	 private ResourceLocation BACKGROUND_IMG;// = new ResourceLocation(PolycraftMod.getAssetName("textures/gui/test_large_background.png"));
	 private ResourceLocation OTHER_RESOURCE;
	 private EntityPlayer player;
	 private ThreadDownloadGUIImage downloadedImage;
	 //private int guiSettings;
	// private int defaultGuiSettings;
	
	public GuiTutorial(EntityPlayer player) {
		// TODO Auto-generated constructor stub
		this.player = player;
		String url = "https://upload.wikimedia.org/wikipedia/commons/c/c6/Sierpinski_square.jpg";
		BACKGROUND_IMG = new ResourceLocation(PolycraftMod.getAssetNameString("textures/gui/tutorial/Slide/Slide1.png"));
		OTHER_RESOURCE = new ResourceLocation(PolycraftMod.getAssetNameString("textures/gui/tutorial/temp/test1.png"));
		downloadedImage = new ThreadDownloadGUIImage(url, null, OTHER_RESOURCE);
		//guiSettings = 1; //set scaled resolution to 1
		//defaultGuiSettings = this.mc.gameSettings.guiScale;
		//default powerpoint slide size:
		//720px width, 405px height.
	}
			
	
	 /**
     * Draws the background image on the screen
     */
    public void drawDefaultBackground()
    {
    	
    	
    	
        //super.drawDefaultBackground();
        
        double x_border = 0.1; //percent offset
        double y_offset = 0.1;
        
        //let's get the scaling factors!
        int width = this.mc.displayWidth;
        int height = this.mc.displayHeight;
        
        int cur_width = this.width;
        int cur_height = this.height;
        
        float scaleX = (float)((float)cur_width/width);
        float scaleY = (float)((float)cur_height/height);
        
        if(scaleX < 0.4 || scaleY < 0.4) {
        	scaleX *= 2;
            scaleY *= 2;
        }
        
        GL11.glPushMatrix();
        if(scaleX != 1 && scaleY != 1)
        	GL11.glScalef((float)(360.0/256*scaleY), (float)(360.0/256*scaleX), 1);
        //GL11.glEnable(GL11.GL_BLEND);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //GL11.glS
        //this.mc.getTextureManager().bindTexture(BACKGROUND_IMG);
        this.mc.getTextureManager().loadTexture(OTHER_RESOURCE, downloadedImage);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((ITextureObject)downloadedImage).getGlTextureId());
        //PolycraftMod.logger.debug("Screen width & Height: " + this.width + " " + this.height);
        //System.out.println("Screen width & Height: " + this.width + " " + this.height);
        //int i = (this.width - 248) / 2;
        //int j = (this.height - 184) / 2; //old was 200
       // this.drawTexturedModalRect(i, j, 0, 0, 248, screenContainerHeight + 30);
        
        int x_start = Math.round((float)(width*x_border));
        int y_start = Math.round((float)(height*y_offset));
        
        if(scaleX == 1) {
        	this.drawTexturedModalRect((int)((width/2 - this.width*scaleX*2/11)), 20, 0, 0, 256, 256);
        }
        else if(scaleX > 0.51 && scaleX < 1) {
        	this.drawTexturedModalRect((int)((width/2 - this.width*scaleX * 1.85)), 20, 0, 0, 256, 256);
        }
        else {
        	this.drawTexturedModalRect((int)((this.width - 256*(256/360.0*scaleX))/2), 20, 0, 0, 256, 256);
        }
        
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glPopMatrix();
        
       // GL11.glPopClientAttrib();
        
    }
    
    public void drawScreen(int mouseX, int mouseY, float otherValue)
    {
    	this.drawDefaultBackground();
    	
    }

    
    //Default scaled resolution (Gui-Scale Auto) is when k = 1000 (k = 0 for the Auto setting)
}
