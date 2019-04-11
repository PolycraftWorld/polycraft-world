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
	public boolean turnRight=false;
	public boolean turnLeft=false;
	public static double prevAng=0;
	public static boolean render=false;
	public static Minecraft mc = Minecraft.getMinecraft();
	
	
	static	ResourceLocation[] texturesFloat = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_14_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_15_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_16_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_17_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_18_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_19_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_20_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_21_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_22_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_23_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_24_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_25_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_26_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_27_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_28_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_29_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_30_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_31_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_32_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_33_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_34_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_35_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_36_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_37_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_38_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_39_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_40_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_41_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_42_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_43_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_44_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_45_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_46_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_47_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_48_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_49_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_50_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_51_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_52_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_53_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/frame_54_delay-0.1s.gif"))
			};
	
	static 			ResourceLocation[] texturesFloatingOut = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingOutGIF/frame_00_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingOutGIF/frame_01_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingOutGIF/frame_02_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingOutGIF/frame_03_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingOutGIF/frame_04_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingOutGIF/frame_05_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingOutGIF/frame_06_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingOutGIF/frame_07_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingOutGIF/frame_08_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingOutGIF/frame_09_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingOutGIF/frame_10_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingOutGIF/frame_11_delay-0.1s.gif"))			
	};
	
	static 			ResourceLocation[] texturesSprint = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_00_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_01_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_02_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_03_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_04_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_05_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_06_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_07_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_08_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_09_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_10_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_11_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/frame_11_delay-0.1s.gif"))
	};
	
	static 			ResourceLocation[] texturesSprintJump = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_00_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_01_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_02_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_03_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_04_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_05_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_06_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_07_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_08_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_09_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_10_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_11_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_12_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_13_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_14_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_15_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_16_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_17_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_18_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_19_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_20_delay-0.1s.gif")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/frame_21_delay-0.1s.gif"))

	};
	
	static 			ResourceLocation[] texturesArrow = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/rightArrow.png")),
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
	 static ResourceLocation[] texturesWASD = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WASD_0.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WASD_1.png")),
				};
	 static ResourceLocation[] texturesSpace = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/space.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/spaceGreen.png")),
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
	 
	 
	 static 				 ResourceLocation[] texturesKBB = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0000.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0001.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0002.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0003.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0004.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0005.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0006.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0007.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0008.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0009.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0010.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0011.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0012.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0013.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0014.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0015.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0016.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0017.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0018.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0019.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0020.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0021.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0022.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0023.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0024.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0025.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0026.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0027.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0028.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0029.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0030.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0031.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0032.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0033.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0034.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0035.jpg")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/KBB_GIF/TutorialTrial6-KBB0036.jpg")),
				
	 };
	 
	 
	 static 				 ResourceLocation[] texturesManagingInventory = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory000.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory001.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory002.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory003.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory004.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory005.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory006.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory007.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory008.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory009.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory010.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory011.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory012.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory013.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory014.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory015.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory016.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory017.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory018.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory019.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory020.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory021.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory022.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory023.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory024.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory025.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory026.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory027.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory028.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory029.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory030.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory031.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory032.png")),
				
				
	 };
	 
	 static 				 ResourceLocation[] texturesAccessingInventories = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories000.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories001.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories002.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories003.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories004.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories005.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories006.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories007.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories008.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories009.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories010.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories011.png")),
				
				
				
	 };
	 
	 static ResourceLocation[] texturesMouseRightClick = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/mouse.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/mouseRightClick.png")),
				};
	 
	 static ResourceLocation[] texturesMouseLeftClick = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/mouse.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/mouseLeftClick.png")),
				};
	 
	 static ResourceLocation[] texturesCtrl = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/control.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/controlGreen.png")),
				};
	 
	

	 
	public TutorialRender()
	{
		turnRight=false;
		turnLeft=false;
		render=true;
	}
	
	public void setAng(Entity entity)
	{
		prevAng=entity.rotationYaw;
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
	
	
	
	public static void renderTutorialSprintJump(Entity player)
	 {
		 
		 float scale =.20F;

		 int i=((player.ticksExisted)%20);
		 // GL11.glPushMatrix();
		 if(i<=2)
		 {
			 i=0;
		 }
		 else if(i==3)
		 {
			 i=1;
		 }
		 else if(i<=5)
		 {
			 i=0;
		 }
		 else
		 {
			 i=1;
		 }

		 
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesWASD[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 260);
		 pop();
		 i=((player.ticksExisted)%20);
		 if(i<=5)
		 {
			 i=0;
		 }
		 else if(i<=7)
		 {
			 i=1;
		 }
		 else 
		 {
			i=0; 
		 }
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesWASD[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 260);
		 pop();
		 
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%12);
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesSprintJump[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	
	public static void renderTutorialSprint(Entity player)
	 {
		 
			 float scale =.20F;

			 int i=((player.ticksExisted)%24);
			 // GL11.glPushMatrix();
			 if(i>12 && i<15)
				 i=1;
			 else
				 i=0;
			 
			 push(scale);
			 mc.getTextureManager().bindTexture(texturesCtrl[i]);
			 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 250);
			 pop();
			
			 
			 float scale2 =.20F;
			 i=((player.ticksExisted)%24);
			 i/=2;
			 
			 push(scale2);
			 mc.getTextureManager().bindTexture(texturesSprint[i]);
			 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
			 pop();	
			 
			 mc.entityRenderer.updateRenderer();
		 
	 }
	
	
	public static void renderLoadingScreen(Entity player)
	 {
		 
		 float scale =.50F;

		 int i=((player.ticksExisted)%20);
		 // GL11.glPushMatrix();
		 if(i<=2)
		 {
			 i=0;
		 }
		 else if(i==3)
		 {
			 i=1;
		 }
		 else if(i<=5)
		 {
			 i=0;
		 }
		 else
		 {
			 i=1;
		 }

		 push(scale);
		 mc.ingameGUI.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0xFF000000);
		 pop();
		 
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesWASD[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 260);
		 pop();
		 
		 
		 mc.entityRenderer.updateRenderer();
	 }
	
	
	public static void renderTutorialFloatOut(Entity player)
	 {
		 
		 float scale =.20F;

		 int i=((player.ticksExisted)%2);
		 // GL11.glPushMatrix();

		 
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesWASD[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 260);
		 pop();
		 
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesSpace[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 260);
		 pop();
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%12);
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesFloatingOut[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	
	
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
		 mc.getTextureManager().bindTexture(texturesWASD[i]);
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
	 
	 public boolean renderTutorialTurnRight(Entity player)
	 {
		 if(turnRight)
		 {
			 return turnRight;
		 }

		 float scale =.125F;
		 int tick=player.ticksExisted%20;
		 if(tick>=15)
			 tick=15;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesArrow[2]);
		 mc.ingameGUI.drawTexturedModalRect(2700+tick*10, 100, 0, 0, 255, 260);
		 pop();
		 
		 
		 scale =.10F;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesArrow[0]);
		 mc.ingameGUI.drawTexturedModalRect(3930, 200, 0, 0, 255, 260);
		 pop();
		 
		 
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesArrow[1]);
		 double ang = player.rotationYaw;
		 if(ang<prevAng)
		 {
			 prevAng=(player.rotationYaw);
		 }
		 mc.ingameGUI.drawTexturedModalRect(3933, 199, 0, 0, (int)(10*tick), 260);
		 pop();

		 scale =.40F;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesArrow[0]);
		 mc.ingameGUI.drawTexturedModalRect(630, 200, 0, 0, 255, 260);
		 pop();
		
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesArrow[1]);
		 ang = player.rotationYaw;
		 if(ang<prevAng)
		 {
			 prevAng=(player.rotationYaw);
		 }
		 mc.ingameGUI.drawTexturedModalRect(633, 199, 0, 0, (int)((ang-prevAng)*3), 260);
		 pop();
		 
		 if((ang-prevAng)>=85)
		 {
			 turnRight=true;
			 //return true;
		 }
		 return turnRight;
	 }
	 
	 public boolean renderTutorialTurnLeft(Entity player)
	 {
		 if(turnLeft)
		 {
			 return turnLeft;
		 }
	
		 float scale =.125F;
		 int tick=player.ticksExisted%20;
		 if(tick>=15)
			 tick=15;

		 push(scale);
		 mc.getTextureManager().bindTexture(texturesArrow[2]);
		 mc.ingameGUI.drawTexturedModalRect(3150-tick*10, 100, 0, 0, 255, 260);
		 pop();
		 
	     scale =.10F;
	     push(scale);
	     GL11.glRotated(180, 0, 0, 1);
	     mc.getTextureManager().bindTexture(texturesArrow[0]);
	     mc.ingameGUI.drawTexturedModalRect(-3800, -455, 0, 0, 255, 260);
	     pop();
	     
	     push(scale);
	     GL11.glRotated(180, 0, 0, 1);
	     mc.getTextureManager().bindTexture(texturesArrow[1]);
	     mc.ingameGUI.drawTexturedModalRect(-3800, -455, 0, 0, (int)(10*tick), 260);
	     pop();
	     
	     scale =.40F;
	     push(scale);
	     GL11.glRotated(180, 0, 0, 1);
	     mc.getTextureManager().bindTexture(texturesArrow[0]);
	     mc.ingameGUI.drawTexturedModalRect(-580, -450, 0, 0, 255, 260);
	     pop();
	     
	     push(scale);
	     GL11.glRotated(180, 0, 0, 1);
	     mc.getTextureManager().bindTexture(texturesArrow[1]);
	     double ang = player.rotationYaw;
	     if(ang>prevAng)
	     {
	    	 prevAng=(player.rotationYaw);
	     }
	     mc.ingameGUI.drawTexturedModalRect(-577, -452, 0, 0, (int)((prevAng-ang)*3), 260);
	     pop();
	     if((prevAng-ang)>=85)
	     {
	    	 turnLeft=true;
	    	 //return true;
	     }
	     return turnLeft;
	 }
	 
	 public static void renderTutorialUseKBB(Entity player)
	 {
		 float scale =.20F;

		 int i=((player.ticksExisted)%111);
		 // GL11.glPushMatrix();
		 if (i>78 && i<83)
		 i=1;
		 else i=0;
		 
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesMouseRightClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 250);
		 pop();
		
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%111);
		 i/=3;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesKBB[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static void renderTutorialManageInventory(Entity player)
	 {
		 float scale =.20F;

		 int i=((player.ticksExisted)%99);
		 // GL11.glPushMatrix();
		 if((i>17 && i<21) || (i>36 && i<40) || (i>50 && i<54) || (i>69 && i<73))
			 i=1;
		 else
			 i=0;
		 
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesMouseLeftClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 250);
		 pop();
		
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%99);
		 i/=3;
		 
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesManagingInventory[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static void renderTutorialAccessInventory(Entity player)
	 {
		 float scale =.20F;

		 int i=((player.ticksExisted)%36);
		 // GL11.glPushMatrix();
		 if(i>30 && i<34)
			 i=1;
		 else
			 i=0;
		 
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesMouseRightClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 250);
		 pop();
		
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%36);
		 i/=3;
		 
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesAccessingInventories[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 
	
}


