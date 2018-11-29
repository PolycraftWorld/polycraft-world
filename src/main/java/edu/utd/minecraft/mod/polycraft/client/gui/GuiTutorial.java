package edu.utd.minecraft.mod.polycraft.client.gui;

import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;

public class GuiTutorial extends GuiScreen {

	 private ResourceLocation BACKGROUND_IMG;// = new ResourceLocation(PolycraftMod.getAssetName("textures/gui/test_large_background.png"));
	 private EntityPlayer player;
	 //private int guiSettings;
	// private int defaultGuiSettings;
	
	public GuiTutorial(EntityPlayer player) {
		// TODO Auto-generated constructor stub
		this.player = player;
		BACKGROUND_IMG = new ResourceLocation(PolycraftMod.getAssetName("textures/gui/tutorial/test1/Slide1.png"));
		//guiSettings = 1; //set scaled resolution to 1
		//defaultGuiSettings = this.mc.gameSettings.guiScale;
		//default powerpoint slide size:
		//720px width, 405px height.
	}
	
	public void resetGuiScale() {
	//	this.guiSettings = this.defaultGuiSettings;
	}
	
	@Override
	public void setWorldAndResolution(Minecraft mc, int scaledWidth, int scaledHeight) {
		
	//	mc.gameSettings.guiScale = this.guiSettings;
//		int k = 1; //set the scaled resolution to the native screen resolution of the display
//		CustomScaledResolution scaledResolution = new CustomScaledResolution(mc, mc.displayWidth, mc.displayHeight, k);
//		
//		this.mc = mc;
//        this.fontRendererObj = mc.fontRenderer;
//        this.width = scaledResolution.getScaledWidth();
//        this.height = scaledResolution.getScaledHeight();
//        if (!MinecraftForge.EVENT_BUS.post(new InitGuiEvent.Pre(this, this.buttonList)))
//        {
//            this.buttonList.clear();
//            this.initGui();
//        }
//        MinecraftForge.EVENT_BUS.post(new InitGuiEvent.Post(this, this.buttonList));
		
		//super.setWorldAndResolution(mc, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
		super.setWorldAndResolution(mc, scaledWidth, scaledHeight);
		
	}
	
	@Override
	public void initGui() {
	//	this.defaultGuiSettings = 0;
		super.initGui();
		
		
		//this.setWorldAndResolution(this.mc, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
		
		//super.
	}
	
	@Override
	public void onGuiClosed() {
		//this.resetGuiScale();
		//this.mc.gameSettings.guiScale = this.guiSettings;
		super.onGuiClosed();
		//mc.gameSettings.guiScale = 0;
		
		//reset the GUI resolution to the default, as defined by the user.
//		CustomScaledResolution scaledResolution = new CustomScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight, this.guiSettings);
//		super.setWorldAndResolution(this.mc, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
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
        
        float scaleX = cur_width/width;
        float scaleY = cur_height/height;
        
        GL11.glPushMatrix();
        GL11.glScalef(scaleX, scaleY, 1);
        //GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //GL11.glS
        this.mc.getTextureManager().bindTexture(BACKGROUND_IMG);
        //PolycraftMod.logger.debug("Screen width & Height: " + this.width + " " + this.height);
        //System.out.println("Screen width & Height: " + this.width + " " + this.height);
        //int i = (this.width - 248) / 2;
        //int j = (this.height - 184) / 2; //old was 200
       // this.drawTexturedModalRect(i, j, 0, 0, 248, screenContainerHeight + 30);
        
        int x_start = Math.round((float)(width*x_border));
        int y_start = Math.round((float)(height*y_offset));
        
        this.drawTexturedModalRect(10, 10, 0, 0, width, 405);
        GL11.glPopMatrix();
        
    }
    
    public void drawScreen(int mouseX, int mouseY, float otherValue)
    {
    	this.drawDefaultBackground();
    	
    }

    
    //Default scaled resolution (Gui-Scale Auto) is when k = 1000 (k = 0 for the Auto setting)
}
