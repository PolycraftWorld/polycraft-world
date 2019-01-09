package edu.utd.minecraft.mod.polycraft.entity.entityliving.render;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityAndroid;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.ResearchAssistantEntity;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.model.ModelPolycraftBiped;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderPolycraftBiped extends RenderBiped {

	private static final ResourceLocation researchAssistantTexture = new ResourceLocation(
			PolycraftMod.getAssetName("textures/entity/researchassistant.png"));
	private static final ResourceLocation androidTexture = new ResourceLocation(
			PolycraftMod.getAssetName("textures/entity/android.png"));

	public RenderPolycraftBiped(ModelBase p_i1262_1_, float p_i1262_2_) {
		super((ModelPolycraftBiped) p_i1262_1_, p_i1262_2_, 1.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		if(entity instanceof ResearchAssistantEntity)
			return researchAssistantTexture;
		else if(entity instanceof EntityAndroid) 
			return androidTexture;
		else
			return researchAssistantTexture;
	}

}