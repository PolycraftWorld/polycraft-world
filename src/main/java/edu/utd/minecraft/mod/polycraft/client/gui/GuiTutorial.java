package edu.utd.minecraft.mod.polycraft.client.gui;

import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiTutorial extends GuiScreen {

	 private ResourceLocation BACKGROUND_IMG;// = new ResourceLocation(PolycraftMod.getAssetName("textures/gui/test_large_background.png"));
	 private EntityPlayer player;
	
	public GuiTutorial(EntityPlayer player) {
		// TODO Auto-generated constructor stub
		this.player = player;
		BACKGROUND_IMG = new ResourceLocation(PolycraftMod.getAssetName("textures/gui/test_large_background.png"));
	}
	
	 /**
     * Draws the background image on the screen
     */
    public void drawDefaultBackground()
    {
        super.drawDefaultBackground();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND_IMG);
        //PolycraftMod.logger.debug("Screen width & Height: " + this.width + " " + this.height);
        //System.out.println("Screen width & Height: " + this.width + " " + this.height);
        int i = (this.width - 248) / 2;
        int j = (this.height - 184) / 2; //old was 200
       // this.drawTexturedModalRect(i, j, 0, 0, 248, screenContainerHeight + 30);
        this.drawTexturedModalRect(i, j, 0, 0, 248, 184);
    }

    
    //Default scaled resolution (Gui-Scale Auto) is when k = 1000 (k = 0 for the Auto setting)
}
