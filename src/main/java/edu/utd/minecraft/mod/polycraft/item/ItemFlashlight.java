package edu.utd.minecraft.mod.polycraft.item;

import java.util.Collection;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.PointLightSource;

public class ItemFlashlight extends PolycraftUtilityItem {

	private static final float[][] lightSourceTransforms = new float[][] {
			new float[] { 0, 0 },
			new float[] { .25f, .25f },
			new float[] { .25f, -.25f },
			new float[] { -.25f, .25f },
			new float[] { -.25f, -.25f },
			new float[] { .5f, .5f },
			new float[] { .5f, -.5f },
			new float[] { -.5f, .5f },
			new float[] { -.5f, -.5f },
			new float[] { .75f, .75f },
			new float[] { .75f, -.75f },
			new float[] { -.75f, .75f },
			new float[] { -.75f, -.75f },
			new float[] { 1, 1 },
			new float[] { 1, -1 },
			new float[] { -1, 1 },
			new float[] { -1, -1 } };

	public static Collection<PointLightSource> createLightSources(final World world) {
		final Collection<PointLightSource> lightSources = Lists.newLinkedList();
		for (int i = 0; i < lightSourceTransforms.length; i++)
			lightSources.add(new PointLightSource(world));
		return lightSources;
	}

	public static void createLightCone(final EntityPlayer player, final Collection<PointLightSource> lightSources, int range) {
		int i = 0;
		for (final PointLightSource source : lightSources) {
			source.updateFromPlayerViewConePart(
					player,
					15, //maxLightLevel,
					15f / (float)range, //lightLevelDecreaseByDistance,
					lightSourceTransforms[i][0] * 15, //viewing cone angle
					lightSourceTransforms[i][1] * 15 //viewing cone angle
			);
			i++;
		}
	}

	public ItemFlashlight(final CustomObject config) {
		this.setTextureName(PolycraftMod.getAssetName("flashlight"));
		this.setCreativeTab(CreativeTabs.tabTools);
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World world, Entity player, int par4, boolean par5) {
		// TODO Auto-generated method stub
		
		if(world.isRemote) {
			return;
		}
		
		final int FLASHLIGHT_DISTANCE = 7;
		int currentFlashlightDistance = FLASHLIGHT_DISTANCE;
		
		int posX = (int) player.posX;
		int posY = (int) player.posY;
		int posZ = (int) player.posZ;
		
		//world.setLightValue(EnumSkyBlock.Block, posX, posY, posZ, 16);
		//FMLClientHandler.instance().getClient().theWorld.updateLightByType(EnumSkyBlock.Sky, (int)player.posX, (int)player.posY, (int)player.posZ, 16);

		double pitch = (double) player.rotationPitch;
		double yaw = (double) player.rotationYaw;
		
		int updatePosX = posX, updatePosZ = posZ, updatePosY = posY;
		
		if(pitch > 0 && pitch <= 90) {
			pitch = 360 - pitch;
		} else {
			pitch *= -1;
		}
		
		updatePosY += (int) (Math.sin(Math.toRadians(pitch)) * currentFlashlightDistance);
		currentFlashlightDistance = (int) (Math.cos(Math.toRadians(pitch)) * currentFlashlightDistance);
		
		updatePosX -= (int) (Math.sin(Math.toRadians(yaw)) * currentFlashlightDistance);
		updatePosZ += (int) (Math.cos(Math.toRadians(yaw)) * currentFlashlightDistance);
		
		world.setLightValue(EnumSkyBlock.Block, (int) updatePosX,
				(int) posY, (int) updatePosZ, 15);
		world.markBlockRangeForRenderUpdate((int) updatePosX,
				(int) posY, (int) updatePosX, 12, 12, 12);
		world.markBlockForUpdate((int) updatePosX, (int) posY,
				(int) updatePosZ);
		world.updateLightByType(EnumSkyBlock.Block, (int) updatePosX,
				(int) posY + 1, (int) updatePosZ);
		world.updateLightByType(EnumSkyBlock.Block, (int) updatePosX,
				(int) posY - 1, (int) updatePosZ);
		world.updateLightByType(EnumSkyBlock.Block,
				(int) updatePosX + 1, (int) posY, (int) updatePosZ);
		world.updateLightByType(EnumSkyBlock.Block,
				(int) updatePosX - 1, (int) posY, (int) updatePosZ);
		world.updateLightByType(EnumSkyBlock.Block, (int) updatePosX,
				(int) posY, (int) updatePosZ + 1);
		world.updateLightByType(EnumSkyBlock.Block, (int) updatePosX,
				(int) posY, (int) updatePosZ - 1);
		
		

		super.onUpdate(par1ItemStack, world, player, par4, par5);
	}
}
