package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class TutorialRender {
	public static double prevAng=0;
	
	public static final TutorialRender instance = new TutorialRender();
	
	@SideOnly(Side.CLIENT)
	public static Minecraft mc = Minecraft.getMinecraft();
	@SideOnly(Side.CLIENT)
	private static FontRenderer fontRenderer=mc.fontRenderer;
	
	
	
	static	ResourceLocation[] texturesFloatJungle = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating000.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating001.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating002.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating003.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating004.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating005.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating006.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating007.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating008.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating009.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating010.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating011.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating012.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating013.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating014.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating015.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating016.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating017.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating018.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating019.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating020.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating021.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating022.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating023.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingJungleGIF/TutorialTrial3-Floating024.png")),
	
	};
	
	static	ResourceLocation[] texturesFloatSwamp = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating000.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating001.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating002.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating003.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating004.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating005.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating006.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating007.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating008.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating009.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating010.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating011.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating012.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating013.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating014.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating015.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating016.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating017.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating018.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating019.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating020.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating021.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating022.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating023.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating024.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating025.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating026.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating027.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating028.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating029.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating030.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/FloatingSwampGIF/TutorialTrial4-Floating031.png")),
			
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
	
	static 			ResourceLocation[] texturesSprint = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting000.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting001.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting002.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting003.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting004.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting005.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting006.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting007.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting008.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting009.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting010.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting011.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting012.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting013.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting014.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting015.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting016.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting017.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting018.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting019.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting020.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting021.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting022.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting023.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting024.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting025.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting026.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting027.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting028.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting029.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting030.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting031.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting032.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting033.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting034.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting035.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintGIF/TutorialTrial3-Sprinting036.png")),
			
	};
	
	static 			ResourceLocation[] texturesSprintJump = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping000.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping001.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping002.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping003.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping004.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping005.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping006.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping007.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping008.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping009.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping010.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping011.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping012.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping013.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping014.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping015.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping016.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping017.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping018.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping019.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping020.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping021.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping022.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping023.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping024.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping025.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping026.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping027.png")),
			new ResourceLocation(PolycraftMod.getAssetName("textures/gui/SprintJumpGIF/TutorialTrial3-SprintJumping028.png")),

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
	 
	 
	 static 				 ResourceLocation[] texturesAccessInventory1 = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccessGIF/TutorialTrial4-AccessingInventories000.png")),
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
	 
	 
	 static 				 ResourceLocation[] texturesManageInventory1 = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManageGIF/TutorialTrial4-ManagingInventory000.png")),
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
	 
	 static 				 ResourceLocation[] texturesPlaceBlocks = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks000.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks001.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks002.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks003.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks004.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks005.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks006.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks007.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks008.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks009.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks010.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks011.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks012.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks013.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks014.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks015.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks016.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks017.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks018.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks019.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks020.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks021.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks022.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks023.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks024.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks025.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks026.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks027.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks028.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks029.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks030.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks031.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks032.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks033.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks034.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks035.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks036.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks037.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks038.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks039.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/PlacingBlocksGIF/TutorialTrial4-PlaceBlocks040.png")),

	 };
	 
	 static 				 ResourceLocation[] texturesAccessInventory2 = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories000.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories001.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories002.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories003.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories004.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories005.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories006.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories007.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories008.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories009.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories010.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories011.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories012.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories013.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories014.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories015.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories016.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories017.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories018.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories019.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories020.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories021.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories022.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryAccess2GIF/TutorialTrial5-AccessingInventories023.png")),
				
	 };
	 
	 static 				 ResourceLocation[] texturesManageInventory2 = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory000.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory001.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory002.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory003.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory004.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory005.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory006.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory007.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory008.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory009.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory010.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory011.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory012.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory013.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory014.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory015.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory016.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory017.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory018.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory019.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory020.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory021.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory022.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory023.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory024.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory025.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory026.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory027.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory028.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory029.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory030.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/InventoryManage2GIF/TutorialTrial5-ManagingInventory031.png")),

	 };
	 
	 static 				 ResourceLocation[] texturesAccessTable = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable000.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable001.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable002.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable003.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable004.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable005.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable006.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable007.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable008.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable009.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable010.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable011.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable012.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable013.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable014.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable015.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable016.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable017.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable018.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable019.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/TableAccessGIF/TutorialTrial5-AccessTable020.png")),

	 };
	 
	 static 				 ResourceLocation[] texturesCraftPick = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick000.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick001.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick002.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick003.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick004.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick005.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick006.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick007.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick008.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick009.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick010.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick011.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick012.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick013.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick014.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick015.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick016.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick017.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick018.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick019.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick020.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick021.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick022.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick023.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick024.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick025.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick026.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick027.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick028.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick029.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick030.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick031.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick032.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick033.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick034.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick035.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick036.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick037.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick038.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick039.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick040.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick041.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick042.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick043.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/CraftPickGIF/TutorialTrial5-CraftPick044.png")),
				
	 };
	 
	 static 				 ResourceLocation[] texturesMine = {	new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine000.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine001.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine002.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine003.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine004.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine005.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine006.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine007.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine008.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine009.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine010.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine011.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine012.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine013.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine014.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine015.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine016.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine017.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine018.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine019.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine020.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine021.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine022.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine023.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine024.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine025.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine026.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine027.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine028.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine029.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine030.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine031.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine032.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine033.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine034.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine035.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine036.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine037.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine038.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine039.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine040.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine041.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/MineGIF/TutorialTrial5-Mine042.png")),
	 };
	 
	 static ResourceLocation[] texturesMouseRightClick = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/mouse.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/mouseRightClick.png")),
				};
	 
	 static ResourceLocation[] texturesMouseLeftClick = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/mouse.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/mouseLeftClick.png")),
				};
	 
	 static ResourceLocation[] textures = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WASD_0.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/WASD_1.png")),
				};
	 
	 static ResourceLocation[] textures4 = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/space.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/spaceGreen.png")),
				};
	
	 static ResourceLocation[] texturesCtrl = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/control.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/controlGreen.png")),
				};
	 
	 static ResourceLocation[] texturesEsc = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/escape.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/escapeGreen.png")),
				};
	 
	 static ResourceLocation[] textures1Key = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/1.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/1Green.png")),
				};
	 
	 static ResourceLocation[] textures2Key = {new ResourceLocation(PolycraftMod.getAssetName("textures/gui/2.png")),
				new ResourceLocation(PolycraftMod.getAssetName("textures/gui/2Green.png")),
				};

	 
	public TutorialRender()
	{
		
	}
	
	public void setAng(Entity entity)
	{
		prevAng=entity.rotationYaw;
	}
	
	 public void renderTutorialDrawString(String str, int x, int y)
	 {
		 push(1F);
		 fontRenderer.drawStringWithShadow(str, x, y, 16777215);	
		 pop();
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
	
	 public boolean renderTutorialLook(Entity player,double ang)
	 {
	
		 float scale =.125F;

		 push(scale);
		 GL11.glTranslated(1800, 1200, 0);
		 GL11.glRotated((ang*180)/Math.PI+90, 0, 0, 1);
		 mc.getTextureManager().bindTexture(texturesArrow[1]);
		 mc.ingameGUI.drawTexturedModalRect(0, 0, 0, 0, 255, 250);
		 pop();
		 
	     return false;
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
	 
	 public static void renderTutorialFloatJungle(Entity player)
	 {
		 float scale =.20F;
		 int i=((player.ticksExisted)%50);
		 if((i>15 && i<32))
			 i=1;
		 else
			 i=0;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesWASD[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, -40, 0, 0,250, 250);
		 pop(); 
		 
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%50);
		 // GL11.glPushMatrix();
		 if((i>1 && i<32))
			 i=1;
		 else
			 i=0;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesSpace[i]);
		 mc.ingameGUI.drawTexturedModalRect(350, 106, 0, 0, 255, 260);
		 pop();
		
		 
		 float scale3 =.20F;
		 i=((player.ticksExisted)%50);
		 i/=2;
		 push(scale3);
		 mc.getTextureManager().bindTexture(texturesFloatJungle[i]);
		 mc.ingameGUI.drawTexturedModalRect(15, 15, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	
	 
	 public static void renderTutorialFloatSwamp(Entity player)
	 {
		 float scale =.20F;
		 int i=((player.ticksExisted)%64);
		 if((i>2 && i<54))
			 i=1;
		 else
			 i=0;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesWASD[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, -40, 0, 0,250, 250);
		 pop(); 
		 
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%64);
		 // GL11.glPushMatrix();
		 if((i>30 && i<55))
			 i=1;
		 else
			 i=0;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesSpace[i]);
		 mc.ingameGUI.drawTexturedModalRect(350, 106, 0, 0, 255, 260);
		 pop();
		
		 
		 float scale3 =.20F;
		 i=((player.ticksExisted)%64);
		 i/=2;
		 push(scale3);
		 mc.getTextureManager().bindTexture(texturesFloatSwamp[i]);
		 mc.ingameGUI.drawTexturedModalRect(15, 15, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	
	 
	public static void renderTutorialSprintJump(Entity player)
	 {
		 float scale =.20F;
		 int i=1;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesWASD[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, -40, 0, 0,250, 250);
		 pop(); 
		 
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%58);
		 // GL11.glPushMatrix();
		 if(i>25 && i<35)
			 i=1;
		 else 
			 i=0;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesSpace[i]);
		 mc.ingameGUI.drawTexturedModalRect(400, 106, 0, 0,255, 250);
		 pop();
		 
		 
		 float scale3 =.20F;
		 i=((player.ticksExisted)%58);
		 // GL11.glPushMatrix();
		 if(i>8 && i<74)
			 i=1;
		 else 
			 i=0;
		 push(scale3);
		 mc.getTextureManager().bindTexture(texturesCtrl[i]);
		 mc.ingameGUI.drawTexturedModalRect(200, 106, 0, 0, 200, 250);
		 pop();
		 
		 
		 float scale4 =.20F;
		 i=((player.ticksExisted)%58);
		 i/=2;
		 push(scale4);
		 mc.getTextureManager().bindTexture(texturesSprintJump[i]);
		 mc.ingameGUI.drawTexturedModalRect(15, 15, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	
	public static void renderTutorialSprint(Entity player)
	 {
		float scale =.20F;

		 int i=1;
		 
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesWASD[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, -40, 0, 0,250, 250);
		 pop(); 
		
		 
		 float scale2 =.20F;

		 i=((player.ticksExisted)%74);
		 // GL11.glPushMatrix();
		 if(i>10 && i<74)
			 i=1;
		 else 
			 i=0;
		 
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesCtrl[i]);
		 mc.ingameGUI.drawTexturedModalRect(200, 107, 0, 0, 200, 250);
		 pop();
		 
		 
		 float scale3 =.20F;
		 i=((player.ticksExisted)%74);
		 i/=2;
		 
		 push(scale3);
		 mc.getTextureManager().bindTexture(texturesSprint[i]);
		 mc.ingameGUI.drawTexturedModalRect(15, 15, 0, 0, 255, 250);
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
	 
	 public static void renderTutorialAccessInventory1(Entity player)
	 {
		 float scale =.15F;
		 int i=((player.ticksExisted)%36);
		 if(i>28 && i<33)
			 i=1;
		 else
			 i=0;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesMouseRightClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(800, 0, 0, 0, 255, 250);
		 pop();
		
		 
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%36);
		 i/=3;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesAccessInventory1[i]);
		 mc.ingameGUI.drawTexturedModalRect(15, 15, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static void renderTutorialManageInventory1(Entity player)
	 {
		 float scale =.20F;
		 int i=((player.ticksExisted)%99);
		 if(i>85 && i<90)
			 i=1;
		 else
			 i=0;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesEsc[i]);
		 mc.ingameGUI.drawTexturedModalRect(200, -20, 0, 0, 255, 250);
		 pop();
		 
		 
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%99);
		 if((i>14 && i<18)||(i>33 && i<38)||(i>50 && i<54)||(i>69 && i<74))
			 i=1;
		 else
			 i=0;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesMouseRightClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(400, 0, 0, 0, 255, 250);
		 pop();
		
		 
		 
		 float scale3 =.20F;
		 i=((player.ticksExisted)%99);
		 i/=3;
		 push(scale3);
		 mc.getTextureManager().bindTexture(texturesManageInventory1[i]);
		 mc.ingameGUI.drawTexturedModalRect(15, 15, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static void renderTutorialPlacingBlocks(Entity player)
	 {
		 float scale =.20F;
		 int i=((player.ticksExisted)%123);
		 if(i>50 && i<53)
			 i=1;
		 else
			 i=0;
		 push(scale);
		 mc.getTextureManager().bindTexture(textures2Key[i]);
		 mc.ingameGUI.drawTexturedModalRect(200, -20, 0, 0, 255, 250);
		 pop();
		 
		 
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%123);
		 if((i>20 && i<23)||(i>40 && i<43)||(i>62 && i<65)||(i>77 && i<80) || (i>92 && i<95)||(i>110 && i<113))
			 i=1;
		 else
			 i=0;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesMouseRightClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(800, 0, 0, 0, 255, 250);
		 pop();
		
		 
		 
		 float scale3 =.20F;
		 i=((player.ticksExisted)%123);
		 i/=3;
		 push(scale3);
		 mc.getTextureManager().bindTexture(texturesPlaceBlocks[i]);
		 mc.ingameGUI.drawTexturedModalRect(15, 15, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static void renderTutorialAccessInventory2(Entity player)
	 {
		 float scale =.20F;
		 int i=((player.ticksExisted)%48);
		 if(i>38 && i<43)
			 i=1;
		 else
			 i=0;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesMouseRightClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 250);
		 pop();
		
		 
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%48);
		 i/=2;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesAccessInventory2[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static void renderTutorialManageInventory2(Entity player)
	 {
		 float scale =.20F;
		 int i=((player.ticksExisted)%64);
		 if((i>13 && i<17)||(i>25 && i<29)||(i>33 && i<38)||(i>45 && i<50))
			 i=1;
		 else
			 i=0;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesMouseLeftClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(2, 2, 0, 0, 255, 250);
		 pop();
		
		 
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%64);
		 i/=2;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesManageInventory2[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static void renderTutorialAccessTable(Entity player)
	 {
		 float scale =.15F;
		 int i=((player.ticksExisted)%42);
		 if(i>38 && i<43)
			 i=1;
		 else
			 i=0;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesMouseRightClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(600, 0, 0, 0, 255, 250);
		 pop();
		
		 
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%42);
		 i/=2;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesAccessTable[i]);
		 mc.ingameGUI.drawTexturedModalRect(15, 15, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static void renderTutorialCraftingPick(Entity player)
	 {
		 float scale =.20F;
		 int i=((player.ticksExisted)%135);
		 if(i>122 && i<125)
			 i=1;
		 else
			 i=0;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesEsc[i]);
		 mc.ingameGUI.drawTexturedModalRect(200, -20, 0, 0, 255, 250);
		 pop();
		 
		 
		 
		 float scale2 =.15F;
		 i=((player.ticksExisted)%135);
		 if((i>12 && i<15) || (i>25 && i<28) || (i>38 && i<41) || (i>51 && i<54) || (i>64 && i<67) || (i>77 && i<80) || (i>85 && i<88) || (i>95 && i<98) || (i>110 && i<113))
			 i=1;
		 else
			 i=0;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesMouseLeftClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(600, 2, 0, 0, 255, 250);
		 pop();
		
		 
		 
		 float scale3 =.20F;
		 i=((player.ticksExisted)%135);
		 i/=3;
		 push(scale3);
		 mc.getTextureManager().bindTexture(texturesCraftPick[i]);
		 mc.ingameGUI.drawTexturedModalRect(15, 15, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	 
	 public static void renderTutorialMining(Entity player)
	 {
		 float scale =.20F;
		 int i=((player.ticksExisted)%86);
		 if ((i>45 && i<52) || (i>81 && i<86))
			 i=1;
		 else i=0;
		 push(scale);
		 mc.getTextureManager().bindTexture(texturesWASD[i]);
		 mc.ingameGUI.drawTexturedModalRect(300, 2, 0, 0, 255, 260);
		 pop();
		 
		 
		 
		 float scale1 =.15F;
		 i=((player.ticksExisted)%86);
		 if((i>5 && i<8) || (i>10 && i<20) || (i>38 && i<45) || (i>51 && i<75))
			 i=1;
		 else
			 i=0;
		 push(scale1);
		 mc.getTextureManager().bindTexture(texturesMouseLeftClick[i]);
		 mc.ingameGUI.drawTexturedModalRect(800, 2, 0, 0, 255, 250);
		 pop();
		
		 
		 
		 float scale2 =.20F;
		 i=((player.ticksExisted)%86);
		 i/=2;
		 push(scale2);
		 mc.getTextureManager().bindTexture(texturesMine[i]);
		 mc.ingameGUI.drawTexturedModalRect(15, 15, 0, 0, 255, 250);
		 pop();	
		 
		 mc.entityRenderer.updateRenderer();
	 }
	
}


