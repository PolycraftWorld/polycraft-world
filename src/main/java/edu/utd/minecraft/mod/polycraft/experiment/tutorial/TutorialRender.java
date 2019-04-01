package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class TutorialRender {
	public static boolean turnRight=false;
	public static boolean turnLeft=false;
	public static double prevAng=0;
	public static boolean render=false;
	public static Minecraft mc = Minecraft.getMinecraft();
	
	static 			ResourceLocation[] textures3 = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/rightArrow.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/rightArrowFill.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/mouse.png")),
			};
	
	 static 				 ResourceLocation[] textures8 = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_00_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_01_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_02_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_03_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_04_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_05_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_06_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_07_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_08_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_09_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_10_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_11_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_12_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_13_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_14_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_15_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_16_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_17_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_18_delay-0.1s.gif")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_19_delay-0.1s.gif")),
				
};
	 static ResourceLocation[] textures7 = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WASD_0.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WASD_1.png")),
				};
	
//	 static ResourceLocation[] textures6 = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_00_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_01_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_02_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_03_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_04_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_05_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_06_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_07_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_08_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_09_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_10_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_11_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_12_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_13_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_14_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_15_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_16_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_17_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_18_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_19_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_20_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_21_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_22_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_23_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_24_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_25_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_26_delay-0.1s.gif")),
//				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGif/frame_27_delay-0.1s.gif")),					
//	 			};
	 
	 
	 static ResourceLocation[] textures = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WASD_0.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WASD_1.png")),
				};
	 static ResourceLocation[] textures4 = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/space.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/spaceGreen.png")),
				};
	

	public  TutorialRender(EntityPlayer player)
	{
		this.turnRight=false;
		this.turnLeft=false;
		this.prevAng=player.rotationYaw;
		this.render=true;
	}
	
	public static void push(float scale)
	{
		GL11.glPushMatrix();
	    mc.entityRenderer.setupOverlayRendering();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_LIGHTING);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    GL11.glScalef(scale, scale, 0);
	    GL11.glEnable(GL11.GL_ALPHA_TEST);
	    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
	}
	
	public static void pop()
	{
		
		 GL11.glDisable(GL11.GL_BLEND);
		 GL11.glPopAttrib();
		 GL11.glPopMatrix();
	}
	
//	 public void renderTutorialJumpForward()
//	 {
//		 float scale =.20F;
//		 push(scale);
//		 mc.getTextureManager().bindTexture(textures[1]);
//		 /* Draw border */
//		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 260);
//		 pop();
//		 
//		 int i=((this.player.ticksExisted)%40)/20;
//		 scale =.4F;
//		 push(scale);
//		 mc.getTextureManager().bindTexture(textures4[i]);
//		 /* Draw border */
//		 mc.ingameGUI.drawTexturedModalRect(20, 0, 0, 0, 255, 260);
//		 pop();
//		 
//		 float scale2 =.25F;
//		 int j=((player.ticksExisted)%27);
//		 push(scale2);
//		 mc.getTextureManager().bindTexture(textures6[j]);
//		 mc.ingameGUI.drawTexturedModalRect(400, 2, 0, 0, 255, 250);
//		 pop();
//				      
//	 }
	
	  public static void renderTutorialWalkForward(Entity player)
	 {
		 
		 float scale =.20F;

		 int i=((player.ticksExisted)%60);
		 // GL11.glPushMatrix();
		 if(i>48)
			 i=0;
		 if(i>20)
			 i=1;
		 else
			 i/=20;
		 
		 push(scale);
		 mc.getTextureManager().bindTexture(textures7[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 260);
		 pop();
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%60);
		 if(i<=18)
		 i=19;
		 else
		 i/=3;
		 push(scale);
		 mc.getTextureManager().bindTexture(textures8[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static boolean renderTutorialTurnRight(Entity player)
	 {
		 

		 float scale =.125F;
		 int tick=player.ticksExisted%20;
		 if(tick>=15)
			 tick=15;
		 push(scale);
		 mc.getTextureManager().bindTexture(textures3[2]);
		 mc.ingameGUI.drawTexturedModalRect(2700+tick*10, 100, 0, 0, 255, 260);
		 pop();
		 
		 
		 scale =.10F;
		 push(scale);
		 mc.getTextureManager().bindTexture(textures3[0]);
		 mc.ingameGUI.drawTexturedModalRect(3930, 200, 0, 0, 255, 260);
		 pop();
		 
		 
		 push(scale);
		 mc.getTextureManager().bindTexture(textures3[1]);
		 double ang = player.rotationYaw;
		 if(ang<prevAng)
		 {
			 prevAng=(player.rotationYaw);
		 }
		 mc.ingameGUI.drawTexturedModalRect(3933, 199, 0, 0, (int)(10*tick), 260);
		 pop();

		 scale =.40F;
		 push(scale);
		 mc.getTextureManager().bindTexture(textures3[0]);
		 mc.ingameGUI.drawTexturedModalRect(630, 200, 0, 0, 255, 260);
		 pop();
		
		 push(scale);
		 mc.getTextureManager().bindTexture(textures3[1]);
		 ang = player.rotationYaw;
		 if(ang<prevAng)
		 {
			 prevAng=(player.rotationYaw);
		 }
		 mc.ingameGUI.drawTexturedModalRect(633, 199, 0, 0, (int)((ang-prevAng)*3), 260);
		 pop();
		 
		 if((ang-prevAng)>=85)
		 {
			 return true;
		 }
		 return false;
	 }
	 
	 public static boolean renderTutorialTurnLeft(Entity player)
	 {

	
		 float scale =.125F;
		 int tick=player.ticksExisted%20;
		 if(tick>=15)
			 tick=15;

		 push(scale);
		 mc.getTextureManager().bindTexture(textures3[2]);
		 mc.ingameGUI.drawTexturedModalRect(3150-tick*10, 100, 0, 0, 255, 260);
		 pop();
		 
	     scale =.10F;
	     push(scale);
	     GL11.glRotated(180, 0, 0, 1);
	     mc.getTextureManager().bindTexture(textures3[0]);
	     mc.ingameGUI.drawTexturedModalRect(-3800, -455, 0, 0, 255, 260);
	     pop();
	     
	     push(scale);
	     GL11.glRotated(180, 0, 0, 1);
	     mc.getTextureManager().bindTexture(textures3[1]);
	     mc.ingameGUI.drawTexturedModalRect(-3800, -455, 0, 0, (int)(10*tick), 260);
	     pop();
	     
	     scale =.40F;
	     push(scale);
	     GL11.glRotated(180, 0, 0, 1);
	     mc.getTextureManager().bindTexture(textures3[0]);
	     mc.ingameGUI.drawTexturedModalRect(-580, -450, 0, 0, 255, 260);
	     pop();
	     
	     push(scale);
	     GL11.glRotated(180, 0, 0, 1);
	     mc.getTextureManager().bindTexture(textures3[1]);
	     double ang = player.rotationYaw;
	     if(ang>prevAng)
	     {
	    	 prevAng=(player.rotationYaw);
	     }
	     mc.ingameGUI.drawTexturedModalRect(-577, -452, 0, 0, (int)((prevAng-ang)*3), 260);
	     pop();
	     if((prevAng-ang)>=85)
	     {
	    	 return true;
	     }
	     return false;
	 }
	
}


