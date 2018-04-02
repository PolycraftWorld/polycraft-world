package edu.utd.minecraft.mod.polycraft.entity.entityliving.render;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.ResearchAssistantEntity;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.model.ModelPolycraftBiped;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderPolycraftBiped extends RenderBiped {

	private static final ResourceLocation mobTexture = new ResourceLocation(
			PolycraftMod.getAssetName("textures/entity/researchassistant.png"));

	public RenderPolycraftBiped(ModelBase p_i1262_1_, float p_i1262_2_) {
		super((ModelPolycraftBiped) p_i1262_1_, p_i1262_2_, 1.0F);
	}

	protected ResourceLocation getEntityTexture(ResearchAssistantEntity entity) {
		return mobTexture;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return this.getEntityTexture((ResearchAssistantEntity) entity);
	}

}