package edu.utd.minecraft.mod.polycraft.entity.entityliving.render;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderDummy extends RenderLiving {
	
	private static final ResourceLocation dummyTextures = new ResourceLocation(
			PolycraftMod.getAssetName("textures/entity/Dummy.png"));
	
	public RenderDummy(ModelBase p_i1262_1_, float p_i1262_2_) {
		super(p_i1262_1_, p_i1262_2_);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		// TODO Auto-generated method stub
		return dummyTextures;
	}

}
