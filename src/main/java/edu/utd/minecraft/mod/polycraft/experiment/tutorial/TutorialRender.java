package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class TutorialRender {
	public static double prevAng=0;
	
	public static final TutorialRender instance = new TutorialRender();
	
	@SideOnly(Side.CLIENT)
	public static Minecraft mc = Minecraft.getMinecraft();
	
	
	static	ResourceLocation[] texturesFloat = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating000.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating001.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating002.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating003.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating004.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating005.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating006.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating007.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating008.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating009.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating010.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating011.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating012.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating013.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating014.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating015.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating016.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating017.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating018.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating019.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating020.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating021.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating022.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating023.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingGIF/TutorialTrial3-Floating024.png")),
	
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
	
	 static ResourceLocation[] texturesJump = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_00.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_01.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_02.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_03.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_04.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_05.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_06.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_07.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_08.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_09.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_10.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_11.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_12.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_13.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_14.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_15.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_16.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_17.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_18.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_19.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_20.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_21.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_22.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_23.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_24.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_25.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_26.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/JumpingGIF/frame_27.png")),
				
	 };
	 
	 
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
	 
	 
	 static 				 ResourceLocation[] texturesAccessInventory = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories000.png")),
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
	 
	 
	 static 				 ResourceLocation[] texturesManageInventory = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory000.png")),
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
	 
	 static ResourceLocation[] texturesMouseRightClick = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/mouse.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/mouseRightClick.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/mouse.png")),
				};
	 
	 static ResourceLocation[] textures = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WASD_0.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WASD_1.png")),
				};
	 static ResourceLocation[] textures4 = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/space.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/spaceGreen.png")),
				};
	

	 
	public TutorialRender()
	{
		
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
	
	 public static void renderTutorialJump(Entity player)
	 {
		 float scale =.20F;

		 int i=((player.ticksExisted)%56);
		 // GL11.glPushMatrix();
		 if((i>5 && i<12) || (i>27 && i<35))
			 i=1;
		 else
			 i=0;
		 
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesSpace[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 260);
		 pop();
		
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%56);
		 i/=2;
		 
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesJump[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static void renderTutorialFloating(Entity player)
	 {
		 float scale =.20F;

		 int i=((player.ticksExisted)%50);
		 // GL11.glPushMatrix();
		 if((i>1 && i<45))
			 i=1;
		 else
			 i=0;
		 
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesSpace[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 260);
		 pop();
		
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%50);
		 i/=2;
		 
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesFloat[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	
	
	
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
		 
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%12);
		 push(scale);
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
		 double ang = player.rotationYaw;
		 if((ang-prevAng)>=85)
		 {
			 return true;
			 //return true;
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

		 mc.ingameGUI.drawTexturedModalRect(3933, 199, 0, 0, (int)(10*tick), 260);
		 pop();

		 scale =.40F;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesArrow[0]);
		 mc.ingameGUI.drawTexturedModalRect(630, 200, 0, 0, 255, 260);
		 pop();
		
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesArrow[1]);

		 if(ang<prevAng)
		 {
			 prevAng=(player.rotationYaw);
		 }
		 mc.ingameGUI.drawTexturedModalRect(633, 199, 0, 0, (int)((ang-prevAng)*3), 260);
		 pop();
		 
		 return false;
	 }
	 
	 public boolean renderTutorialTurnLeft(Entity player)
	 {
	     double ang = player.rotationYaw;
	     if((prevAng-ang)>=85)
	     {
	    	 return true;
	    	 //return true;
	     }
	
		 float scale =.125F;
		 int tick=player.ticksExisted%20;
		 if(tick>=15)
			 tick=15;

		 push(scale);
		 mc.getTextureManager().bindTexture(texturesArrow[2]);
		 mc.ingameGUI.drawTexturedModalRect(3150-tick*10, 100, 0, 0, 255, 250);
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

	     if(ang>prevAng)
	     {
	    	 prevAng=(player.rotationYaw);
	     }
	     mc.ingameGUI.drawTexturedModalRect(-577, -452, 0, 0, (int)((prevAng-ang)*3), 260);
	     pop();

	     return false;
	 }
	 
	 public static void renderTutorialUseKBB(Entity player)
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
		 mc.getTextureManager().bindTexture(texturesMouseRightClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 260);
		 pop();
		
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%110);
		 if(i<=18)
		 i=19;
		 else
		 i/=3;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesKBB[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static void renderTutorialAccessInventory(Entity player)
	 {
		 float scale =.20F;

		 int i=((player.ticksExisted)%36);
		 // GL11.glPushMatrix();
		 if(i>28 && i<33)
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
		 mc.getTextureManager().bindTexture(texturesAccessInventory[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static void renderTutorialManageInventory(Entity player)
	 {
		 float scale =.20F;

		 int i=((player.ticksExisted)%99);
		 // GL11.glPushMatrix();
		 if((i>14 && i<18)||(i>33 && i<38)||(i>50 && i<54)||(i>69 && i<74))
			 i=1;
		 else
			 i=0;
		 
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesMouseRightClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 250);
		 pop();
		
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%99);
		 i/=3;
		 
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesManageInventory[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	
}


