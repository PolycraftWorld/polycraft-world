package edu.utd.minecraft.mod.polycraft.entity.entityliving.render;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityAndroid;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.ResearchAssistantEntity;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.model.ModelPolycraftBiped;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderPolycraftBiped<T extends EntityLiving> extends RenderBiped<T> {

	private static final ResourceLocation researchAssistantTexture = new ResourceLocation(
			PolycraftMod.getAssetNameString("textures/entity/researchassistant.png"));
	private static final ResourceLocation androidTexture = new ResourceLocation(
			PolycraftMod.getAssetNameString("textures/entity/android.png"));

	public RenderPolycraftBiped(ModelBase p_i1262_1_, float p_i1262_2_) {
		super(Minecraft.getMinecraft().getRenderManager(), (ModelPolycraftBiped) p_i1262_1_, p_i1262_2_, 1.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(T entity) {
		if(entity instanceof ResearchAssistantEntity)
			return researchAssistantTexture;
		else if(entity instanceof EntityAndroid) 
			return androidTexture;
		else
			return researchAssistantTexture;
	}

}