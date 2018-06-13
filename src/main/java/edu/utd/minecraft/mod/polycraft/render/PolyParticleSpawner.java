package edu.utd.minecraft.mod.polycraft.render;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class PolyParticleSpawner
{
	private static Minecraft mc = Minecraft.getMinecraft();
	
	public static EntityFX EntityBreakingParticle(Item item, double par2, double par4, double par6, double par8, double par10, double par12)
	{
		if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null)
		{
			int var14 = mc.gameSettings.particleSetting;

			if (var14 == 1 && mc.theWorld.rand.nextInt(3) == 0)
			{
				var14 = 2;
			}

			double var15 = mc.renderViewEntity.posX - par2;
			double var17 = mc.renderViewEntity.posY - par4;
			double var19 = mc.renderViewEntity.posZ - par6;
			EntityFX var21 = null;
			double var22 = 16.0D;

			if (var15 * var15 + var17 * var17 + var19 * var19 > var22 * var22)
			{
				return null;
			}
			else if (var14 > 1)
			{
				return null;
			}
			else
			{

				var21 = new EntityBreakingFX(mc.theWorld, par2, par4, par6, item);;
				

				mc.effectRenderer.addEffect(var21);
				return var21;
			}
		}
		return null;
	}
	
	public static EntityFX EntityHeal( double par2, double par4, double par6, double par8, double par10, double par12)
	{
		if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null)
		{
			int var14 = mc.gameSettings.particleSetting;

			if (var14 == 1 && mc.theWorld.rand.nextInt(3) == 0)
			{
				var14 = 2;
			}

			double var15 = mc.renderViewEntity.posX - par2;
			double var17 = mc.renderViewEntity.posY - par4;
			double var19 = mc.renderViewEntity.posZ - par6;
			EntityFX var21 = null;
			double var22 = 16.0D;

			if (var15 * var15 + var17 * var17 + var19 * var19 > var22 * var22)
			{
				return null;
			}
			else if (var14 > 1)
			{
				return null;
			}
			else
			{
				
				var21 = new EntityAuraFX(mc.theWorld, par2, par4, par6, par8, par10, par12);
	            ((EntityFX)var21).setParticleTextureIndex(82);
                ((EntityFX)var21).setRBGColorF(1.0F, 1.0F, 1.0F);

				mc.effectRenderer.addEffect(var21);
				return var21;
			}
		}
		return null;
	}
}
