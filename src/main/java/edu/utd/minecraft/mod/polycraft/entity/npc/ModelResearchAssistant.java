package edu.utd.minecraft.mod.polycraft.entity.npc;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class ModelResearchAssistant extends ModelBiped {

	public ModelResearchAssistant() {
		this(0.0F);
	}

	public ModelResearchAssistant(float p_i1148_1_) {
		this(p_i1148_1_, 0.0F, 64, 64);
	}

	public ModelResearchAssistant(float p_i1149_1_, float p_i1149_2_, int p_i1149_3_, int p_i1149_4_) {
		super(p_i1149_1_, p_i1149_2_, p_i1149_3_, p_i1149_4_);
	}

	public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
			float p_78088_6_, float p_78088_7_) {
		// Rotation angles are called by the superclass method.
		super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
	}
}
