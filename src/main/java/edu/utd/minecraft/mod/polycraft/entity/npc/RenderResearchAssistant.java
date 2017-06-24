package edu.utd.minecraft.mod.polycraft.entity.npc;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderResearchAssistant extends RenderBiped {

	private static final ResourceLocation mobTexture = new ResourceLocation(
			PolycraftMod.getAssetName("textures/entity/testmob.png"));

	public RenderResearchAssistant(ModelBase p_i1262_1_, float p_i1262_2_) {
		super((ModelResearchAssistant) p_i1262_1_, p_i1262_2_, 1.0F);
	}

	protected ResourceLocation getEntityTexture(EntityResearchAssistant entity) {
		return mobTexture;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return this.getEntityTexture((EntityResearchAssistant) entity);
	}

}
