package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class TutorialRender {
	public static boolean turnRight=false;
	public static boolean turnLeft=false;
	public static double prevAng=0;
	public static boolean render=false;

	public static void start(EntityPlayer player)
	{
		turnRight=false;
		turnLeft=false;
		prevAng=player.rotationYaw;
		render=!render;
	}
	
	 
	 public static void renderTutorialWalkForward()
	 {
		 float scale =.20F;
		 ResourceLocation[] textures = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WASD_0.png")),
					new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WASD_1.png")),
					};
		
		 Minecraft mc = Minecraft.getMinecraft();
		 EntityClientPlayerMP player = mc.thePlayer;
		 int i=((player.ticksExisted)%60);
		 // GL11.glPushMatrix();
		 if(i>48)
			 i=0;
		 if(i>20)
			 i=1;
		 else
			 i/=20;
		 
	      GL11.glPushMatrix();
	      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
	      GL11.glEnable(GL11.GL_BLEND);

	      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	      GL11.glScalef(scale, scale, 0);
	        GL11.glEnable(GL11.GL_ALPHA_TEST);
	        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
			//GL11.glDisable(GL11.GL_LIGHTING);
//			 //ResourceLocation texture = new ResourceLocation(
//						PolycraftMod.getAssetName("textures/blocks/test.gif"));

			mc.getTextureManager().bindTexture(textures[i]);

			/* Draw border */
			mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 260);
			GL11.glDisable(GL11.GL_BLEND);


		      GL11.glPopAttrib();
		      GL11.glPopMatrix();
		      
		      //////////////////////////////////////////////////
		      
		      float scale2 =.20F;

				 ResourceLocation[] textures2 = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WalkingGIF/frame_00_delay-0.1s.gif")),
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
				
				 //Minecraft mc = Minecraft.getMinecraft();
				 //EntityClientPlayerMP player = mc.thePlayer;
				 i=((player.ticksExisted)%60);
				 if(i<=18)
					 i=19;
				 else
					 i/=3;
				 // GL11.glPushMatrix();
			      GL11.glPushMatrix();
			      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			      GL11.glEnable(GL11.GL_BLEND);
		
			      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			      GL11.glScalef(scale2, scale2, 0);
			        GL11.glEnable(GL11.GL_ALPHA_TEST);
			        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
					//GL11.glDisable(GL11.GL_LIGHTING);
		//			 //ResourceLocation texture = new ResourceLocation(
		//						PolycraftMod.getAssetName("textures/blocks/test.gif"));
		
					mc.getTextureManager().bindTexture(textures2[i]);
		
					/* Draw border */
					mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
					GL11.glDisable(GL11.GL_BLEND);
		
		
				      GL11.glPopAttrib();
				      GL11.glPopMatrix();
				      
	 }
	 
	 public static boolean renderTutorialTurnRight()
	 {
		 	Minecraft mc = Minecraft.getMinecraft();
			EntityClientPlayerMP player = mc.thePlayer;

			ResourceLocation[] textures3 = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/rightArrow.png")),
							new ResourceLocation(PolycraftMod.getAssetName("textures/gui/rightArrowFill.png")),
							};


				float scale =.40F;


				 // GL11.glPushMatrix();
				 
			      GL11.glPushMatrix();
			      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			      GL11.glEnable(GL11.GL_BLEND);
		
			      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			      GL11.glScalef(scale, scale, 0);
			        GL11.glEnable(GL11.GL_ALPHA_TEST);
			        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
					//GL11.glDisable(GL11.GL_LIGHTING);
		//			 //ResourceLocation texture = new ResourceLocation(
		//						PolycraftMod.getAssetName("textures/blocks/test.gif"));
		
					mc.getTextureManager().bindTexture(textures3[0]);
		
					/* Draw border */
					mc.ingameGUI.drawTexturedModalRect(630, 200, 0, 0, 255, 260);
					GL11.glDisable(GL11.GL_BLEND);
		
		
				      GL11.glPopAttrib();
				      GL11.glPopMatrix();
				      
				      GL11.glPushMatrix();
				      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				      GL11.glEnable(GL11.GL_BLEND);
			
				      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				      GL11.glScalef(scale, scale, 0);
				        GL11.glEnable(GL11.GL_ALPHA_TEST);
				        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
						//GL11.glDisable(GL11.GL_LIGHTING);
			//			 //ResourceLocation texture = new ResourceLocation(
			//						PolycraftMod.getAssetName("textures/blocks/test.gif"));
			
						mc.getTextureManager().bindTexture(textures3[1]);
						double trueAng=ClientEnforcer.INSTANCE.prevAng;
						double ang = player.rotationYaw;
						if(ang<ClientEnforcer.INSTANCE.prevAng)
						{
								ClientEnforcer.INSTANCE.prevAng=(player.rotationYaw);
						}

							mc.ingameGUI.drawTexturedModalRect(633, 199, 0, 0, (int)((ang-ClientEnforcer.INSTANCE.prevAng)*3), 260);

						/* Draw border */
						
						GL11.glDisable(GL11.GL_BLEND);
			
			
					      GL11.glPopAttrib();
					      GL11.glPopMatrix();
					      if((ang-ClientEnforcer.INSTANCE.prevAng)>=85)
					      {
					    	  //test=4;
					    	  return true;
					      }
					      return false;
	 }
	 
	 public static boolean renderTutorialTurnLeft()
	 {

			ResourceLocation[] textures3 = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/rightArrow.png")),
							new ResourceLocation(PolycraftMod.getAssetName("textures/gui/rightArrowFill.png")),
							};
			float scale =.40F;
		 	Minecraft mc = Minecraft.getMinecraft();
			EntityClientPlayerMP player = mc.thePlayer;
			
//		 GL11.glPushMatrix();
//	      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
//	      GL11.glEnable(GL11.GL_BLEND);
//
//	      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//	      GL11.glScalef(scale, scale, 0);
//	        GL11.glEnable(GL11.GL_ALPHA_TEST);
//	        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
//			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
//			
//			//GL11.glDisable(GL11.GL_LIGHTING);
////			 //ResourceLocation texture = new ResourceLocation(
////						PolycraftMod.getAssetName("textures/blocks/test.gif"));
//
//			mc.getTextureManager().bindTexture(textures3[1]);
//
//				mc.ingameGUI.drawTexturedModalRect(553, 198, 0, 0, (int)(255), 260);
//
//			/* Draw border */
//			
//			GL11.glDisable(GL11.GL_BLEND);
//
//
//		      GL11.glPopAttrib();
//		      GL11.glPopMatrix();
		      
				 scale =.40F;


				 // GL11.glPushMatrix();
				 
			      GL11.glPushMatrix();
			      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			      GL11.glEnable(GL11.GL_BLEND);
		
			      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			      GL11.glScalef(scale, scale, 0);
			        GL11.glEnable(GL11.GL_ALPHA_TEST);
			        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
					GL11.glFrontFace(GL11.GL_CCW);
					GL11.glRotated(180, 0, 0, 1);
					//GL11.glDisable(GL11.GL_LIGHTING);
		//			 //ResourceLocation texture = new ResourceLocation(
		//						PolycraftMod.getAssetName("textures/blocks/test.gif"));
		
					mc.getTextureManager().bindTexture(textures3[0]);
		
					/* Draw border */
					mc.ingameGUI.drawTexturedModalRect(-580, -450, 0, 0, 255, 260);
					GL11.glDisable(GL11.GL_BLEND);
		
		
				      GL11.glPopAttrib();
				      GL11.glPopMatrix();
				      
				      GL11.glPushMatrix();
				      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				      GL11.glEnable(GL11.GL_BLEND);
			
				      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				      GL11.glScalef(scale, scale, 0);
				        GL11.glEnable(GL11.GL_ALPHA_TEST);
				        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
						GL11.glRotated(180, 0, 0, 1);
						//GL11.glDisable(GL11.GL_LIGHTING);
			//			 //ResourceLocation texture = new ResourceLocation(
			//						PolycraftMod.getAssetName("textures/blocks/test.gif"));
			
						mc.getTextureManager().bindTexture(textures3[1]);
						double trueAng=player.rotationYaw;
						double ang = player.rotationYaw;
						if(ang>ClientEnforcer.INSTANCE.prevAng)
						{
							ClientEnforcer.INSTANCE.prevAng=(player.rotationYaw);
						}

							mc.ingameGUI.drawTexturedModalRect(-577, -452, 0, 0, (int)((ClientEnforcer.INSTANCE.prevAng-ang)*3), 260);

						/* Draw border */
						
						GL11.glDisable(GL11.GL_BLEND);
			
			
					      GL11.glPopAttrib();
					      GL11.glPopMatrix();
					      if((ClientEnforcer.INSTANCE.prevAng-ang)>=85)
					      {
					    	  //test=6;
					    	  return true;
					      }
					      return false;
	 }
	
}


